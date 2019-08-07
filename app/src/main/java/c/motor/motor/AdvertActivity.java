package c.motor.motor;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import c.motor.motor.adapter.SliderAdapter;
import c.motor.motor.addAdvertActivity.AddAdvertActivity;
import c.motor.motor.model.Advert;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

public class AdvertActivity extends AppCompatActivity {

    private static final int ADD_ACTIVITY_CODE = 200;
    private static final int REQUEST_INVITE= 532;

    private FirebaseFirestore firebaseFirestore;

    private String documentName;
    private String userId;
    private FirebaseUser user;
    private Boolean isFavorite = false;
    String reasonOfClose = "Sold";

    private Advert advert;
    ArrayList<String> urls;
    ClipboardManager clipboard;

    private ConstraintLayout loadingPage;
    private ScrollView advertPage;
    private FloatingActionButton fabCall;
    private LinearLayout
            sameAdvertBlock,
            ownerBlock,
            reopenBlock;

    private Button closeBtn, editBtn, reopenBtn;

    private RelativeLayout frameLayout;
    private Toolbar toolbar;
  //  public SliderView sliderView;
    public TextView
            titleTextView,
            isNewTextView,
            soldTextView,
            currencyTextView,
            priceTextView,
            locationTextView,
            sellerTextView,
            phoneTextView,
            descriptionTextView,
            dateTimeTextView,
            yearTextView,
            categoryTextView;
    public ImageButton copyPhoneButton;
    public LinearLayout yearLayout;
    private Menu menu;
    private MenuItem menuItemFavorite;
    private int status;
    private String countryNameExstras;
    private String collectionPath;
    private String favoriteCollectionPath;
    private String indexName;

    DecimalFormat priceFormat = new DecimalFormat("###,###,###,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert);
        Intent intent = getIntent();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId= user.getUid();
        }
        if(intent != null){
            documentName = intent.getStringExtra("documentName");
            countryNameExstras = intent.getStringExtra("country");
            System.out.println("Advert activity countryNameExstras is " + countryNameExstras);
            if (countryNameExstras == null){

            }
        }
        setupCountryDataBase(countryNameExstras);

        loadingPage = findViewById(R.id.advert_load_page);
        advertPage = findViewById(R.id.advert_page);

