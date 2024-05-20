package com.example.directions.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.directions.ApiService.ApiClient;
import com.example.directions.ApiService.Service;
import com.example.directions.ArraySavingClass.array_saving_class;
import com.example.directions.ListViewFunction.FavoritesDTO;
import com.example.directions.ListViewFunction.ListViewAdapter;
import com.example.directions.ListViewFunction.ListViewItem;
import com.example.directions.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.directions.Activity.ResultActivity.cafeBtnClicked;
import static com.example.directions.Activity.ResultActivity.cafeExistData;
import static com.example.directions.Activity.ResultActivity.cultureBtnClicked;
import static com.example.directions.Activity.ResultActivity.cultureExistData;
import static com.example.directions.Activity.ResultActivity.foodBtnClicked;
import static com.example.directions.Activity.ResultActivity.foodExistData;
import static com.example.directions.Activity.ResultActivity.cafeSize;
import static com.example.directions.Activity.ResultActivity.foodSize;
import static com.example.directions.Activity.ResultActivity.cultureSize;
import static com.example.directions.ArraySavingClass.array_saving_class.cafeLon;
import static com.example.directions.ArraySavingClass.array_saving_class.transList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class infraListActivity extends AppCompatActivity {

        private int userid;
        TMapView tmap;
        TMapPoint subwayP;
        double subLat = 0;
        double subLon = 0;
        Bitmap centerMarkerImage;

        @Override
        protected void onResume() {
            super.onResume();
            overridePendingTransition(0,0); // 액티비티를 종료할 때 애니메이션을 없애줌
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_infra_list);

            RelativeLayout infraRelativeLayout = (RelativeLayout) findViewById(R.id.InfraRelativeLayout);
            ImageButton btn1 = (ImageButton) findViewById(R.id.back_btn);

            tmap = new TMapView(this);
            LinearLayout tmaplayout = (LinearLayout) findViewById(R.id.main_layout_tmap);
            centerMarkerImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker_icon);
            tmaplayout.addView(tmap);

            btn1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(infraListActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            TMapMarkerItem center = new TMapMarkerItem();
            TMapPoint centerP = new TMapPoint(array_saving_class.center_point.getLatitude(), array_saving_class.center_point.getLongitude());

            center.setCalloutTitle("테스트");
            center.setAutoCalloutVisible(true);

            TMapData forFindStation = new TMapData(); // 인근 지하철을 찾은 데이터를 넣을 TMapData
            TMapPoint centerPoint = centerP;

            //주변에 있는 지하철역을 검색
            forFindStation.findAroundNamePOI(centerPoint, "지하철", 3, 1, new TMapData.FindAroundNamePOIListenerCallback() {
                @Override
                public void onFindAroundNamePOI(ArrayList<TMapPOIItem> POI_item) {
                    try {
                        if (POI_item != null) {
                            TMapPoint point = POI_item.get(0).getPOIPoint();
                            TMapMarkerItem C_marker = new TMapMarkerItem();
                            C_marker.setTMapPoint(point);
                            C_marker.setVisible(TMapMarkerItem.VISIBLE);
                            tmap.addMarkerItem("C_Point", C_marker);
                            tmap.setCenterPoint(C_marker.longitude, C_marker.latitude); // 위도경도 바꿔서

                            subLat = C_marker.getTMapPoint().getLatitude();
                            subLon = C_marker.getTMapPoint().getLongitude();

                            subwayP = new TMapPoint(subLat, subLon);

                            C_marker.setCalloutTitle(POI_item.get(0).getPOIName());
                            C_marker.setCanShowCallout(true);
                            C_marker.setIcon(centerMarkerImage);
                        } else {
                            Toast.makeText(getApplicationContext(), "지하철이 없습니다", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            final ImageButton cafeBtn = (ImageButton) findViewById(R.id.cafeBtn);
            final ImageButton foodBtn = (ImageButton) findViewById(R.id.foodBtn);
            final ImageButton cultureBtn = (ImageButton) findViewById(R.id.cultureBtn);

            final ListView cafeListView;
            final ListView foodListView;
            final ListView cultureListView;

            ListViewAdapter cafeListViewAdapter = new ListViewAdapter();
            ListViewAdapter foodListViewAdapter = new ListViewAdapter();
            ListViewAdapter cultureListViewAdapter = new ListViewAdapter();

            cafeListView = (ListView) findViewById(R.id.cafeListView);
            foodListView = (ListView) findViewById(R.id.foodListView);
            cultureListView = (ListView) findViewById(R.id.cultureListView);

            cafeListView.setAdapter(cafeListViewAdapter);
            foodListView.setAdapter(foodListViewAdapter);
            cultureListView.setAdapter(cultureListViewAdapter);

            if (cafeBtnClicked == true) { // 카페 버튼이 클릭되었는지 확인하고 클릭되었다면

                infraRelativeLayout.setBackground(null); // 처음 화면에 표시되는 텍스트를 없앰
                cafeBtn.setSelected(false); // false 일때 빨간색으로 카페 버튼의 이미지가 세팅됨
                foodBtn.setSelected(true);
                cultureBtn.setSelected(true);
                // 나머지 버튼들을 눌리지 않은 상태로 초기화시킴
                cafeBtn.setImageResource(R.drawable.tabiconcafeorg); // 인텐트를 띄워 이미지를 로딩할 때 카페 버튼을 눌려져있다는 모습의 빨간색 이미지로 세팅
                cafeListView.setVisibility(View.VISIBLE); // 카페 검색 리스트만 표시
                foodListView.setVisibility(View.GONE);
                cultureListView.setVisibility(View.GONE);
                // 나머지는 숨김

                for (int i = 0; i < cafeSize; i++) {

                    TMapMarkerItem marker = new TMapMarkerItem();
                    marker.setTMapPoint(array_saving_class.cafeTMapPoint[i]);
                    marker.setName(array_saving_class.cafeList[i]);
                    marker.setVisible(TMapMarkerItem.VISIBLE);
                    marker.setCalloutTitle(marker.getName());
                    marker.setCanShowCallout(true);
                    Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cafe);
                    int newWidth = 70;
                    int newHeight = 70;
                    Bitmap scaledBitmap1 = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
                    marker.setIcon(scaledBitmap1);
                    int imageResource = R.drawable.rightarrow;
                    // 이미지 리소스 ID를 확인하고 적절한 이미지로 수정
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResource);

                    marker.setCalloutRightButtonImage(bitmap);
                    tmap.addMarkerItem(array_saving_class.cafeList[i], marker);

                    // 지도의 마커 클릭 이벤트 설정
                    tmap.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                        @Override
                        public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                            String markerName = tMapMarkerItem.getName();
                            int position = cafeListViewAdapter.getPosition(markerName);

                            if (position != -1) {
                                // 리스트뷰의 해당 위치로 이동
                                cafeListView.setSelection(position);
                                Toast.makeText(getApplicationContext(), markerName + position + "찾은 위치로 이동합니다", Toast.LENGTH_SHORT).show();
                            } else {
                                cafeListView.setSelection(-1);
                            }
                        }
                    });
                }
            } else if (foodBtnClicked == true) { // 음식점 버튼이 클릭되었는지 확인하고 클릭되었다면

                infraRelativeLayout.setBackground(null); // 처음 화면에 표시되는 텍스트를 없앰
                cafeBtn.setSelected(true);
                foodBtn.setSelected(false);
                cultureBtn.setSelected(true);
                // 나머지 버튼들을 눌리지 않은 상태로 초기화시킴
                foodBtn.setImageResource(R.drawable.tabiconrestaurantorg); // 인텐트를 띄워 이미지를 로딩할 때 음식점 버튼을 눌려져있다는 모습의 빨간색 이미지로 세팅
                cafeListView.setVisibility(View.GONE);
                foodListView.setVisibility(View.VISIBLE); // 음식점 검색 리스트만 표시
                cultureListView.setVisibility(View.GONE);
                // 나머지는 숨김

                for (int i = 0; i < foodSize; i++) {

                    TMapMarkerItem marker = new TMapMarkerItem();
                    marker.setTMapPoint(array_saving_class.foodTMapPoint[i]);
                    marker.setName(array_saving_class.foodList[i]);
                    marker.setVisible(TMapMarkerItem.VISIBLE);
                    marker.setCalloutTitle(marker.getName());
                    marker.setCanShowCallout(true);
                    Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.food);
                    int newWidth = 70;
                    int newHeight = 70;
                    Bitmap scaledBitmap1 = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
                    marker.setIcon(scaledBitmap1);
                    int imageResource = R.drawable.rightarrow;
                    // 이미지 리소스 ID를 확인하고 적절한 이미지로 수정
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResource);

                    marker.setCalloutRightButtonImage(bitmap);
                    tmap.addMarkerItem(array_saving_class.foodList[i], marker);

                    // 지도의 마커 클릭 이벤트 설정
                    tmap.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                        @Override
                        public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                            String markerName = tMapMarkerItem.getName();
                            int position = foodListViewAdapter.getPosition(markerName);

                            if (position != -1) {
                                // 리스트뷰의 해당 위치로 이동
                                foodListView.setSelection(position);
                                Toast.makeText(getApplicationContext(), markerName + position + "찾은 위치로 이동합니다", Toast.LENGTH_SHORT).show();
                            } else {
                                foodListView.setSelection(-1);
                            }
                        }
                    });
                }
            } else if (cultureBtnClicked == true) { // 문화시설 버튼이 클릭되었는지 확인하고 클릭되었다면

                infraRelativeLayout.setBackground(null); // 처음 화면에 표시되는 텍스트를 없앰
                cafeBtn.setSelected(true);
                foodBtn.setSelected(true);
                cultureBtn.setSelected(false);
                // 나머지 버튼들을 눌리지 않은 상태로 초기화시킴
                cultureBtn.setImageResource(R.drawable.tabiconcultureorg); // 인텐트를 띄워 이미지를 로딩할 때 음식점 버튼을 눌려져있다는 모습의 빨간색 이미지로 세팅
                cafeListView.setVisibility(View.GONE);
                foodListView.setVisibility(View.GONE);
                cultureListView.setVisibility(View.VISIBLE); // 문화시설 검색 리스트만 표시
                // 나머지는 숨김

                for (int i = 0; i < cultureSize; i++) {

                    TMapMarkerItem marker = new TMapMarkerItem();
                    marker.setTMapPoint(array_saving_class.cultureTMapPoint[i]);
                    marker.setName(array_saving_class.cultureList[i]);
                    marker.setVisible(TMapMarkerItem.VISIBLE);
                    marker.setCalloutTitle(marker.getName());
                    marker.setCanShowCallout(true);
                    Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.movie);
                    int newWidth = 70;
                    int newHeight = 70;
                    Bitmap scaledBitmap1 = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
                    marker.setIcon(scaledBitmap1);
                    int imageResource = R.drawable.rightarrow;
                    // 이미지 리소스 ID를 확인하고 적절한 이미지로 수정
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResource);

                    marker.setCalloutRightButtonImage(bitmap);
                    tmap.addMarkerItem(array_saving_class.cultureList[i], marker);

                    // 지도의 마커 클릭 이벤트 설정
                    tmap.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                        @Override
                        public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                            String markerName = tMapMarkerItem.getName();
                            int position = cultureListViewAdapter.getPosition(markerName);

                            if (position != -1) {
                                // 리스트뷰의 해당 위치로 이동
                                cultureListView.setSelection(position);
                                Toast.makeText(getApplicationContext(), markerName + position + "찾은 위치로 이동합니다", Toast.LENGTH_SHORT).show();
                            } else {
                                cultureListView.setSelection(-1);
                            }
                        }
                    });
                }
            } else {

                cafeBtn.setSelected(true);
                foodBtn.setSelected(true);
                cultureBtn.setSelected(true);
                // 모든 버튼을 눌리지 않은 상태로 초기화
            }

            for (int i = 0; i < cafeSize; i++) {
                cafeListViewAdapter.addItem(array_saving_class.cafeList[i], array_saving_class.cafeAddress[i], array_saving_class.cafeLat[i], cafeLon[i]);
            }
            for (int i = 0; i < foodSize; i++) {
                foodListViewAdapter.addItem(array_saving_class.foodList[i], array_saving_class.foodAddress[i], array_saving_class.foodLat[i], array_saving_class.foodLon[i]);
            }
            for (int i = 0; i < cultureSize; i++) {
                cultureListViewAdapter.addItem(array_saving_class.cultureList[i], array_saving_class.cultureAddress[i], array_saving_class.cultureLat[i], array_saving_class.cultureLon[i]);
            }
            // 어답터에 주변시설 주소의 이름과 상세주소, 위도와 경도 추가


            cafeBtn.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (cafeExistData = false) { // 카페 검색결과 데이터가 존재하는지 확인하고 값이 존재하지 않으면
                        Handler toastHandler = new Handler(Looper.getMainLooper());
                        toastHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "근처에 검색된 카페가 없습니다.", Toast.LENGTH_SHORT).show(); // 근처 카페가 없다고 표시

                            }
                        }, 0);
                    }

                    cafeListView.setVisibility(View.VISIBLE); // 카페 검색 리스트만 표시
                    foodListView.setVisibility(View.GONE);
                    cultureListView.setVisibility(View.GONE);

                    cafeBtnClicked = true;
                    foodBtnClicked = false;
                    cultureBtnClicked = false;


                    view.postInvalidate(); // 뷰에 반영

                    cafeBtnClicked = true; // 카페 버튼이 눌렸다는것을 true로 설정
                    foodBtnClicked = false;
                    cultureBtnClicked = false;
                    // 나머지 버튼은 안눌려있는 것으로 값을 초기화

                    Intent intent = getIntent(); // 리스트 갱신을 위해 인텐트로 액티비티를 다시 띄워줌
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 다시 액티비티를 띄울때 애니메이션을 없애줌
                    finish(); // 현재 액티비티 종료
                    startActivity(intent); // 새로운 액티비티 실행
                }
            });

            foodBtn.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (foodExistData = false) { // 음식점 검색결과 데이터가 존재하는지 확인하고 값이 존재하지 않으면
                        Handler toastHandler = new Handler(Looper.getMainLooper());
                        toastHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "근처에 검색된 음식점이 없습니다.", Toast.LENGTH_SHORT).show(); // 근처 음식점이 없다고 표시

                            }
                        }, 0);
                    }

                    cafeListView.setVisibility(View.GONE);
                    foodListView.setVisibility(View.VISIBLE); // 음식점 검색 리스트만 표시
                    cultureListView.setVisibility(View.GONE);

                    cafeBtnClicked = false;
                    foodBtnClicked = true; // 음식점 버튼이 눌렸다는것을 true로 설정
                    cultureBtnClicked = false;

                    view.postInvalidate(); // 뷰에 반영

                    cafeBtnClicked = false;
                    foodBtnClicked = true;
                    cultureBtnClicked = false;
                    // 나머지 버튼은 안눌려있는 것으로 값을 초기화

                    Intent intent = getIntent(); // 리스트 갱신을 위해 인텐트로 액티비티를 다시 띄워줌
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 다시 액티비티를 띄울때 애니메이션을 없애줌
                    finish(); // 현재 액티비티 종료
                    startActivity(intent); // 새로운 액티비티 실행

                }
            });

            cultureBtn.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cultureExistData = false) { // 문화시설 검색결과 데이터가 존재하는지 확인하고 값이 존재하지 않으면
                        Handler toastHandler = new Handler(Looper.getMainLooper());
                        toastHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "근처에 검색된 문화시설이 없습니다.", Toast.LENGTH_SHORT).show(); // 근처 문화시설이 없다고 표시

                            }
                        }, 0);
                    }

                    cafeListView.setVisibility(View.GONE);
                    foodListView.setVisibility(View.GONE);
                    cultureListView.setVisibility(View.VISIBLE); // 문화시설 검색 리스트만 표시

                    view.postInvalidate(); // 뷰에 반영

                    cafeBtnClicked = false;
                    foodBtnClicked = false;
                    cultureBtnClicked = true; // 문화시설 버튼이 눌렸다는것을 true로 설정
                    // 나머지 버튼은 안눌려있는 것으로 값을 초기화

                    Intent intent = getIntent(); // 리스트 갱신을 위해 인텐트로 액티비티를 다시 띄워줌
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 다시 액티비티를 띄울때 애니메이션을 없애줌
                    finish(); // 현재 액티비티 종료
                    startActivity(intent); // 새로운 액티비티 실행

                }
            });

            cafeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListViewItem selectedItem = (ListViewItem) parent.getItemAtPosition(position);
                    String searchQuery = selectedItem.getPOIStr();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("naversearchapp://keywordsearch?mode=result&query=" + searchQuery + "&version=10"));
                    startActivity(intent);
                }
            });

            cafeListViewAdapter.setOnImageViewClickListener(new ListViewAdapter.OnImageViewClickListener() {
                @Override
                public void onImageViewClick(int position) {
                    ListViewItem clickedItem = (ListViewItem) cafeListViewAdapter.getItem(position);
                    String selectedPOI = clickedItem.getPOIStr();
                    String selectedAddress = clickedItem.getAddressStr();
                    Double selectedLat = clickedItem.getLat();
                    Double selectedLon = clickedItem.getLon();
                    Toast.makeText(infraListActivity.this, selectedPOI + "장소가 즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    registerMe(selectedPOI, selectedAddress, selectedLat, selectedLon);
                }
            });

            foodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListViewItem selectedItem = (ListViewItem) parent.getItemAtPosition(position);
                    String searchQuery = selectedItem.getPOIStr();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("naversearchapp://keywordsearch?mode=result&query=" + searchQuery + "&version=10"));
                    startActivity(intent);
                }
            });

            foodListViewAdapter.setOnImageViewClickListener(new ListViewAdapter.OnImageViewClickListener() {
                @Override
                public void onImageViewClick(int position) {
                    ListViewItem clickedItem = (ListViewItem) foodListViewAdapter.getItem(position);
                    String selectedPOI = clickedItem.getPOIStr();
                    String selectedAddress = clickedItem.getAddressStr();
                    Double selectedLat = clickedItem.getLat();
                    Double selectedLon = clickedItem.getLon();
                    Toast.makeText(infraListActivity.this, selectedPOI + "장소가 즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    registerMe(selectedPOI, selectedAddress, selectedLat, selectedLon);
                }
            });

            cultureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListViewItem selectedItem = (ListViewItem) parent.getItemAtPosition(position);
                    String searchQuery = selectedItem.getPOIStr();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("naversearchapp://keywordsearch?mode=result&query=" + searchQuery + "&version=10"));
                    startActivity(intent);
                }
            });

            cultureListViewAdapter.setOnImageViewClickListener(new ListViewAdapter.OnImageViewClickListener() {
                @Override
                public void onImageViewClick(int position) {
                    ListViewItem clickedItem = (ListViewItem) cultureListViewAdapter.getItem(position);
                    String selectedPOI = clickedItem.getPOIStr();
                    String selectedAddress = clickedItem.getAddressStr();
                    Double selectedLat = clickedItem.getLat();
                    Double selectedLon = clickedItem.getLon();
                    Toast.makeText(infraListActivity.this, selectedPOI + "장소가 즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    registerMe(selectedPOI, selectedAddress, selectedLat, selectedLon);                }
            });
        }

            private void registerMe(String title, String address, double lat, double lon) {
                FavoritesDTO newAddress = new FavoritesDTO();
                newAddress.setTitle(title);
                newAddress.setAddress(address);
                newAddress.setLat(lat);
                newAddress.setLon(lon);

                saveDataToDB(newAddress);
            }
            private void saveDataToDB(FavoritesDTO favoritesDTO) {
                Service api = ApiClient.getApiClient().create(Service.class);
                Call<FavoritesDTO> call = api.intsertfavorites(
                        userid,
                        favoritesDTO.getTitle(),
                        favoritesDTO.getAddress(),
                        favoritesDTO.getLat(),
                        favoritesDTO.getLon()
                );
                call.enqueue(new Callback<FavoritesDTO>()
                {
                    @Override
                    public void onResponse(Call<FavoritesDTO> call, Response<FavoritesDTO> response) {
                        int statusCode = response.code();

                        if (response.isSuccessful()) {
                            FavoritesDTO result = response.body();
                            Log.d(TAG, "onSuccess: 성공" + result.toString());
                        } else {
                            String errorMessage = "Unknown error occurred";

                            if (response.errorBody() != null) {
                                try {
                                    errorMessage = response.errorBody().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            Log.e(TAG, "HTTP Status Code: " + statusCode + ", Error Message: " + errorMessage);
                            Log.d(TAG, "onResponse: 삭제 실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<FavoritesDTO> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+ t.getMessage());
                    }
                });
            }
        }

