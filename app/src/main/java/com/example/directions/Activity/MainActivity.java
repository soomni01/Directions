package com.example.directions.Activity;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.example.directions.ArraySavingClass.array_saving_class.buttonNum;
import static com.example.directions.ArraySavingClass.array_saving_class.buttonNumArr;
import static com.example.directions.ArraySavingClass.array_saving_class.final_Point_size;
import static com.example.directions.ArraySavingClass.array_saving_class.nameOfIt;
import static com.example.directions.ArraySavingClass.array_saving_class.overLap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.directions.R;
import com.example.directions.ArraySavingClass.array_saving_class;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageButton plus_btn;
    static int POIitemSize;
    static String POIResult[] = new String[100]; //POI의 이름을 담는 배열
    static String AddressResult[] = new String[100]; //POI의 주소를 담는 배열
    static double POILat[]= new double[100]; //POI의 Latitude를 담는 배열
    static double POILon[]= new double[100]; //POI의 Longitude를 담는 배열
    static int btnClickSize[] = new int[6]; // 배열의 인덱스에 해당하는 버튼을 눌러 값을 저장했었는지 체크하는 배열 ex) btnClickSize[1] = 1 이면 첫번째 버튼에 값이 들어갔었다는 것.
    static Button findAddressbtn[] = new Button[6]; //위치를 검색하기 위한 5개의 버튼, 구현상의 편의를 위해 index 0은 사용하지 않는다.
    private static final String KEY_VISIBILITY = "visibility_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TMapView tMapView = new TMapView(this); // key값 설정을 위한 tmapView 생성
        tMapView.setSKTMapApiKey("WggO9W2KT1HepbHAYgbt76NghWmyn5S5XASd57Xd"); // api key 설정

        Button r_btn = (Button) findViewById(R.id.result_btn);
        ImageButton s_btn = (ImageButton) findViewById(R.id.star_btn);

        findAddressbtn[1] = (Button) findViewById(R.id.place_btn1);
        findAddressbtn[2] = (Button) findViewById(R.id.place_btn2);
        findAddressbtn[3] = (Button) findViewById(R.id.place_btn3);
        findAddressbtn[4] = (Button) findViewById(R.id.place_btn4);
        findAddressbtn[5] = (Button) findViewById(R.id.place_btn5);

        //장소 검색
        findAddressbtn[1].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                array_saving_class.buttonNum = 1;
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        findAddressbtn[2].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                array_saving_class.buttonNum = 2;
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        findAddressbtn[3].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                array_saving_class.buttonNum = 3;
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        findAddressbtn[4].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                array_saving_class.buttonNum = 4;
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        findAddressbtn[5].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                array_saving_class.buttonNum = 5;
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        for(int i = 1; i<=5 ; i++) {
            if (array_saving_class.buttonName[i] != null) { // 버튼에 해당하는 텍스트가 주어지지 않았으면
                findAddressbtn[i].setText(array_saving_class.buttonName[i]); // buttonName[i]에 해당하는 String값을 버튼에 할당
            }
        }

        //검색 결과
        r_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //버튼을 누르면 좌표들의 무게중심 계산

                if (array_saving_class.centerOfIt >= 1) { // 입력된 주소가 2개 이상 일때
                    for (int i = 0; i < final_Point_size; i++) {
                        array_saving_class.totalLat += array_saving_class.final_Point[buttonNumArr.get(i)].getLatitude(); // 마지막 지점의 latitude값을 totalLat에 더한다.
                        array_saving_class.totalLon += array_saving_class.final_Point[buttonNumArr.get(i)].getLongitude(); // 마지막 지점의 longitude totalLon에 더한다.
                    }

                    array_saving_class.center_point.setLatitude(array_saving_class.totalLat / final_Point_size); // 중앙지점을 이제까지 다 더한 값에 지점들의 크기로 나눈다.
                    array_saving_class.center_point.setLongitude(array_saving_class.totalLon / final_Point_size);


                    array_saving_class.totalLat = array_saving_class.totalLon = 0; // 다음번에 중간지점을 찾을 수 있게 값을 초기화


                    Intent GoToCenterPoint = new Intent(getApplicationContext(), ResultActivity.class);
                    startActivity(GoToCenterPoint);
                    finish();
                } else { //입력된 주소가 한 개 이하일 때
                    Toast.makeText(MainActivity.this, "주소 검색을 먼저 해주세요!", Toast.LENGTH_LONG).show();
                }
            }
        });

        //즐겨찾기 화면 이동
        s_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intent);
            }
        });
    }
}