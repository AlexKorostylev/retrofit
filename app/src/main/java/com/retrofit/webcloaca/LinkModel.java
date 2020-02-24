package com.retrofit.webcloaca;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinkModel {
    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("status")
    @Expose
    private String status;


    public String getLink() {
        return link;
    }

    public void setLink(String link){
        this.link = link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }
}
