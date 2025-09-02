package com.baghdadhomes.Models;

import java.util.ArrayList;

public class FilterIntentModel {
    String status;
    String type;
    String sub_type;
    String location;
    String min_price;
    String max_price;
    String max_area;
    String min_area;
    ArrayList<String> area;

    public FilterIntentModel(String status, String type, String sub_type, String location, String min_price, String max_price, String max_area, String min_area, ArrayList<String> area) {
        this.status = status;
        this.type = type;
        this.sub_type = sub_type;
        this.location = location;
        this.min_price = min_price;
        this.max_price = max_price;
        this.max_area = max_area;
        this.min_area = min_area;
        this.area = area;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSub_type() {
        return sub_type;
    }

    public void setSub_type(String sub_type) {
        this.sub_type = sub_type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMin_price() {
        return min_price;
    }

    public void setMin_price(String min_price) {
        this.min_price = min_price;
    }

    public String getMax_price() {
        return max_price;
    }

    public void setMax_price(String max_price) {
        this.max_price = max_price;
    }

    public String getMax_area() {
        return max_area;
    }

    public void setMax_area(String max_area) {
        this.max_area = max_area;
    }

    public String getMin_area() {
        return min_area;
    }

    public void setMin_area(String min_area) {
        this.min_area = min_area;
    }

    public ArrayList<String> getArea() {
        return area;
    }

    public void setArea(ArrayList<String> area) {
        this.area = area;
    }
}
