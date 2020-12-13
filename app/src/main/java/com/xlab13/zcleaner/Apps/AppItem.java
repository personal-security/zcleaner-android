package com.xlab13.zcleaner.Apps;

import android.graphics.Bitmap;

public class AppItem {

    public String Title, Description, PackageName;
    public int Awards;
    public Bitmap Image;

    public AppItem(String title, String description, String packageName, int awards){
        this.Title = title;
        this.Description = description;
        this.PackageName = packageName;

        this.Image = null;
    }
}