        //get document data
        if(documentName != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection(collectionPath).document(documentName)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        urls = new ArrayList<>();
                        titleTextView = findViewById(R.id.advert_title);
                        soldTextView = findViewById(R.id.advert_title_closed);
                        isNewTextView = findViewById(R.id.advert_title_isNew);
                        currencyTextView = findViewById(R.id.advert_currency);
                        priceTextView = findViewById(R.id.advert_price);
                        locationTextView = findViewById(R.id.advert_location);
                        dateTimeTextView =findViewById(R.id.advert_dateTime);
                        sellerTextView = findViewById(R.id.advert_seller_name);
                        phoneTextView = findViewById(R.id.advert_phone);
                        descriptionTextView = findViewById(R.id.advert_description);
                        copyPhoneButton = findViewById(R.id.advert_phone_copy);
                        sameAdvertBlock = findViewById(R.id.advert_the_same_block);
                        ownerBlock = findViewById(R.id.advert_owner_block);
                        reopenBlock = findViewById(R.id.advert_owner_reopen_block);
                        yearLayout = findViewById(R.id.advert_year_layout);
                        yearTextView = findViewById(R.id.advert_year_textView);
                        categoryTextView = findViewById(R.id.advert_category_textView);

                        DocumentSnapshot document = task.getResult();

                        if(document.contains("isClosed")){
                            if((boolean)document.get("isClosed") == true){
                                setupAdvert(document, true);
                                setupClosedAdvert();
                                titleTextView.setPadding(0,12,12,12);
                                status = 2;
                            }else{
                                setupAdvert(document, false);
                                status = 1;
                            }
                        }else{
                            setupAdvert(document,false);
                            status = 1;
                        }

                    }else{
                        //problem with loading
                    }
                }
            });
        }else{
            //if document name null
        }
        toolbar = findViewById(R.id.advert_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_undo_blue);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdvertActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.advert, menu);
        this.menu = menu;
        //menu.findItem(R.id.action_search).setVisible(false);
        //menuItemFavorite = (MenuItem)findViewById(R.id.advert_action_favorite);
        //menuItemFavorite.setIcon(getResources().getDrawable(R.drawable.ic_favorite_blue));
        setupFavoriteOption();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItemFavorite = menu.findItem(R.id.advert_action_favorite);
        //menuItemFavorite.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_blue));
        setupFavoriteOption();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ACTIVITY_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.advert_action_share) {

            BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                    .setCanonicalIdentifier(documentName)
                    .setTitle(titleTextView.getText().toString())
                    .setContentImageUrl(urls.get(0))
                    .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                    .addContentMetadata("documentName", documentName)
                    .addContentMetadata("countryName", countryNameExstras);
//
//            LinkProperties linkProperties = new LinkProperties()
//                    .setFeature("invite")
//                    .setChannel("someChannel");


            branchUniversalObject.generateShortUrl(
                    getBaseContext(),
                    new LinkProperties(),
                    new Branch.BranchLinkCreateListener() {
                        @Override
                        public void onLinkCreate(String url, BranchError error) {
                            Log.d("BranchTest", "BranchLinkCreateListener.onLinkCreate was triggered");
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            String shareBody = documentName;
                            intent.putExtra(Intent.EXTRA_TEXT, url);
                            startActivity(Intent.createChooser(intent, "Share"));
                        }
                    });



//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/plain");
//            String shareBody = documentName;
//            intent.putExtra(Intent.EXTRA_TEXT, "http://motor");
//            startActivity(Intent.createChooser(intent, "Share"));

//            Intent intent = new AppInviteInvitation.IntentBuilder("title")
//                    .setMessage("message")
//                    .setDeepLink(Uri.parse("https://firebase.google.com/docs/invites/android"))
//                    //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
//                    .setCallToActionText("action")
//                    .build();
//            startActivityForResult(intent., REQUEST_INVITE);
//            return true;
        }
        if (id == R.id.advert_action_favorite) {
            if(userId == null || userId.equals("")){
                regIntent();
            }else{
                if (isFavorite){
                    unLike();
                }else{
                    like();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpSlider(ArrayList<String> urls, Boolean isSold, String category){

        final SliderView sliderView = findViewById(R.id.advert_imageSlider);
        final SliderAdapter adapter = new SliderAdapter(this, urls, urls.size(), isSold, category);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setAutoCycle(false);
    }

    private void setUpSearchFragment(String tittleQuery){
        System.out.println("Advert activity countryNameExstras is 22 " + countryNameExstras);
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tittleQuery", tittleQuery);
        bundle.putString("countryQuery", countryNameExstras);
        bundle.putString("categoryQuery", advert.getCategory().toLowerCase());
        bundle.putString("cityQuery", advert.getCity());
        bundle.putString("except",documentName);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        searchFragment.setArguments(bundle);
        fragmentTransaction.replace(frameLayout.getId(), searchFragment);
        fragmentTransaction.commit();

    }

    private void closeAdvert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AdvertActivity.this);
        final String[] mChooseReason = { "Sold", "Not actual", "Different" };
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Please choose reason")
                .setCancelable(false)

                .setNegativeButton("back",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        })

                .setPositiveButton("close advert",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();

                                HashMap<String, Object> closeData = new HashMap<>();
                                closeData.put("isClosed", true);
                                closeData.put("reasonOfClosing", reasonOfClose);
                                closeData.put("closeDateTime", new Timestamp(new Date()));

                                firebaseFirestore.collection(collectionPath).document(documentName).update(closeData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        deleteAlgolia(documentName);

                                        Intent advertActivity = new Intent(AdvertActivity.this, AdvertActivity.class);
                                        advertActivity.putExtra("documentName", documentName);
                                        advertActivity.putExtra("country", countryNameExstras);
                                        startActivity(advertActivity);

                                        finish();
                                    }
                                });
                            }
                        })

                // добавляем переключатели
                .setSingleChoiceItems(mChooseReason, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int item) {
                                reasonOfClose = mChooseReason[item];
                                Toast.makeText(
                                        getApplicationContext(),
                                        "choose: "
                                                + mChooseReason[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("RestrictedApi")
    private void setupAdvert(DocumentSnapshot document, Boolean isSold){
        advert = Advert.documetnSnapshotToAdvert(document);

        if(advert.getDownloadUrls() != null){
            if(advert.getDownloadUrls().size() != 0);
            urls = new ArrayList<String>(advert.getDownloadUrls());
        }

        setUpSlider(urls, isSold, advert.getCategory());
        if(advert.getNew()){
            isNewTextView.setVisibility(View.VISIBLE);
            titleTextView.setPadding(0,12,12,12);
        }
        if(advert.getTittle() != null)
            titleTextView.setText(advert.getTittle());
        if(advert.getCurrency() != null)
            currencyTextView.setText(advert.getCurrency().toUpperCase());
        priceTextView.setText(
                String.format(priceFormat.format(advert.getPrice()))
        );
        locationTextView.setText(advert.getCountry() + " - " + advert.getCity());
        sellerTextView.setText(advert.getCreatorName());
        phoneTextView.setText(advert.getPhone());
        descriptionTextView.setText(advert.getDescription());
        categoryTextView.setText(advert.getCategory());
        if(advert.getYear() == 0){
            yearLayout.setVisibility(View.GONE);
        }else{
            yearTextView.setText(Integer.toString(advert.getYear()));
        }

        int currentDate = Calendar.getInstance().get(Calendar.DATE);
        Date date = new Date(advert.getDateTime().toDate().getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd-yyyy 'at' HH:mm");
        SimpleDateFormat todayDateFormat = new SimpleDateFormat("'Today at' HH:mm");
        SimpleDateFormat yesterdayDateFormat = new SimpleDateFormat("'Yesterday at' HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int advertDate = cal.get(Calendar.DATE);
        if(currentDate == advertDate){
            dateTimeTextView.setText(todayDateFormat.format(date));
        }else if(currentDate-1 == advertDate ){
            dateTimeTextView.setText(yesterdayDateFormat.format(date));
        }else{
            dateTimeTextView.setText(dateFormat.format(date));
        }



        copyPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("phone", advert.getPhone());
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(AdvertActivity.this, "Phone number was copied", Toast.LENGTH_LONG).show();
            }
        });
        fabCall = findViewById(R.id.fab_call);
        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                         Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                String phone = advert.getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
        frameLayout = findViewById(R.id.advert_same_frame_layout);

        loadingPage.setVisibility(View.GONE);
        advertPage.setVisibility(View.VISIBLE);

        if(userId != null && userId.equals(advert.getCreatorId())){
            sameAdvertBlock.setVisibility(View.GONE);
            if(isSold){
                reopenBlock.setVisibility(View.VISIBLE);
                ownerBlock.setVisibility(View.GONE);

            }else{
                ownerBlock.setVisibility(View.VISIBLE);
            }
            closeBtn = findViewById(R.id.advert_owner_close_btn);
            editBtn = findViewById(R.id.advert_owner_edit_btn);
            reopenBtn = findViewById(R.id.advert_owner_reopen_btn);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeAdvert();
                }
            });
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addIntent = new Intent(AdvertActivity.this , AddAdvertActivity.class);
                    addIntent.putExtra("documentName", documentName);
                    addIntent.putExtra("status", status);
                    addIntent.putExtra("country", advert.getCountry());
                    startActivityForResult(addIntent, ADD_ACTIVITY_CODE);
                }
            });
            reopenBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addIntent = new Intent(AdvertActivity.this , AddAdvertActivity.class);
                    addIntent.putExtra("documentName", documentName);
                    addIntent.putExtra("status", status);
                    addIntent.putExtra("country", advert.getCountry());
                    startActivityForResult(addIntent, ADD_ACTIVITY_CODE);
                }
            });

        }else{
            setUpSearchFragment("");
            fabCall.setVisibility(View.VISIBLE);
            ownerBlock.setVisibility(View.GONE);
        }
    }
    @SuppressLint("ResourceAsColor")
    private void setupClosedAdvert(){
        soldTextView.setVisibility(View.VISIBLE);
        titleTextView.setTextColor(R.color.colorGray);
        currencyTextView.setTextColor(R.color.colorGray);
        priceTextView.setTextColor(R.color.colorGray);
        locationTextView.setTextColor(R.color.colorGray);
        sellerTextView.setTextColor(R.color.colorGray);
        phoneTextView.setTextColor(R.color.colorGray);
        descriptionTextView.setTextColor(R.color.colorGray);
        dateTimeTextView.setTextColor(R.color.colorGray);
    }


    private void setupFavoriteOption(){
        String favoriteDocumentName = documentName + "-" + userId;
        firebaseFirestore.collection(favoriteCollectionPath).document(favoriteDocumentName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        menuItemFavorite.setIcon(getResources().getDrawable(R.drawable.ic_favorite_blue));
                        isFavorite = true;
                        //document liked
                    } else{
                        isFavorite = false;
                        //document unliked
                    }
                }else{
                    isFavorite = false;
                    //document unliked
                }
            }
        });
    }

    private void like(){
        String favoriteDocumentName = documentName + "-" + userId;
        Map<String, Object> likeData = new HashMap<>();
        likeData.put("documentName", documentName);
        likeData.put("userId", userId);
        likeData.put("isLiked", true);
        likeData.put("dateTime", new Timestamp(new Date()));
        firebaseFirestore.collection(favoriteCollectionPath).document(favoriteDocumentName).set(likeData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        menuItemFavorite.setIcon(getResources().getDrawable(R.drawable.ic_favorite_blue));
                        isFavorite = true;
                        Toast.makeText(AdvertActivity.this, "Added to favorite", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdvertActivity.this, "Problem with internet connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void unLike(){
        String favoriteDocumentName = documentName + "-" + userId;
        firebaseFirestore.collection(favoriteCollectionPath).document(favoriteDocumentName).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        menuItemFavorite.setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_blue));
                        isFavorite = false;
                        Toast.makeText(AdvertActivity.this, "Was remove from favorite", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdvertActivity.this, "Problem with internet connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void regIntent(){
        Intent addIntent = new Intent(AdvertActivity.this ,RegisterActivity.class);
        startActivity(addIntent);
        //MainActivity.this.finish();
    }

    private void deleteAlgolia(String objectID) {
        Client client = new Client("STZV8Z6R6G", "ce681eb47a47a0f885d46617049791c7");
        final Index index = client.getIndex(indexName);
        index.deleteObjectAsync(objectID, null);
        System.out.println("was deleted" + objectID);
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

}
