package com.dius.guardian;

import retrofit.Call;
import retrofit.http.GET;

public interface NodeService {
    @GET("/dangerZone")
    Call<Emergency> emergency();

}
