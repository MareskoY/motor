package c.motor.motor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import c.motor.motor.R;
import c.motor.motor.addAdvertActivity.AddAdvertActivity;
import c.motor.motor.addAdvertActivity.PhotoWidgetFragment;

public class UploadPhotoAdapter extends RecyclerView.Adapter<UploadPhotoAdapter.UploadPhotoAdapterVH>{

    private Context context;
    AddAdvertActivity addAdvertActivity;
    PhotoWidgetFragment photoWidgetFragment;
    ArrayList<String> downloadUrls;
    ArrayList<String> fileNames;

    public UploadPhotoAdapter(Context context, AddAdvertActivity addAdvertActivity, PhotoWidgetFragment photoWidgetFragment, ArrayList<String> downloadUrls, ArrayList<String> fileNames) {
        this.context = context;
        this.addAdvertActivity = addAdvertActivity;
        this.photoWidgetFragment = photoWidgetFragment;
        this.downloadUrls = downloadUrls;
        this.fileNames = fileNames;
    }

    @NonNull
    @Override
    public UploadPhotoAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_photo_grid_card, parent, false);
        UploadPhotoAdapter.UploadPhotoAdapterVH holder = new UploadPhotoAdapter.UploadPhotoAdapterVH(view);
        return new UploadPhotoAdapter.UploadPhotoAdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UploadPhotoAdapterVH holder, final int i) {
        //first setup
        Glide.with(holder.itemView)
                .load(downloadUrls.get(i))
                .error(R.mipmap.empty_image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imageView);
        holder.deleteBtn.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.GONE);

        //actions
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.progressBar.setVisibility(View.VISIBLE);
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReference().child("ADVERTS_IMAGES_TEST_FOLDER3");

                final StorageReference filePath = storageReference.child(fileNames.get(i));
                filePath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show();
                        downloadUrls.remove(i);
                        fileNames.remove(i);

                        addAdvertActivity.downloadUrls = downloadUrls;
                        addAdvertActivity.fileNames = fileNames;




                        photoWidgetFragment.imgCount = photoWidgetFragment.imgCount - 1;
                        photoWidgetFragment.addButton.setVisibility(View.VISIBLE);
                        UploadPhotoAdapter.this.notifyDataSetChanged();
                        holder.progressBar.setVisibility(View.GONE);
                        addAdvertActivity.isPhotoChanged = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show();
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return downloadUrls.size();
    }

    class UploadPhotoAdapterVH extends RecyclerView.ViewHolder {

        View itemView;
        ProgressBar progressBar;
        ImageView imageView;
        ImageButton deleteBtn;


        public UploadPhotoAdapterVH (View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.load_photo_grid_card_progressBar);
            imageView = itemView.findViewById(R.id.load_photo_grid_card_img);
            deleteBtn = itemView.findViewById(R.id.load_photo_grid_card_delete);
            this.itemView = itemView;
        }
    }
}
