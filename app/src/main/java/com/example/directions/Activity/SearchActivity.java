package com.example.directions.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.directions.ApiService.ApiClient;
import com.example.directions.ApiService.Service;
import com.example.directions.Directions_Mark;
import com.example.directions.ListViewFunction.FavoritesAdapter;
import com.example.directions.ListViewFunction.FavoritesDTO;
import com.example.directions.ListViewFunction.ListViewItem;
import com.example.directions.R;
import com.skt.Tmap.TMapPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.directions.ArraySavingClass.array_saving_class;
import com.example.directions.ListViewFunction.ListViewAdapter;

import static com.example.directions.Activity.ListViewActivity.markerImage;
import static com.example.directions.Activity.MainActivity.findAddressbtn;
import static com.example.directions.Activity.MainActivity.POIitemSize;
import static com.example.directions.Activity.MainActivity.AddressResult;
import static com.example.directions.Activity.MainActivity.POILat;
import static com.example.directions.Activity.MainActivity.POILon;
import static com.example.directions.Activity.MainActivity.POIResult;
import static com.example.directions.Activity.MainActivity.btnClickSize;
import static com.example.directions.ArraySavingClass.array_saving_class.buttonNum;
import static com.example.directions.ArraySavingClass.array_saving_class.alTMapPoint;
import static com.example.directions.ArraySavingClass.array_saving_class.buttonNumArr;
import static com.example.directions.ArraySavingClass.array_saving_class.final_Point;
import static com.example.directions.ArraySavingClass.array_saving_class.final_Point_size;
import static com.example.directions.ArraySavingClass.array_saving_class.final_location;
import static com.example.directions.ArraySavingClass.array_saving_class.final_location_size;
import static com.example.directions.ArraySavingClass.array_saving_class.nameOfIt;
import static com.example.directions.ArraySavingClass.array_saving_class.overLap;

