package com.application.tippzi.Models;

import java.util.ArrayList;

public class BarModel extends BaseListInfo {
    public int bar_id = 0;
    public String business_name = "";
    public String category = "";
    public String post_code = "";
    public String address = "";
    public String lat = "";
    public String lon = "";
    public double distance = 0f;
    public String telephone = "";
    public String website = "";
    public String email = "";
    public String description = "";
    public String music_type = "";
    public OpenHourModel open_time = new OpenHourModel();
    public GalleryModel galleryModel = new GalleryModel();
    public ArrayList<DealModel> deals = new ArrayList<DealModel>();
    public String service_name = "";
}
