package c.motor.motor.model;

import com.google.firebase.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

public class AdvertSearch {
    private String title;
    private String image;
    private int price;
    private long dateTime;
    private String city;
    private String country;
    private String currency;
    private String documentReference;
    private String category;
    private boolean isNew;

    public AdvertSearch(){
        //Empty constructor
    }

    public AdvertSearch(String title, String image, int price, long dateTime, String city, String country, String currency, String documentReference, String category, boolean isNew){

        this.title = title;
        this.image = image;
        this.price = price;
        this.dateTime = dateTime;
        this.city = city;
        this.country = country;
        this.currency = currency;
        this.documentReference = documentReference;
        this.category = category;
        this.isNew = isNew;
    }

    public static AdvertSearch jsonToAdvertSearch(JSONObject jsonObject) throws JSONException {
        AdvertSearch advertSearch = new AdvertSearch(
                jsonObject.getString("tittle"),
                jsonObject.getString("image"),
                jsonObject.getInt("price"),
                jsonObject.getLong("dateTime"),
                jsonObject.getString("city"),
                jsonObject.getString("country"),
                jsonObject.getString("currency"),
                jsonObject.getString("documentReference"),
                jsonObject.getString("category"),
                jsonObject.getBoolean("isNew")
        );
        return advertSearch;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(String documentReference) {
        this.documentReference = documentReference;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}

