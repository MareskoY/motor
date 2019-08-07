//package c.motor.motor;
//
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//import android.telephony.TelephonyManager;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.ScrollView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.algolia.search.saas.Client;
//import com.algolia.search.saas.Index;
//import com.google.android.gms.tasks.Continuation;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.Timestamp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Random;
//
//import c.motor.motor.model.Advert;
//import c.motor.motor.model.User;
//import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
//import in.galaxyofandroid.spinerdialog.SpinnerDialog;
//
//import static android.app.Activity.RESULT_OK;
//
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class CreateAdvertFragment extends Fragment {
//
//
//    public CreateAdvertFragment() {
//        // Required empty public constructor
//    }
//    private FrameLayout frameLayout;
//    private ScrollView scrollView;
//
//    private ImageButton closeBtn;
//    private ImageButton returnBtn;
//
//    private TextView categoryTextView;
//    private TextView countryChooseTextView;
//    private TextView cityChooseTextView;
//    private EditText titleInput;
//    private EditText priceInput;
//    private TextView currencyTextView;
//    private EditText phoneInput;
//    private EditText descriptionInput;
//
//
//    private LinearLayout addPhotoBtn;
//    private ImageView imagePreview;
//    private ImageView deletePhotoPreview;
//    private Uri imageUri;
//    private ConstraintLayout photoLayout;
//    private ProgressBar mainImageProgressBar;
//
//    private ConstraintLayout additionalPhotoLayout;
//    private ImageView
//            additionalImagePreview1, additionalImageDeletePreview1,
//            additionalImagePreview2, additionalImageDeletePreview2,
//            additionalImagePreview3, additionalImageDeletePreview3;
//    private ProgressBar
//            additionalImageProgressBar1,
//            additionalImageProgressBar2,
//            additionalImageProgressBar3;
//
//    private Uri
//            imageUri1, imageUri2, imageUri3;
//
//    private String
//            stringUriMain, stringUri1,
//            stringUri2, stringUri3;
//    private String
//            downloadStringUriMain, downloadStringUri1,
//            downloadStringUri2, downloadStringUri3;
//
//    private Button createAdvertBtn;
//
//    private ArrayList<String> countryItems=new ArrayList<>();
//    private ArrayList<String> cityItems=new ArrayList<>();
//    private SpinnerDialog spinnerDialogCountry;
//    private SpinnerDialog spinnerDialogCity;
//
//    private String countryName[];
//    private String cityName[];
//    private String previousItem;
//    private String currentCountry;
//
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore firebaseFirestore;
//    private FirebaseStorage firebaseStorage;
//    private StorageReference storageReference;
//
//    private static final int Gallery_Pick = 1;
//    private static final int Gallery_Pick1 = 2;
//    private static final int Gallery_Pick2 = 3;
//    private static final int Gallery_Pick3 = 4;
//
//    String userId, userName;
//    String category, country, city,
//            title, description, phone,
//            stringPrice;
//    double price;
//    Timestamp dateTime;
//    Date date;
//    String currency;
//
//    Uri downloadUri;
//    String stringDownloadUri;
//    //UploadTask uploadTask;
//
//    private static final int MAX_LENGTH = 30;
//
//    boolean uploadResult = false;
//
//    private ProgressDialog loadingBar;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_create_advert, container, false);
//
//
//        Bundle bundle = getArguments();
//        int id = bundle.getInt("Id");
//        String category = bundle.getString("Title");
//        int icon = bundle.getInt("Icon");
//
//        // Save params
//
//        country = bundle.getString("Country");
//        city = bundle.getString("City");
//        title = bundle.getString("Tittle");
//        stringPrice = bundle.getString("Price");
//        phone = bundle.getString("Phone");
//        description = bundle.getString("Description");
////        if(bundle.getString("ImageUri") != null)
////            imageUri = Uri.parse(bundle.getString("ImageUri"));
////        if(bundle.getString("ImageUri1") != null)
////            imageUri1 = Uri.parse(bundle.getString("ImageUri1"));
////        if(bundle.getString("ImageUri") != null)
////            imageUri2 = Uri.parse(bundle.getString("ImageUri2"));
////        if(bundle.getString("ImageUri") != null)
////            imageUri3 = Uri.parse(bundle.getString("ImageUri3"));
////
////        stringUriMain = bundle.getString("StringUriMain");
////        stringUri1 = bundle.getString("StringUri1");
////        stringUri2 = bundle.getString("StringUri2");
////        stringUri3 = bundle.getString("StringUri3");
////
////        downloadStringUriMain = bundle.getString("StringUriMain");
////        downloadStringUri1 = bundle.getString("StringUri1");
////        downloadStringUri2 = bundle.getString("StringUri2");
////        downloadStringUri3 = bundle.getString("StringUri3");
////
////        if(bundle.getString("ImageUri") != null)
////            System.out.println(imageUri.toString() +"---------------------------------------------lololo--------------------------");
//
//        mAuth = FirebaseAuth.getInstance();
//        firebaseFirestore = FirebaseFirestore.getInstance();
//        firebaseStorage = FirebaseStorage.getInstance();
//        storageReference = firebaseStorage.getReference().child("ADVERTS_IMAGES_TEST_FOLDER3");
//
//        frameLayout = getActivity().findViewById(R.id.create_advert_container);
//        scrollView = getActivity().findViewById(R.id.add_advert_scrollview);
//
//        closeBtn = view.findViewById(R.id.add_advert_close);
//        returnBtn = view.findViewById(R.id.add_advert_go_back);
//
//        categoryTextView = view.findViewById(R.id.add_advert_text_view);
//        countryChooseTextView = view.findViewById(R.id.add_advert_text_location_country);
//        cityChooseTextView = view.findViewById(R.id.add_advert_text_location_city);
//        titleInput = view.findViewById(R.id.add_advert_tittle);
//        priceInput = view.findViewById(R.id.add_advert_price);
//        currencyTextView = view.findViewById(R.id.add_advert_currency);
//        phoneInput = view.findViewById(R.id.add_advert_phone);
//        descriptionInput = view.findViewById(R.id.add_advert_description);
//
//        addPhotoBtn = view.findViewById(R.id.add_advert_add_photos_first_button);
//        imagePreview = view.findViewById(R.id.add_advert_add_photo);
//        photoLayout = view.findViewById(R.id.add_advert_add_photo_container);
//        deletePhotoPreview = view.findViewById(R.id.add_advert_add_delete_photo);
//        mainImageProgressBar = view.findViewById(R.id.add_advert_add_delete_photo_progressBar);
//
//
//        additionalPhotoLayout = view.findViewById(R.id.add_advert_add_additional_photo_container);
//
//        additionalImagePreview1 = view.findViewById(R.id.add_advert_add_additional_photo1);
//        additionalImagePreview2 = view.findViewById(R.id.add_advert_add_additional_photo2);
//        additionalImagePreview3 = view.findViewById(R.id.add_advert_add_additional_photo3);
//
//        additionalImageDeletePreview1 =view.findViewById(R.id.add_advert_delete_additional_photo1);
//        additionalImageDeletePreview2 =view.findViewById(R.id.add_advert_delete_additional_photo2);
//        additionalImageDeletePreview3 =view.findViewById(R.id.add_advert_delete_additional_photo3);
//
//        additionalImageProgressBar1 = view.findViewById(R.id.add_advert_additional_progressBar1);
//        additionalImageProgressBar2 = view.findViewById(R.id.add_advert_additional_progressBar2);
//        additionalImageProgressBar3 = view.findViewById(R.id.add_advert_additional_progressBar3);
//
//        createAdvertBtn = view.findViewById(R.id.add_advert_create_advert);
//
//        categoryTextView.setText(category);
//        Drawable categoryIcon = getContext().getResources().getDrawable( icon);
//        Drawable editIcon = getContext().getResources().getDrawable( R.drawable.ic_edit_blue);
//        categoryTextView.setCompoundDrawablesWithIntrinsicBounds(categoryIcon,null,editIcon,null);
//        cityChooseTextView = view.findViewById(R.id.add_advert_text_location_city);
//
//        countryName = getResources().getStringArray(R.array.countryNameEng);
//        initArray(countryItems, countryName);
//
//        currentCountry = getUserCountry(getContext());
//
//        firstSetup();
//
//        String currentUserId = mAuth.getCurrentUser().getUid();
//        userId = currentUserId;
//        userName = getUserName(currentUserId);
//
//        loadingBar = new ProgressDialog(getActivity());
//
//        //Save params
//
////        if(!country.equals("Your country*"))
////            countryChooseTextView.setText(country);
//
//        titleInput.setText(title);
//        priceInput.setText(stringPrice);
//        phoneInput.setText(phone);
//        descriptionInput.setText(description);
//
//        if(imageUri != null){
//            photoLayout.setVisibility(View.VISIBLE);
//            additionalPhotoLayout.setVisibility(View.VISIBLE);
//            imagePreview.setImageURI(imageUri);
//            addPhotoBtn.setVisibility(View.GONE);
//            deletePhotoPreview.setVisibility(View.VISIBLE);
//        }
//        if(imageUri1 != null){
//
//        }
//        if(imageUri2 != null){
//
//        }
//        if(imageUri3 != null){
//
//        }
//
//
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        previousItem = countryChooseTextView.getText().toString();
//        spinnerDialogCountry = new SpinnerDialog(getActivity(),countryItems,"Choose country","close");
//        spinnerDialogCountry.bindOnSpinerListener(new OnSpinerItemClick() {
//            @Override
//            public void onClick(String item, int position) {
//
//                previousItem = countryChooseTextView.getText().toString();
//
//                cityChooseTextView.setVisibility(View.VISIBLE);
//                countryChooseTextView.setText(item);
//                countryChooseTextView.setBackgroundResource(R.drawable.text_view_shape);
//                if (!item.contentEquals(previousItem)){
//                    cityChooseTextView.setText("Your City*");
//                }
//
//                if(item.contentEquals("Vietnam")){
//                    currencyTextView.setText("VND");
//                    cityName = getResources().getStringArray(R.array.cities_vietnam_eng);
//                    initArray(cityItems, cityName);
//
//
//                }else if(item.contentEquals("Philippines")){
//                    currencyTextView.setText("PHP");
//                    cityName = getResources().getStringArray(R.array.cities_philippines);
//                    initArray(cityItems, cityName);
//
//                }
//
//            }
//        });
//        cityChooseTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                spinnerDialogCity = new SpinnerDialog(getActivity(),cityItems,"Choose city", "close");
//                spinnerDialogCity.showSpinerDialog();
//                spinnerDialogCity.bindOnSpinerListener(new OnSpinerItemClick() {
//                    @Override
//                    public void onClick(String item, int position) {
//                        cityChooseTextView.setBackgroundResource(R.drawable.text_view_shape);
//                        cityChooseTextView.setText(item);
//                    }
//                });
//            }
//        });
//        countryChooseTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                spinnerDialogCountry.showSpinerDialog();
//            }
//        });
//
//
//        createAdvertBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkInputs();
//                if(checkInputs())
//                    createAdvert();
//            }
//        });
//
//        returnBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setFragment(new CreateAdvertCategoryFragment());
//            }
//        });
//        closeBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
////                String[] photoUrls = {"5jM2osm3jORFJDivfxTrnA5OAsg2_!5NSzVHKB4Y(C", "5jM2osm3jORFJDivfxTrnA5OAsg2_#Y-;(F!\\kET*'P{",
////                "5jM2osm3jORFJDivfxTrnA5OAsg2_*t={LU+f%*MzXt^y^*:p^'$x,aT", " 5jM2osm3jORFJDivfxTrnA5OAsg2_9[K\\#<i(",
////                "5jM2osm3jORFJDivfxTrnA5OAsg2_?h)hhnx7R'71SeN@wtbU.B' 3", " 5jM2osm3jORFJDivfxTrnA5OAsg2_dLKHQ5)",
////                "5jM2osm3jORFJDivfxTrnA5OAsg2_j,C;y\" vRC1", " 5jM2osm3jORFJDivfxTrnA5OAsg2_N", " 5jM2osm3jORFJDivfxTrnA5OAsg2_O'",
////                "5jM2osm3jORFJDivfxTrnA5OAsg2_u\\;6>vCE+Q", "5jM2osm3jORFJDivfxTrnA5OAsg2_rJ)w8/", "5jM2osm3jORFJDivfxTrnA5OAsg2_UmRCK-", "5jM2osm3jORFJDivfxTrnA5OAsg2_zA|>gAD6z.e!Z)/"};
////                for(int i = 0; i < photoUrls.length; i++) {
////                    deletePhoto(photoUrls[i], null, null);
////                }
//                mainIntent();
//            }
//        });
//        categoryTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setFragment(new CreateAdvertCategoryFragment());
//            }
//        });
//        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenGallery(Gallery_Pick);
//            }
//        });
//        additionalImagePreview1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenGallery(Gallery_Pick1);
//            }
//        });
//        additionalImagePreview2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenGallery(Gallery_Pick2);
//            }
//        });
//        additionalImagePreview3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenGallery(Gallery_Pick3);
//            }
//        });
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == Gallery_Pick && resultCode==RESULT_OK && data != null){
//            imageUri = data.getData();
//            photoLayout.setVisibility(View.VISIBLE);
//            additionalPhotoLayout.setVisibility(View.VISIBLE);
//            imagePreview.setImageURI(imageUri);
//            addPhotoBtn.setVisibility(View.GONE);
//
//
//            ///-------------------------------------------------------------------------------
//            loadPhoto(imagePreview, deletePhotoPreview, mainImageProgressBar,1 );
//
//            deletePhotoPreview.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    addPhotoBtn.setVisibility(View.VISIBLE);
//                    photoLayout.setVisibility(View.GONE);
//                    additionalPhotoLayout.setVisibility(View.GONE);
//
//                    deletePhoto(stringUriMain, stringUriMain, downloadStringUriMain);
//
//                    if(stringUri1 != null){
//                        deletePhoto(stringUri1, stringUri1, downloadStringUri1);
//                    }
//                    if(stringUri2 != null){
//                        deletePhoto(stringUri2, stringUri2, downloadStringUri2);
//
//                    }
//                    if(stringUri2 != null){
//                        deletePhoto(stringUri3, stringUri3, downloadStringUri3);
//
//                    }
//                    imageUri = null;
//                    imageUri1 = null;
//                    imageUri2 = null;
//                    imageUri3 = null;
//                    additionalImagePreview1.setImageResource(R.drawable.ic_add_a_photo_blue);
//                    additionalImagePreview2.setImageResource(R.drawable.ic_add_a_photo_blue);
//                    additionalImagePreview3.setImageResource(R.drawable.ic_add_a_photo_blue);
//                }
//            });
//
//            imagePreview.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    deletePhoto(stringUriMain, stringUriMain, downloadStringUriMain);
//                    OpenGallery(Gallery_Pick);
//                }
//            });
//        }
//        if(requestCode == Gallery_Pick1 && resultCode==RESULT_OK && data != null){
//            imageUri1 = data.getData();
//            additionalImagePreview1.setImageURI(imageUri1);
//            ////////
//            loadPhoto(additionalImagePreview1, additionalImageDeletePreview1, additionalImageProgressBar1,2 );
//            ////////
//            //additionalImageDeletePreview1.setVisibility(View.VISIBLE);
//            additionalImageDeletePreview1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    imageUri1 = null;
//                    ///////
//                    deletePhoto(stringUri1, stringUri1, downloadStringUri1);
//                    //////
//                    additionalImagePreview1.setImageResource(R.drawable.ic_add_a_photo_blue);
//                    additionalImageDeletePreview1.setVisibility(View.GONE);
//                }
//            });
//            additionalImagePreview1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ///////
//                    deletePhoto(stringUri1, stringUri1, downloadStringUri1);
//                    //////
//                    OpenGallery(Gallery_Pick1);
//                }
//            });
//        }
//        if(requestCode == Gallery_Pick2 && resultCode==RESULT_OK && data != null){
//            imageUri2 = data.getData();
//            additionalImagePreview2.setImageURI(imageUri2);
//            ////////
//            loadPhoto(additionalImagePreview2, additionalImageDeletePreview2, additionalImageProgressBar2,3 );
//            ////////
//            //additionalImageDeletePreview2.setVisibility(View.VISIBLE);
//            additionalImageDeletePreview2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    imageUri2 = null;
//                    ///////
//                    deletePhoto(stringUri2, stringUri2, downloadStringUri2);
//                    //////
//                    additionalImagePreview2.setImageResource(R.drawable.ic_add_a_photo_blue);
//                    additionalImageDeletePreview2.setVisibility(View.GONE);
//                }
//            });
//            additionalImagePreview2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ///////
//                    deletePhoto(stringUri2, stringUri2, downloadStringUri2);
//                    //////
//                    OpenGallery(Gallery_Pick2);
//                }
//            });
//        }
//        if(requestCode == Gallery_Pick3 && resultCode==RESULT_OK && data != null){
//            imageUri3 = data.getData();
//            additionalImagePreview3.setImageURI(imageUri3);
//            ////////
//            loadPhoto(additionalImagePreview3, additionalImageDeletePreview3, additionalImageProgressBar3,4 );
//            ////////
//            //additionalImageDeletePreview3.setVisibility(View.VISIBLE);
//            additionalImageDeletePreview3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    imageUri3 = null;
//                    ///////
//                    deletePhoto(stringUri3, stringUri3, downloadStringUri3);
//                    //////
//                    additionalImagePreview3.setImageResource(R.drawable.ic_add_a_photo_blue);
//                    additionalImageDeletePreview3.setVisibility(View.GONE);
//                }
//            });
//            additionalImagePreview3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ///////
//                    deletePhoto(stringUri3, stringUri3, downloadStringUri3);
//                    //////
//                    OpenGallery(Gallery_Pick3);
//                }
//            });
//        }
//
//
//    }
//    private void setFragment(Fragment fragment) {
//
//        //Save params
//
//        Bundle bundle = new Bundle();
//        bundle.putString("Country", countryChooseTextView.getText().toString());
//        bundle.putString("City", cityChooseTextView.getText().toString());
//        bundle.putString("Tittle", titleInput.getText().toString());
//        bundle.putString("Price", priceInput.getText().toString());
//        bundle.putString("Phone", phoneInput.getText().toString());
//        bundle.putString("Description", descriptionInput.getText().toString());
////        if(imageUri != null)
////            bundle.putString("ImageUri", imageUri.toString());
////        else
////            bundle.putString("ImageUri", null);
////
////        if(imageUri1 != null)
////            bundle.putString("ImageUri1", imageUri1.toString());
////        else
////            bundle.putString("ImageUri1", null);
////
////        if(imageUri2 != null)
////            bundle.putString("ImageUri2", imageUri2.toString());
////        else
////            bundle.putString("ImageUri2", null);
////
////        if(imageUri3 != null)
////            bundle.putString("ImageUri3", imageUri3.toString());
////        else
////            bundle.putString("ImageUri3", null);
////
////        bundle.putString("StringUriMain", stringUriMain);
////        bundle.putString("StringUri1", stringUri1);
////        bundle.putString("StringUri2", stringUri2);
////        bundle.putString("StringUri3", stringUri3);
////
////        bundle.putString("DownloadStringUriMain", downloadStringUriMain);
////        bundle.putString("DownloadStringUri1", downloadStringUri1);
////        bundle.putString("DownloadStringUri2", downloadStringUri2);
////        bundle.putString("DownloadStringUri3", downloadStringUri3);
//
//
//
//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragment.setArguments(bundle);
//        fragmentTransaction.replace(frameLayout.getId(), fragment);
//        fragmentTransaction.commit();
//    }
//    private void mainIntent(){
//        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
//        startActivity(mainIntent);
//        getActivity().finish();
//    }
//    private void initArray(ArrayList<String> list, String[] values){
//        list.clear();
//        for(int i = 0; i <values.length; i++){
//            list.add(values[i]);
//        }
//    }
//    private boolean checkInputs() {
//        boolean result = true;
//
//        Drawable customErrorIcon = getResources().getDrawable(R.drawable.ic_error_outline_black_24dp);
//        customErrorIcon.setBounds(0, 0, customErrorIcon.getIntrinsicWidth(), customErrorIcon.getIntrinsicHeight());
//
//        if (countryChooseTextView.getText().equals("Your Country*")) {
//            countryChooseTextView.setBackgroundResource(R.drawable.text_view_wrong_shape);
//            //scrollView.smoothScrollTo(0,0);
//            result = false;
//        }
//        if (cityChooseTextView.getText().equals("Your City*")) {
//            cityChooseTextView.setBackgroundResource(R.drawable.text_view_wrong_shape);
//            //scrollView.smoothScrollTo(0,0);
//            result = false;
//        }
//        if (TextUtils.isEmpty(titleInput.getText())) {
//            titleInput.setError("Title could not be empty!", customErrorIcon);
//            result = false;
//        }
//        if (TextUtils.isEmpty(priceInput.getText())) {
//            priceInput.setError("Price could not be empty!", customErrorIcon);
//            result = false;
//        }
//        if (TextUtils.isEmpty(phoneInput.getText())) {
//            phoneInput.setError("Phone could not be empty!!", customErrorIcon);
//            result = false;
//        }
//        if (TextUtils.isEmpty(descriptionInput.getText())) {
//            descriptionInput.setError("Description could not be empty!", customErrorIcon);
//            result = false;
//        }
//        return result;
//    }
//    public static String getUserCountry(Context context) {
//        try {
//            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            final String simCountry = tm.getSimCountryIso();
//            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
//                return simCountry.toLowerCase(Locale.US);
//            }
//            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
//                String networkCountry = tm.getNetworkCountryIso();
//                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
//                    return networkCountry.toLowerCase(Locale.US);
//                }
//            }
//        }
//        catch (Exception e) { }
//        return null;
//    }
//    public void firstSetup(){
//        if(currentCountry.equals("vn")){
//            currencyTextView.setText("VND");
//            cityName = getResources().getStringArray(R.array.cities_vietnam_eng);
//            initArray(cityItems, cityName);
//            cityChooseTextView.setVisibility(View.VISIBLE);
//            countryChooseTextView.setText("Vietnam");
//            //save params
//            cityChooseTextView.setText(city);
//
//
//        }else if(currentCountry.equals("ph")){
//            currencyTextView.setText("PHP");
//            cityName = getResources().getStringArray(R.array.cities_philippines);
//            initArray(cityItems, cityName);
//            cityChooseTextView.setVisibility(View.VISIBLE);
//            countryChooseTextView.setText("Philippines");
//            //save params
//            cityChooseTextView.setText(city);
//
//        }
//        if( country.equals("Vietnam")){
//            currencyTextView.setText("VND");
//            cityName = getResources().getStringArray(R.array.cities_vietnam_eng);
//            initArray(cityItems, cityName);
//            cityChooseTextView.setVisibility(View.VISIBLE);
//            countryChooseTextView.setText("Vietnam");
//            //save params
//            cityChooseTextView.setText(city);
//        }else if(country.equals("Philippines")){
//            currencyTextView.setText("PHP");
//            cityName = getResources().getStringArray(R.array.cities_philippines);
//            initArray(cityItems, cityName);
//            cityChooseTextView.setVisibility(View.VISIBLE);
//            countryChooseTextView.setText("Philippines");
//            //save params
//            cityChooseTextView.setText(city);
//        }
//    }
//    private void OpenGallery(int res) {
//        Intent galleryIntent = new Intent();
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent,res);
//
//    }
//    private void createAdvert(){
//        String currentUserId = mAuth.getCurrentUser().getUid();
//
//        loadingBar.setTitle("Creating new Advert");
//        loadingBar.setMessage("Please wait, while we are creating your new advert");
//        loadingBar.show();
//        loadingBar.setCanceledOnTouchOutside(false);
//
////        userId = currentUserId;
////        userName = getUserName(currentUserId);
//        category = categoryTextView.getText().toString();
//        country = countryChooseTextView.getText().toString();
//        city = cityChooseTextView.getText().toString();
//        title = titleInput.getText().toString();
//        description = descriptionInput.getText().toString();
//        phone = phoneInput.getText().toString();
//        date = new Date();
//        dateTime = new Timestamp(date);
//        price = Double.parseDouble(priceInput.getText().toString());
//        currency = currencyTextView.getText().toString();
//
////        String  mainImg,
////                img1,img2,img3;
////
////        if(imageUri != null){
////            mainImg = uploadPhoto(imagePreview);
////        }else{
////            mainImg = "null";
////        }
////        if(imageUri1 != null){
////            img1 = uploadPhoto(additionalImagePreview1);
////        }else{
////            img1 = "null";
////        }
////        if(imageUri2 != null){
////            img2 = uploadPhoto(additionalImagePreview2);
////        }else{
////            img2 = "null";
////        }
////        if(imageUri3 != null){
////            img3 = uploadPhoto(additionalImagePreview3);
////        }else{
////            img3 = "null";
////        }
//
//
//
//        Advert advertData =
//                new Advert(userId, userName,
//                        category, country,
//                        city, title,
//                        description, phone,
//                        stringUriMain, stringUri1,
//                        stringUri2, stringUri3,
//                        downloadStringUriMain, downloadStringUri1,
//                        downloadStringUri2, downloadStringUri3,
//                        dateTime, price, currency);
//
//        Map<String,Object> userdata = advertData.toMap();
//        System.out.println(dateTime.getNanoseconds() + "Date time 1");
//
//
//        Client client = new Client("STZV8Z6R6G", "ce681eb47a47a0f885d46617049791c7");
//        final Index index = client.getIndex("adverts_new");
//
////        Map<String,Object> userdata = new HashMap<>();
////        userdata.put("fullname","1234");
////        userdata.put("email",1234);
//
//        firebaseFirestore.collection("TEST2")
//                .add(userdata)
//                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        if (task.isSuccessful()){
//                            //add to algolia index
//                            System.out.println(dateTime.toString()+ "Date time 2");
//                            Map<String, Object> algoliaData = new HashMap<>();
//                            algoliaData.put("title", title);
//                            algoliaData.put("dateTime",date.getTime());
//                            algoliaData.put("image", downloadStringUriMain);
//                            algoliaData.put("country", country);
//                            algoliaData.put("city", city);
//                            algoliaData.put("price", price);
//                            algoliaData.put("currency", currency);
//                            algoliaData.put("category", category);
//                            algoliaData.put("documentReference", task.getResult().getId());
//
//                            List<JSONObject> array = new ArrayList<>();
//
//                            JSONObject obj = new JSONObject(algoliaData);
//                            JSONArray tags = new JSONArray();
//                            tags.put(country);
//                            tags.put(city);
//                            tags.put(category);
//                            if(downloadStringUriMain != null && downloadStringUriMain.equals(""))
//                                tags.put("haveImage");
//                            try {
//                                obj.put("_tags", tags);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            array.add(obj);
//                            index.addObjectsAsync(new JSONArray(array), null);
//                            loadingBar.cancel();
//                            mainIntent();
//                        }else{
//                            String error = task.getException().getMessage();
//                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
//                            loadingBar.cancel();
//                        }
//                    }
//                });
//    }
//    private String getUserName(String userId) {
//        userName = "error";
//        firebaseFirestore.collection("USERS").document(userId)
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        DocumentSnapshot userDoc = task.getResult();
//
//                        User currentUser = User.documentSnapshotToUser(userDoc);
//                        userName = currentUser.getUsername();
//
//                    } else {
//                        //error
//                    }
//                } else {
//
//                }
//            }
//        });
//        return userName;
//    }
//
//    private String uploadPhoto( ImageView photoView)
//
//    {
//
//        //String uploadUrl;
//
//        String randomName = userId + "_" + random();
//        final StorageReference filePath = storageReference.child("test_true_" + randomName);
//
//        photoView.setDrawingCacheEnabled(true);
//        photoView.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
//        byte[] data = baos.toByteArray();
//
//        final UploadTask uploadTask = filePath.putBytes(data);
//        uploadTask .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                String message = e.toString();
//                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
//
//                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
//                        }
//                    }
//                });
//            }
//        });
//
//        return "test_true_" + randomName;
//
//        /*
//        uploadTask = filePath.putBytes(data);
//        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                System.out.println("Upload is " + progress + "% done");
//            }
//        });
//
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                System.out.println("провал ");
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                System.out.println("успех ");
//
//                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                        if (!task.isSuccessful()) {
//                            throw task.getException();
//                        }
//
//                        // Continue with the task to get the download URL
//                        return filePath.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if (task.isSuccessful()) {
//                            downloadUri = task.getResult();
//                            System.out.println("первая линия " + downloadUri.toString());
//                        } else {
//                            // Handle failures
//                            // ...
//                        }
//                    }
//                });
//            }
//        });
//        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                if(task.isSuccessful()){
//                    System.out.println("успех2 ");
//                    loadingBar.cancel();
//                }else{
//                    System.out.println("провал2 ");
//                    loadingBar.cancel();
//                }
//            }
//        });
//
//        if (downloadUri != null) {
//            stringDownloadUri = downloadUri.toString();
//            System.out.println("вторая линия" + stringDownloadUri);
//        }
//        System.out.println("третяя линия" + stringDownloadUri);
//        if(uploadTask.isComplete())
//            return stringDownloadUri;
//        else
//            return stringDownloadUri;
//
//        */
//
//
////        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
////                if(task.isSuccessful()){
////                    uploadResult = true;
////                }else{
////                    uploadResult =false;
////                }
////            }
////        });
//        //uploadUrl = filePath.getDownloadUrl().toString();
//
////        uploadTask.addOnFailureListener(new OnFailureListener() {
////            @Override
////            public void onFailure(@NonNull Exception exception) {
////                uploadResult = true;
////            }
////        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
////            @Override
////            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////                uploadResult =false;
////            }
////        });
////
////        if (!uploadResult){
////            randomName = "upload error";
////        }
//    }
//
//    private String random() {
//        Random generator = new Random();
//        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(MAX_LENGTH);
//        char tempChar;
//        for (int i = 0; i < randomLength; i++){
//            tempChar = (char) (generator.nextInt(36) + 12);
//            randomStringBuilder.append(tempChar);
//        }
//        return randomStringBuilder.toString();
//    }
//    private void loadPhoto(final ImageView photoView, final ImageView deletePhotoView, final ProgressBar progressBar,final int number){
//
//        photoView.setEnabled(false);
//        createAdvertBtn.setEnabled(false);
//        createAdvertBtn.setTextColor(Color.argb(70,255,255,255));
//        progressBar.setVisibility(View.VISIBLE);
//        deletePhotoView.setVisibility(View.GONE);
//
//        final String randomName = userId + "_" + random();
//        final StorageReference filePath = storageReference.child(randomName);
//
//        photoView.setDrawingCacheEnabled(true);
//        photoView.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
//        byte[] data = baos.toByteArray();
//
//        final UploadTask uploadTask2 = filePath.putBytes(data);
//        uploadTask2 .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                String message = e.toString();
//                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
//
//                Task<Uri> uriTask = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
//                            createAdvertBtn.setEnabled(true);
//                            createAdvertBtn.setTextColor(Color.rgb(255,255,255));
//                            progressBar.setVisibility(View.GONE);
//                            deletePhotoView.setVisibility(View.VISIBLE);
//                            if(number == 1) {
//                                stringUriMain = randomName;
//                                downloadStringUriMain = task.getResult().toString();
//                            }else if(number == 2){
//                                stringUri1 = randomName;
//                                downloadStringUri1 = task.getResult().toString();
//                            }else if(number == 3){
//                                stringUri2 = randomName;
//                                downloadStringUri2 = task.getResult().toString();
//                            }else if(number == 4){
//                                stringUri3 = randomName;
//                                downloadStringUri3 = task.getResult().toString();
//                            }
//                            photoView.setEnabled(true);
//                            System.out.println("lolo1" + stringUriMain);
//                            System.out.println("lolo1" + downloadStringUriMain);
//                        }else{
//                            createAdvertBtn.setEnabled(true);
//                            createAdvertBtn.setTextColor(Color.rgb(255,255,255));
//                            progressBar.setVisibility(View.GONE);
//                            deletePhotoView.setVisibility(View.VISIBLE);
//                            photoView.setEnabled(true);
//                        }
//
//                    }
//                });
//            }
//        });
//    }
//    private void deletePhoto(String childPath, String urlString, String stringDownloadUrl){
//
//
//        final StorageReference filePath = storageReference.child(childPath);
//        filePath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getActivity(), "Image deleted", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Uh-oh, an error occurred!
//            }
//        });
//        urlString = null;
//        stringDownloadUrl =null;
//    }
//    private void deleteAll(){
//        final StorageReference filePath = storageReference;
//        filePath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getActivity(), "Image deleted", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Uh-oh, an error occurred!
//            }
//        });
//    }
//}
//
//
