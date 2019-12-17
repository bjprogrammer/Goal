package com.goalwise.goal.main;

import android.content.Context;
import android.os.Handler;

import com.goalwise.goal.database.DatabaseCreator;
import com.goalwise.goal.database.Search;
import com.goalwise.goal.database.SearchDao;
import com.goalwise.goal.model.ListResponse;
import com.goalwise.goal.network.NetworkError;
import com.goalwise.goal.network.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.disposables.Disposable;

public class MainPresenter implements MainContract.MainPresenter{

    private MainContract.MainView view;
    private Disposable disposable;
    private Context context;

    MainPresenter(Context context, MainContract.MainView view) {
        this.view=view;
        this.context=context;
    }

    public void getFundsList( String query) {
        view.showWait();


        new Service(context).getFundsList(new Service.FundsCallback() {
            @Override
            public void onSuccess(ListResponse response)  {
                view.removeWait();

                if(response!=null) {
                    if(response.getStatus().equals("success")) {

                        SearchDao searchDao = DatabaseCreator.getAppDatabase(context).getSearchDao();
                        Search search = new Search();
                        search.setKeyword(query);

                        Executor executor = Executors.newFixedThreadPool(2);
                        executor.execute(() -> {
                            searchDao.insert(search);
                            view.onSuccess(response);

                        });
                    }else {
                        view.onSuccess(null);
                    }
                }
            }

            @Override
            public void onError(final NetworkError networkError) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.removeWait();

                        if(!disposable.isDisposed()) {
                            view.onFailure(networkError.getAppErrorMessage());
                        }
                    }
                }, 300);
            }

            @Override
            public void getDisposable(Disposable d) {
                disposable=d;
            }
        }, query);
    }

    public void unSubscribe(){

        view.removeWait();

        if(disposable!=null){
            disposable.dispose();
        }
    }
}
