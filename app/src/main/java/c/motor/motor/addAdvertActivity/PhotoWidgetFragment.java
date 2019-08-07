package c.motor.motor.addAdvertActivity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import c.motor.motor.R;
import c.motor.motor.adapter.FavoriteRecycleViewAdapter;
import c.motor.motor.adapter.UploadPhotoAdapter;
import c.motor.motor.helpers.Helpers;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoWidgetFragment extends Fragment {

//    CropImageView
//            mainImageView;

    final private int MAX_IMG_COUNT = 4;

    RecyclerView recyclerView;

    private UploadPhotoAdapter adapter;

    public LinearLayout
            addButton,
            photosLayout;
    Boolean noPhotos = true;



    Boolean isEditing = false;

    AddAdvertActivity addAdvertActivity;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    //system
    private String stringDownloadUri;

    public RelativeLayout loader;


    public ArrayList<String>
            downloadUrls = new ArrayList<String>(),
            fileNames = new ArrayList<String>();

    public int imgCount;



    public PhotoWidgetFragment(AddAdvertActivity addAdvertActivity) {
        // Required empty public constructor
        this.addAdvertActivity = addAdvertActivity;
        isEditing = false;
    }

    public PhotoWidgetFragment(AddAdvertActivity addAdvertActivity,ArrayList<String> downloadUrls, ArrayList<String> fileNames) {
        this.addAdvertActivity = addAdvertActivity;
        this.downloadUrls = downloadUrls;
        this.fileNames = fileNames;
        isEditing = true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_widget, container, false);
        firstSetup(view);

        //addAdvertActivity = (AddAdvertActivity)getActivity();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imgCount++;
                if(imgCount >= MAX_IMG_COUNT){
                    addButton.setVisibility(View.GONE);
                }
                loadPhoto(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                System.out.println("+++++++ " + error);
            }
        }
    }

    private void onChooseFile(){
        CropImage.activity()
                //.setAspectRatio(39,27)
                .start(getContext(), this);
    }

    private void firstSetup(View view) {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("ADVERTS_IMAGES_TEST_FOLDER3");

        addButton = view.findViewById(R.id.photoFragment_add_img);
        recyclerView = view.findViewById(R.id.photo_widget_recycle_view);
        loader = view.findViewById(R.id.photo_widget_loader);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        if(downloadUrls.size() != 0 && isEditing){
            adapter = new UploadPhotoAdapter(getContext(), addAdvertActivity,PhotoWidgetFragment.this,  downloadUrls, fileNames);
            recyclerView.setAdapter(adapter);
            noPhotos = false;
            imgCount = downloadUrls.size();
        }else{
            adapter = new UploadPhotoAdapter(getContext(), addAdvertActivity,PhotoWidgetFragment.this,  downloadUrls, fileNames);
            recyclerView.setAdapter(adapter);
            imgCount = 0;
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseFile();
            }
        });
    }

    private void loadPhoto(Uri uri){
        availableButtons(false);
        loader.setVisibility(View.VISIBLE);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String fileChildPath = userId + "_" + Helpers.getRandomString();
        final StorageReference filePath = storageReference.child(fileChildPath);

        byte[] compressData = CompressPhoto(uri);


        final UploadTask uploadTask = filePath.putBytes(compressData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            stringDownloadUri = task.getResult().toString();
                            downloadUrls.add(stringDownloadUri);
                            fileNames.add(fileChildPath);
                            saveImageList(fileChildPath);
                            addAdvertActivity.downloadUrls = downloadUrls;
                            addAdvertActivity.fileNames = fileNames;
                            System.out.println("data---- " + stringDownloadUri + " " + fileChildPath);

                            loader.setVisibility(View.GONE);
                            if(isEditing){
                                adapter.notifyDataSetChanged();
                            }else{
                                adapter = new UploadPhotoAdapter(getContext(), addAdvertActivity,PhotoWidgetFragment.this, downloadUrls, fileNames);
                                recyclerView.setAdapter(adapter);
                            }


                            availableButtons(true);
                            Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
                        }else{

                        }
                    }
                });
            }
        });









//        //final UploadTask uploadTask = filePath.putBytes(compressData);
//        filePath.putBytes(compressData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                if(task.isSuccessful()){
//
//
//                    Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
//                    stringDownloadUri = filePath.getDownloadUrl().
//
//                    downloadUrls.add(stringDownloadUri);
//                    fileNames.add(fileChildPath);
//                    saveImageList(fileChildPath);
//                    System.out.println("data+++ " + stringDownloadUri + " " + fileChildPath);
//
//                    if(noPhotos){
//                        if(downloadUrls.size() != 0) {
//                            adapter = new UploadPhotoAdapter(getContext(), addAdvertActivity, downloadUrls, fileNames);
//                            recyclerView.setAdapter(adapter);
//                        }else{
//                            noPhotos = false;
//                        }
//                    }else{
//                        adapter.notifyDataSetChanged();
//                    }
//
//
//
//                }else{
//                    Toast.makeText(getActivity(), "Internet issue, try again", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }


    private static byte[] CompressPhoto(Uri fileUri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

    private void availableButtons(Boolean bool){
        addAdvertActivity.createButton.setEnabled(bool);
        addAdvertActivity.createButton.setTextColor(Color.rgb(255,255,255));
        addAdvertActivity.reopenButton.setEnabled(bool);
        addAdvertActivity.reopenButton.setTextColor(Color.rgb(255,255,255));
        addAdvertActivity.saveChangesButton.setEnabled(bool);
        addAdvertActivity.saveChangesButton.setTextColor(Color.rgb(255,255,255));
    }

    private void saveImageList(String fileChildPath){
        Map<String, Object> data = new HashMap<>();
        data.put("fileChildPath", fileChildPath);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("IMAGE_LINKS").add(data);
    }


}

























