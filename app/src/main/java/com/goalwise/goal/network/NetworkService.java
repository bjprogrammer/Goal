package com.goalwise.goal.network;


import com.goalwise.goal.model.ListResponse;
import com.goalwise.goal.utils.Constants;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

//ALL API calls endpoints
public interface NetworkService {
    @POST("dev/search")
    @Headers({"Content-Type:"+ Constants.CONTENT_TYPE, "x-api-key:"+ Constants.API_KEY})
    Observable<ListResponse> searchFund(@Body RequestBody body) ;
}