import com.example.directions.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private int userid;
    TMapView tMapView;

    static Bitmap markerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final ListView FavoritesListView ;

        tMapView = new TMapView(this);
        markerImage = BitmapFactory.decodeResource(this.getResources() ,R.drawable.markerp_icon); // 출발지점의 마커로 사용할 이미지 지정


        EditText inputText = (EditText)findViewById(R.id.searchText);
        ImageButton searchBtn = (ImageButton) findViewById(R.id.search_btn);
        ListView FavoritesListview = (ListView) findViewById(R.id.favorit_list);

        //검색 버튼 누르면 리스트뷰에 장소 표시
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strData = inputText.getText().toString(); // EditText에서 받은 String값을 strData에 저장

                TMapData tmapData = new TMapData();

                tmapData.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(final ArrayList<TMapPOIItem> poiItem) {

                        if (poiItem.size() == 0) { // 검색결과 없을 시
                            Handler toastHandler = new Handler(Looper.getMainLooper());
                            toastHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_LONG).show();
                                }
                            }, 0);
                        } else {
                            for (int i = 0; i < poiItem.size(); i++) {

                                TMapPOIItem item = poiItem.get(i);

                                POIResult[i] = item.getPOIName(); // POIResult[i]에 item에서 가져온 POI 이름을 저장
                                AddressResult[i] = item.getPOIAddress().replace("null", ""); // AddressResult[i]에 item에서 가져온 POI 주소를 저장
                                POILat[i] = item.getPOIPoint().getLatitude(); // POI지점에 위도를 POILat[i]에 저장
                                POILon[i] = item.getPOIPoint().getLongitude(); // POI지점에 경도를 POILon[i]에 저장
                                POIitemSize = poiItem.size(); // poiItem값을 전해주기 위해 POIitemSize에 저장
                            }

                                // 어답터에 주소의 이름과 상세주소, 위도 경도 추가
                            Intent ListViewIntent = new Intent(getApplicationContext(), ListViewActivity.class);
                            startActivity(ListViewIntent); // 리스트뷰 띄우는 액티비티로 이동
                        }
                    }
                });
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

                    FavoritesAdapter adapter = new FavoritesAdapter(SearchActivity.this, addressList);
                    ListView listView = findViewById(R.id.favorit_list);
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

        FavoritesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FavoritesDTO favoritesDTO = (FavoritesDTO) parent.getItemAtPosition(position);
                if (favoritesDTO != null) {
                    handleItemClick(favoritesDTO);
                }
            }
        });
    }

    public void handleItemClick(FavoritesDTO favoritesDTO) {
        String clicktitle = favoritesDTO.getTitle();
        String clickaddress = favoritesDTO.getAddress();
        double clicklat = favoritesDTO.getLat();
        double clicklon = favoritesDTO.getLon();

        TMapPoint tMapPoint = new TMapPoint(clicklat, clicklon);

        String resultAddress = clicktitle;
        boolean isExist = false; //전에 설정한 주소중에 같은 주소가 있는지를 판단하는 변수
        for (int i = 1; i < 6; i++) { // 전에 설정한 주소 중 같은 주소가 있으면 isExist를 true로 바꿈

            if (resultAddress.equals(array_saving_class.nameOfIt[i])) {
                isExist = true;
                break;
            }
        }
        if (isExist == true) {                // 전에 설정한 주소 중 같은 주소가 있을 때
            Toast.makeText(getApplicationContext(), "이미 이 주소는 설정이 되었습니다!", Toast.LENGTH_SHORT).show();
            isExist = false;
        } else { //같은 주소가 없으면*/
            array_saving_class.alTMapPoint[buttonNum] = tMapPoint; //alTMapPoint의 버튼 인덱스 위치에 tMapPoint 저장
            array_saving_class.nameOfIt[buttonNum] = clicktitle; //nameOfIt의 버튼 인덱스 위치에 POIResult의 position 인덱스에 저장된 값 저장
            array_saving_class.addressOfIt[buttonNum] = clickaddress; //addressOfIt의 버튼 인덱스 위치에

            if (btnClickSize[buttonNum] == 1) { // 기존의 배열에 값이 있으면

            } else { // 기존의 배열에 값이 없으면
                array_saving_class.alTMapPoint_size++; //lTMapPoint_size에 1을 더한다.
                array_saving_class.nameOfIt_size++; //nameOfIt_size에 1을 더한다.
                array_saving_class.addressOfIt_size++; //addressOfIt_size에 1을 더한다.
            }

            Directions_Mark.markReturn(markerImage, tMapView,alTMapPoint, nameOfIt, buttonNum, btnClickSize, buttonNumArr); // 검색한 위치에 마커를 뜨게 하는 메소드


            //선택해서 값 넘기기
            array_saving_class.final_location[buttonNum] = array_saving_class.nameOfIt[buttonNum]; //마지막으로 결정한 최종위치의 이름을 저장 현재 클릭한 버튼의 인덱스에 해당하는 곳에
            final_location_size++; //마지막으로 결정한 최종위치의 이름 개수를 올림

            array_saving_class.autoZoomLocation[buttonNum] = new TMapPoint(alTMapPoint[buttonNum].getLatitude(), alTMapPoint[buttonNum].getLongitude());


            array_saving_class.final_Point[buttonNum] = (new TMapPoint(alTMapPoint[buttonNum].getLatitude(), alTMapPoint[buttonNum].getLongitude()));
            // 마지막으로 결정한 최종위치의 좌표값을 저장
            if (overLap[buttonNum] == 1) { // 장소가 지정되어 있지 않을 때

            } else { // 장소가 지정되어 있을 떄
                final_Point_size++; // 마지막으로 결정한 최종위치의 좌표 개수를 올림
                Log.e(TAG, "final_location_size"+final_location_size);
                Log.e(TAG, "final_location"+final_location[buttonNum]);

            }

            array_saving_class.buttonName[buttonNum] = nameOfIt[buttonNum]; //버튼 이름을 저장하는 배열에 갈 곳의 이름을 저장

            overLap[buttonNum] = 1; // 장소가 지정되어 있다고 설정함
            //buttonNumArr.add(buttonNum);

            findAddressbtn[buttonNum].setText(nameOfIt[buttonNum]);// 버튼 이름을 사용자가 지정한 곳의 이름으로 바꿈
            Intent goToMain = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(goToMain);
            array_saving_class.centerOfIt++; //총 몇명이 저장되어 있는지에 대한 변수의 값을 1 증가
        }
    }
}

