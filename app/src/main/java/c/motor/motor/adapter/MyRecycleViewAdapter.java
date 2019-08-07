package c.motor.motor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import c.motor.motor.AdvertActivity;
import c.motor.motor.R;
import c.motor.motor.helpers.Helpers;
import c.motor.motor.model.Advert;
import c.motor.motor.model.AdvertPreview;
import c.motor.motor.model.AdvertSearch;

public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.MyRecycleHolder>{

    private Context mContext;
    private int currentDate = Calendar.getInstance().get(Calendar.DATE);
    ArrayList<AdvertPreview> advertSearchArrayList;
    ArrayList<String> documentNameArrayList;
    private HashMap<String, Boolean> likedList = new HashMap<>();

    private String country;



    DecimalFormat priceFormat = new DecimalFormat("###,###,###,###,###");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd-yyyy 'at' HH:mm");
    SimpleDateFormat todayDateFormat = new SimpleDateFormat("'Today at' HH:mm");
    SimpleDateFormat yesterdayDateFormat = new SimpleDateFormat("'Yesterday at' HH:mm");

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String userId;

    public MyRecycleViewAdapter(Context mContext, ArrayList<AdvertPreview> advertSearchArrayList, ArrayList<String> documentNameArrayList, String country) {
        this.mContext = mContext;
        this.advertSearchArrayList = advertSearchArrayList;
        this.documentNameArrayList = documentNameArrayList;
        this.country = country;

    }

    @NonNull
    @Override
    public MyRecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_advert_grid_card, parent, false);
        MyRecycleViewAdapter.MyRecycleHolder holder = new MyRecycleViewAdapter.MyRecycleHolder(view);
        return new MyRecycleViewAdapter.MyRecycleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecycleHolder holder,final int position) {
        if(advertSearchArrayList.get(position).getDownloadUrlMain() != null){
            Glide.with(mContext)
                    .load(advertSearchArrayList.get(position).getDownloadUrlMain())
                    .error(R.mipmap.empty_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.apply(requestOptions)
                    .into(holder.mainImageView);
        }else{
            Glide.with(mContext)
                    .load(Helpers.emptyCatImg(advertSearchArrayList.get(position).getCategory().toLowerCase()))
                    .error(R.mipmap.empty_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.apply(requestOptions)
                    .into(holder.mainImageView);
        }
        holder.likeButton.setVisibility(View.GONE);

        if(advertSearchArrayList.get(position).getNew()){
            holder.isNewTextView.setVisibility(View.VISIBLE);
        }

        holder.titleTextView.setText(advertSearchArrayList.get(position).getTitle());
        holder.priceTextView.setText(advertSearchArrayList.get(position).getCurrency() +
                " " +
                String.format(priceFormat.format(advertSearchArrayList.get(position).getPrice())));

        Date date = advertSearchArrayList.get(position).getDateTime().toDate();
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
        holder.cityTextView.setText(advertSearchArrayList.get(position).getCity());

        if(advertSearchArrayList.get(position).getClosed()){
            holder.soldImageView.setVisibility(View.VISIBLE);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent advertActivity = new Intent(mContext, AdvertActivity.class);
                advertActivity.putExtra("documentName", documentNameArrayList.get(position));
                advertActivity.putExtra("country", country);
                mContext.startActivity(advertActivity);
            }
        });



    }

    @Override
    public int getItemCount() {
        return advertSearchArrayList.size();
    }


    public class MyRecycleHolder extends RecyclerView.ViewHolder {

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

        public MyRecycleHolder(View itemView){
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
