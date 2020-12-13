package com.xlab13.zcleaner.Apps;

import java.util.List;

public class AppsResponse {

    public List<AppItem> items;
    public String message;
    public boolean status;

    public AppsResponse(List<AppItem> items, String message, boolean status){
        this.items = items;
        this.message = message;
        this.status = status;
    }
}