package com.example.moviesbook.Json_Books;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Pdf {

    @SerializedName("isAvailable")
    @Expose
    private Boolean isAvailable;

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

}
