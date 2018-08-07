package com.application.tippzi.Models;

/**
 * Created by E on 11/2/2017.
 */

public class EditDealModel {
    public int user_id = 0;
    public int bar_id = 0;
    public int deal_id = 0;
    public String title = "";
    public String description = "";
    public String duration = "";
    public String qty = "";
    public DealDaysModel deal_days = new DealDaysModel();
}
