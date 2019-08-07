package c.motor.motor;

import android.content.Intent;
import android.graphics.Color;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import c.motor.motor.helpers.AppData;
import c.motor.motor.helpers.Helpers;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class SetupSearchActivity extends AppCompatActivity {

    final private int MIN_YEAR = 1960;
    final private int MAX_YEAR = 2030;

    private FloatingActionButton searchFab;
    private ImageButton returnBtn;
    private LinearLayout clearBtn;

    private TextView categoryInput;
    private TextView countryInput;
    private TextView cityInput;
    private EditText priceFromInput;
    private EditText priceTillInput;
    private CheckBox pictureCheckBox;
    private CheckBox onlyNewCheckBox;
    private RadioGroup sortingRadioGroup;
    private RadioButton byDateRadioButton;
    private RadioButton byHighPriceRadioButton;
    private RadioButton byLowPriceRadioButton;
    private RangeSeekBar yearRange;
    private LinearLayout yearLayout;


    private ArrayList<String> categoryItems=new ArrayList<>();
    private ArrayList<String> countryItems=new ArrayList<>();
    private ArrayList<String> cityItems=new ArrayList<>();
    private String categoryName[];
    private String countryName[];
    private String cityName[];
    private SpinnerDialog spinnerDialogCategory;
    private SpinnerDialog spinnerDialogCountry;
    private SpinnerDialog spinnerDialogCity;
    private String previousItemCountry;

    private String
            category, categoryOld,
            city, cityOld,
            country, countryOld;

    private long
            priceFrom, priceFromOld,
            priceTill, priceTillOld;

    private boolean
            onlyNew = false, onlyNewOld,
            isPicture = false, isPictureOld,
            isSortByDate = true, isSortByDateOld,
            isSortByHighPrice = false, isSortByHighPriceOld,
            isSortByLowPrice = false, isSortByLowPriceOld;

    private int
            minYear = 1960, minYearOld,
            maxYear = 2030, maxYearOld;

    private String currentCountry;
    private String currentCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent != null){
            category = intent.getStringExtra("category");
            city = intent.getStringExtra("city");
            country = intent.getStringExtra("country");
            priceFrom = intent.getLongExtra("priceFrom", 0);
            priceTill = intent.getLongExtra("priceTill", 0);
            onlyNew = intent.getBooleanExtra("onlyNew", false);
            isPicture = intent.getBooleanExtra("pictureCheckBox", false);
            isSortByDate = intent.getBooleanExtra("sortByDate", true);
            isSortByHighPrice = intent.getBooleanExtra("sortByHighPrice", false);
            isSortByLowPrice = intent.getBooleanExtra("sortByLowPrice",false);
            minYear = intent.getIntExtra("minYear", MIN_YEAR);
            maxYear = intent.getIntExtra("maxYear", MAX_YEAR);

            categoryOld = category;
            cityOld = city;
            countryOld = country;
            onlyNewOld = onlyNew;
            priceFromOld = priceFrom;
            priceTillOld = priceTill;
            isPictureOld = isPicture;
            isSortByDateOld = isSortByDate;
            isSortByHighPriceOld = isSortByHighPrice;
            isSortByLowPriceOld = isSortByLowPrice;
            minYearOld = minYear;
            maxYearOld = maxYear;

        }

        setContentView(R.layout.activity_setup_search);
        searchFab = findViewById(R.id.setup_search_floatingSearchButton);
        returnBtn = findViewById(R.id.setup_search_go_back);
        clearBtn = findViewById(R.id.setup_search_clear);

        categoryInput = findViewById(R.id.setup_search_category);
        countryInput = findViewById(R.id.setup_search_country);
        cityInput = findViewById(R.id.setup_search_city);
        priceFromInput = findViewById(R.id.setup_search_price_from);
        priceTillInput = findViewById(R.id.setup_search_price_till);
        pictureCheckBox = findViewById(R.id.setup_search_checkbox_picture);
        onlyNewCheckBox = findViewById(R.id.setup_search_checkbox_new_products);
        sortingRadioGroup = findViewById(R.id.setup_search_radio_group);
        byDateRadioButton = findViewById(R.id.setup_search_radiobutton_sorting_date);
        byHighPriceRadioButton = findViewById(R.id.setup_search_radiobutton_sorting_high_price);
        byLowPriceRadioButton = findViewById(R.id.setup_search_radiobutton_sorting_less_price);
        yearRange = findViewById(R.id.setup_search_year_range);
        yearLayout = findViewById(R.id.setup_search_year_layout);

        System.out.println(yearRange.getAbsoluteMinValue() + " " + yearRange.getSelectedMaxValue() + " dsjfghjdfu");

        firstSetup();

        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainPage();
            }
        });
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comeBack();
            }
        });
        //category settings
        categoryName = getResources().getStringArray(R.array.categories_eng);
        categoryItems = Helpers.initArray(categoryName);
        spinnerDialogCategory = new SpinnerDialog(SetupSearchActivity.this, categoryItems, "Choose category", "close");
        categoryInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialogCategory.showSpinerDialog();
                spinnerDialogCategory.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String item, int position) {
                        categoryDepends(item);
                        categoryInput.setText(item);
                        categoryInput.setTextColor(Color.BLACK);
                    }
                });
            }
        });

        //region settings
        //country
        countryName = getResources().getStringArray(R.array.countryNameEng);
        countryItems = Helpers.initArray(countryName);
        spinnerDialogCountry = new SpinnerDialog(SetupSearchActivity.this, countryItems, "Choose country", "close");
        countryInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousItemCountry = countryInput.getText().toString();
                spinnerDialogCountry.showSpinerDialog();
                spinnerDialogCountry.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String item, int position) {
                        countryInput.setText(item);
                        countryInput.setTextColor(Color.BLACK);
                        if(!item.contentEquals(previousItemCountry)){
                            cityInput.setText("City");
                            cityInput.setTextColor(Color.parseColor("#919191"));

                        }
                        if(item.contentEquals("Vietnam")) {
                            cityName = getResources().getStringArray(R.array.cities_vietnam_eng);
                            cityItems = Helpers.initArrayWithAll(cityName);
                        }else if (item.contentEquals("Philippines")){
                            cityName = getResources().getStringArray(R.array.cities_philippines);
                            cityItems = Helpers.initArrayWithAll(cityName);
                        }
                    }
                });
            }
        });
        //city
        cityInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialogCity = new SpinnerDialog(SetupSearchActivity.this,cityItems,"Choose city", "close");
                spinnerDialogCity.showSpinerDialog();
                spinnerDialogCity.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String item, int position) {
                        cityInput.setText(item);
                        cityInput.setTextColor(Color.BLACK);
                    }
                });
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearch();
            }
        });

    }

    private void openMainPage(){
        preparationData();
        Intent addIntent = new Intent();
        addIntent.putExtra("category", category);
        addIntent.putExtra("country", country);
        addIntent.putExtra("city", city);
        addIntent.putExtra("priceFrom", priceFrom);
        addIntent.putExtra("priceTill", priceTill);
        addIntent.putExtra("onlyNew", onlyNew);
        addIntent.putExtra("pictureCheckBox", isPicture);
        addIntent.putExtra("sortByDate", isSortByDate);
        addIntent.putExtra("sortByHighPrice", isSortByHighPrice);
        addIntent.putExtra("sortByLowPrice", isSortByLowPrice);
        addIntent.putExtra("minYear", minYear);
        addIntent.putExtra("maxYear", maxYear);
        System.out.println("year1: " + minYear + " - " + maxYear);
        setResult(RESULT_OK, addIntent);
        finish();
    }

    private void comeBack(){
        finish();
    }

    private void firstSetup(){
        if(category != null && !category.contentEquals("All categories")) {
            categoryInput.setTextColor(Color.BLACK);
            categoryInput.setText(category);
            categoryDepends(category);
        }
        if(country != null && !country.contentEquals("Country")) {
            countryInput.setTextColor(Color.BLACK);
            countryInput.setText(country);
            if(country.contentEquals("Vietnam")) {
                cityName = getResources().getStringArray(R.array.cities_vietnam_eng);
                cityItems = Helpers.initArrayWithAll(cityName);
            }else if (country.contentEquals("Philippines")){
                cityName = getResources().getStringArray(R.array.cities_philippines);
                cityItems = Helpers.initArrayWithAll(cityName);
            }
        }
        if(city != null && !city.contentEquals("City")) {
            cityInput.setTextColor(Color.BLACK);
            cityInput.setText(city);
        }
        if(priceFrom != 0)
            priceFromInput.setText(Long.toString(priceFrom));
        if(priceTill != 0)
            priceTillInput.setText(Long.toString(priceTill));
        onlyNewCheckBox.setChecked(onlyNew);
        pictureCheckBox.setChecked(isPicture);
        byDateRadioButton.setChecked(isSortByDate);
        byHighPriceRadioButton.setChecked(isSortByHighPrice);
        byLowPriceRadioButton.setChecked(isSortByLowPrice);

        System.out.println(minYear + " " + maxYear + " cocococ");
        rangeSeekBarSetup(minYear, maxYear);
    }

    private void preparationData(){
        category = categoryInput.getText().toString();
        country = countryInput.getText().toString();
        city = cityInput.getText().toString();
        if(!TextUtils.isEmpty(priceFromInput.getText())) {
            priceFrom = (long) Double.parseDouble(priceFromInput.getText().toString());
        }else{
            priceFrom = 0;
        }
        if(!TextUtils.isEmpty(priceTillInput.getText())) {
            priceTill = (long) Double.parseDouble(priceTillInput.getText().toString());
        }else{
            priceTill = 0;
        }
        onlyNew = onlyNewCheckBox.isChecked();
        isPicture = pictureCheckBox.isChecked();
        isSortByDate = byDateRadioButton.isChecked();
        isSortByHighPrice = byHighPriceRadioButton.isChecked();
        isSortByLowPrice = byLowPriceRadioButton.isChecked();
    }

    private void clearSearch(){
        currentCountry = AppData.getPreference(getApplicationContext(), "country");
        currentCity = AppData.getPreference(getApplicationContext(), "city");
        if(currentCountry.contentEquals("Vietnam")){
            countryInput.setText("Vietnam");
            cityName = getResources().getStringArray(R.array.cities_vietnam_eng);
            countryInput.setTextColor(Color.BLACK);
            cityOld = "Vietnam";
        }else if(currentCountry.contentEquals("Philippines")){
            countryInput.setText("Philippines");
            cityName = getResources().getStringArray(R.array.cities_philippines);
            countryInput.setTextColor(Color.BLACK);
            cityOld = "Philippines";
        }else{
            countryInput.setText("Country");
            cityOld = "Country";
        }

        categoryInput.setText("All categories");
        categoryDepends("All categories");
        cityInput.setText(currentCity);
        priceFromInput.setText("");
        priceTillInput.setText("");
        onlyNewCheckBox.setChecked(false);
        pictureCheckBox.setChecked(false);
        byDateRadioButton.setChecked(true);
        byLowPriceRadioButton.setChecked(false);
        byHighPriceRadioButton.setChecked(false);
        rangeSeekBarSetup(MIN_YEAR, MAX_YEAR);

        categoryOld = "All categories";
        cityOld = "City";
        priceFromOld = 0;
        priceTillOld = 0;
        onlyNew = false;
        isPictureOld = false;
        isSortByDateOld = true;
        isSortByHighPriceOld = false;
        isSortByLowPriceOld = false;
        minYear = MIN_YEAR;
        maxYear = MAX_YEAR;

    }


    private void rangeSeekBarSetup(int MIN, int MAX ){
        yearRange.setSelectedMaxValue(MAX);
        yearRange.setSelectedMinValue(MIN);

        yearRange.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                minYear = (int)minValue;
                maxYear = (int)maxValue;
            }
        });

    }

    private void categoryDepends(String item){
        switch (item){
            case "Bike":
                yearLayout.setVisibility(View.VISIBLE);
                break;
            case "Car":
                yearLayout.setVisibility(View.VISIBLE);
                break;
            case "Motorcycle":
                yearLayout.setVisibility(View.VISIBLE);
                break;
            case "Truck":
                yearLayout.setVisibility(View.VISIBLE);
                break;
            default:
                yearLayout.setVisibility(View.GONE);
                minYear = MIN_YEAR;
                maxYear = MAX_YEAR;
                rangeSeekBarSetup(MIN_YEAR, MAX_YEAR);
        }
    }
}
