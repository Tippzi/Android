package com.application.tippzi.Models;

import java.util.ArrayList;

public class CustomerModel {
    public int user_id = 0;
    public String user_name = "";
    public String email = "";
    public String gender = "";
    public String birthday = "";
    public String username = "";
    public ArrayList<BarModel> bars = new ArrayList<BarModel>();
    public ArrayList<WalletModel> walletModels = new ArrayList<WalletModel>();
}