package com.example.musicclient.datalayer.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Restrictions {

    @SerializedName("reason")
    @Expose
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
