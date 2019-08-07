package c.motor.motor.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import c.motor.motor.AdvertActivity;
import c.motor.motor.R;
import c.motor.motor.helpers.Helpers;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    ArrayList<String> urls;
    int size;
    private Boolean isSold;
    private String category;

    public SliderAdapter(Context context, ArrayList<String> urls, int size, Boolean isSold, String category) {
        this.context = context;
        this.urls = urls;
        this.size = size;
        this.isSold = isSold;
        this.category = category;

    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int i) {
        if(isSold){
            viewHolder.imageViewSold.setVisibility(View.VISIBLE);
        }
        if(size != 0){
            Glide.with(viewHolder.itemView)
                    .load(urls.get(i))
                    .error(R.mipmap.empty_image)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.imageViewBackground);
        }else{
            Glide.with(viewHolder.itemView).load(Helpers.emptyCatImg(category.toLowerCase())).into(viewHolder.imageViewBackground);
        }

    }

    @Override
    public int getCount() {
        if(size == 0){
            return 1;
        }else{
            return size;
        }

    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        ImageView imageViewSold;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            imageViewSold = itemView.findViewById(R.id.iv_auto_image_slider_sold);
            this.itemView = itemView;
        }
    }
}


