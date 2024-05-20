package com.example.directions.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.directions.ApiService.ApiClient;
import com.example.directions.ApiService.Service;
import com.example.directions.ListViewFunction.FavoritesAdapter;
import com.example.directions.ListViewFunction.FavoritesDTO;
import com.example.directions.R;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity {

    private static final String TAG = "YourActivity";

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ImageButton btn = (ImageButton) findViewById(R.id.back_btn2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Retrofit 설정(API)
        Service apiInterface = ApiClient.getApiClient().create(Service.class);

        int userId = 123;
        String title = "yourTitle";
        String address = "yourAddress";
        double lat = 123.3;
        double lon = 455.6;

        Call<List<FavoritesDTO>> call = apiInterface.favoriteslist(userId, title, address, lat, lon);

        call.enqueue(new Callback<List<FavoritesDTO>>() {
            @Override
            public void onResponse(Call<List<FavoritesDTO>> call, Response<List<FavoritesDTO>> response) {
                if (response.isSuccessful()) {
                    //API 호출이 성공한 경우
                    List<FavoritesDTO> addressList = response.body();

                    FavoritesAdapter adapter = new FavoritesAdapter(FavoritesActivity.this, addressList);
                    ListView listView = findViewById(R.id.fv_list);
                    listView.setAdapter(adapter);

                } else {
                    //서버로부터 응답이 실패한 경우
                    int statusCode = response.code();
                    String errorMessage = "Unknown error occurred";

                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e(TAG, "HTTP Status Code: " + statusCode + ", Error Message: " + errorMessage);
                }
            }
            @Override
            public void onFailure(Call<List<FavoritesDTO>> call, Throwable t) {
                //통신 실패 -> 네트워크 연결이나 서버와의 통신 문제 등을 처리
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}

