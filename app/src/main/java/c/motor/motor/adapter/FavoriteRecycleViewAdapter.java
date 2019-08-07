package c.motor.motor.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import c.motor.motor.AdvertActivity;
import c.motor.motor.R;
import c.motor.motor.helpers.Helpers;
import c.motor.motor.model.Advert;

public class FavoriteRecycleViewAdapter extends RecyclerView.Adapter<FavoriteRecycleViewAdapter.FavoriteRecycleHolder> {

    private Context mContext;
    private ArrayList<String> documentIdList;
    private HashMap<String, Boolean> unLikedList = new HashMap<>();
    private Query query;
    private Advert advert;
    DecimalFormat priceFormat = new DecimalFormat("###,###,###,###,###");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd-yyyy 'at' HH:mm");
    SimpleDateFormat todayDateFormat = new SimpleDateFormat("'Today at' HH:mm");
    SimpleDateFormat yesterdayDateFormat = new SimpleDateFormat("'Yesterday at' HH:mm");
    private int currentDate = Calendar.getInstance().get(Calendar.DATE);
    private String country;

    private String collectionPath, favoriteCollectionPath, indexName;

    public FavoriteRecycleViewAdapter (Context mContext, ArrayList<String> documentIdList, String country){
        this.mContext = mContext;
        this.documentIdList = documentIdList;
        this.country = country;
        setupCountryDataBase(country);
    }

