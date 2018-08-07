package com.application.tippzi.Models;

public class WalletModel extends BaseListInfo {
    public int bar_id = 0;
    public String bar_name = "";
    public String bar_address = "";
    public String category = "";
    public String lat = "";
    public String lon = "";
    public String music_type = "";
    public Double distance = 0.00;
    public OpenHourModel open_time = new OpenHourModel();
    public int deal_id = 0;
    public String deal_title = "";
    public String deal_description = "";
    public String deal_duration = "";
    public int deal_qty = 0;
    public int impressions = 0;
    public int in_wallet = 0;
    public int claimed = 0;
    public boolean wallet_check = false;
    public boolean claim_check = false;
    public boolean impressions_check = false;
}
