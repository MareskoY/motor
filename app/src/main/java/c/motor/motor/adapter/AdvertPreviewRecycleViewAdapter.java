package c.motor.motor.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import c.motor.motor.AdvertActivity;
import c.motor.motor.R;
import c.motor.motor.model.AdvertPreview;

import static android.app.PendingIntent.getActivity;

public class AdvertPreviewRecycleViewAdapter extends RecyclerView.Adapter<AdvertPreviewRecycleViewAdapter.AdvertPreviewViewHolder>{

    private Context mContext;
    private int currentDate = Calendar.getInstance().get(Calendar.DATE);
    ArrayList<AdvertPreview> advertPreviewArrayList;
    private String documentName;

    public AdvertPreviewRecycleViewAdapter(Context mContext, ArrayList<AdvertPreview> advertPreviewArrayList) {
        this.mContext = mContext;
        this.advertPreviewArrayList = advertPreviewArrayList;
    }

    @NonNull
    @Override
    public AdvertPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_advert_grid_card, parent, false);
        AdvertPreviewRecycleViewAdapter.AdvertPreviewViewHolder holder = new AdvertPreviewRecycleViewAdapter.AdvertPreviewViewHolder(view);
        return new AdvertPreviewRecycleViewAdapter.AdvertPreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertPreviewViewHolder holder, final int i) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        try {
            if (advertPreviewArrayList.get(i).getDownloadUrlMain() != null) {
                Glide.with(mContext)
                        .load(advertPreviewArrayList.get(i).getDownloadUrlMain())
                        .error(R.mipmap.empty_image)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        //.apply(requestOptions)
                        .into(holder.mainImageView);
            }else{
                holder.mainImageView.setImageResource(R.drawable.ic_directions_car_blue);
            }
            //Tittle
            holder.tittleTextView.setText(advertPreviewArrayList.get(i).getTitle());
            // Price
            Double doublePrice = advertPreviewArrayList.get(i).getPrice();
            int intPrice = doublePrice.intValue();
            holder.priceTextView.setText(String.valueOf(intPrice) + " " + advertPreviewArrayList.get(i).getCurrency());
            // Date
            Timestamp timestamp = advertPreviewArrayList.get(i).getDateTime();
            Date date = timestamp.toDate();
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

            // City
            holder.cityTextView.setText(advertPreviewArrayList.get(i).getCity());

            holder.mainImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    documentName = advertPreviewArrayList.get(i).getTitle();
                    Intent advertActivity = new Intent(mContext, AdvertActivity.class);
                    advertActivity.putExtra("documentName", documentName);
                    mContext.startActivity(advertActivity);
                    Toast.makeText(mContext, advertPreviewArrayList.get(i).getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return advertPreviewArrayList.size();
    }

    public class AdvertPreviewViewHolder extends RecyclerView.ViewHolder {

        private ImageView
                mainImageView;

        private TextView
                tittleTextView,
                priceTextView,
                dateTextView,
                cityTextView;


        public AdvertPreviewViewHolder(@NonNull View itemView) {
            super(itemView);

            mainImageView = itemView.findViewById(R.id.advert_grid_card_img);

            tittleTextView = itemView.findViewById(R.id.advert_grid_card_tittle);
            priceTextView = itemView.findViewById(R.id.advert_grid_card_price);
            dateTextView = itemView.findViewById(R.id.advert_grid_card_date);
            cityTextView = itemView.findViewById(R.id.advert_grid_card_city);


        }
    }
}
