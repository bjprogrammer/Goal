package com.goalwise.goal.network;

import android.content.Context;

import com.goalwise.goal.model.ListResponse;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;


//Networking using RxJava
public class Service {
    private Context context;
    public Service(Context context){
        this.context=context;
    }


    public void getFundsList(final FundsCallback callback, String query){
        Map<String, Object> jsonParams = new HashMap<>();
        jsonParams.put("keyword", query);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;"),(new JSONObject(jsonParams)).toString());
        NetworkAPI.getClient(context).create(NetworkService.class).searchFund(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<ListResponse>() {
                @Override
                public void onSubscribe(Disposable d) {
                    callback.getDisposable(d);
                }

                @Override
                public void onNext(ListResponse response) {
                    callback.onSuccess(response);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(new NetworkError(e));
                }

                @Override
                public void onComplete() { }
            });
    }



    public interface FundsCallback{
        void onSuccess(ListResponse response);
        void onError(NetworkError networkError);
        void getDisposable(Disposable disposable);
    }
}

