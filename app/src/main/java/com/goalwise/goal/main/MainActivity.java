package com.goalwise.goal.main;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.goalwise.goal.R;
import com.goalwise.goal.database.DatabaseCreator;
import com.goalwise.goal.database.Search;
import com.goalwise.goal.database.SearchDao;
import com.goalwise.goal.databinding.ActivityMainBinding;
import com.goalwise.goal.model.ListResponse;
import com.goalwise.goal.utils.ConnectivityReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import es.dmoral.toasty.Toasty;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;


public class MainActivity extends AppCompatActivity implements MainContract.MainView, ConnectivityReceiver.ConnectivityReceiverListener{
    private ActivityMainBinding binding;
    private MainPresenter presenter;

    private boolean flag = true;
    private IntentFilter intentFilter;
    private ConnectivityReceiver receiver;

    private MainAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String currentQuery;
    private ArrayList<String> searchValues = new ArrayList<>();

    private ArrayAdapter<String> searchAdapter;
    private TextView emptyList;
    private AutoCompleteTextView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        presenter = new MainPresenter(getApplicationContext(),this);

        recyclerView = binding.recyclerView;
        emptyList = binding.tvEmpty;
        progressBar = binding.progressBar;

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();

        //Configuring customized Toast messages
        Toasty.Config.getInstance()
                .setErrorColor( getResources().getColor(R.color.colorPrimaryDark) )
                .setSuccessColor(getResources().getColor(R.color.colorPrimaryDark) )
                .setTextColor(Color.WHITE)
                .tintIcon(true)
                .setTextSize(18)
                .apply();

        adapter = new MainAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        binding.getRoot().findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search = binding.getRoot().findViewById(R.id.searchView);
        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,searchValues);
        search.setAdapter(searchAdapter);

        SearchDao searchDao = DatabaseCreator.getAppDatabase(MainActivity.this).getSearchDao();
        Executor executor = Executors.newFixedThreadPool(2);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Search> searches = searchDao.getSearchList();

                if(!searches.isEmpty()) {
                    for (int i = searches.size() - 1; i > 0; i--) {
                        searchValues.add(searches.get(i).getKeyword());
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        search.setThreshold(3);

        search.setDropDownHeight(600);
//        search.setOnTouchListener((v, event) -> {
//            search.showDropDown();
//            return true;
//        });

        search.setOnItemClickListener((parent, view, position, id) -> presenter.getFundsList(searchValues.get(position)));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>=3) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            presenter.unSubscribe();
                            currentQuery = s.toString();

                            presenter.getFundsList(currentQuery);
                        }
                    },500);
                } else {
                    presenter.unSubscribe();
                    showNoListView();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver, intentFilter);
        ConnectivityReceiver.connectivityReceiverListener = this;
    }

    //Checking internet flag using broadcast receiver
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(flag!=isConnected)
        {
            if(isConnected){
                Toasty.success(this, "Connected to internet", Toast.LENGTH_SHORT, true).show();
            }
            else
            {
                Toasty.error(getApplicationContext(), "Not connected to internet", Toast.LENGTH_LONG, true).show();
            }
        }

        flag= (isConnected);
    }

    @Override
    protected void onDestroy() {
        presenter.unSubscribe();
        presenter = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception e) { }
        }
    }

    @Override
    public void showWait() {
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void removeWait() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {
        progressBar.setVisibility(View.GONE);
        Toasty.error(getApplicationContext(), appErrorMessage, Toast.LENGTH_LONG, true).show();
    }

    @Override
    public void onSuccess(ListResponse response) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(response!=null){
                    if(!response.getData().isEmpty()){
                        adapter.addAll(response);
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyList.setVisibility(View.GONE);
                    }
                    else
                    {
                        showNoListView();
                    }

                    SearchDao searchDao = DatabaseCreator.getAppDatabase(MainActivity.this).getSearchDao();
                    Executor executor = Executors.newFixedThreadPool(2);
                    executor.execute(() -> {
                        searchValues.clear();

                        List<Search> searches = searchDao.getSearchList();

                        if(!searches.isEmpty()) {

                            for (int i = searches.size() - 1; i > 0; i--) {
                                searchValues.add(searches.get(i).getKeyword());
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });

                }
                else
                {
                    showNoListView();
                }


                search.dismissDropDown();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
            }
        });

    }


    private void showNoListView(){
        recyclerView.setVisibility(View.GONE);
        emptyList.setVisibility(View.VISIBLE);
    }

}
