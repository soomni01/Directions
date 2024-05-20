package com.example.directions.ListViewFunction;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.directions.Activity.FavoritesActivity;
import com.example.directions.Activity.ListViewActivity;
import com.example.directions.Activity.SearchActivity;
import com.example.directions.ApiService.ApiClient;
import com.example.directions.ApiService.Service;
import com.example.directions.R;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesAdapter extends ArrayAdapter<FavoritesDTO> {

    private Context context;

    public FavoritesAdapter(@NonNull Context context, @NonNull List<FavoritesDTO> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview2_xml, parent, false);
        }

        FavoritesDTO address = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.tv1);
        TextView addressTextView = convertView.findViewById(R.id.tv2);
        ImageView imageView = convertView.findViewById(R.id.deleteBtn);

        titleTextView.setText(address != null ? address.getTitle() : "");
        addressTextView.setText(address != null ? address.getAddress() : "");

        // SearchActivity에서는 이미지를 보이지 않게 처리
        if (context instanceof SearchActivity) {
            imageView.setVisibility(View.GONE);
        } else if (context instanceof FavoritesActivity) {
            // FavoritesActivity에서는 이미지를 보이도록 설정하고 클릭 이벤트 처리
            imageView.setVisibility(View.VISIBLE);

            imageView.setOnClickListener(v -> {
                String placeNameToDelete = address != null ? address.getTitle() : "";
                if (!TextUtils.isEmpty(placeNameToDelete)) {
                    deleteItem(position, placeNameToDelete);
                }
            });
        }

        return convertView;
    }

    // 리스트뷰에서 아이템 삭제
    private void deleteItem(int position, String placeNameToDelete) {
        remove(getItem(position));
        notifyDataSetChanged();

        // 여기에서 데이터베이스에서도 해당 아이템을 삭제하는 작업을 수행
        deleteFromDatabase(placeNameToDelete);
    }

    // 데이터베이스에서 아이템 삭제
    private void deleteFromDatabase(String placeNameToDelete) {
        Service apiInterface = ApiClient.getApiClient().create(Service.class);
        Call<FavoritesDTO> call = apiInterface.deletefavorites(placeNameToDelete);

        call.enqueue(new Callback<FavoritesDTO>() {
            @Override
            public void onResponse(Call<FavoritesDTO> call, Response<FavoritesDTO> response) {
                if (response.isSuccessful()) {
                    // 삭제가 성공한 경우
                    Log.d("FavoritesAdapter", "onResponse: 삭제 성공");
                } else {
                    // 삭제가 실패한 경우
                    Log.e("FavoritesAdapter", "onResponse: 삭제 실패");
                }
            }

            @Override
            public void onFailure(Call<FavoritesDTO> call, Throwable t) {
                // 통신 실패
                // 네트워크 연결이나 서버와의 통신 문제 등을 처리
                Log.e("FavoritesAdapter", "onFailure: " + t.getMessage());
            }
        });
    }
}

