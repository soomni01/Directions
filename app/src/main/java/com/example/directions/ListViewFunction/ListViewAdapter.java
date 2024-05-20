package com.example.directions.ListViewFunction;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.directions.Activity.ListViewActivity;
import com.example.directions.Activity.MainActivity;
import com.example.directions.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

  // 이미지뷰 클릭 이벤트 처리를 위한 리스너 인터페이스
  public interface OnImageViewClickListener {
    void onImageViewClick(int position);
  }
  private OnImageViewClickListener onImageViewClickListener;
  private boolean hideImageInMainActivity = false;

  // MainActivity에서 이미지를 보이지 않게 설정하는 메서드
  public void hideImageInMainActivity(boolean hide) {
    hideImageInMainActivity = hide;
    notifyDataSetChanged();
  }

  // 이미지뷰 클릭 이벤트를 처리할 리스너 등록
  public void setOnImageViewClickListener(OnImageViewClickListener listener) {
    this.onImageViewClickListener = listener;
  }

  // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
  private ArrayList<ListViewItem> listViewItemList = new ArrayList<com.example.directions.ListViewFunction.ListViewItem>() ;

  // ListViewAdapter의 생성자
  public ListViewAdapter() {
  }

  // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
  @Override
  public int getCount() {
    return listViewItemList.size() ;
  }

  // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    final Context context = parent.getContext();

    // "listview_item" Layout을 inflate하여 convertView 참조 획득.
    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.listview_item, parent, false);
    }

    // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
    TextView POITextView = (TextView) convertView.findViewById(R.id.textView1) ;
    TextView AddressTextView = (TextView) convertView.findViewById(R.id.textView2) ;
    ImageView imageView = convertView.findViewById(R.id.fv_btn);

    // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
    com.example.directions.ListViewFunction.ListViewItem listViewItem = listViewItemList.get(position);

    // 아이템 내 각 위젯에 데이터 반영
    POITextView.setText(listViewItem.getPOIStr());
    AddressTextView.setText(listViewItem.getAddressStr());

    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // 이미지뷰 클릭 이벤트를 리스너를 통해 처리
        if (onImageViewClickListener != null) {
          onImageViewClickListener.onImageViewClick(position);
        }
      }
    });

    // MainActivity에서 이미지를 보이지 않게 처리
    if (context instanceof ListViewActivity && hideImageInMainActivity) {
      imageView.setVisibility(View.GONE);
    } else {
      imageView.setVisibility(View.VISIBLE);
    }

    return convertView;
  }

  // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
  @Override
  public long getItemId(int position) {
    return position ;
  }

  // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
  @Override
  public Object getItem(int position) {
    return listViewItemList.get(position) ;
  }

  // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
  public void addItem(String POI, String Address, double Lat, double Lon) {

    com.example.directions.ListViewFunction.ListViewItem item = new com.example.directions.ListViewFunction.ListViewItem();

    item.setPOIStr(POI);
    item.setAddressStr(Address);
    item.setLat(Lat);
    item.setLon(Lon);

    listViewItemList.add(item);

  }
  public void removeItem(){

    com.example.directions.ListViewFunction.ListViewItem item = new com.example.directions.ListViewFunction.ListViewItem();
    listViewItemList.remove(item);
  }
  public int getPosition(String POI) {
    for (int i = 0; i < listViewItemList.size(); i++) {
      ListViewItem item = listViewItemList.get(i);
      if (item != null && item.getPOIStr().equals(POI)) {
        return i;
      }
    }
    return -1; // 찾지 못한 경우
  }
}