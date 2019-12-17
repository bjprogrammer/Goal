package com.goalwise.goal.network;

import android.content.Context;

import com.goalwise.goal.BuildConfig;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

class NetworkAPI {
    private static Retrofit retrofit = null;

    public  static Retrofit getClient(Context context) {
        File httpCacheDirectory = new File(context.getCacheDir(), "offlineCache");

        //10 MB
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .removeHeader("Pragma")
                                .build();

                        okhttp3.Response response = chain.proceed(request);

                        CacheControl cacheControl = new CacheControl.Builder()
                                .maxAge(5, TimeUnit.MINUTES)
                                .build();


                        return response.newBuilder()
                                .removeHeader("Pragma")
                                .header("Cache-Control", cacheControl.toString())
                                .build();

                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        try{
                            return chain.proceed(original);
                        }catch (Exception e){

                            CacheControl cacheControl = new CacheControl.Builder()
                                    .onlyIfCached()
                                    .maxStale(30, TimeUnit.DAYS)
                                    .build();

                            Request request = original.newBuilder()
                                    .removeHeader("Pragma")
                                    .header("Cache-Control", cacheControl.toString())
                                    .build();

                            return  chain.proceed(request);
                        }
                    }
                })
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .cache(cache)
                .build();


        if(retrofit==null) {
            retrofit= new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASEURL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
