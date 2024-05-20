package com.example.directions.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.directions.ArraySavingClass.array_saving_class;
import com.example.directions.Directions_Mark;
import com.example.directions.ListViewFunction.ListViewAdapter;
import com.example.directions.ListViewFunction.ListViewItem;
import com.example.directions.R;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import static com.example.directions.Activity.MainActivity.AddressResult;
import static com.example.directions.Activity.MainActivity.POILat;
import static com.example.directions.Activity.MainActivity.POILon;
import static com.example.directions.Activity.MainActivity.POIResult;
import static com.example.directions.Activity.MainActivity.POIitemSize;
import static com.example.directions.Activity.MainActivity.btnClickSize;
import static com.example.directions.Activity.MainActivity.findAddressbtn;
import static com.example.directions.ArraySavingClass.array_saving_class.alTMapPoint;
import static com.example.directions.ArraySavingClass.array_saving_class.buttonNum;
import static com.example.directions.ArraySavingClass.array_saving_class.buttonNumArr;
import static com.example.directions.ArraySavingClass.array_saving_class.final_Point_size;
import static com.example.directions.ArraySavingClass.array_saving_class.final_location_size;
import static com.example.directions.ArraySavingClass.array_saving_class.nameOfIt;
import static com.example.directions.ArraySavingClass.array_saving_class.overLap;

public class ListViewActivity extends AppCompatActivity {
    static ListViewAdapter listViewAdapter;
    TMapView tMapView;
    static Bitmap markerImage;
    TMapPoint initialPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        final ListView AddressListView;

        tMapView = new TMapView(this);

        ListViewAdapter listViewAdapter = new ListViewAdapter();

        // MainActivity에서 이미지를 보이지 않게 설정
        listViewAdapter.hideImageInMainActivity(true);

        markerImage = BitmapFactory.decodeResource(this.getResources() ,R.drawable.markerp_icon); // 출발지점의 마커로 사용할 이미지 지정


        AddressListView = (ListView) findViewById(R.id.rv_list);
        AddressListView.setAdapter(listViewAdapter); // 리스트뷰에 어답터 연결

        if(initialPoint != null) {

            initialPoint = new TMapPoint(0, 0);

            tMapView.setCenterPoint(alTMapPoint[buttonNum].getLongitude(), alTMapPoint[buttonNum].getLatitude()); // tMapView가 보여지는 곳을 검색한 좌표로 설정함
        }

        for (int i = 0; i < POIitemSize; i++) {
            listViewAdapter.addItem(POIResult[i], AddressResult[i], POILat[i], POILon[i]);
        } // 어답터에 주소의 이름과 상세주소, 위도 경도 추가
        AddressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //리스트뷰를 클릭했을때

                TMapPoint tMapPoint = new TMapPoint(POILat[position], POILon[position]); //각 아이템의 좌표

                String resultAddress = POIResult[position]; //각 아이템의 주소
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
                    array_saving_class.nameOfIt[buttonNum] = POIResult[position]; //nameOfIt의 버튼 인덱스 위치에 POIResult의 position 인덱스에 저장된 값 저장
                    array_saving_class.addressOfIt[buttonNum] = AddressResult[position]; //addressOfIt의 버튼 인덱스 위치에 AddressResult의 position 인덱스에 저장된 값 저장

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
                    }

                    array_saving_class.buttonName[buttonNum] = nameOfIt[buttonNum]; //버튼 이름을 저장하는 배열에 갈 곳의 이름을 저장

                    overLap[buttonNum] = 1; // 장소가 지정되어 있다고 설정함
                    findAddressbtn[buttonNum].setText(nameOfIt[buttonNum]);// 버튼 이름을 사용자가 지정한 곳의 이름으로 바꿈
                    Intent goToMain = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(goToMain);
                    array_saving_class.centerOfIt++; //총 몇명이 저장되어 있는지에 대한 변수의 값을 1 증가
                }
            }
        });
    }
}

