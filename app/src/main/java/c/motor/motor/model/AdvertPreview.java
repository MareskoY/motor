package c.motor.motor.model;

import com.google.firebase.Timestamp;
import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.DocumentSnapshot;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvertPreview {
    private String downloadUrlMain;
    private String title;
    private long price;
    private Timestamp dateTime;
    private String city;
    private String currency;
    private String category;
    private Boolean isClosed;
    private Boolean isNew;


    public AdvertPreview(){
        //empty constructor
    }

    public AdvertPreview(String downloadUrlMain, String title, long price, Timestamp dateTime, String city, String currency, String category, Boolean isClosed, Boolean isNew) {
        this.downloadUrlMain = downloadUrlMain;
        this.title = title;
        this.price = price;
        this.dateTime = dateTime;
        this.city = city;
        this.currency = currency;
        this.category = category;
        this.isClosed = isClosed;
        this.isNew = isNew;
    }


//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("downloadUrlMain", downloadUrlMain);
//        result.put("tittle", title);
//        result.put("price", price);
//        result.put("dateTime", dateTime);
//        result.put("city", city);
//        result.put("currency", currency);
//
//        return result;
//    }

    public static AdvertPreview documentSnapshotToAdvertPreview(DocumentSnapshot documentSnapshot){
        AdvertPreview advertPreview;
        String downloadImg = null;
        if(documentSnapshot.contains("downloadUrls")){
            List<String> downloadUrls = (List<String>)documentSnapshot.get("downloadUrls");
            if(downloadUrls != null){
                if(downloadUrls.size() != 0){
                    downloadImg = downloadUrls.get(0);
                }
            }
        }
        if(documentSnapshot.contains("isClosed")){
            if((Boolean)documentSnapshot.get("isClosed")){
                advertPreview = new AdvertPreview(
                        downloadImg,
                        documentSnapshot.get("tittle").toString(),
                        (long)documentSnapshot.get("price"),
                        documentSnapshot.getTimestamp("dateTime"),
                        documentSnapshot.get("city").toString(),
                        documentSnapshot.get("currency").toString(),
                        documentSnapshot.get("category").toString(),
                        true,
                        documentSnapshot.getBoolean("isNew")

                );
            }else{

                advertPreview = new AdvertPreview(
                        downloadImg,
                        documentSnapshot.get("tittle").toString(),
                        (long)documentSnapshot.get("price"),
                        documentSnapshot.getTimestamp("dateTime"),
                        documentSnapshot.get("city").toString(),
                        documentSnapshot.get("currency").toString(),
                        documentSnapshot.get("category").toString(),
                        false,
                        documentSnapshot.getBoolean("isNew")
                );
            }
        }else{
            advertPreview = new AdvertPreview(
                    downloadImg,
                    documentSnapshot.get("tittle").toString(),
                    (long)documentSnapshot.get("price"),
                    documentSnapshot.getTimestamp("dateTime"),
                    documentSnapshot.get("city").toString(),
                    documentSnapshot.get("currency").toString(),
                    documentSnapshot.get("category").toString(),
                    false,
                    documentSnapshot.getBoolean("isNew")
            );
        }


        return advertPreview;

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDownloadUrlMain() {
        return downloadUrlMain;
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public void setClosed(Boolean closed) {
        isClosed = closed;
    }

    public void setDownloadUrlMain(String downloadUrlMain) {
        this.downloadUrlMain = downloadUrlMain;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }
}
