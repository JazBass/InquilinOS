package com.mycityhome.InquilinOs.Model;

import java.io.Serializable;

public class Event implements Serializable {

    public String name, description, url, stDate, endDate, stHour, endHour, price, streetAddress;
    public String[] address;
    public String latitude, longitude;

    public Event(String name, String description, String url, String stDate,
                 String endDate, String stHour, String endHour, String latitude, String longitude,
                 String  price, String streetAddress) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.stDate = stDate;
        this.endDate = endDate;
        this.stHour = stHour;
        this.endHour = endHour;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.streetAddress = streetAddress;
    }

    public String getName() {
        return name;
    }

    public String getStDate() {
        return stDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getLongitude(){ return longitude;}

    public String getLatitude(){ return latitude;}

    public String getUrl(){ return url;}
}
