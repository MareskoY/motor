package c.motor.motor.model;

import com.google.firebase.Timestamp;
import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Advert {

    String  creatorId,
            creatorName,
            category,
            country,
            city,
            tittle,
            description,
            phone;
    List<String>
            imgNames,
            downloadUrls;
    Boolean isNew;

    Timestamp dateTime;
    long price;
    String  currency;
    int year;

    boolean isClosed;
    String reasonOfClosing;
    Timestamp closeDateTime;


    public Advert(List<String> imgNames,
                  List<String> downloadUrls) {
        this.imgNames = imgNames;
        this.downloadUrls = downloadUrls;
    }


    public Advert(String creatorId,
                  String creatorName,
                  String category,
                  String country,
                  String city,
                  String tittle,
                  String description,
                  String phone,
                  List<String> imgNames,
                  List<String> downloadUrls,
                  Boolean isNew,
                  Timestamp dateTime,
                  long price,
                  String currency,
                  int year) {
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.category = category;
        this.country = country;
        this.city = city;
        this.tittle = tittle;
        this.description = description;
        this.phone = phone;
        this.imgNames = imgNames;
        this.downloadUrls = downloadUrls;
        this.isNew = isNew;
        this.dateTime = dateTime;
        this.price = price;
        this.currency = currency;
        this.year = year;
    }

    public Advert(String creatorId,
                  String creatorName,
                  String category,
                  String country,
                  String city,
                  String tittle,
                  String description,
                  String phone,
                  List<String> imgNames,
                  List<String> downloadUrls,
                  Boolean isNew,
                  Timestamp dateTime,
                  long price,
                  String currency,
                  int year,
                  boolean isClosed,
                  String reasonOfClosing,
                  Timestamp closeDateTime) {
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.category = category;
        this.country = country;
        this.city = city;
        this.tittle = tittle;
        this.description = description;
        this.phone = phone;
        this.imgNames = imgNames;
        this.downloadUrls = downloadUrls;
        this.isNew = isNew;
        this.dateTime = dateTime;
        this.price = price;
        this.currency = currency;
        this.year = year;
        this.isClosed = isClosed;
        this.reasonOfClosing = reasonOfClosing;
        this.closeDateTime = closeDateTime;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", creatorId);
        result.put("userName", creatorName);
        result.put("category", category);
        result.put("country", country);
        result.put("city", city);
        result.put("tittle", tittle);
        result.put("description", description);
        result.put("phone", phone);
        result.put("imgNames", imgNames);
        result.put("downloadUrls", downloadUrls);
        result.put("isNew", isNew);
        result.put("dateTime", dateTime);
        result.put("price", price);
        result.put("currency", currency);
        result.put("year",year);

        return result;
    }

    @Exclude
    public Map<String, Object> toMapPhoto() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("imgNames", imgNames);
        result.put("downloadUrls", downloadUrls);

        return result;
    }

    public static Advert documetnSnapshotToAdvert(DocumentSnapshot documentSnapshot){
        int year;
        if(documentSnapshot.contains("year")){
            year = documentSnapshot.getLong("year").intValue();
        }else{
            year = 0;
        }
        System.out.println("BBBBBBBBBBBBBB" + year);
        Advert advert = new Advert(
                documentSnapshot.get("userId").toString(),
                documentSnapshot.get("userName").toString(),
                documentSnapshot.get("category").toString(),
                documentSnapshot.get("country").toString(),
                documentSnapshot.get("city").toString(),
                documentSnapshot.get("tittle").toString(),
                documentSnapshot.get("description").toString(),
                documentSnapshot.get("phone").toString(),
                (List<String>)documentSnapshot.get("imgNames"),
                (List<String>)documentSnapshot.get("downloadUrls"),
                (boolean)documentSnapshot.getBoolean("isNew"),
                //(Timestamp) new Timestamp((Date)documentSnapshot.get("dateTime")),
                (Timestamp) documentSnapshot.get("dateTime"),
                (long) documentSnapshot.get("price"),
                documentSnapshot.get("currency").toString(),
                year
        );
        return advert;
    }

    public static Advert documetnSnapshotToCloseAdvert(DocumentSnapshot documentSnapshot){
        Advert advert = new Advert(
                documentSnapshot.get("userId").toString(),
                documentSnapshot.get("userName").toString(),
                documentSnapshot.get("category").toString(),
                documentSnapshot.get("country").toString(),
                documentSnapshot.get("city").toString(),
                documentSnapshot.get("tittle").toString(),
                documentSnapshot.get("description").toString(),
                documentSnapshot.get("phone").toString(),
                (List<String>)documentSnapshot.get("imgNames"),
                (List<String>)documentSnapshot.get("downloadUrls"),
                (boolean)documentSnapshot.getBoolean("isNew"),
                //(Timestamp) new Timestamp((Date)documentSnapshot.get("dateTime")),
                (Timestamp) documentSnapshot.get("dateTime"),
                (long) documentSnapshot.get("price"),
                documentSnapshot.get("currency").toString(),
                (int) documentSnapshot.get("year"),
                (boolean)documentSnapshot.get("isClosed"),
                (String)documentSnapshot.get("reasonOfClosing"),
                //(Timestamp) new Timestamp((Date)documentSnapshot.get("closeDateTime"))
                (Timestamp) documentSnapshot.get("closeDateTime")
        );
        return advert;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public String getReasonOfClosing() {
        return reasonOfClosing;
    }

    public void setReasonOfClosing(String reasonOfClosing) {
        this.reasonOfClosing = reasonOfClosing;
    }

    public Timestamp getCloseDateTime() {
        return closeDateTime;
    }

    public void setCloseDateTime(Timestamp closeTime) {
        this.closeDateTime = closeTime;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String title) {
        this.tittle = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<String> getImgNames() {
        return imgNames;
    }

    public void setImgNames(List<String> imgNames) {
        this.imgNames = imgNames;
    }

    public List<String> getDownloadUrls() {
        return downloadUrls;
    }

    public void setDownloadUrls(List<String> downloadUrls) {
        this.downloadUrls = downloadUrls;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }
}
