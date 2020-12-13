package com.xlab13.zcleaner.Apps;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AppsApi {

    @GET("partner/app/list")
    Call<AppsResponse> apps();

}
