package c.motor.motor.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import c.motor.motor.AdvertActivity;
import c.motor.motor.MainActivity;
import c.motor.motor.R;
import c.motor.motor.RegisterActivity;
import c.motor.motor.helpers.Helpers;
import c.motor.motor.model.AdvertSearch;

public class SearchAdvertAdapter extends RecyclerView.Adapter<SearchAdvertAdapter.SearchAdvertPreviewViewHolder> {
    private Context mContext;
    private int currentDate = Calendar.getInstance().get(Calendar.DATE);
    ArrayList<AdvertSearch> advertSearchArrayList;
    private HashMap<String, Boolean> likedList = new HashMap<>();
    private String country;

    private String collectionPath, favoriteCollectionPath, indexName;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String userId;

    public SearchAdvertAdapter(Context mContext, ArrayList<AdvertSearch> advertSearchArrayList, String country){
        this.mContext = mContext;
        this.advertSearchArrayList = advertSearchArrayList;
        this.country = country;
        setupCountryDataBase(country);
    }

    @NonNull
    @Override
    public SearchAdvertPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_advert_grid_card, parent, false);
        SearchAdvertAdapter.SearchAdvertPreviewViewHolder holder = new SearchAdvertAdapter.SearchAdvertPreviewViewHolder(view);
        return new SearchAdvertAdapter.SearchAdvertPreviewViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAdvertPreviewViewHolder holder, final int i) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        try {
            holder.tittleTextView.setText(advertSearchArrayList.get(i).getTitle());
            if(advertSearchArrayList.get(i).getImage() != null && !advertSearchArrayList.get(i).getImage().equals("null")){
                Glide.with(mContext)
                        .load(advertSearchArrayList.get(i).getImage())
                        .error(Helpers.emptyCatImg(advertSearchArrayList.get(i).getCategory().toLowerCase()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mainImageView);
            }else{
                Glide.with(mContext)
                        .load(Helpers.emptyCatImg(advertSearchArrayList.get(i).getCategory().toLowerCase()))
                        .into(holder.mainImageView);
            }
            DecimalFormat df = new DecimalFormat("###,###,###,###,###");

            holder.priceTextView.setText(
                    String.format(df.format(advertSearchArrayList.get(i).getPrice()))
                    + " "
                    + advertSearchArrayList.get(i).getCurrency());

            Date date = new Date(advertSearchArrayList.get(i).getDateTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd-yyyy 'at' HH:mm");
            SimpleDateFormat todayDateFormat = new SimpleDateFormat("'Today at' HH:mm");
            SimpleDateFormat yesterdayDateFormat = new SimpleDateFormat("'Yesterday at' HH:mm");
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

            holder.cityTextView.setText(advertSearchArrayList.get(i).getCity());

            if(advertSearchArrayList.get(i).isNew()){
                holder.isNewTextView.setVisibility(View.VISIBLE);
            }else{
                //for Hoa if u remove "else" u will get a bug
                holder.isNewTextView.setVisibility(View.GONE);
            }




//            final String favoriteDocumentName = advertSearchArrayList.get(i).getDocumentReference() + "-" + userId;
            holder.advertCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("documentName " + advertSearchArrayList.get(i).getDocumentReference());
                    Intent advertActivity = new Intent(mContext, AdvertActivity.class);
                    advertActivity.putExtra("documentName", advertSearchArrayList.get(i).getDocumentReference());
                    advertActivity.putExtra("country", advertSearchArrayList.get(i).getCountry());
                    mContext.startActivity(advertActivity);

                }
            });

            //favorite logic
            if (firebaseUser != null ){

                userId = firebaseUser.getUid();

                final String favoriteDocumentName = advertSearchArrayList.get(i).getDocumentReference() + "-" + userId;

                FirebaseFirestore
                        .getInstance()
                        .collection(favoriteCollectionPath)
                        .document(favoriteDocumentName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult().contains("isLiked")){
                                        Glide.with(holder.itemView).load(R.drawable.ic_favorite_blue).into(holder.likeButton);
                                        likedList.put(advertSearchArrayList.get(i).getDocumentReference(), true);
                                        holder.likeButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                likeOptions(true,holder,advertSearchArrayList.get(i).getDocumentReference(),favoriteDocumentName);
                                            }
                                        });

                                    }else{
                                        Glide.with(holder.itemView).load(R.drawable.ic_favorite_border_blue).into(holder.likeButton);
                                        holder.likeButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                likeOptions(false,holder,advertSearchArrayList.get(i).getDocumentReference(),favoriteDocumentName);
                                            }
                                        });
                                    }
                                }else{
                                    Glide.with(holder.itemView).load(R.drawable.ic_favorite_border_blue).into(holder.likeButton);
                                    holder.likeButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            likeOptions(false,holder,advertSearchArrayList.get(i).getDocumentReference(),favoriteDocumentName);
                                        }
                                    });
                                }
                            }
                        });



            }else{
                //open register activity
//                Intent addIntent = new Intent(mContext , RegisterActivity.class);
//                mContext.startActivity(addIntent);
            }






        } catch (Exception e) {

        }
    }

    private void likeOptions(Boolean isliked, final SearchAdvertPreviewViewHolder holder, final String documentName, final String favoriteDocumentName){
        try {
            if (likedList.isEmpty()) {
                if (isliked) {
                    //unlike
                    FirebaseFirestore.getInstance().collection(favoriteCollectionPath).document(favoriteDocumentName).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    likedList.put(documentName, true);

                                    Toast.makeText(mContext, "Was remove from favorite", Toast.LENGTH_SHORT).show();
                                    Glide.with(holder.itemView).load(R.drawable.ic_favorite_border_blue).into(holder.likeButton);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Problem with internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //like
                    Map<String, Object> likeData = new HashMap<>();
                    likeData.put("documentName", documentName);
                    likeData.put("userId", userId);
                    likeData.put("isLiked", true);
                    likeData.put("dateTime", new Timestamp(new Date()));
                    FirebaseFirestore.getInstance().collection(favoriteCollectionPath).document(favoriteDocumentName).set(likeData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Glide.with(holder.itemView).load(R.drawable.ic_favorite_blue).into(holder.likeButton);
                                    likedList.put(documentName, true);
                                    Toast.makeText(mContext, "Added to favorite", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Problem with internet connection", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else if (likedList.containsKey(documentName)) {
                if (likedList.get(documentName)) {
                    //unlike
                    FirebaseFirestore.getInstance().collection(favoriteCollectionPath).document(favoriteDocumentName).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    likedList.put(documentName, false);

                                    Toast.makeText(mContext, "Was remove from favorite", Toast.LENGTH_SHORT).show();
                                    Glide.with(holder.itemView).load(R.drawable.ic_favorite_border_blue).into(holder.likeButton);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Problem with internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //like
                    Map<String, Object> likeData = new HashMap<>();
                    likeData.put("documentName", documentName);
                    likeData.put("userId", userId);
                    likeData.put("isLiked", true);
                    likeData.put("dateTime", new Timestamp(new Date()));
                    FirebaseFirestore.getInstance().collection(favoriteCollectionPath).document(favoriteDocumentName).set(likeData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Glide.with(holder.itemView).load(R.drawable.ic_favorite_blue).into(holder.likeButton);
                                    likedList.put(documentName, true);
                                    Toast.makeText(mContext, "Added to favorite", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Problem with internet connection", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                if (isliked) {
                    //unlike
                    FirebaseFirestore.getInstance().collection(favoriteCollectionPath).document(favoriteDocumentName).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    likedList.put(documentName, false);

                                    Toast.makeText(mContext, "Was remove from favorite", Toast.LENGTH_SHORT).show();
                                    Glide.with(holder.itemView).load(R.drawable.ic_favorite_border_blue).into(holder.likeButton);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Problem with internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //like
                    Map<String, Object> likeData = new HashMap<>();
                    likeData.put("documentName", documentName);
                    likeData.put("userId", userId);
                    likeData.put("isLiked", true);
                    likeData.put("dateTime", new Timestamp(new Date()));
                    FirebaseFirestore.getInstance().collection(favoriteCollectionPath).document(favoriteDocumentName).set(likeData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Glide.with(holder.itemView).load(R.drawable.ic_favorite_blue).into(holder.likeButton);
                                    likedList.put(documentName, true);
                                    Toast.makeText(mContext, "Added to favorite", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Problem with internet connection", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }catch (Exception e) {
            Toast.makeText(mContext, "Problem with internet connection", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public int getItemCount() {
        return advertSearchArrayList.size();
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

    public class SearchAdvertPreviewViewHolder extends RecyclerView.ViewHolder {

        private CardView
                advertCard;

        private ImageView
                mainImageView;

        private TextView
                tittleTextView,
                priceTextView,
                dateTextView,
                cityTextView;

        private TextView
                isNewTextView;

        private ImageButton
                likeButton;

        public SearchAdvertPreviewViewHolder(@NonNull View itemView) {
            super(itemView);

            advertCard = itemView.findViewById(R.id.advert_grid_full_card);

            mainImageView = itemView.findViewById(R.id.advert_grid_card_img);

            tittleTextView = itemView.findViewById(R.id.advert_grid_card_tittle);
            priceTextView = itemView.findViewById(R.id.advert_grid_card_price);
            dateTextView = itemView.findViewById(R.id.advert_grid_card_date);
            cityTextView = itemView.findViewById(R.id.advert_grid_card_city);
            isNewTextView = itemView.findViewById(R.id.advert_grid_card_new_identifier);

            likeButton = itemView.findViewById(R.id.advert_grid_like_button);


        }
    }
}
