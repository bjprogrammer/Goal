package com.goalwise.goal.main;

import com.goalwise.goal.model.ListResponse;

class MainContract{

    interface MainView {
        void showWait();
        void removeWait();
        void onFailure(String appErrorMessage);
        void onSuccess(ListResponse response);
    }

    interface MainPresenter{
         void getFundsList(String query);
         void unSubscribe();
    }
}
