package c.motor.motor;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import c.motor.motor.adapter.AdvertPreviewRecycleViewAdapter;
import c.motor.motor.helpers.Helpers;
import c.motor.motor.model.AdvertPreview;
import me.mvdw.recyclerviewmergeadapter.adapter.RecyclerViewMergeAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }
    final private int NUM_COLLUMNS = 2;
    final private int LOAD_COUNT = 5;

    private ArrayList<AdvertPreview> advertPreviewArrayList = new ArrayList<>();

    private RecyclerView recyclerView;
    private ProgressBar loadProgressBar;
    private ProgressBar upLoadProgressBar;

    private FirebaseFirestore db;
    private AdvertPreviewRecycleViewAdapter adapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    CollectionReference ref;
    Query query;
    RecyclerViewMergeAdapter recyclerViewMergeAdapter;

    private DocumentSnapshot lastVisible;
    private boolean isScrolling;
    private boolean isLastItemReached = false;
    int count = 0 ;
    int uploadCount;
    int uploadRequestCount;
    int fullUploadCount = 0;
    boolean loadMore = true;

    private String countryQuery;
    private String tittleQuery;
    List<String>  list = new ArrayList<>();
    Task<QuerySnapshot> waitingTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            tittleQuery = bundle.getString("tittleQuery");
            System.out.println(tittleQuery + "    111111");
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.main_home_recycle_view);
        loadProgressBar = view.findViewById(R.id.main_home_load_progressBar);
        upLoadProgressBar = view.findViewById(R.id.main_home_upLoad_progressBar);

        countryQuery = Helpers.getUserCountry(getContext());

        if(tittleQuery == null) {
            setUpAdvertsRecycleView();
            setUpFireBase();
            setUpQuery();
            loadData();
        }else{
            recyclerViewMergeAdapter = new RecyclerViewMergeAdapter();
            setUpAdvertsRecycleView();
            setUpFireBase();
            setUpQuery();
            loadDataWithTittle(tittleQuery);
        }
//            setUpAdvertsRecycleView();
//            setUpFireBase();
//            setUpQuery();
//            loadData();
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

                if (isScrolling && firstVisibleItem + visibleItemCount == totalItemCount && !isLastItemReached){
                    isScrolling = false;
                    if(tittleQuery == null){
                        upLoadData();
                    }else{
                        try {
                            upLoadDataWithTittle();
                        } catch (InterruptedException e) {
                            //could not load
                        } catch (ExecutionException e) {
                            //could not load
                        } catch (TimeoutException e) {
                            //could not load
                        }
                    }

                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void setUpAdvertsRecycleView(){
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
    }

    private void setUpFireBase() {
        db = FirebaseFirestore.getInstance();
        ref = db.collection("TEST");
    }

    private void setUpQuery() {
        if (countryQuery != null){
            query = ref.whereEqualTo("country",countryQuery);
        }
    }
    private void loadData() {
        query
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .limit(LOAD_COUNT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot queryDocumentSnapshots: task.getResult()){
                            AdvertPreview advertPreview = AdvertPreview.documentSnapshotToAdvertPreview(queryDocumentSnapshots);
                            if (advertPreview != null)
                                advertPreviewArrayList.add(advertPreview);
                        }
                        adapter = new AdvertPreviewRecycleViewAdapter(getContext(), advertPreviewArrayList);
                        recyclerView.setAdapter(adapter);
                        loadProgressBar.setVisibility(View.GONE);
                        try {
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }catch (Exception e){
                            //lastVisible = task.getResult().getDocuments().get(task.getResult().size());;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //at problem moment
            }
        });
    }
    private void upLoadData() {
        upLoadProgressBar.setVisibility(View.VISIBLE);
        query
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .limit(LOAD_COUNT)
                .startAfter(lastVisible)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot queryDocumentSnapshots: task.getResult()){
                            AdvertPreview advertPreview = AdvertPreview.documentSnapshotToAdvertPreview(queryDocumentSnapshots);
                            if (advertPreview != null)
                                advertPreviewArrayList.add(advertPreview);
                        }
                        adapter.notifyDataSetChanged();
                        try {
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }catch(Exception e){

                        }
                        upLoadProgressBar.setVisibility(View.GONE);
                        if(task.getResult().size() < LOAD_COUNT){
                            isLastItemReached = true;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //at problem moment
            }
        });
    }
    private void loadDataWithTittle(String tittle){
        //Algolia search
        Client client = new Client("STZV8Z6R6G", "ce681eb47a47a0f885d46617049791c7");
        Index index = client.getIndex("adverts");
        com.algolia.search.saas.Query algoliaQuery = new com.algolia.search.saas.Query(tittle)
                .setAttributesToRetrieve("tittle")
                .setHitsPerPage(200);
        index.searchAsync(algoliaQuery, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                System.out.println(content.toString());
                try {
                    JSONArray hits = content.getJSONArray("hits");
                    System.out.println("-----------rrrrrrrrrr-------------" + hits);
                    for(int i = 0; i < hits.length(); i++){
                        JSONObject jsonObject = hits.getJSONObject(i);
                        String productName = jsonObject.getString("tittle");
                        list.add(productName);
                        System.out.println("-----------ololol3-------------" + productName);
                    }
                    if(hits.length() > 0) {
                        //firestore request
                        List<Task<QuerySnapshot>> queryTasks = new ArrayList<Task<QuerySnapshot>>();
                        for (int j = 0; j < LOAD_COUNT && j < list.size(); j++ ){
                            query = ref.whereEqualTo("tittle", list.get(j));
                            Task searchTask = query
                                    .orderBy("dateTime", Query.Direction.DESCENDING)
                                    .get();
                            queryTasks.add(searchTask);
                        }
                        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(queryTasks);
                        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                            @Override
                            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                                for (QuerySnapshot queryDocumentSnapshots : querySnapshots){
                                    count++;
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                        AdvertPreview advertPreview = AdvertPreview.documentSnapshotToAdvertPreview(documentSnapshot);
                                        if (advertPreview != null) {
                                            System.out.println(documentSnapshot.getData().get("tittle").toString());
                                            advertPreviewArrayList.add(advertPreview);
                                        }
                                    }
                                    adapter = new AdvertPreviewRecycleViewAdapter(getContext(), advertPreviewArrayList);
                                    if (count == LOAD_COUNT || count == list.size()){
                                        recyclerView.setAdapter(adapter);
                                        loadProgressBar.setVisibility(View.GONE);
                                    }else{
                                        recyclerView.setAdapter(adapter);
                                    }
                                }
                            }
                        });
                    } else{
                        loadProgressBar.setVisibility(View.GONE);
                        // если ничего не нашел показать картинку
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loadProgressBar.setVisibility(View.GONE);
                    // если нет сети
                }
            }
        });
    }
    private void upLoadDataWithTittle() throws InterruptedException, ExecutionException, TimeoutException {
        uploadCount = 0;
        uploadRequestCount = 0;
        upLoadProgressBar.setVisibility(View.VISIBLE);
        List<Task<QuerySnapshot>> queryTasks = new ArrayList<Task<QuerySnapshot>>();

        for (int j = count; uploadCount < LOAD_COUNT && j < list.size(); j++ ){
            query = ref.whereEqualTo("tittle", list.get(j));
            Task searchTask = query
                        .orderBy("dateTime", Query.Direction.DESCENDING)
                        .get();
            queryTasks.add(searchTask);

            System.out.println("count!!: " + count);
            uploadCount++;
        }
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(queryTasks);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots){
                    count++;
                    uploadRequestCount++;
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        AdvertPreview advertPreview = AdvertPreview.documentSnapshotToAdvertPreview(documentSnapshot);
                        if (advertPreview != null) {
                            advertPreviewArrayList.add(advertPreview);
                            System.out.println(documentSnapshot.getData().get("tittle").toString() + "         +++++++++++++++");
                        }
                    }
                    adapter = new AdvertPreviewRecycleViewAdapter(getContext(), advertPreviewArrayList);
                    System.out.println("last call count: " + count);
                    System.out.println("uploadCount: " + uploadCount);
                    if (uploadRequestCount == LOAD_COUNT || count == list.size()){
                        System.out.println("last call ");
                        upLoadProgressBar.setVisibility(View.GONE);
                        if(uploadCount < LOAD_COUNT){
                            isLastItemReached = true;
                        }
                    }else{
                        recyclerView.setAdapter(adapter);
                    }
                }
                recyclerView.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
            }
        });






