package c.motor.motor.addAdvertActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import c.motor.motor.AdvertActivity;
import c.motor.motor.R;
import c.motor.motor.helpers.AppData;
import c.motor.motor.model.Advert;
import c.motor.motor.model.User;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static c.motor.motor.helpers.Helpers.getUserCountry;

public class AddAdvertActivity extends AppCompatActivity {

    final private int REOPEN_COUNT = 3;

    private ConstraintLayout loadPage;

    private FrameLayout categoryFrameLayout;

    private CardView
            bikeCategory,
            carCategory,
            sportBikeCategory,
            bigCarCategory,
            cycleCategory,
            otherCategory;

    private ScrollView createScrollView;

    private TextView
            categoryInput,
            countryInput,
            cityInput;

    private EditText
            titleInput,
            priceInput,
            descriptionInput,
            yearInput;

    private MaskedEditText
            maskedPhoneInput,
            maskedPhoneVNInput,
            maskedPhonePHInput;

    private TextView
            currencyTextView;

    private CheckBox
            removeMask,
            isNewCheckBox;

    private RadioButton
            isNewRadioBtn,
            isUsedRadioBtn;

    private LinearLayout
            yearLayout;

    FrameLayout photoFrameLayout;

    protected Button createButton;
    protected Button reopenButton;
    protected Button saveChangesButton;

    private FirebaseAuth mAuth;

    // data
    public String
            category,
            city,
            country,
            currency,
            description,
            phone,
            title;
    String userId, userName;
    boolean isNew = false;
    private Timestamp dateTime;
    public Date date;
    private int reopenCount = 0;
    public int year;
    private long price;
    public ArrayList<String>
            downloadUrls,
            fileNames;

    public String previewImg;

    private Advert advert;

    private Client client;

    private ProgressDialog loadingBar;

    public boolean isPhotoChanged = false;
    public boolean isMainPhotoChanged = false;




    //
    protected Button getCreateButton() {
        return createButton;
    }

    protected void setCreateButton(Button createButton) {
        this.createButton = createButton;
    }

    protected Button getReopenButton() {
        return reopenButton;
    }

    protected void setReopenButton(Button reopenButton) {
        this.reopenButton = reopenButton;
    }

    public Button getSaveChangesButton() {
        return saveChangesButton;
    }

    public void setSaveChangesButton(Button saveChangesButton) {
        this.saveChangesButton = saveChangesButton;
    }

    private String countryName[];
    private String cityName[];
    public ArrayList<String> countryItems=new ArrayList<>();
    public ArrayList<String> cityItems=new ArrayList<>();
    private SpinnerDialog spinnerDialogCountry;
    private SpinnerDialog spinnerDialogCity;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private Index index;

    private Menu menu;
    private MenuItem menuItemClose;


