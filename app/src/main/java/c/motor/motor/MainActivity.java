package c.motor.motor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

import c.motor.motor.addAdvertActivity.AddAdvertActivity;
import c.motor.motor.helpers.Helpers;
import c.motor.motor.helpers.SharedPreferenceUtils;
import c.motor.motor.model.User;
import c.motor.motor.privateActivity.PrivateMainActivity;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final private int SEARCH_SETUP_ACTIVITY_CODE = 606;
    private static final int ADD_ACTIVITY_CODE = 200;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;

    private EditText searchInput;
    private Drawable clearSearchImg;
    private Drawable searchImg;

    private String userId;
    private User currentUser;
    private String userName, userEmail;
    private String textQuery;

    private FrameLayout frameLayout;
    //private ScrollView frameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String
            categorySearch = null,
            countrySearch = null,
            citySearch = null;

    private long
            priceFromSearch = 0,
            priceTillSearch = 0;

    private boolean
            onlyNew = false,
            pictureSearch = false,
            sortByDate = true,
            sortByHighPrice = false,
            sortByLowPrice = false;

    private int
            minYear = 1960,
            maxYear = 2030;

    private String currentCountry;
    private String currentCity;
    private String currentLanguage;
    private boolean searchSetupIsEmpty = true;

    Dialog settingsDialog;

    private boolean isFirstSetup = false;

    //settings variable
    ArrayList<String> countryItems = new ArrayList<>();
    ArrayList<String> cityItems = new ArrayList<>();
    ArrayList<String> languageItems = new ArrayList<>();

    SpinnerDialog
            spinnerDialogCountry,
            spinnerDialogCity,
            spinnerDialogLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpCountryAndLanguage();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null){
                    //addAdvertIntent();
                    Intent addIntent = new Intent(MainActivity.this , AddAdvertActivity.class);
                    startActivity(addIntent);
                }else{
                    regIntent();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationIcon(R.drawable.ic_menu_blue);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if(user != null)
        {
            navigationView.getMenu().clear();
            navigationView.inflateHeaderView(R.layout.nav_header_main_with_login);
            navigationView.inflateMenu(R.menu.navigation_with_login);

            View header = navigationView.getHeaderView(0);

            final TextView userNameTextView = header.findViewById(R.id.nav_header_user_name);
            final TextView userEmailTextView = header.findViewById(R.id.nav_header_user_email);

            userId = user.getUid();
            firebaseFirestore.collection("USERS").document(userId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            DocumentSnapshot userDoc = task.getResult();

                            currentUser =User.documentSnapshotToUser(userDoc);
                            userEmail = currentUser.getEmail();
                            userName = currentUser.getUsername();

                            userNameTextView.setText(userName);
                            userEmailTextView.setText(userEmail);
                        } else {
                            userNameTextView.setText("Your account is broken");
                            userEmailTextView.setText("Your account is broken");
                        }
                    } else {
                        userNameTextView.setText("Your account is broken");
                        userEmailTextView.setText("Your account is broken");
                    }
                }
            });
        } else
        {
            navigationView.getMenu().clear();
            navigationView.inflateHeaderView(R.layout.nav_header_main_with_logout);
            navigationView.inflateMenu(R.menu.navigation_with_logout);


            View header = navigationView.getHeaderView(0);

            Button loginBtn = header.findViewById(R.id.menu_sign_in_btn);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    regIntent();
                }
            });
        }

        searchInput = findViewById(R.id.toolbar_main_search);
        clearSearchImg = getDrawable( R.drawable.ic_cancel_gray);
        searchImg = getDrawable(R.drawable.ic_search_gray);

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);
        frameLayout = findViewById(R.id.main_frameLayout);
        swipeRefreshLayout = findViewById(R.id.main_swipe_refresh);
        setUpSearchFragment("");
        //setUpHomeFragment(null);
        //setFragment(new HomeFragment());
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpSearchFragment("");
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        settingsDialog = new Dialog(this);

        if(isFirstSetup){
            ShowSettingsPopup(getCurrentFocus());
        }

    }

    public void setUpCountryAndLanguage() {
        firstPreferenceCountry();
        firstPreferenceLanguage();
        SharedPreferenceUtils.Of utils = SharedPreferenceUtils.of(getApplicationContext());

        currentCountry = utils.getItem("country", null);
        currentCity = utils.getItem("city", null);
        currentLanguage = utils.getItem("language", null);

        System.out.println("currentCountry" + currentCountry);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_UP) {
            final View view = getCurrentFocus();

            if(view != null) {
                final boolean consumed = super.dispatchTouchEvent(ev);

                final View viewTmp = getCurrentFocus();
                final View viewNew = viewTmp != null ? viewTmp : view;

                if(viewNew.equals(view)) {
                    final Rect rect = new Rect();
                    final int[] coordinates = new int[2];

                    view.getLocationOnScreen(coordinates);

                    rect.set(coordinates[0], coordinates[1], coordinates[0] + view.getWidth(), coordinates[1] + view.getHeight());

                    final int x = (int) ev.getX();
                    final int y = (int) ev.getY();

                    if(rect.contains(x, y)) {
                        return consumed;
                    }
                }
                else if(viewNew instanceof EditText) {
                    return consumed;
                }

                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(viewNew.getWindowToken(), 0);

                viewNew.clearFocus();

                return consumed;
            }
        }

        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH_SETUP_ACTIVITY_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if(data != null){
                    categorySearch = data.getStringExtra("category");
                    citySearch = data.getStringExtra("city");
                    countrySearch = data.getStringExtra("country");
                    priceFromSearch = data.getLongExtra("priceFrom", 0);
                    priceTillSearch = data.getLongExtra("priceTill", 0);
                    onlyNew = data.getBooleanExtra("onlyNew", false);
                    pictureSearch = data.getBooleanExtra("pictureCheckBox", false);
                    sortByDate = data.getBooleanExtra("sortByDate", true);
                    sortByHighPrice = data.getBooleanExtra("sortByHighPrice", false);
                    sortByLowPrice = data.getBooleanExtra("sortByLowPrice",false);
                    minYear = data.getIntExtra("minYear",minYear);
                    maxYear = data.getIntExtra("maxYear",maxYear);
                    System.out.println("search setup country " + countrySearch);

                    System.out.println("only new is " + onlyNew);
                    searchSetupIsEmpty = false;
                }else{
                    searchSetupIsEmpty = true;
                }
                searchData(textQuery);
            }
        }
        if (requestCode == ADD_ACTIVITY_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if(data != null){
                    categorySearch = data.getStringExtra("category");
                    citySearch = data.getStringExtra("city");
                    countrySearch = data.getStringExtra("country");
                    priceFromSearch = data.getLongExtra("priceFrom", 0);
                    priceTillSearch = data.getLongExtra("priceTill", 0);
                    onlyNew = data.getBooleanExtra("onlyNew", false);
                    pictureSearch = data.getBooleanExtra("pictureCheckBox", false);
                    sortByDate = data.getBooleanExtra("sortByDate", true);
                    sortByHighPrice = data.getBooleanExtra("sortByHighPrice", false);
                    sortByLowPrice = data.getBooleanExtra("sortByLowPrice",false);
                    minYear = data.getIntExtra("minYear",minYear);
                    maxYear = data.getIntExtra("maxYear",maxYear);
                    System.out.println("search setup country " + countrySearch);

                    System.out.println("only new is " + onlyNew);
                    searchSetupIsEmpty = false;
                }else{
                    searchSetupIsEmpty = true;
                }
                searchData(textQuery);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchSettings = menu.findItem(R.id.action_search_settings);
        searchSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setupSearchIntent();
                return true;
            }
        });
        //SearchView
        searchInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setCursorVisible(true);
            }
        });
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searchInput.setCursorVisible(true);
                searchInput.setCompoundDrawablesWithIntrinsicBounds( searchImg, null, clearSearchImg, null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setCursorVisible(true);
                searchInput.setCompoundDrawablesWithIntrinsicBounds( searchImg, null, clearSearchImg, null);
            }
        });
        searchInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                try{
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (searchInput.getRight() - searchInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            searchInput.setText("");
                            searchInput.setCompoundDrawablesWithIntrinsicBounds( searchImg, null, null, null);

                            return true;
                        }
                    }
                    return false;
                }
                catch(Exception e){
                    return false;
                }
            }
        });
        searchInput.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        textQuery = searchInput.getText().toString().trim();
                        searchInput.setText(textQuery);

                        if(textQuery.equals("") || textQuery.equals(" ")) {
                            searchData("");
                            System.out.println("----------------");
                        } else {
                            searchData(textQuery);
                            System.out.println("++++++++++++++++");
                        }
                        searchInput.setCompoundDrawablesWithIntrinsicBounds( searchImg, null, null, null);
                        searchInput.setCursorVisible(false);
                        InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        input.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        });
        return true;
    }

    private void searchData(String query) {
        //setUpHomeFragment(query);
        setUpSearchFragment(query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            searchData(textQuery);
        } else if (id == R.id.nav_saved_advert) {
            if(user != null){
                setupPrivateAdvertActivity("Favorite", 1);
            }else{
                regIntent();
            }
        } else if (id == R.id.nav_my_adverts) {
            if(user != null){
                setupPrivateAdvertActivity("My adverts", 2);
            }else{
                regIntent();
            }
        } else if (id == R.id.nav_add_advert) {
            if(user != null){
                //addAdvertIntent();
                Intent addIntent = new Intent(MainActivity.this , AddAdvertActivity.class);
                startActivity(addIntent);
            }else{
                regIntent();
            }
        } else if (id == R.id.nav_user_settings) {
            ShowSettingsPopup(getCurrentFocus());

        } else if (id == R.id.nav_Logout){
            mAuth.signOut();
            Intent addIntent = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(addIntent);
            MainActivity.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void regIntent(){
        Intent addIntent = new Intent(MainActivity.this ,RegisterActivity.class);
        startActivity(addIntent);
        MainActivity.this.finish();
    }
    private void setupPrivateAdvertActivity(String title, int i){
        Intent addIntent = new Intent(MainActivity.this , PrivateMainActivity.class);
        addIntent.putExtra("title", title);
        addIntent.putExtra("id", i);
        startActivity(addIntent);
        MainActivity.this.finish();
    }
    private void setupSearchIntent(){
        Intent addIntent = new Intent(MainActivity.this ,SetupSearchActivity.class);
        System.out.println(currentCountry + "current country");
        if(countrySearch == null){
           if(currentCountry.contentEquals("Vietnam") || currentCountry.contentEquals("Philippines")){
               addIntent.putExtra("country", currentCountry);
           }else{
               addIntent.putExtra("country", countrySearch);
           }
        }else{
            addIntent.putExtra("country", countrySearch);
        }
        if(citySearch == null){
            addIntent.putExtra("city", currentCity);
        }else{
            addIntent.putExtra("city", countrySearch);
        }

        addIntent.putExtra("category", categorySearch);
        //addIntent.putExtra("city", citySearch);
        addIntent.putExtra("priceFrom", priceFromSearch);
        addIntent.putExtra("priceTill", priceTillSearch);
        addIntent.putExtra("onlyNew", onlyNew);
        addIntent.putExtra("pictureCheckBox", pictureSearch);
        addIntent.putExtra("sortByDate", sortByDate);
        addIntent.putExtra("sortByHighPrice", sortByHighPrice);
        addIntent.putExtra("sortByLowPrice", sortByLowPrice);
        System.out.println("TEars " +minYear + " " + maxYear);
        addIntent.putExtra("minYear", minYear);
        addIntent.putExtra("maxYear", maxYear);
        startActivityForResult (addIntent, SEARCH_SETUP_ACTIVITY_CODE);
        //MainActivity.this.finish();
    }
    private void setUpSearchFragment(String tittleQuery){
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        System.out.println("year2: " + minYear + " - " + maxYear);
        if(searchSetupIsEmpty) {
            bundle.putString("tittleQuery", tittleQuery);
            bundle.putString("countryQuery", currentCountry);
            bundle.putString("cityQuery", currentCity);

        }else{
            bundle.putString("tittleQuery", tittleQuery);
            if(countrySearch != null){
                bundle.putString("countryQuery", countrySearch);
            }else{
                bundle.putString("countryQuery", currentCountry);
            }
            bundle.putString("cityQuery", citySearch);
            bundle.putString("categoryQuery", categorySearch);
            bundle.putLong("priceFromQuery", priceFromSearch);
            bundle.putLong("priceTillQuery", priceTillSearch);
            bundle.putBoolean("withPictureQuery", pictureSearch);
            bundle.putBoolean("onlyNewQuery",onlyNew);
            bundle.putBoolean("sortByDate", sortByDate);
            bundle.putBoolean("sortByHighPrice", sortByHighPrice);
            bundle.putBoolean("sortByLowPrice", sortByLowPrice);
            bundle.putLong("minYear", minYear);
            bundle.putLong("maxYear", maxYear);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        searchFragment.setArguments(bundle);
        fragmentTransaction.replace(frameLayout.getId(), searchFragment);
        fragmentTransaction.commit();
    }

    private void firstPreferenceCountry(){
        Context context = getApplicationContext();
        String savedCountry = SharedPreferenceUtils.of(context).getItem("country", null);
        System.out.println("first setup +++ " + savedCountry);

        if (savedCountry != null) {
            return;
        }

        isFirstSetup = true;
        String userCountry = Helpers.getUserCountry(context, "Vietnam");
        SharedPreferenceUtils.of(context).setItem("country", userCountry);
    }

    private void firstPreferenceLanguage() {
        Context context = getApplicationContext();
        SharedPreferenceUtils.Of utils =SharedPreferenceUtils.of(context);
        String oldLanguage = utils.getItem("language", null);
        if (oldLanguage != null) {
            return;
        }
        String systemLanguage = Locale.getDefault().getLanguage();
        if (systemLanguage.equals("vn")) {
            utils.setItem("language", "Vietnamese");
        } else {
            utils.setItem("language", "English");
        }
    }

    public void ShowSettingsPopup (View v){
        //ImageButton closeBtn;
        final TextView countryInput;
        final TextView cityInput;
        final TextView languageInput;

        String previousCountry;

        Button saveButton;

        settingsDialog.setContentView(R.layout.popup_settings_layout);
        //closeBtn =  settingsDialog.findViewById(R.id.setup_user_close_btn);
        countryInput = settingsDialog.findViewById(R.id.setup_user_country_input);
        cityInput = settingsDialog.findViewById(R.id.setup_user_city_input);
        languageInput = settingsDialog.findViewById(R.id.setup_user_language_input);
        saveButton = settingsDialog.findViewById(R.id.setup_user_save);

        initArray(countryItems, getResources().getStringArray(R.array.countryNameEng));
        initArray(languageItems, getResources().getStringArray(R.array.languageNameEng));
        if(currentCity != null){
            cityInput.setText(currentCity);
        }
        countryInput.setText(currentCountry);
        languageInput.setText(currentLanguage);

        if(currentCountry.equals("Vietnam")){
            initArrayWithAll(cityItems, getResources().getStringArray(R.array.cities_vietnam_eng));
        }
        if(currentCountry.equals("Philippines")){
            initArrayWithAll(cityItems, getResources().getStringArray(R.array.cities_philippines));
        }


        spinnerDialogCountry = new SpinnerDialog(MainActivity.this,countryItems,"Choose country","close");
        spinnerDialogCountry.bindOnSpinerListener(new OnSpinerItemClick() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(String item, int position) {
                countryInput.setText(item);
                if(item.equals("Vietnam")){
                    initArrayWithAll(cityItems, getResources().getStringArray(R.array.cities_vietnam_eng));
                    cityInput.setText("All");
                }
                else if(item.equals("Philippines")){
                    initArrayWithAll(cityItems, getResources().getStringArray(R.array.cities_philippines));
                    cityInput.setText("All");
                }
                else{

                }
            }
        });

        countryInput.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                    spinnerDialogCountry.showSpinerDialog();
                    countryInput.setTextColor(R.color.colorGray);

            }
        });




        cityInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialogCity = new SpinnerDialog(MainActivity.this,cityItems,"Choose country","close");
                spinnerDialogCity.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String item, int position) {
                        cityInput.setText(item);
                    }
                });
                spinnerDialogCity.showSpinerDialog();
            }
        });


        spinnerDialogLanguage = new SpinnerDialog(MainActivity.this,languageItems,"Choose language","close");
        spinnerDialogLanguage.bindOnSpinerListener(new OnSpinerItemClick() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(String item, int position) {
                languageInput.setText(item);
            }
        });

        languageInput.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                spinnerDialogLanguage.showSpinerDialog();
                languageInput.setTextColor(R.color.colorGray);

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferenceUtils.Of utils = SharedPreferenceUtils.of(getApplicationContext());

                String country = countryInput.getText().toString();
                String city = cityInput.getText().toString();
                String language = languageInput.getText().toString();

                utils.setItem("country", country);
                utils.setItem("city", city);
                utils.setItem("language", language);


                Intent intent = new Intent(MainActivity.this , IntroActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
                settingsDialog.cancel();
            }
        });


        settingsDialog.show();
    }

    private void initArray(ArrayList<String> list, String[] values){
        list.clear();
        for(int i = 0; i <values.length; i++){
            list.add(values[i]);
        }
    }

    private void initArrayWithAll(ArrayList<String> list, String[] values){
        list.clear();
        list.add("All");
        for(int i = 0; i <values.length; i++){
            list.add(values[i]);
        }
    }
}