//        uploadCount = 0;
//        System.out.println("uploadCount" + ":" + uploadCount +"______" + "count" + ":" + count);
//        System.out.println("upload +++++++++++");
//        upLoadProgressBar.setVisibility(View.VISIBLE);
//
////        Task<QuerySnapshot> task = query.get();
////        task.addOnCompleteListener();
////
////        QuerySnapshot queryDocumentSnapshots = Tasks.await(task);
////        queryDocumentSnapshots
//        for(int i = count; i < list.size() && uploadCount < LOAD_COUNT; i++) {
//
//
//            System.out.println("uploadCount" + ":" + uploadCount +"______" + "count" + ":" + count);
//            System.out.println("______" + "i" + ":" + i);
//            query = ref.whereEqualTo("tittle",list.get(i));
//            query
//                    .orderBy("dateTime", Query.Direction.DESCENDING)
//                    .startAfter(lastVisible)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            for(DocumentSnapshot queryDocumentSnapshots: task.getResult()){
//                                AdvertPreview advertPreview = AdvertPreview.documentSnapshotToAdvertPreview(queryDocumentSnapshots);
//                                if (advertPreview != null) {
//                                    System.out.println("______" + "count++++++++" + ":" + count);
//                                    advertPreviewArrayList.add(advertPreview);
//                                }
//                                adapter = new AdvertPreviewRecycleViewAdapter(getContext(), advertPreviewArrayList);
//                                //waitingTask = task;
//                                if (uploadCount == LOAD_COUNT) {
//                                    recyclerViewMergeAdapter.clearAdapters();
//                                    recyclerViewMergeAdapter.addAdapter(adapter);
//                                    recyclerView.setAdapter(recyclerViewMergeAdapter);
//                                    loadProgressBar.setVisibility(View.GONE);
//                                    System.out.println("finished");
//                                    lastVisible = task.getResult().getDocuments().get(task.getResult().size()-1);
//                                } else {
//                                    recyclerViewMergeAdapter.addAdapter(adapter);
//                                }
//                                if(uploadCount < LOAD_COUNT){
//                                    isLastItemReached = true;
//                                }
//                                uploadCount++;
//                                count++;
//                            }
//
//                        }
//                    });
//
//            //////
//        }
    }
}