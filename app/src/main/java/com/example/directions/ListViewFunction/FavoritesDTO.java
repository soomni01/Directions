package com.example.directions.ListViewFunction;

import com.google.gson.annotations.SerializedName;

//data transfer object : 계층 간 데이터 교환을 위한 클래스
public class FavoritesDTO {
    @SerializedName("userid")
    private int userid;
    @SerializedName("title")
    private String title;
    @SerializedName("address")
    private String address;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lon")
    private double lon;

        /*public addressDTO(String userid, String title, String address, double lat, double lon) {
            this.userid = userid;
            this.title = title;
            this.address = address;
            this.lat = lat;
            this.lon = lon;
        }*/

    public int getUserid() { return userid; }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "favorite {" +
                ", userid='" + userid +
                ", address=" + address + '\'' +
                ", title='" + title + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}