    @NonNull
    @Override
    public FavoriteRecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_advert_grid_card, parent, false);
        FavoriteRecycleViewAdapter.FavoriteRecycleHolder holder = new FavoriteRecycleViewAdapter.FavoriteRecycleHolder(view);
        return new FavoriteRecycleViewAdapter.FavoriteRecycleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriteRecycleHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);

        FirebaseFirestore
                .getInstance()
                .collection(collectionPath)
                .document(documentIdList.get(position))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.contains("isClosed")){
                                if((boolean)document.get("isClosed")){
                                    setupPreview(document, holder, position);
                                    setupCloseView(holder);
                                }else{
                                    setupPreview(document, holder, position);
                                }
                            }else{
                                setupPreview(document, holder, position);
                            }
                        }else{
                            holder.cardView.setVisibility(View.GONE);
                        }
                    }
                });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent advertActivity = new Intent(mContext, AdvertActivity.class);
                advertActivity.putExtra("documentName", documentIdList.get(position));
                advertActivity.putExtra("country", country);
                mContext.startActivity(advertActivity);
            }
        });
        //load doc


    }

    private void setupCountryDataBase(String currentCountry){
        if(currentCountry.equals("Vietnam")){
            collectionPath = "TEST_VN";
            favoriteCollectionPath = "FAVORITE_VN_TEST";
            indexName = "adverts_VN_TEST";
        }else if(currentCountry.equals("Philippines")){
            collectionPath = "TEST_PH";
            favoriteCollectionPath = "FAVORITE_PH_TEST";
            indexName = "adverts_PH_TEST";
        }
    }

    @Override
    public int getItemCount() {
        return documentIdList.size();
    }

    private void setupPreview(final DocumentSnapshot document, @NonNull final FavoriteRecycleHolder holder, final int number){
        advert = Advert.documetnSnapshotToAdvert(document);
        if(advert.getDownloadUrls() != null){
            if(advert.getDownloadUrls().size() != 0) {
                Glide.with(mContext)
                        .load(advert.getDownloadUrls().get(0))
                        .error(R.mipmap.empty_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.apply(requestOptions)
                        .into(holder.mainImageView);
            }else{
                Glide.with(mContext)
                        .load(Helpers.emptyCatImg(advert.getCategory().toLowerCase()))
                        .error(R.mipmap.empty_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.apply(requestOptions)
                        .into(holder.mainImageView);
            }
        }else {
            Glide.with(mContext)
                    .load(Helpers.emptyCatImg(advert.getCategory().toLowerCase()))
                    .error(R.mipmap.empty_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.apply(requestOptions)
                    .into(holder.mainImageView);
        }
        holder.titleTextView.setText(advert.getTittle());
        holder.cityTextView.setText(advert.getCity());
        holder.dateTextView.setText(advert.getCity());
        holder.priceTextView.setText(advert.getCurrency() + " " + String.format(priceFormat.format(advert.getPrice())));

        if(advert.getNew()){
            holder.isNewTextView.setVisibility(View.VISIBLE);
        }
        Date date = advert.getDateTime().toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int advertDate = cal.get(Calendar.DATE);
        if(currentDate == advertDate){
            holder.dateTextView.setText(todayDateFormat.format(date));
        }else if(currentDate-1 == advertDate ){
            holder.dateTextView.setText(yesterdayDateFormat.format(date));
        }else{
            holder.dateTextView.setText(dateFormat.format(date));
        }

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String favoriteDocumentName = documentIdList.get(number) + "-" + userId;



        //unlike
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (unLikedList.isEmpty()) {
                        //unlike
                        FirebaseFirestore.getInstance().collection(favoriteCollectionPath).document(favoriteDocumentName).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        unLikedList.put(documentIdList.get(number), true);

                                        Toast.makeText(mContext, "Was remove from favorite", Toast.LENGTH_SHORT).show();
                                        Glide.with(holder.itemView).load(R.drawable.ic_favorite_border_blue).into(holder.likeButton);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "Problem with internet connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (unLikedList.get(documentIdList.get(number))) {
                        //like
                        Map<String, Object> likeData = new HashMap<>();
                        likeData.put("documentName", documentIdList.get(number));
                        likeData.put("userId", userId);
                        likeData.put("isLiked", true);
                        likeData.put("dateTime", new Timestamp(new Date()));
                        FirebaseFirestore.getInstance().collection(favoriteCollectionPath).document(favoriteDocumentName).set(likeData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Glide.with(holder.itemView).load(R.drawable.ic_favorite_blue).into(holder.likeButton);
                                        unLikedList.remove(documentIdList.get(number));
                                        Toast.makeText(mContext, "Added to favorite", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "Problem with internet connection", Toast.LENGTH_LONG).show();
                            }
                        });

                    } else {

                        //unlike
                        FirebaseFirestore.getInstance().collection(favoriteCollectionPath).document(favoriteDocumentName).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (!unLikedList.containsKey(documentIdList.get(number)))
                                            unLikedList.put(documentIdList.get(number), true);

                                        Toast.makeText(mContext, "Was remove from favorite", Toast.LENGTH_SHORT).show();
                                        Glide.with(holder.itemView).load(R.drawable.ic_favorite_border_blue).into(holder.likeButton);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "Problem with internet connection", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }catch (Exception e){
                    Toast.makeText(mContext, "Problem with internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @SuppressLint("ResourceAsColor")
    private void setupCloseView(@NonNull final FavoriteRecycleHolder holder){
        holder.soldImageView.setVisibility(View.VISIBLE);
        holder.priceTextView.setTextColor(R.color.colorGray);
        holder.titleTextView.setTextColor(R.color.colorGray);
        holder.dateTextView.setTextColor(R.color.colorGray);
        holder.cityTextView.setTextColor(R.color.colorGray);
    }


    public class FavoriteRecycleHolder extends RecyclerView.ViewHolder {

        private CardView
                cardView;

        private ImageView
                mainImageView,
                soldImageView;

        private TextView
                isNewTextView,
                titleTextView,
                priceTextView,
                dateTextView,
                cityTextView;

        private ImageButton
                likeButton;

        public FavoriteRecycleHolder(View itemView){
            super(itemView);

            cardView = itemView.findViewById(R.id.preview_advert_full_card);

            mainImageView = itemView.findViewById(R.id.preview_advert_img);
            soldImageView = itemView.findViewById(R.id.preview_advert_img_sold);

            titleTextView = itemView.findViewById(R.id.preview_advert_title);
            priceTextView = itemView.findViewById(R.id.preview_advert_price);
            dateTextView = itemView.findViewById(R.id.preview_advert_date);
            cityTextView = itemView.findViewById(R.id.preview_advert_city);
            isNewTextView = itemView.findViewById(R.id.preview_advert_isNew);

            likeButton = itemView.findViewById(R.id.preview_advert_like_button);

        }

    }

}
