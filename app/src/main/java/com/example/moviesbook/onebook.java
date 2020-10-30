package com.example.moviesbook;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Dimensions {

    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("width")
    @Expose
    private String width;
    @SerializedName("thickness")
    @Expose
    private String thickness;

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

}
class Epub {

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

class PanelizationSummary {

    @SerializedName("containsEpubBubbles")
    @Expose
    private Boolean containsEpubBubbles;
    @SerializedName("containsImageBubbles")
    @Expose
    private Boolean containsImageBubbles;

    public Boolean getContainsEpubBubbles() {
        return containsEpubBubbles;
    }

    public void setContainsEpubBubbles(Boolean containsEpubBubbles) {
        this.containsEpubBubbles = containsEpubBubbles;
    }

    public Boolean getContainsImageBubbles() {
        return containsImageBubbles;
    }

    public void setContainsImageBubbles(Boolean containsImageBubbles) {
        this.containsImageBubbles = containsImageBubbles;
    }

}
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
class ReadingModes {

    @SerializedName("text")
    @Expose
    private Boolean text;
    @SerializedName("image")
    @Expose
    private Boolean image;

    public Boolean getText() {
        return text;
    }

    public void setText(Boolean text) {
        this.text = text;
    }

    public Boolean getImage() {
        return image;
    }

    public void setImage(Boolean image) {
        this.image = image;
    }

}


 class SaleInfo {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("saleability")
    @Expose
    private String saleability;
    @SerializedName("isEbook")
    @Expose
    private Boolean isEbook;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSaleability() {
        return saleability;
    }

    public void setSaleability(String saleability) {
        this.saleability = saleability;
    }

    public Boolean getIsEbook() {
        return isEbook;
    }

    public void setIsEbook(Boolean isEbook) {
        this.isEbook = isEbook;
    }

}
