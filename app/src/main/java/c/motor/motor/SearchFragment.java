package c.motor.motor;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.IndexQuery;
import com.algolia.search.saas.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import c.motor.motor.adapter.SearchAdvertAdapter;
import c.motor.motor.model.AdvertSearch;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    public SearchFragment() {
        // Required empty public constructor
    }

    final private int NUM_COLLUMNS = 2;
    final private int LOAD_COUNT = 9;

    private ArrayList<AdvertSearch> advertPreviewSearchArrayList = new ArrayList<>();

    private RecyclerView recyclerView;
    private ProgressBar loadProgressBar;
    private ProgressBar upLoadProgressBar;
    private ImageView noInternetImdView;
    private SwipeRefreshLayout swipeRefreshLayout;

    Client.MultipleQueriesStrategy strategy;
    Client client;
    List<IndexQuery> queries;
    int page;
    int nbPages;

    StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SearchAdvertAdapter adapter;

    private boolean isScrolling;
    private boolean isLastItemReached = false;
    private boolean isLoading = false;

    private String tittleQuery;
    private String categoryQuery;
    private String countryQuery;
    private String cityQuery;
    private long priceFromQuery;
    private long priceTillQuery;
    private long minYear;
    private long maxYear;
    private boolean withPictureQuery;
    private boolean onlyNewQuery;
    private boolean sortByDate;
    private boolean sortByHighPrice;
    private boolean sortByLowPrice;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private Index index;
    private String indexName;
    private String indexNameHighPrice;
    private String indexNameLowPrice;
    private String exceptObjectId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            tittleQuery = bundle.getString("tittleQuery");
            categoryQuery = bundle.getString("categoryQuery");
            countryQuery = bundle.getString("countryQuery");
            cityQuery = bundle.getString("cityQuery");
            priceFromQuery = bundle.getLong("priceFromQuery");
            priceTillQuery = bundle.getLong("priceTillQuery");
            minYear = bundle.getLong("minYear");
            maxYear = bundle.getLong("maxYear");
            withPictureQuery = bundle.getBoolean("withPictureQuery", false);
            onlyNewQuery = bundle.getBoolean("onlyNewQuery", false);
            sortByDate = bundle.getBoolean("sortByDate", true);
            sortByHighPrice = bundle.getBoolean("sortByHighPrice", false);
            sortByLowPrice = bundle.getBoolean("sortByLowPrice", false);
            if(bundle.containsKey("except")){
                exceptObjectId = bundle.getString("except");
            }

        }

        System.out.println("countryQuery " + countryQuery);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.main_search_recycle_view);
        loadProgressBar = view.findViewById(R.id.main_search_load_progressBar);
        upLoadProgressBar = view.findViewById(R.id.main_search_upLoad_progressBar);
        noInternetImdView = view.findViewById(R.id.main_search_no_internet);

        setUpAdvertsRecycleView();
        setUpAlgoliaClient();
        loadData(tittleQuery);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(null)[0];
                int visibleItemCount = staggeredGridLayoutManager.getChildCount();
                int totalItemCount = staggeredGridLayoutManager.getItemCount();
                if (isScrolling && firstVisibleItem + visibleItemCount == totalItemCount && !isLastItemReached && !isLoading){
                    isScrolling = false;
                    isLoading = true;
                    upLoadData(tittleQuery);
                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
    }


    private void setUpAdvertsRecycleView(){
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
    }
    private void setUpAlgoliaClient(){
        strategy = Client.MultipleQueriesStrategy.NONE;
        client = new Client("STZV8Z6R6G" , "ce681eb47a47a0f885d46617049791c7");
    }

    private void loadData(String tittleQuery){
        setUpAlgoliaQuery(tittleQuery,0);
        client.multipleQueriesAsync(queries, strategy, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                if(content != null){
                    try {
                        System.out.println(content.toString());
                        JSONArray hits = content.optJSONArray("results").getJSONObject(0).getJSONArray("hits");
                        page = content.optJSONArray("results").getJSONObject(0).getInt("page");
                        nbPages = content.optJSONArray("results").getJSONObject(0).getInt("nbPages");
                        if (page + 1 == nbPages)
                            isLastItemReached = true;
                        System.out.println(page);
                        for(int i = 0; i < hits.length(); i++){
                            JSONObject jsonObject = hits.getJSONObject(i);
                            String title = jsonObject.getString("title");
                            String image = jsonObject.getString("image");
                            int price = jsonObject.getInt("price");
                            long dateTime = jsonObject.getLong("dateTime");
                            String city = jsonObject.getString("city");
                            String country = jsonObject.getString("country");
                            String currency = jsonObject.getString("currency");
                            String documentReference = jsonObject.getString("documentReference");
                            String category = jsonObject.getString("category");
                            boolean isNew = jsonObject.getBoolean("isNew");
                            System.out.println(title);
                            AdvertSearch advertSearch = new AdvertSearch(title, image, price, dateTime, city, country, currency, documentReference, category, isNew);
                            advertPreviewSearchArrayList.add(advertSearch);
                        }
                        System.out.println("good " + hits.length());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter = new SearchAdvertAdapter(getContext(), advertPreviewSearchArrayList, countryQuery);
                    recyclerView.setAdapter(adapter);
                    loadProgressBar.setVisibility(View.GONE);
                }else{
                    //internet error
                    loadProgressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    noInternetImdView.setVisibility(View.VISIBLE);
                }
           }
       });

    }

    private void upLoadData(String tittleQuery) {
        System.out.println("UPLOAD");
        setUpAlgoliaQuery(tittleQuery,page + 1);
        upLoadProgressBar.setVisibility(View.VISIBLE);
        client.multipleQueriesAsync(queries, strategy, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                if(content != null) {
                    System.out.println(content.toString());
                    try {
                        JSONArray hits = content.optJSONArray("results").getJSONObject(0).getJSONArray("hits");
                        page = content.optJSONArray("results").getJSONObject(0).getInt("page");
                        nbPages = content.optJSONArray("results").getJSONObject(0).getInt("nbPages");
                        if (page + 1 == nbPages)
                            isLastItemReached = true;
                        System.out.println(page);
                        for (int i = 0; i < hits.length(); i++) {
                            JSONObject jsonObject = hits.getJSONObject(i);
                            String title = jsonObject.getString("title");
                            String image = jsonObject.getString("image");
                            int price = jsonObject.getInt("price");
                            long dateTime = jsonObject.getLong("dateTime");
                            String city = jsonObject.getString("city");
                            String country = jsonObject.getString("country");
                            String currency = jsonObject.getString("currency");
                            String documentReference = jsonObject.getString("documentReference");
                            boolean isNew = jsonObject.getBoolean("isNew");
                            String category = jsonObject.getString("category");
                            System.out.println(title);
                            AdvertSearch advertSearch = new AdvertSearch(title, image, price, dateTime, city, country, currency, documentReference, category, isNew);
                            advertPreviewSearchArrayList.add(advertSearch);
                        }
                        System.out.println("good " + hits.length());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                    isLoading = false;
                    upLoadProgressBar.setVisibility(View.GONE);
                }else{
                    upLoadProgressBar.setVisibility(View.GONE);
                    noInternetImdView.setVisibility(View.VISIBLE);
                    //internet problem
                }
            }
        });
    }


    private void setUpAlgoliaQuery(String text, int numberOfPage){
        setupCountryDataBase(countryQuery);
        queries = new ArrayList<>();
        Query searchQuery = new Query(text);
        JSONArray tags = new JSONArray();

        if(exceptObjectId != null) {
            searchQuery.setFilters("NOT objectID:" + exceptObjectId);
        }

        System.out.println("cityQuery " + cityQuery);

        if(cityQuery != null){
            if(!cityQuery.equals("City") && !cityQuery.equals("All")){
                tags.put(cityQuery);
            }
        }
        if(withPictureQuery){
            tags.put("haveImage");
        }
        if(onlyNewQuery){
            tags.put("newProduct");
        }
        if(categoryQuery != null && !categoryQuery.equals("All categories")){
            tags.put(categoryQuery);
        }
        if(priceFromQuery != 0 || priceTillQuery != 0){
            searchQuery.setFilters("price:" + priceFromQuery + " TO " + priceTillQuery);
        }
        System.out.println("year: " + minYear + " - " + maxYear);
        if(!(minYear == 0 || maxYear == 0) && !(minYear == 1960 && maxYear == 2030)){
            System.out.println("year4: " + minYear + " - " + maxYear);
            searchQuery.setFilters("year:" + minYear + " TO " + maxYear);
        }
        searchQuery.setTagFilters(tags);
        searchQuery.setPage(numberOfPage).setHitsPerPage(LOAD_COUNT);

        if(sortByDate){
            queries.add(new IndexQuery(indexName, searchQuery));
        }else if(sortByHighPrice){
            queries.add(new IndexQuery(indexNameHighPrice, searchQuery));
        }else if(sortByLowPrice){
            queries.add(new IndexQuery(indexNameLowPrice, searchQuery));
        }else{
            queries.add(new IndexQuery(indexName, searchQuery));
        }
    }

    private void setupCountryDataBase(String currentCountry){
        if(currentCountry.equals("Vietnam")){
            //collectionReference = firebaseFirestore.collection("TEST_VN");
            //index = client.getIndex("adverts_VN_TEST");
            indexName = "adverts_VN_TEST";
            indexNameHighPrice = "adverts_VN_high_price_TEST";
            indexNameLowPrice = "adverts_VN_low_price_TEST";
        }else if(currentCountry.equals("Philippines")){
            //collectionReference = firebaseFirestore.collection("TEST_PH");
            //index = client.getIndex("adverts_PH_TEST");
            indexName = "adverts_PH_TEST";
            indexNameHighPrice = "adverts_PH_TEST";
            indexNameLowPrice = "adverts_PH_TEST";
        }
    }

}
