package com.application.tippzi.Models;

/**
 * Created by E on 11/3/2017.
 */

public class EditBarModel {
    public int bar_id = 0;
    public String business_name = "";
    public String category = "";
    public String post_code = "";
    public String address = "";
    public String lat = "";
    public String lon = "";
    public String telephone = "";
    public String website = "";
    public String email = "";
    public String description = "";
    public String music_type = "";
    public OpenHourModel open_time = new OpenHourModel();
    public GalleryModel galleryModel = new GalleryModel();
}
