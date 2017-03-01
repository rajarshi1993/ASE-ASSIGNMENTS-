package com.dius.guardian;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class RetrieveEmergencyTask extends AsyncTask<String, Void, Emergency> {

    protected Emergency doInBackground(String... urls) {
        return makeHttpRequest();
    }

    protected Emergency makeHttpRequest() {
        Log.i("Main", "calling1");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.165:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NodeService service = retrofit.create(NodeService.class);
        Call<Emergency> emergencyCall = service.emergency();
        try {
            Response<Emergency> execute = emergencyCall.execute();
            Emergency emergency = execute.body();
            Log.i("Main", "My emergency " + emergency);
            return emergency;

        } catch (IOException e) {
            Log.e("Main", "error " + e.getLocalizedMessage(), e);
        }
        return null;
    }

    protected void onPostExecute(Emergency emergency) {

    }
}
