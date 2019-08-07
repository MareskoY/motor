package c.motor.motor.privateActivity;


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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import c.motor.motor.R;
import c.motor.motor.adapter.AdvertPreviewRecycleViewAdapter;
import c.motor.motor.adapter.FavoriteRecycleViewAdapter;
import c.motor.motor.adapter.MyRecycleViewAdapter;
import c.motor.motor.helpers.AppData;
import c.motor.motor.model.AdvertPreview;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAdvertsFragment extends Fragment {

    final private int NUM_COLLUMNS = 1;
    final private int LOAD_COUNT = 10;

    private RecyclerView recyclerView;
    private ProgressBar loadProgressBar;
    private ProgressBar upLoadProgressBar;

    private String userId;
    private FirebaseUser user;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private FirebaseFirestore db;
    private CollectionReference ref;
    private Query query;
    private DocumentSnapshot lastVisible;
    private boolean isLastItemReached = false;
    private MyRecycleViewAdapter adapter;
    private Boolean isScrolling = false;

    private ArrayList<AdvertPreview> advertPreviewArrayList = new ArrayList<>();
    private ArrayList<String> documentNameArrayList = new ArrayList<>();

    private String currentCountry;

    private String collectionPath, favoriteCollectionPath, indexName;


    public MyAdvertsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_adverts, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            currentCountry = bundle.getString("country");
        }
        setupCountryDataBase(currentCountry);

        recyclerView = view.findViewById(R.id.myAdverts_recycle_view);
        loadProgressBar = view.findViewById(R.id.myAdverts_load_progressBar);
        upLoadProgressBar = view.findViewById(R.id.myAdverts_upLoad_progressBar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        setupQuery(userId);
        loadData();

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

                if (isScrolling && !isLastItemReached){
                    isScrolling = false;
                    upLoadData();

                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
    }


    private void setupQuery(String userId){
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        db = FirebaseFirestore.getInstance();
        ref = db.collection(collectionPath);
        query = ref.whereEqualTo("userId", userId)
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .limit(LOAD_COUNT);
    }

    private void loadData(){
        query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot queryDocumentSnapshots: task.getResult()){
                            AdvertPreview advertPreview = AdvertPreview.documentSnapshotToAdvertPreview(queryDocumentSnapshots);
                            if (advertPreview != null){
                                documentNameArrayList.add(queryDocumentSnapshots.getId());
                                advertPreviewArrayList.add(advertPreview);
                            }
                        }
                        adapter = new MyRecycleViewAdapter(getContext(), advertPreviewArrayList, documentNameArrayList, currentCountry);
                        recyclerView.setAdapter(adapter);
                        loadProgressBar.setVisibility(View.GONE);
                        if(task.getResult().size() != 0)
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        if(task.getResult().size() < LOAD_COUNT){
                            isLastItemReached = true;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void upLoadData(){
        upLoadProgressBar.setVisibility(View.VISIBLE);
        query
                .startAfter(lastVisible)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot queryDocumentSnapshots: task.getResult()){
                            AdvertPreview advertPreview = AdvertPreview.documentSnapshotToAdvertPreview(queryDocumentSnapshots);
                            if (advertPreview != null){
                                documentNameArrayList.add(queryDocumentSnapshots.getId());
                                advertPreviewArrayList.add(advertPreview);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        upLoadProgressBar.setVisibility(View.GONE);
                        if(task.getResult().size() < LOAD_COUNT){
                            isLastItemReached = true;
                        }
                    }
                });
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
