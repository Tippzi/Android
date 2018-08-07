package com.application.tippzi.Models;

public class DealModel extends BaseListInfo {
    public int deal_id = 0;
    public String deal_title = "";
    public String deal_description = "";
    public String deal_duration = "";
    public int deal_qty = 0;
    public int impressions = 0;
    public int in_wallet = 0;
    public int claimed = 0;
    public int engagement = 0;
    public int clicks = 0;
    public boolean wallet_check = false;
    public boolean claim_check = false;
    public boolean impressions_check = false;
    //
    public int index_number = 0 ;
    public DealDaysModel deal_days = new DealDaysModel();
    public String category = "";
}