//package c.motor.motor.addAdvertActivity;
//
//
//        import android.annotation.SuppressLint;
//        import android.app.ProgressDialog;
//        import android.content.DialogInterface;
//        import android.content.Intent;
//        import android.database.Cursor;
//        import android.graphics.Bitmap;
//        import android.graphics.BitmapFactory;
//        import android.graphics.Color;
//        import android.graphics.drawable.BitmapDrawable;
//        import android.net.Uri;
//        import android.os.Bundle;
//
//        import androidx.annotation.NonNull;
//        import androidx.appcompat.app.AlertDialog;
//        import androidx.fragment.app.Fragment;
//
//        import android.provider.MediaStore;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.ImageButton;
//        import android.widget.ImageView;
//        import android.widget.LinearLayout;
//        import android.widget.ProgressBar;
//        import android.widget.Toast;
//
//        import com.bumptech.glide.Glide;
//        import com.bumptech.glide.load.engine.DiskCacheStrategy;
//        import com.google.android.gms.tasks.Continuation;
//        import com.google.android.gms.tasks.OnCanceledListener;
//        import com.google.android.gms.tasks.OnCompleteListener;
//        import com.google.android.gms.tasks.OnFailureListener;
//        import com.google.android.gms.tasks.OnSuccessListener;
//        import com.google.android.gms.tasks.Task;
//        import com.google.firebase.auth.FirebaseAuth;
//        import com.google.firebase.firestore.FirebaseFirestore;
//        import com.google.firebase.storage.FirebaseStorage;
//        import com.google.firebase.storage.StorageReference;
//        import com.google.firebase.storage.UploadTask;
//        import com.theartofdev.edmodo.cropper.CropImage;
//        import com.theartofdev.edmodo.cropper.CropImageView;
//
//        import java.io.ByteArrayOutputStream;
//        import java.io.File;
//        import java.util.ArrayList;
//        import java.util.HashMap;
//        import java.util.Map;
//
//        import c.motor.motor.R;
//        import c.motor.motor.helpers.Helpers;
//
//        import static android.app.Activity.RESULT_OK;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class PhotoWidgetFragment extends Fragment {
//
////    CropImageView
////            mainImageView;
//
//    ImageView
//            mainImageView,
//            additionalImageView1,
//            additionalImageView2,
//            additionalImageView3;
//
//    ImageButton
//            deleteMainPhoto,
//            deleteAdditionalPhoto1,
//            deleteAdditionalPhoto2,
//            deleteAdditionalPhoto3;
//
//    ProgressBar
//            mainProgressBar,
//            additionalProgressBar1,
//            additionalProgressBar2,
//            additionalProgressBar3;
//
//    ImageButton
//            repeatPhotoMain,
//            repeatPhotoAdditional1,
//            repeatPhotoAdditional2,
//            repeatPhotoAdditional3;
//
//    Boolean
//            cropMainActiv = false,
//            crop1Activ = false,
//            crop2Activ = false,
//            crop3Activ = false;
//
//
//    LinearLayout
//            firstButton,
//            photosLayout;
//
//    private static final int Gallery_Pick_Main = 0;
//    private static final int Gallery_Pick1 = 1;
//    private static final int Gallery_Pick2 = 2;
//    private static final int Gallery_Pick3 = 3;
//
//    public String
//            downloadPhotoMain,
//            downloadPhotoAdditional1,
//            downloadPhotoAdditional2,
//            downloadPhotoAdditional3;
//    public String
//            storageRefMainPhoto,
//            storageRefAdditional1,
//            storageRefAdditional2,
//            storageRefAdditional3;
//
//
//
//    Boolean isEditing = false;
//
//    AddAdvertActivity addAdvertActivity;
//
//    private FirebaseStorage firebaseStorage;
//    private StorageReference storageReference;
//
//    //system
//    private String stringDownloadUri;
//
//    private ProgressDialog loadingBar;
//
//
//
//
//    public PhotoWidgetFragment() {
//        // Required empty public constructor
//        isEditing = false;
//    }
//
//    public PhotoWidgetFragment(String downloadPhotoMain,
//                               String downloadPhotoAdditional1,
//                               String downloadPhotoAdditional2,
//                               String downloadPhotoAdditional3,
//                               String storageRefMainPhoto,
//                               String storageRefAdditional1,
//                               String storageRefAdditional2,
//                               String storageRefAdditional3) {
//        this.downloadPhotoMain = downloadPhotoMain;
//        this.downloadPhotoAdditional1 = downloadPhotoAdditional1;
//        this.downloadPhotoAdditional2 = downloadPhotoAdditional2;
//        this.downloadPhotoAdditional3 = downloadPhotoAdditional3;
//
//        this.storageRefMainPhoto = storageRefMainPhoto;
//        this.storageRefAdditional1 = storageRefAdditional1;
//        this.storageRefAdditional2 = storageRefAdditional2;
//        this.storageRefAdditional3 = storageRefAdditional3;
//        isEditing = true;
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_photo_widget, container, false);
//        firstSetup(view);
//
//        addAdvertActivity = (AddAdvertActivity)getActivity();
//        System.out.println("storageRefMainPhoto = " + storageRefMainPhoto);
//        System.out.println("storageRefMainPhoto = " + downloadPhotoMain);
//
//        if(storageRefMainPhoto != null) {
//            System.out.println("storageRefMainPhoto is not null");
//        }
//
//        return view;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri resultUri = result.getUri();
//                if(cropMainActiv){
//                    cropMainActiv = false;
//                    mainImageView.setImageURI(resultUri);
//                    deleteMainPhoto.setVisibility(View.VISIBLE);
//                    addAdvertActivity.isPhotoChanged = true;
//                    addAdvertActivity.isMainPhotoChanged = true;
//
//                    loadPhoto(mainImageView, deleteMainPhoto, mainProgressBar,repeatPhotoMain, Gallery_Pick_Main);
//                }
//                if(crop1Activ){
//                    crop1Activ = false;
//                    additionalImageView1.setImageURI(resultUri);
//                    deleteAdditionalPhoto1.setVisibility(View.VISIBLE);
//                    addAdvertActivity.isPhotoChanged = true;
//
//                    loadPhoto(additionalImageView1, deleteAdditionalPhoto1, additionalProgressBar1,repeatPhotoAdditional1, Gallery_Pick1);
//                }
//                if(crop2Activ){
//                    crop2Activ = false;
//                    additionalImageView2.setImageURI(resultUri);
//                    deleteAdditionalPhoto2.setVisibility(View.VISIBLE);
//                    addAdvertActivity.isPhotoChanged = true;
//
//                    loadPhoto(additionalImageView2, deleteAdditionalPhoto2, additionalProgressBar2, repeatPhotoAdditional2, Gallery_Pick2);
//                }
//                if(crop3Activ){
//                    crop3Activ = false;
//                    additionalImageView3.setImageURI(resultUri);
//                    deleteAdditionalPhoto3.setVisibility(View.VISIBLE);
//                    addAdvertActivity.isPhotoChanged = true;
//
//                    loadPhoto(additionalImageView3, deleteAdditionalPhoto3, additionalProgressBar3,repeatPhotoAdditional3, Gallery_Pick3);
//                }
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//    }
//
//    private void onChooseFile(View v, int code){
//        CropImage.activity()
//                .start(getContext(), this);
//        if (code == Gallery_Pick_Main){
//            cropMainActiv = true;
//        }
//        if(code == Gallery_Pick1){
//            crop1Activ = true;
//        }
//        if(code == Gallery_Pick2){
//            crop2Activ = true;
//        }
//        if(code == Gallery_Pick3){
//            crop3Activ = true;
//        }
//    }
//
//    private void firstSetup(View view) {
//        firebaseStorage = FirebaseStorage.getInstance();
//        storageReference = firebaseStorage.getReference().child("ADVERTS_IMAGES_TEST_FOLDER3");
//
//        loadingBar = new ProgressDialog(getActivity());
//
//        mainImageView = view.findViewById(R.id.photoFragment_photo_main);
//        additionalImageView1 = view.findViewById(R.id.photoFragment_additional_photo1);
//        additionalImageView2 = view.findViewById(R.id.photoFragment_additional_photo2);
//        additionalImageView3 = view.findViewById(R.id.photoFragment_additional_photo3);
//
//        deleteMainPhoto = view.findViewById(R.id.photoFragment_delete_photo);
//        deleteAdditionalPhoto1 = view.findViewById(R.id.add_advert_delete_additional_photo1);
//        deleteAdditionalPhoto2 = view.findViewById(R.id.add_advert_delete_additional_photo2);
//        deleteAdditionalPhoto3 = view.findViewById(R.id.add_advert_delete_additional_photo3);
//
//        mainProgressBar = view.findViewById(R.id.photoFragment_delete_photo_progressBar);
//        additionalProgressBar1 = view.findViewById(R.id.photoFragment_progressBar1);
//        additionalProgressBar2 = view.findViewById(R.id.photoFragment_progressBar2);
//        additionalProgressBar3 = view.findViewById(R.id.photoFragment_progressBar3);
//
//        repeatPhotoMain = view.findViewById(R.id.photoFragment_repeat_mainPhoto);
//        repeatPhotoAdditional1 = view.findViewById(R.id.photoFragment_repeat_additional_photo1);
//        repeatPhotoAdditional2 = view.findViewById(R.id.photoFragment_repeat_additional_photo2);
//        repeatPhotoAdditional3 = view.findViewById(R.id.photoFragment_repeat_additional_photo3);
//
//        firstButton = view.findViewById(R.id.photoFragment_add_first_img);
//        photosLayout = view.findViewById(R.id.photoFragment_photos_layout);
//
//        firstButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firstButton.setVisibility(View.GONE);
//                photosLayout.setVisibility(View.VISIBLE);
//                onChooseFile(v, Gallery_Pick_Main);
//            }
//        });
//
//        if(downloadPhotoMain != null) {
//            firstButton.setVisibility(View.GONE);
//            photosLayout.setVisibility(View.VISIBLE);
//
//            Glide.with(getContext())
//                    .load(downloadPhotoMain)
//                    .error(R.mipmap.empty_image)
//                    .thumbnail(0.01f)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    //.apply(requestOptions)
//                    .into(mainImageView);
//            deleteMainPhoto.setVisibility(View.VISIBLE);
//            if(downloadPhotoAdditional1 != null){
//                Glide.with(getContext())
//                        .load(downloadPhotoAdditional1)
//                        .error(R.mipmap.empty_image)
//                        .thumbnail(0.01f)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        //.apply(requestOptions)
//                        .into(additionalImageView1);
//                deleteAdditionalPhoto1.setVisibility(View.VISIBLE);
//            }
//            if(downloadPhotoAdditional2 != null){
//                Glide.with(getContext())
//                        .load(downloadPhotoAdditional2)
//                        .error(R.mipmap.empty_image)
//                        .thumbnail(0.01f)
//                        //.diskCacheStrategy(DiskCacheStrategy.NONE)
//                        //.apply(requestOptions)
//                        .into(additionalImageView2);
//                deleteAdditionalPhoto2.setVisibility(View.VISIBLE);
//            }
//            if(downloadPhotoAdditional3 != null){
//                Glide.with(getContext())
//                        .load(downloadPhotoAdditional3)
//                        .error(R.mipmap.empty_image)
//                        .thumbnail(0.01f)
//                        //.diskCacheStrategy(DiskCacheStrategy.NONE)
//                        //.apply(requestOptions)
//                        .into(additionalImageView3);
//                deleteAdditionalPhoto3.setVisibility(View.VISIBLE);
//            }
//        }
//
//        mainImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onChooseFile(v, Gallery_Pick_Main);
//            }
//        });
//
//        additionalImageView1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onChooseFile(v, Gallery_Pick1);
//            }
//        });
//
//        additionalImageView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onChooseFile(v, Gallery_Pick2);
//            }
//        });
//
//        additionalImageView3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onChooseFile(v, Gallery_Pick3);
//            }
//        });
//
//        deleteMainPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//
//                builder.setTitle("Confirm");
//                builder.setMessage("Are you sure, that want delete all photos?");
//
//                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        mainImageView.setImageURI(null);
//                        mainImageView.setImageResource(R.drawable.ic_add_a_photo_blue);
//                        deletePhoto(storageRefMainPhoto, 0);
//                        deletePhoto(storageRefAdditional1, 1);
//                        deletePhoto(storageRefAdditional2, 2);
//                        deletePhoto(storageRefAdditional3, 3);
//                        firstButton.setVisibility(View.VISIBLE);
//                        photosLayout.setVisibility(View.GONE);
//                        addAdvertActivity.isPhotoChanged = true;
//                        addAdvertActivity.isMainPhotoChanged = true;
//
//                        dialog.dismiss();
//                    }
//                });
//
//                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        // Do nothing
//                        dialog.dismiss();
//                    }
//                });
//
//                AlertDialog alert = builder.create();
//                alert.show();
//
//            }
//        });
//
//        deleteAdditionalPhoto1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePhoto(storageRefAdditional1, 1);
//                addAdvertActivity.isPhotoChanged = true;
//            }
//        });
//
//        deleteAdditionalPhoto2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePhoto(storageRefAdditional2, 2);
//                addAdvertActivity.isPhotoChanged = true;
//            }
//        });
//
//        deleteAdditionalPhoto3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePhoto(storageRefAdditional3, 3);
//                addAdvertActivity.isPhotoChanged = true;
//            }
//        });
//
//    }
//
//
//    @SuppressLint("ResourceAsColor")
//    private void loadPhoto(
//            final ImageView photoView,
//            final ImageButton deleteButton,
//            final ProgressBar progressBar,
//            final ImageButton repeatLoad,
//            final int id
//    ){
//        loadingBar.setTitle("loading....");
//        loadingBar.setMessage("Please wait, while we are loaded your photo");
//        loadingBar.show();
//        loadingBar.setCanceledOnTouchOutside(false);
//
//        photoView.setEnabled(false);
//        addAdvertActivity.createButton.setEnabled(false);
//        addAdvertActivity.createButton.setTextColor(R.color.colorGray);
//        addAdvertActivity.reopenButton.setEnabled(false);
//        addAdvertActivity.reopenButton.setTextColor(R.color.colorGray);
//        addAdvertActivity.saveChangesButton.setEnabled(false);
//        addAdvertActivity.saveChangesButton.setTextColor(R.color.colorGray);
//        progressBar.setVisibility(View.VISIBLE);
//        deleteButton.setVisibility(View.GONE);
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        final String fileChildPath = userId + "_" + Helpers.getRandomString();
//        final StorageReference filePath = storageReference.child(fileChildPath);
//
//        byte[] compressData = CompressPhoto(photoView);
//
//        final UploadTask uploadTask = filePath.putBytes(compressData);
//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
//                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                        if(!task.isSuccessful()){
//                            throw task .getException();
//                        }
//                        stringDownloadUri = filePath.getDownloadUrl().toString();
//                        System.out.println("вторая линия" + stringDownloadUri);
//                        return filePath.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if(task.isSuccessful()){
//
//                            Map<String, Object> data = new HashMap<>();
//                            data.put("fileChildPath", fileChildPath);
//                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//                            firebaseFirestore.collection("IMAGE_LINKS").add(data);
//
//                            addAdvertActivity.createButton.setEnabled(true);
//                            addAdvertActivity.createButton.setTextColor(Color.rgb(255,255,255));
//                            addAdvertActivity.reopenButton.setEnabled(true);
//                            addAdvertActivity.reopenButton.setTextColor(Color.rgb(255,255,255));
//                            addAdvertActivity.saveChangesButton.setEnabled(true);
//                            addAdvertActivity.saveChangesButton.setTextColor(Color.rgb(255,255,255));
//                            loadingBar.cancel();
//                            progressBar.setVisibility(View.GONE);
//                            deleteButton.setVisibility(View.VISIBLE);
//                            if(id == Gallery_Pick_Main) {
//                                if(downloadPhotoMain != null || storageRefMainPhoto != null) {
//                                    deletePhoto(storageRefMainPhoto, 0);
//                                }
//                                storageRefMainPhoto = fileChildPath;
//                                downloadPhotoMain = task.getResult().toString();
//                                //
//                                addAdvertActivity.imgMain = fileChildPath;
//                                addAdvertActivity.downloadUrlMain = task.getResult().toString();
//                                //
//                            }else if(id == Gallery_Pick1){
//                                if(downloadPhotoAdditional1 != null || storageRefAdditional1 != null){
//                                    deletePhoto(storageRefAdditional1, 1);
//                                }
//                                storageRefAdditional1 = fileChildPath;
//                                downloadPhotoAdditional1 = task.getResult().toString();
//                                //
//                                addAdvertActivity.imgUri1 = fileChildPath;
//                                addAdvertActivity.downloadUrl1 = task.getResult().toString();
//                                //
//                                deleteAdditionalPhoto1.setVisibility(View.VISIBLE);
//
//                            }else if(id == Gallery_Pick2){
//                                if(downloadPhotoAdditional2 != null || storageRefAdditional2 != null){
//                                    deletePhoto(storageRefAdditional2, 2);
//                                }
//                                storageRefAdditional2 = fileChildPath;
//                                downloadPhotoAdditional2 = task.getResult().toString();
//                                //
//                                addAdvertActivity.imgUri2 = fileChildPath;
//                                addAdvertActivity.downloadUrl2 = task.getResult().toString();
//                            }else if(id == Gallery_Pick3){
//                                if(downloadPhotoAdditional3 != null || storageRefAdditional3 != null){
//                                    deletePhoto(storageRefAdditional3, 3 );
//                                }
//                                storageRefAdditional3 = fileChildPath;
//                                downloadPhotoAdditional3 = task.getResult().toString();
//                                //
//                                addAdvertActivity.imgUri3 = fileChildPath;
//                                addAdvertActivity.downloadUrl3 = task.getResult().toString();
//                            }
//                            photoView.setEnabled(true);
//                        }else{
//                            addAdvertActivity.createButton.setEnabled(true);
//                            addAdvertActivity.createButton.setTextColor(Color.rgb(255,255,255));
//                            addAdvertActivity.reopenButton.setEnabled(true);
//                            addAdvertActivity.reopenButton.setTextColor(Color.rgb(255,255,255));
//                            addAdvertActivity.saveChangesButton.setEnabled(true);
//                            addAdvertActivity.saveChangesButton.setTextColor(Color.rgb(255,255,255));
//                            progressBar.setVisibility(View.GONE);
//                            deleteButton.setVisibility(View.VISIBLE);
//                            photoView.setEnabled(true);
//                            loadingBar.cancel();
//                        }
//                    }
//                });
//            }
//        });
//        uploadTask.addOnCanceledListener(new OnCanceledListener() {
//            @Override
//            public void onCanceled() {
//                Toast.makeText(getActivity(), "Internet issue try again", Toast.LENGTH_SHORT).show();
//                repeatLoad.setVisibility(View.VISIBLE);
//                repeatLoad.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        repeatLoad.setVisibility(View.GONE);
//                        loadPhoto(photoView,deleteButton,progressBar,repeatLoad,id);
//                    }
//                });
//            }
//        });
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getActivity(), "Internet issue try again", Toast.LENGTH_SHORT).show();
//                repeatLoad.setVisibility(View.VISIBLE);
//                repeatLoad.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        repeatLoad.setVisibility(View.GONE);
//                        loadPhoto(photoView,deleteButton,progressBar,repeatLoad,id);
//                    }
//                });
//            }
//        });
//
//    }
//
////    private static byte[] CompressPhoto(Uri localUri){
////
////        //String imageFile = new File(getRealPathFromURI(localUri));
////        System.out.println();
////        Bitmap bmp = BitmapFactory.decodeFile(localUri.toString());
////        ByteArrayOutputStream bos = new ByteArrayOutputStream();
////        bmp.compress(Bitmap.CompressFormat.JPEG, 20, bos);
////        byte[] data = bos.toByteArray();
////
////        return data;
////    }
//
//    private static byte[] CompressPhoto(ImageView photoView){
//        photoView.setDrawingCacheEnabled(true);
//        photoView.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//        return data;
//    }
//
//
//    private void deletePhoto(String childPath ,int id){
//
//        if(childPath != null) {
//            if(!childPath.isEmpty()) {
//                final StorageReference filePath = storageReference.child(childPath);
//                filePath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(getActivity(), "Image deleted", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Uh-oh, an error occurred!
//                    }
//                });
//            }
//        }
//
//        if(id == Gallery_Pick_Main ) {
//            storageRefMainPhoto = null;
//            downloadPhotoMain = null;
//            addAdvertActivity.setDownloadUrlMain(null);
//            addAdvertActivity.setImgMain(null);
//            mainImageView.setImageResource(R.drawable.ic_add_a_photo_blue);
//        }else if(id == Gallery_Pick1){
//            storageRefAdditional1 = null;
//            downloadPhotoAdditional1 = null;
//            addAdvertActivity.setDownloadUrl1(null);
//            addAdvertActivity.setImgUri1(null);
//            additionalImageView1.setImageResource(R.drawable.ic_add_a_photo_blue);
//            deleteAdditionalPhoto1.setVisibility(View.GONE);
//        }else if(id == Gallery_Pick2){
//            storageRefAdditional2 = null;
//            downloadPhotoAdditional2 = null;
//            addAdvertActivity.setDownloadUrl2(null);
//            addAdvertActivity.setImgUri2(null);
//            additionalImageView2.setImageResource(R.drawable.ic_add_a_photo_blue);
//            deleteAdditionalPhoto2.setVisibility(View.GONE);
//        }else if(id == Gallery_Pick3){
//            storageRefAdditional3 = null;
//            downloadPhotoAdditional3 = null;
//            addAdvertActivity.setDownloadUrl3(null);
//            addAdvertActivity.setImgUri3(null);
//            additionalImageView3.setImageResource(R.drawable.ic_add_a_photo_blue);
//            deleteAdditionalPhoto3.setVisibility(View.GONE);
//        }
//    }
//
//}



















