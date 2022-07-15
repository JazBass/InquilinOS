package com.mycityhome.InquilinOs;

import com.google.android.gms.maps.model.LatLng;

public class Event {

    String name, description, url, stDate, endDate, stHour, endHour, price, streetAddress;
    String[] address;
    boolean isFree;
    LatLng location;
    public Event(String name){
        this.name = name;
    }
    public Event(String name, String description, String url, String[] address, String stDate,
                 String endDate, String stHour, String endHour, LatLng location, String  price,
                 String streetAddress) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.address = address;
        this.stDate = stDate;
        this.endDate = endDate;
        this.stHour = stHour;
        this.endHour = endHour;
        this.location = location;
        this.price = price;
        this.streetAddress = streetAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStDate() {
        return stDate;
    }

    public void setStDate(String stDate) {
        this.stDate = stDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStHour() {
        return stHour;
    }

    public void setStHour(String stHour) {
        this.stHour = stHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String[] getAddress() {
        return address;
    }

    public void setAddress(String[] address) {
        this.address = address;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
}