    private String documentName;
    private String countryNameExstras;
    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advert);

        System.out.println("start AddAdvertActivity");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            documentName = extras.getString("documentName");
            status = extras.getInt("status");
            countryNameExstras = extras.getString("country");
            //The key argument here must match that used in the other activity
        }

        client = new Client("STZV8Z6R6G", "ce681eb47a47a0f885d46617049791c7");


        if(documentName == null){
            setupDB();
            setupCreateView();
            setupCategoryView();
            setupFirstParams();
            endOfLoad(0);
            setupFunctional();
            setupBtn();
        }else{
            setupDB();
            loadData();
            setupFunctional();
            setupBtn();
        }



        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_undo_blue);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPhotoChanged){
                    if(status == 0){
                        deleteAllPhotos();
                    }else{
                        updateOnlyPhoto();
                    }
                }
                AddAdvertActivity.this.finish();
            }
        });


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            if(isPhotoChanged){
                if(status == 0){
                    deleteAllPhotos();
                }else{
                    updateOnlyPhoto();
                }
            }
            AddAdvertActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_advert, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItemClose = menu.findItem(R.id.add_advert_close_category);
        menuItemClose.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_advert_close_category) {
            createScrollView.setVisibility(View.VISIBLE);
            categoryFrameLayout.setVisibility(View.GONE);
            menuItemClose.setVisible(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupFunctional() {

        final String previousItem = countryInput.getText().toString();
        spinnerDialogCountry = new SpinnerDialog(AddAdvertActivity.this,countryItems,"Choose country","close");
        spinnerDialogCountry.bindOnSpinerListener(new OnSpinerItemClick() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(String item, int position) {
                if (!item.contentEquals(previousItem)){
                    cityInput.setText("City");
                    cityInput.setTextColor(R.color.colorGray);
                }
                if(item.contentEquals("Vietnam")){
                    setupVN();
                    setupCountryDataBase("Vietnam");
                }else if(item.contentEquals("Philippines")){
                    setupPH();
                    setupCountryDataBase("Philippines");
                }
            }
        });
        countryInput.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(status == 0){
                    spinnerDialogCountry.showSpinerDialog();
                }else{
                    countryInput.setTextColor(R.color.colorGray);
                }

            }
        });
        cityInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialogCity = new SpinnerDialog(AddAdvertActivity.this,cityItems,"Choose city", "close");
                spinnerDialogCity.showSpinerDialog();
                spinnerDialogCity.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String item, int position) {
                        cityInput.setBackgroundResource(R.drawable.text_view_shape);
                        cityInput.setText(item);
                        cityInput.setTextColor(Color.parseColor("#000000"));
                    }
                });
            }
        });
        categoryInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createScrollView.setVisibility(View.GONE);
                categoryFrameLayout.setVisibility(View.VISIBLE);
                menuItemClose.setVisible(true);
            }
        });
        removeMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(removeMask.isChecked()){
                    maskedPhoneInput.setVisibility(View.VISIBLE);
                    maskedPhoneVNInput.setVisibility(View.GONE);
                    maskedPhonePHInput.setVisibility(View.GONE);
                }else{
                   if(countryInput.getText().equals("Vietnam") ){
                       maskedPhoneInput.setVisibility(View.GONE);
                       maskedPhoneVNInput.setVisibility(View.VISIBLE);
                       maskedPhonePHInput.setVisibility(View.GONE);
                   }else if(countryInput.getText().equals("Philippines")){
                       maskedPhoneInput.setVisibility(View.GONE);
                       maskedPhoneVNInput.setVisibility(View.GONE);
                       maskedPhonePHInput.setVisibility(View.VISIBLE);
                   }else{
                       maskedPhoneInput.setVisibility(View.VISIBLE);
                       maskedPhoneVNInput.setVisibility(View.GONE);
                       maskedPhonePHInput.setVisibility(View.GONE);
                   }
                }
            }
        });
        isNewCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNewCheckBox.isChecked()){
                    isNew = true;
                }else{
                    isNew = false;
                }
            }
        });
        isNewRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNewRadioBtn.isChecked()){
                    isNew = true;
                }else{
                    isNew = false;
                }
            }
        });
        isUsedRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNewRadioBtn.isChecked()){
                    isNew = true;
                }else{
                    isNew = false;
                }
            }
        });

    }

    private void setupFirstParams() {
        countryName = getResources().getStringArray(R.array.countryNameEng);
        initArray(countryItems, countryName);
        String currentCountry = AppData.getPreference(getApplicationContext(), "country");
        System.out.println("currentCountry " + currentCountry);
        if(currentCountry.equals("Vietnam")){
            setupVN();
            setupCountryDataBase("Vietnam");
        }else if(currentCountry.equals("Philippines")){
            setupPH();
            setupCountryDataBase("Philippines");
        }else{
            currencyTextView.setText("");
            maskedPhoneInput.setVisibility(View.VISIBLE);
            maskedPhoneVNInput.setVisibility(View.GONE);
            maskedPhonePHInput.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void setupCreateView(){
        loadPage = findViewById(R.id.add_advert_load_page);

        createScrollView = findViewById(R.id.add_activity_create_frameLayout);
        if(documentName == null)
            createScrollView.setVisibility(View.GONE);

        categoryInput = findViewById(R.id.add_advert_category_input);
        countryInput = findViewById(R.id.add_advert_country_input);
        cityInput = findViewById(R.id.add_advert_city_input);
        titleInput = findViewById(R.id.add_advert_title);
        priceInput = findViewById(R.id.add_advert_price_input);
        descriptionInput = findViewById(R.id.add_advert_description_input);
        currencyTextView = findViewById(R.id.add_advert_currency_textView);
        createButton = findViewById(R.id.add_advert_create_btn);
        reopenButton = findViewById(R.id.add_advert_reopen_btn);
        saveChangesButton = findViewById(R.id.add_advert_save_changes_btn);
        maskedPhoneInput = findViewById(R.id.add_advert_masked_phone_input);
        maskedPhoneVNInput = findViewById(R.id.add_advert_maskedVN_phone_input);
        maskedPhonePHInput = findViewById(R.id.add_advert_maskedPH_phone_input);
        removeMask = findViewById(R.id.add_advert_remove_phone_mask);
        isNewCheckBox = findViewById(R.id.add_advert_isNew);
        isNewRadioBtn = findViewById(R.id.add_advert_isNew_radio);
        isUsedRadioBtn = findViewById(R.id.add_advert_isUsed_radio);
        yearInput = findViewById(R.id.add_advert_year_input);
        yearLayout = findViewById(R.id.add_advert_year_layout);

        loadingBar = new ProgressDialog(AddAdvertActivity.this);


        if(documentName == null){
            photoFrameLayout = findViewById(R.id.add_advert_photo_frame);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(photoFrameLayout.getId(), new PhotoWidgetFragment(AddAdvertActivity.this));
            fragmentTransaction.commit();
        }

    }

    private void setupDB(){
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId  = mAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("USERS").document(userId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        DocumentSnapshot userDoc = task.getResult();
                        User currentUser =User.documentSnapshotToUser(userDoc);
                        userName = currentUser.getUsername();
                    } else {
                        userName = "error";
                    }
                } else {
                    userName = "error";
                }
            }
        });

    }

    private void setupCategoryView(){
        categoryFrameLayout = findViewById(R.id.add_activity_category_frameLayout);
        if(documentName == null)
            createScrollView.setVisibility(View.VISIBLE);

        bikeCategory = findViewById(R.id.add_advert_category_bike);
        carCategory = findViewById(R.id.add_advert_category_car);
        sportBikeCategory = findViewById(R.id.add_advert_category_sport_bike);
        bigCarCategory = findViewById(R.id.add_advert_category_big_car);
        cycleCategory = findViewById(R.id.add_advert_category_cycle);
        otherCategory = findViewById(R.id.add_advert_category_other);

        bikeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryInput.setText("Bike");
                categoryFrameLayout.setVisibility(View.GONE);
                createScrollView.setVisibility(View.VISIBLE);
                menuItemClose.setVisible(false);
                yearLayout.setVisibility(View.VISIBLE);
            }
        });

        carCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryInput.setText("Car");
                categoryFrameLayout.setVisibility(View.GONE);
                createScrollView.setVisibility(View.VISIBLE);
                menuItemClose.setVisible(false);
                yearLayout.setVisibility(View.VISIBLE);
            }
        });

        sportBikeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryInput.setText("Motorcycle");
                categoryFrameLayout.setVisibility(View.GONE);
                createScrollView.setVisibility(View.VISIBLE);
                menuItemClose.setVisible(false);
                yearLayout.setVisibility(View.VISIBLE);
            }
        });

        bigCarCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryInput.setText("Truck");
                categoryFrameLayout.setVisibility(View.GONE);
                createScrollView.setVisibility(View.VISIBLE);
                menuItemClose.setVisible(false);
                yearLayout.setVisibility(View.VISIBLE);
            }
        });

        cycleCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryInput.setText("Cycle");
                categoryFrameLayout.setVisibility(View.GONE);
                createScrollView.setVisibility(View.VISIBLE);
                menuItemClose.setVisible(false);
                yearLayout.setVisibility(View.GONE);
            }
        });

        otherCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryInput.setText("Other");
                categoryFrameLayout.setVisibility(View.GONE);
                createScrollView.setVisibility(View.VISIBLE);
                menuItemClose.setVisible(false);
                yearLayout.setVisibility(View.GONE);

            }
        });

    }

    private void loadData() {
        setupCreateView();
        setupCategoryView();

        countryName = getResources().getStringArray(R.array.countryNameEng);
        initArray(countryItems, countryName);

        setupCountryDataBase(countryNameExstras);

        //firebaseFirestore.collection("TEST").document(documentName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        collectionReference.document(documentName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    getData(document);
                    putData();
                    setupButton();
                    endOfLoad(1);
                }else{
                    //internet error
                }
            }
        });

    }

    private void getData(DocumentSnapshot doc){
        advert = Advert.documetnSnapshotToAdvert(doc);
        category = advert.getCategory();
        country = advert.getCountry();
        city = advert.getCity();
        title = advert.getTittle();
        price = (long)advert.getPrice();
        phone = advert.getPhone();
        year = advert.getYear();
        description = advert.getDescription();
        if(advert.getDownloadUrls() != null){
            downloadUrls = new ArrayList<String>(advert.getDownloadUrls());
            fileNames = new ArrayList<String>(advert.getImgNames());
        }else{
            downloadUrls = new ArrayList<String>();
            fileNames = new ArrayList<String>();
        }
        dateTime = advert.getDateTime();
        isNew = advert.getNew();
        if(doc.contains("reopenCount")){
            reopenCount = doc.getLong("reopenCount").intValue();
        }

    }

    private void putData(){
        categoryInput.setText(category);
        if(category.equals("Other") || category.equals("Cycle")){
            yearLayout.setVisibility(View.GONE);
        }


        countryInput.setText(country);
        if(country.equals("Vietnam")){
            setupVN();

        }else if(country.equals("Philippines")){
            setupPH();
        }
        cityInput.setText(city);
        cityInput.setTextColor(Color.parseColor("#000000"));
        titleInput.setText(title);
        priceInput.setText(Long.toString(price));
        if(year!=0) {
            yearInput.setText(Integer.toString(year));
        }

        maskedPhoneInput.setText(phone);
        removeMask.setChecked(true);
        maskedPhoneInput.setVisibility(View.VISIBLE);
        maskedPhoneVNInput.setVisibility(View.GONE);
        maskedPhonePHInput.setVisibility(View.GONE);

        isNewCheckBox.setChecked(isNew);

        //radio
        if(isNew){
            isUsedRadioBtn.setChecked(false);
            isNewRadioBtn.setChecked(true);
        }else{
            isNewRadioBtn.setChecked(false);
            isUsedRadioBtn.setChecked(true);
        }


        descriptionInput.setText(description);

        photoFrameLayout = findViewById(R.id.add_advert_photo_frame);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(photoFrameLayout.getId(), new PhotoWidgetFragment(AddAdvertActivity.this, downloadUrls, fileNames));
        fragmentTransaction.commit();

    }

    @SuppressLint("ResourceAsColor")
    private void setupButton(){
        if(status == 1){
            saveChangesButton.setVisibility(View.VISIBLE);
            createButton.setVisibility(View.GONE);
            reopenButton.setVisibility(View.GONE);
            countryInput.setTextColor(R.color.colorGray);
        }else if(status == 2){
            reopenButton.setVisibility(View.VISIBLE);
            createButton.setVisibility(View.GONE);
            saveChangesButton.setVisibility(View.GONE);
            countryInput.setTextColor(R.color.colorGray);
        }else{
            createButton.setVisibility(View.VISIBLE);
            saveChangesButton.setVisibility(View.GONE);
            reopenButton.setVisibility(View.GONE);
        }

    }

    private void setupBtn(){
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputs()){
                    createAdvert();
                }
            }
        });
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputs()){
                    updateAdvert();
                }
            }
        });
        reopenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputs()){
                    reopenAdvert();
                }
            }
        });
    }

    private boolean checkInputs() {
        boolean result = true;

        Drawable customErrorIcon = getResources().getDrawable(R.drawable.ic_error_outline_black_24dp);
        customErrorIcon.setBounds(0, 0, customErrorIcon.getIntrinsicWidth(), customErrorIcon.getIntrinsicHeight());

        if (countryInput.getText().equals("Country")) {
            countryInput.setBackgroundResource(R.drawable.text_view_wrong_shape);
            //createScrollView.smoothScrollTo(0,0);
            result = false;
        }
        if (cityInput.getText().equals("City")) {
            cityInput.setBackgroundResource(R.drawable.text_view_wrong_shape);
            //scrollView.smoothScrollTo(0,0);
            result = false;
        }
        if (TextUtils.isEmpty(titleInput.getText().toString().trim())) {
            titleInput.setError("Title could not be empty!", customErrorIcon);
//            createScrollView.smoothScrollTo(0,0);
//            createScrollView.fling(0);
            result = false;
        }
        if (TextUtils.isEmpty(priceInput.getText())) {
            priceInput.setError("Price could not be empty!", customErrorIcon);
            result = false;
        }

        if(maskedPhoneInput.getVisibility() == View.VISIBLE){
            if (TextUtils.isEmpty(maskedPhoneInput.getUnmaskedText())) {
                maskedPhoneInput.setError("Phone could not be empty!!", customErrorIcon);
                result = false;
            }
        }else if(maskedPhonePHInput.getVisibility() == View.VISIBLE){
            if (TextUtils.isEmpty(maskedPhonePHInput.getUnmaskedText())) {
                maskedPhonePHInput.setError("Phone could not be empty!!", customErrorIcon);
                result = false;
            }
        }else if(maskedPhoneVNInput.getVisibility() == View.VISIBLE){
            if (TextUtils.isEmpty(maskedPhoneVNInput.getUnmaskedText())) {
                maskedPhoneVNInput.setError("Phone could not be empty!!", customErrorIcon);
                result = false;
            }
        }


        if (TextUtils.isEmpty(descriptionInput.getText().toString().trim())) {
            descriptionInput.setError("Description could not be empty!", customErrorIcon);
            result = false;
        }

        if(!TextUtils.isEmpty(yearInput.getText())){
            String yearStr =  yearInput.getText().toString();
            System.out.println("AAAAAAAAA " + yearStr);
            if(1960 > Integer.parseInt(yearStr) || 2020 < Integer.parseInt(yearStr)){
                yearInput.setError("Year should be upper then 1960 and less then current year", customErrorIcon);
                result = false;
            }
        }


        if(!result){
            Toast.makeText(AddAdvertActivity.this, "please fill all fields", Toast.LENGTH_SHORT).show();
            createScrollView.scrollTo(0,0);
        }


        return result;
    }

    private void preparationDataForAdvert(){

        category = categoryInput.getText().toString();
        country = countryInput.getText().toString();
        city = cityInput.getText().toString();
        title = titleInput.getText().toString();
        description = descriptionInput.getText().toString();
        date = new Date();
        dateTime = new Timestamp(date);
        price = Integer.parseInt(priceInput.getText().toString());
        currency = currencyTextView.getText().toString();

        if(!TextUtils.isEmpty(yearInput.getText())){
            year = Integer.parseInt(yearInput.getText().toString());
        }else{
            year = 0;
        }
        if(maskedPhoneInput.getVisibility() == View.VISIBLE){
            if (!TextUtils.isEmpty(maskedPhoneInput.getUnmaskedText())) {
                phone = maskedPhoneInput.getText().toString();
            }
        }else if(maskedPhonePHInput.getVisibility() == View.VISIBLE){
            if (!TextUtils.isEmpty(maskedPhonePHInput.getUnmaskedText())) {
                phone = maskedPhonePHInput.getText().toString();
            }
        }else if(maskedPhoneVNInput.getVisibility() == View.VISIBLE){
            if (!TextUtils.isEmpty(maskedPhoneVNInput.getUnmaskedText())) {
                phone = maskedPhoneVNInput.getText().toString();
            }
        }
    }


    private void createAdvert(){

        loadingBar.setTitle("Creating new Advert");
        loadingBar.setMessage("Please wait, while we are creating your new advert");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        preparationDataForAdvert();

        Advert newAdvertData = new Advert (
                userId, userName,
                category, country, city, title, description, phone,
                fileNames,
                downloadUrls,
                isNew,
                dateTime, price, currency, year
        );

        Map<String,Object> advertData = newAdvertData.toMap();
        //firebaseFirestore.collection("TEST")
        collectionReference
                .add(advertData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            try {
                                if(downloadUrls != null){
                                    if(downloadUrls.size() > 0){
                                        previewImg = downloadUrls.get(0);
                                    }
                                }
                                addAlgolia(title, date, previewImg, country, city, price, currency, year, category, task.getResult().getId(), isNew);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            //error
                        }
                    }
                });


    }

    private void updateAdvert(){
        loadingBar.setTitle("Update your Advert");
        loadingBar.setMessage("Please wait, while we are updating your advert");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        preparationDataForAdvert();

        Advert newAdvertData = new Advert (
                userId, userName,
                category, country, city, title, description, phone,
                fileNames,
                downloadUrls,
                isNew,
                dateTime, price, currency, year
        );

        Map<String,Object> advertData = newAdvertData.toMap();
        //firebaseFirestore.collection("TEST")
        collectionReference
                .document(documentName)
                .update(advertData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                            try {
                                if(downloadUrls != null){
                                    if(downloadUrls.size() > 0){
                                        previewImg = downloadUrls.get(0);
                                    }
                                }
                                updateAlgolia(title, previewImg, country, city, price, currency, year, category, documentName, isNew);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            //error
                        }
            }
        });

    }

    private void reopenAdvert(){
        loadingBar.setTitle("Reopen your Advert");
        loadingBar.setMessage("Please wait, while we are reopen your advert");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        preparationDataForAdvert();

        Advert newAdvertData = new Advert (
                userId, userName,
                category, country, city, title, description, phone,
                fileNames,
                downloadUrls,
                isNew,
                dateTime, price, currency, year
        );

        Map<String,Object> advertData = newAdvertData.toMap();
        advertData.put("isClosed", false);
        advertData.put("reasonOfClosing", "reopen");
        advertData.put("closeDateTime", new Timestamp(new Date()));
        advertData.put("reopenCount", reopenCount+1);
        if(reopenCount >= REOPEN_COUNT){
            loadingBar.cancel();
            loadingBar.setTitle("No more");
            loadingBar.setMessage("Yopu could nor reopen advert more than 3 times");
            loadingBar.closeOptionsMenu();
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

        }else {
            //firebaseFirestore.collection("TEST")
            collectionReference
                    .document(documentName)
                    .update(advertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        try {
                            if(downloadUrls != null){
                                if(downloadUrls.size() > 0){
                                    previewImg = downloadUrls.get(0);
                                }
                            }
                            addAlgolia(title, date, previewImg, country, city, price, currency, year, category, documentName, isNew);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //error
                    }
                }
            });
        }
    }

    private void updateAlgolia(String title, String downloadStringUriMain, String country, String city, long price, String currency, int year, String category, String documentName, boolean isNew) throws JSONException{

        //final Index index = client.getIndex("adverts_new");

        JSONObject object = new JSONObject()
                .put("title", title)
                .put("image", downloadStringUriMain)
                .put("country", country)
                .put("city", city)
                .put("price", price)
                .put("currency", currency)
                .put("year",year)
                .put("category", category)
                .put("documentReference", documentName)
                .put("isNew", isNew);
        JSONArray tags = new JSONArray()
                .put(country)
                .put(city)
                .put(category);
        if(downloadUrls != null && downloadUrls.size() > 0)
            tags.put("haveImage");
        if(isNew)
            tags.put("newProduct");

        object.put("_tags", tags);

        index.partialUpdateObjectAsync(object, documentName, null);

        loadingBar.cancel();

        Intent advertActivity = new Intent(AddAdvertActivity.this, AdvertActivity.class);
        advertActivity.putExtra("documentName", documentName);
        advertActivity.putExtra("country", country);
        AddAdvertActivity.this.startActivity(advertActivity);
        setResult(RESULT_OK);
        AddAdvertActivity.this.finish();
    }

    private void addAlgolia(String title, Date dateTime, String downloadStringUriMain, String country, String city, long price, String currency, int year, String category, String documentName, boolean isNew) throws JSONException {

        //final Index index = client.getIndex("adverts_new");

        JSONObject object = new JSONObject()
                .put("title", title)
                .put("dateTime",dateTime.getTime())
                .put("country", country)
                .put("city", city)
                .put("price", price)
                .put("currency", currency)
                .put("year",year)
                .put("category", category)
                .put("documentReference", documentName)
                .put("isNew", isNew);

        if (downloadStringUriMain == null){
            object.put("image", "null");
        }else{
            object.put("image", downloadStringUriMain);
        }

        JSONArray tags = new JSONArray()
                .put(country)
                .put(city)
                .put(category);
        if(downloadUrls != null && downloadUrls.size() > 0)
            tags.put("haveImage");
        if(isNew)
            tags.put("newProduct");

        object.put("_tags", tags);

        index.addObjectAsync(object, documentName, null);

        loadingBar.cancel();

        Intent advertActivity = new Intent(AddAdvertActivity.this, AdvertActivity.class);
        advertActivity.putExtra("documentName", documentName);
        advertActivity.putExtra("country", country);
        AddAdvertActivity.this.startActivity(advertActivity);
        setResult(RESULT_OK);
        AddAdvertActivity.this.finish();
    }

    private void endOfLoad(int id){
        if(id == 0) {
            loadPage.setVisibility(View.GONE);
            createScrollView.setVisibility(View.GONE);
            categoryFrameLayout.setVisibility(View.VISIBLE);
        }else{
            loadPage.setVisibility(View.GONE);
            createScrollView.setVisibility(View.VISIBLE);
        }
    }

    private void setupCountryDataBase(String currentCountry){
        if(currentCountry.equals("Vietnam")){
            collectionReference = firebaseFirestore.collection("TEST_VN");
            index = client.getIndex("adverts_VN_TEST");
        }else if(currentCountry.equals("Philippines")){
            collectionReference = firebaseFirestore.collection("TEST_PH");
            index = client.getIndex("adverts_PH_TEST");
        }
    }

    private void updateOnlyPhoto(){

        Advert newAdvertData = new Advert (
                fileNames,
                downloadUrls
        );

        Map<String,Object> advertData = newAdvertData.toMapPhoto();

        collectionReference
                .document(documentName)
                .update(advertData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    try {
                        if(downloadUrls != null){
                            if(downloadUrls.size() > 0){
                                previewImg = downloadUrls.get(0);
                            }
                        }
                        updateAlgoliaPhoto(previewImg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //error
                }
            }
        });
    }

    private void updateAlgoliaPhoto(String downloadStringUriMain) throws JSONException {
        JSONObject object = new JSONObject();

        if (downloadStringUriMain == null){
            object.put("image", "null");
        }else{
            object.put("image", downloadStringUriMain);
        }
        //index.addObjectAsync(object, documentName, null);
        index.partialUpdateObjectAsync(object, documentName, null);
        loadingBar.cancel();
    }

    private void deleteAllPhotos(){
        if(fileNames.size() > 0){
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference().child("ADVERTS_IMAGES_TEST_FOLDER3");
            for (String fileName : fileNames) {
                StorageReference filePath = storageReference.child(fileName);
                filePath.delete();
            }
        }
    };

    //----------------------------------------------------------------------system-----------------------------------------------------------------

    private void initArray(ArrayList<String> list, String[] values){
        list.clear();
        for(int i = 0; i <values.length; i++){
            list.add(values[i]);
        }
    }

    private void setupVN(){
        countryInput.setText("Vietnam");
        countryInput.setTextColor(Color.parseColor("#000000"));
        currencyTextView.setText("VND");
        cityName = getResources().getStringArray(R.array.cities_vietnam_eng);
        initArray(cityItems, cityName);
        maskedPhoneInput.setVisibility(View.GONE);
        maskedPhoneVNInput.setVisibility(View.VISIBLE);
        maskedPhonePHInput.setVisibility(View.GONE);
    }

    private void setupPH(){
        countryInput.setText("Philippines");
        countryInput.setTextColor(Color.parseColor("#000000"));
        currencyTextView.setText("PHP");
        cityName = getResources().getStringArray(R.array.cities_philippines);
        initArray(cityItems, cityName);
        maskedPhoneInput.setVisibility(View.GONE);
        maskedPhoneVNInput.setVisibility(View.GONE);
        maskedPhonePHInput.setVisibility(View.VISIBLE);
    }


}