//package c.motor.motor.addAdvertActivity;
//
//
//        import android.annotation.SuppressLint;
//        import android.content.DialogInterface;
//        import android.content.Intent;
//        import android.graphics.Bitmap;
//        import android.graphics.Color;
//        import android.graphics.drawable.BitmapDrawable;
//        import android.net.Uri;
//        import android.os.Bundle;
//
//        import androidx.annotation.NonNull;
//        import androidx.appcompat.app.AlertDialog;
//        import androidx.fragment.app.Fragment;
//
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.ImageButton;
//        import android.widget.ImageView;
//        import android.widget.LinearLayout;
//        import android.widget.ProgressBar;
//        import android.widget.Toast;
//
//        import com.bumptech.glide.Glide;
//        import com.bumptech.glide.load.engine.DiskCacheStrategy;
//        import com.google.android.gms.tasks.Continuation;
//        import com.google.android.gms.tasks.OnCanceledListener;
//        import com.google.android.gms.tasks.OnCompleteListener;
//        import com.google.android.gms.tasks.OnFailureListener;
//        import com.google.android.gms.tasks.OnSuccessListener;
//        import com.google.android.gms.tasks.Task;
//        import com.google.firebase.auth.FirebaseAuth;
//        import com.google.firebase.storage.FirebaseStorage;
//        import com.google.firebase.storage.StorageReference;
//        import com.google.firebase.storage.UploadTask;
//
//        import java.io.ByteArrayOutputStream;
//
//        import c.motor.motor.R;
//        import c.motor.motor.helpers.Helpers;
//
//        import static android.app.Activity.RESULT_OK;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class PhotoWidgetFragment extends Fragment {
//
//    ImageView
//            mainImageView,
//            additionalImageView1,
//            additionalImageView2,
//            additionalImageView3;
//
//    ImageButton
//            deleteMainPhoto,
//            deleteAdditionalPhoto1,
//            deleteAdditionalPhoto2,
//            deleteAdditionalPhoto3;
//
//    ProgressBar
//            mainProgressBar,
//            additionalProgressBar1,
//            additionalProgressBar2,
//            additionalProgressBar3;
//
//    ImageButton
//            repeatPhotoMain,
//            repeatPhotoAdditional1,
//            repeatPhotoAdditional2,
//            repeatPhotoAdditional3;
//
//
//    LinearLayout
//            firstButton,
//            photosLayout;
//
//    private static final int Gallery_Pick_Main = 0;
//    private static final int Gallery_Pick1 = 1;
//    private static final int Gallery_Pick2 = 2;
//    private static final int Gallery_Pick3 = 3;
//
//    public String
//            downloadPhotoMain,
//            downloadPhotoAdditional1,
//            downloadPhotoAdditional2,
//            downloadPhotoAdditional3;
//    public String
//            storageRefMainPhoto,
//            storageRefAdditional1,
//            storageRefAdditional2,
//            storageRefAdditional3;
//
//    Boolean isEditing = false;
//
//    AddAdvertActivity addAdvertActivity;
//
//    private FirebaseStorage firebaseStorage;
//    private StorageReference storageReference;
//
//    //system
//    private String stringDownloadUri;
//
//
//
//
//    public PhotoWidgetFragment() {
//        // Required empty public constructor
//        isEditing = false;
//    }
//
//    public PhotoWidgetFragment(String downloadPhotoMain,
//                               String downloadPhotoAdditional1,
//                               String downloadPhotoAdditional2,
//                               String downloadPhotoAdditional3,
//                               String storageRefMainPhoto,
//                               String storageRefAdditional1,
//                               String storageRefAdditional2,
//                               String storageRefAdditional3) {
//        this.downloadPhotoMain = downloadPhotoMain;
//        this.downloadPhotoAdditional1 = downloadPhotoAdditional1;
//        this.downloadPhotoAdditional2 = downloadPhotoAdditional2;
//        this.downloadPhotoAdditional3 = downloadPhotoAdditional3;
//
//        this.storageRefMainPhoto = storageRefMainPhoto;
//        this.storageRefAdditional1 = storageRefAdditional1;
//        this.storageRefAdditional2 = storageRefAdditional2;
//        this.storageRefAdditional3 = storageRefAdditional3;
//        isEditing = true;
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_photo_widget, container, false);
//        firstSetup(view);
//
//        addAdvertActivity = (AddAdvertActivity)getActivity();
//        System.out.println("storageRefMainPhoto = " + storageRefMainPhoto);
//        System.out.println("storageRefMainPhoto = " + downloadPhotoMain);
//
//        if(storageRefMainPhoto != null) {
//            System.out.println("storageRefMainPhoto is not null");
//        }
//
////
////
////        getActivity().
//        return view;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Uri localUri;
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == Gallery_Pick_Main && resultCode==RESULT_OK && data != null){
//            localUri = data.getData();
//            mainImageView.setImageURI(localUri);
//
//            loadPhoto(mainImageView, deleteMainPhoto, mainProgressBar,repeatPhotoMain, Gallery_Pick_Main);
//
//            deleteMainPhoto.setVisibility(View.VISIBLE);
//            deleteMainPhoto.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //setupDeleteButton(deleteMainPhoto);
//
//                    deletePhoto(storageRefMainPhoto, 0);
//                    deletePhoto(storageRefAdditional1, 1);
//                    deletePhoto(storageRefAdditional2, 2);
//                    deletePhoto(storageRefAdditional3, 3);
//
//                    firstButton.setVisibility(View.VISIBLE);
//                    photosLayout.setVisibility(View.GONE);
//
//                    deleteMainPhoto.setVisibility(View.GONE);
//                    deleteAdditionalPhoto1.setVisibility(View.GONE);
//                    deleteAdditionalPhoto2.setVisibility(View.GONE);
//                    deleteAdditionalPhoto3.setVisibility(View.GONE);
//                }
//            });
//        }
//        if(requestCode == Gallery_Pick1 && resultCode==RESULT_OK && data != null){
//            localUri = data.getData();
//            additionalImageView1.setImageURI(localUri);
//
//            loadPhoto(additionalImageView1, deleteAdditionalPhoto1, additionalProgressBar1,repeatPhotoAdditional1, Gallery_Pick1);
//
//            deleteAdditionalPhoto1.setVisibility(View.VISIBLE);
//            deleteAdditionalPhoto1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //setupDeleteButton(deleteAdditionalPhoto1);
//                    deletePhoto(storageRefAdditional1, 1);
//                    deleteAdditionalPhoto1.setVisibility(View.GONE);
//                }
//            });
//        }
//        if(requestCode == Gallery_Pick2 && resultCode==RESULT_OK && data != null){
//            localUri = data.getData();
//            additionalImageView2.setImageURI(localUri);
//
//            loadPhoto(additionalImageView2, deleteAdditionalPhoto2, additionalProgressBar2,repeatPhotoAdditional2, Gallery_Pick2);
//
//            deleteAdditionalPhoto2.setVisibility(View.VISIBLE);
//            deleteAdditionalPhoto2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //setupDeleteButton(deleteAdditionalPhoto2);
//                    deletePhoto(storageRefAdditional2, 2);
//                    deleteAdditionalPhoto2.setVisibility(View.GONE);
//                }
//            });
//        }
//        if(requestCode == Gallery_Pick3 && resultCode==RESULT_OK && data != null){
//            localUri = data.getData();
//            additionalImageView3.setImageURI(localUri);
//
//            loadPhoto(additionalImageView3, deleteAdditionalPhoto3, additionalProgressBar3,repeatPhotoAdditional3, Gallery_Pick3);
//
//            deleteAdditionalPhoto3.setVisibility(View.VISIBLE);
//            deleteAdditionalPhoto3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //setupDeleteButton(deleteAdditionalPhoto3);
//                    deletePhoto(storageRefAdditional3, 3);
//                    deleteAdditionalPhoto3.setVisibility(View.GONE);
//                }
//            });
//        }
//    }
//
//    private void firstSetup(View view) {
//        firebaseStorage = FirebaseStorage.getInstance();
//        storageReference = firebaseStorage.getReference().child("ADVERTS_IMAGES_TEST_FOLDER3");
//
//        mainImageView = view.findViewById(R.id.photoFragment_photo_main);
//        additionalImageView1 = view.findViewById(R.id.photoFragment_additional_photo1);
//        additionalImageView2 = view.findViewById(R.id.photoFragment_additional_photo2);
//        additionalImageView3 = view.findViewById(R.id.photoFragment_additional_photo3);
//
//        deleteMainPhoto = view.findViewById(R.id.photoFragment_delete_photo);
//        deleteAdditionalPhoto1 = view.findViewById(R.id.add_advert_delete_additional_photo1);
//        deleteAdditionalPhoto2 = view.findViewById(R.id.add_advert_delete_additional_photo2);
//        deleteAdditionalPhoto3 = view.findViewById(R.id.add_advert_delete_additional_photo3);
//
//        mainProgressBar = view.findViewById(R.id.photoFragment_delete_photo_progressBar);
//        additionalProgressBar1 = view.findViewById(R.id.photoFragment_progressBar1);
//        additionalProgressBar2 = view.findViewById(R.id.photoFragment_progressBar2);
//        additionalProgressBar3 = view.findViewById(R.id.photoFragment_progressBar3);
//
//        repeatPhotoMain = view.findViewById(R.id.photoFragment_repeat_mainPhoto);
//        repeatPhotoAdditional1 = view.findViewById(R.id.photoFragment_repeat_additional_photo1);
//        repeatPhotoAdditional2 = view.findViewById(R.id.photoFragment_repeat_additional_photo2);
//        repeatPhotoAdditional3 = view.findViewById(R.id.photoFragment_repeat_additional_photo3);
//
//        firstButton = view.findViewById(R.id.photoFragment_add_first_img);
//        photosLayout = view.findViewById(R.id.photoFragment_photos_layout);
//
//        firstButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenGallery(Gallery_Pick_Main);
//                firstButton.setVisibility(View.GONE);
//                photosLayout.setVisibility(View.VISIBLE);
//            }
//        });
//
//        if(downloadPhotoMain != null) {
//            firstButton.setVisibility(View.GONE);
//            photosLayout.setVisibility(View.VISIBLE);
//
//            Glide.with(getContext())
//                    .load(downloadPhotoMain)
//                    .error(R.mipmap.empty_image)
//                    .thumbnail(0.01f)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    //.apply(requestOptions)
//                    .into(mainImageView);
//            deleteMainPhoto.setVisibility(View.VISIBLE);
//            if(downloadPhotoAdditional1 != null){
//                Glide.with(getContext())
//                        .load(downloadPhotoAdditional1)
//                        .error(R.mipmap.empty_image)
//                        .thumbnail(0.01f)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        //.apply(requestOptions)
//                        .into(additionalImageView1);
//                deleteAdditionalPhoto1.setVisibility(View.VISIBLE);
//            }
//            if(downloadPhotoAdditional2 != null){
//                Glide.with(getContext())
//                        .load(downloadPhotoAdditional2)
//                        .error(R.mipmap.empty_image)
//                        .thumbnail(0.01f)
//                        //.diskCacheStrategy(DiskCacheStrategy.NONE)
//                        //.apply(requestOptions)
//                        .into(additionalImageView2);
//                deleteAdditionalPhoto2.setVisibility(View.VISIBLE);
//            }
//            if(downloadPhotoAdditional3 != null){
//                Glide.with(getContext())
//                        .load(downloadPhotoAdditional3)
//                        .error(R.mipmap.empty_image)
//                        .thumbnail(0.01f)
//                        //.diskCacheStrategy(DiskCacheStrategy.NONE)
//                        //.apply(requestOptions)
//                        .into(additionalImageView3);
//                deleteAdditionalPhoto3.setVisibility(View.VISIBLE);
//            }
//        }
//
//        mainImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenGallery(Gallery_Pick_Main);
//            }
//        });
//
//        additionalImageView1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenGallery(Gallery_Pick1);
//            }
//        });
//
//        additionalImageView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenGallery(Gallery_Pick2);
//            }
//        });
//
//        additionalImageView3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenGallery(Gallery_Pick3);
//            }
//        });
//
//        deleteMainPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mainImageView.setImageURI(null);
//                mainImageView.setImageResource(R.drawable.ic_add_a_photo_blue);
//                deletePhoto(storageRefMainPhoto, 0);
//                deletePhoto(storageRefAdditional1, 1);
//                deletePhoto(storageRefAdditional2, 2);
//                deletePhoto(storageRefAdditional3, 3);
//                firstButton.setVisibility(View.VISIBLE);
//                photosLayout.setVisibility(View.GONE);
//            }
//        });
//
//        deleteAdditionalPhoto1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePhoto(storageRefAdditional1, 1);
//            }
//        });
//
//        deleteAdditionalPhoto2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePhoto(storageRefAdditional2, 2);
//            }
//        });
//
//        deleteAdditionalPhoto3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePhoto(storageRefAdditional3, 3);
//            }
//        });
//
//    }
//
//
//    private void OpenGallery(int res) {
//        Intent galleryIntent = new Intent();
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent,res);
//    }
//
//    @SuppressLint("ResourceAsColor")
//    private void loadPhoto(
//            final ImageView photoView,
//            final ImageButton deleteButton,
//            final ProgressBar progressBar,
//            final ImageButton repeatLoad,
//            final int id
//    ){
//        photoView.setEnabled(false);
//        addAdvertActivity.createButton.setEnabled(false);
//        addAdvertActivity.createButton.setTextColor(R.color.colorGray);
//        addAdvertActivity.reopenButton.setEnabled(false);
//        addAdvertActivity.reopenButton.setTextColor(R.color.colorGray);
//        addAdvertActivity.saveChangesButton.setEnabled(true);
//        addAdvertActivity.saveChangesButton.setTextColor(R.color.colorGray);
//        progressBar.setVisibility(View.VISIBLE);
//        deleteButton.setVisibility(View.GONE);
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        final String fileChildPath = userId + "_" + Helpers.getRandomString();
//        final StorageReference filePath = storageReference.child(fileChildPath);
//
//        byte[] compressData = CompressPhoto(photoView);
//
//        final UploadTask uploadTask = filePath.putBytes(compressData);
//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
//                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                        if(!task.isSuccessful()){
//                            throw task .getException();
//                        }
//                        stringDownloadUri = filePath.getDownloadUrl().toString();
//                        System.out.println("вторая линия" + stringDownloadUri);
//                        return filePath.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if(task.isSuccessful()){
//                            addAdvertActivity.createButton.setEnabled(true);
//                            addAdvertActivity.createButton.setTextColor(Color.rgb(255,255,255));
//                            addAdvertActivity.reopenButton.setEnabled(true);
//                            addAdvertActivity.reopenButton.setTextColor(Color.rgb(255,255,255));
//                            addAdvertActivity.saveChangesButton.setEnabled(true);
//                            addAdvertActivity.saveChangesButton.setTextColor(Color.rgb(255,255,255));
//                            progressBar.setVisibility(View.GONE);
//                            deleteButton.setVisibility(View.VISIBLE);
//                            if(id == Gallery_Pick_Main) {
//                                if(downloadPhotoMain != null || storageRefMainPhoto != null) {
//                                    deletePhoto(storageRefMainPhoto, 0);
//                                }
//                                storageRefMainPhoto = fileChildPath;
//                                downloadPhotoMain = task.getResult().toString();
//                                //
//                                addAdvertActivity.imgMain = fileChildPath;
//                                addAdvertActivity.downloadUrlMain = task.getResult().toString();
//                                //
//                                Glide.with(getContext())
//                                        .load(downloadPhotoMain)
//                                        .error(R.mipmap.empty_image)
//                                        .thumbnail(0.01f)
//                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                        //.apply(requestOptions)
//                                        .into(mainImageView);
//                            }else if(id == Gallery_Pick1){
//                                if(downloadPhotoAdditional1 != null || storageRefAdditional1 != null){
//                                    deletePhoto(storageRefAdditional1, 1);
//                                }
//                                storageRefAdditional1 = fileChildPath;
//                                downloadPhotoAdditional1 = task.getResult().toString();
//                                //
//                                addAdvertActivity.imgUri1 = fileChildPath;
//                                addAdvertActivity.downloadUrl1 = task.getResult().toString();
//                                //
//                                Glide.with(getContext())
//                                        .load(downloadPhotoAdditional1)
//                                        .error(R.mipmap.empty_image)
//                                        .thumbnail(0.01f)
//                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                        //.apply(requestOptions)
//                                        .into(additionalImageView1);
//                                deleteAdditionalPhoto1.setVisibility(View.VISIBLE);
//
//                            }else if(id == Gallery_Pick2){
//                                if(downloadPhotoAdditional2 != null || storageRefAdditional2 != null){
//                                    deletePhoto(storageRefAdditional2, 2);
//                                }
//                                storageRefAdditional2 = fileChildPath;
//                                downloadPhotoAdditional2 = task.getResult().toString();
//                                //
//                                addAdvertActivity.imgUri2 = fileChildPath;
//                                addAdvertActivity.downloadUrl2 = task.getResult().toString();
//                            }else if(id == Gallery_Pick3){
//                                if(downloadPhotoAdditional3 != null || storageRefAdditional3 != null){
//                                    deletePhoto(storageRefAdditional3, 3 );
//                                }
//                                storageRefAdditional3 = fileChildPath;
//                                downloadPhotoAdditional3 = task.getResult().toString();
//                                //
//                                addAdvertActivity.imgUri3 = fileChildPath;
//                                addAdvertActivity.downloadUrl3 = task.getResult().toString();
//                            }
//                            photoView.setEnabled(true);
//                        }else{
//                            addAdvertActivity.createButton.setEnabled(true);
//                            addAdvertActivity.createButton.setTextColor(Color.rgb(255,255,255));
//                            addAdvertActivity.reopenButton.setEnabled(true);
//                            addAdvertActivity.reopenButton.setTextColor(Color.rgb(255,255,255));
//                            addAdvertActivity.saveChangesButton.setEnabled(true);
//                            addAdvertActivity.saveChangesButton.setTextColor(Color.rgb(255,255,255));
//                            progressBar.setVisibility(View.GONE);
//                            deleteButton.setVisibility(View.VISIBLE);
//                            photoView.setEnabled(true);
//                        }
//                    }
//                });
//            }
//        });
//        uploadTask.addOnCanceledListener(new OnCanceledListener() {
//            @Override
//            public void onCanceled() {
//                Toast.makeText(getActivity(), "Internet issue try again", Toast.LENGTH_SHORT).show();
//                repeatLoad.setVisibility(View.VISIBLE);
//                repeatLoad.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        repeatLoad.setVisibility(View.GONE);
//                        loadPhoto(photoView,deleteButton,progressBar,repeatLoad,id);
//                    }
//                });
//            }
//        });
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getActivity(), "Internet issue try again", Toast.LENGTH_SHORT).show();
//                repeatLoad.setVisibility(View.VISIBLE);
//                repeatLoad.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        repeatLoad.setVisibility(View.GONE);
//                        loadPhoto(photoView,deleteButton,progressBar,repeatLoad,id);
//                    }
//                });
//            }
//        });
//
//    }
//
//    private static byte[] CompressPhoto(ImageView photoView){
//        photoView.setDrawingCacheEnabled(true);
//        photoView.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//        return data;
//    }
//
//    private void deletePhoto(String childPath ,int id){
//
//        if(childPath != null) {
//            if(!childPath.isEmpty()) {
//                final StorageReference filePath = storageReference.child(childPath);
//                filePath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(getActivity(), "Image deleted", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Uh-oh, an error occurred!
//                    }
//                });
//            }
//        }
//
//        if(id == Gallery_Pick_Main ) {
//            storageRefMainPhoto = null;
//            downloadPhotoMain = null;
//            addAdvertActivity.setDownloadUrlMain(null);
//            addAdvertActivity.setImgMain(null);
//            mainImageView.setImageResource(R.drawable.ic_add_a_photo_blue);
//        }else if(id == Gallery_Pick1){
//            storageRefAdditional1 = null;
//            downloadPhotoAdditional1 = null;
//            addAdvertActivity.setDownloadUrl1(null);
//            addAdvertActivity.setImgUri1(null);
//            additionalImageView1.setImageResource(R.drawable.ic_add_a_photo_blue);
//            deleteAdditionalPhoto1.setVisibility(View.GONE);
//        }else if(id == Gallery_Pick2){
//            storageRefAdditional2 = null;
//            downloadPhotoAdditional2 = null;
//            addAdvertActivity.setDownloadUrl2(null);
//            addAdvertActivity.setImgUri2(null);
//            additionalImageView2.setImageResource(R.drawable.ic_add_a_photo_blue);
//            deleteAdditionalPhoto2.setVisibility(View.GONE);
//        }else if(id == Gallery_Pick3){
//            storageRefAdditional3 = null;
//            downloadPhotoAdditional3 = null;
//            addAdvertActivity.setDownloadUrl3(null);
//            addAdvertActivity.setImgUri3(null);
//            additionalImageView3.setImageResource(R.drawable.ic_add_a_photo_blue);
//            deleteAdditionalPhoto3.setVisibility(View.GONE);
//        }
//    }
//
//
//    private void setupDeleteButton (ImageButton deleteButton)
//    {
//        if(deleteButton == deleteMainPhoto){
//            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    switch (which){
//                        case DialogInterface.BUTTON_POSITIVE:
//
//                            firstButton.setVisibility(View.VISIBLE);
//                            photosLayout.setVisibility(View.GONE);
//
//                            deletePhoto(storageRefMainPhoto, 0);
//
//                            if(storageRefAdditional1 != null){
//                                deletePhoto(storageRefAdditional1, 1);
//                            }
//                            if(storageRefAdditional2 != null){
//                                deletePhoto(storageRefAdditional2, 2);
//
//                            }
//                            if(storageRefAdditional3 != null){
//                                deletePhoto(storageRefAdditional3, 3);
//
//                            }
//
//                            storageRefMainPhoto = null;
//                            downloadPhotoMain = null;
//                            storageRefAdditional1 = null;
//                            downloadPhotoAdditional1 = null;
//                            storageRefAdditional2 = null;
//                            downloadPhotoAdditional2 = null;
//                            storageRefAdditional3 = null;
//                            downloadPhotoAdditional3 = null;
//
//                            mainImageView.setImageResource(R.drawable.ic_add_a_photo_blue);
//                            additionalImageView1.setImageResource(R.drawable.ic_add_a_photo_blue);
//                            additionalImageView2.setImageResource(R.drawable.ic_add_a_photo_blue);
//                            additionalImageView3.setImageResource(R.drawable.ic_add_a_photo_blue);
//
//                            break;
//
//                        case DialogInterface.BUTTON_NEGATIVE:
//                            //No button clicked
//                            break;
//                    }
//                }
//            };
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setMessage("do you want delete all images??").setPositiveButton("Yes", dialogClickListener)
//                    .setNegativeButton("No", dialogClickListener).show();
//
//        }
//        if(deleteButton == deleteAdditionalPhoto1){
//            ///////
//            deletePhoto(storageRefAdditional1, 1);
//            //////
//            additionalImageView1.setImageResource(R.drawable.ic_add_a_photo_blue);
//            deleteAdditionalPhoto1.setVisibility(View.GONE);
//            storageRefAdditional1 = null;
//            downloadPhotoAdditional1 = null;
//        }
//        if(deleteButton == deleteAdditionalPhoto2){
//            ///////
//            deletePhoto(storageRefAdditional2, 2);
//            //////
//            additionalImageView2.setImageResource(R.drawable.ic_add_a_photo_blue);
//            deleteAdditionalPhoto2.setVisibility(View.GONE);
//            storageRefAdditional2 = null;
//            downloadPhotoAdditional2 = null;
//
//        }
//        if(deleteButton == deleteAdditionalPhoto3){
//            ///////
//            deletePhoto(storageRefAdditional3, 3);
//            //////
//            additionalImageView3.setImageResource(R.drawable.ic_add_a_photo_blue);
//            deleteAdditionalPhoto3.setVisibility(View.GONE);
//            storageRefAdditional3 = null;
//            downloadPhotoAdditional3 = null;
//
//        }
//    }
//}





































