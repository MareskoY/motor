//package c.motor.motor;
//
//
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.cardview.widget.CardView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class
//CreateAdvertCategoryFragment extends Fragment {
//
//
//    public CreateAdvertCategoryFragment() {
//        // Required empty public constructor
//    }
//
//    private FrameLayout frameLayout;
//
//    private ImageButton closeBtn;
//
//    private CardView bikeBtn;
//    private CardView carBtn;
//    private CardView sportBikeBtn;
//    private CardView bigCarBtn;
//    private CardView cycleBtn;
//    private CardView otherBtn;
//
//    String  country, city, tittle,
//            price, phone, description;
////    String  imageUri, imageUri1, imageUri2, imageUri3,
////            stringUriMain, stringUri1, stringUri2, stringUri3,
////            downloadStringUriMain, downloadStringUri1, downloadStringUri2, downloadStringUri3;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        // Save params
//
//        Bundle bundle = getArguments();
//        country = bundle.getString("Country");
//        city = bundle.getString("City");
//        tittle = bundle.getString("Tittle");
//        price = bundle.getString("Price");
//        phone = bundle.getString("Phone");
//        description = bundle.getString("Description");
//        System.out.println("-----------ololol--------------");
//
//
////        imageUri = bundle.getString("ImageUri");
////        imageUri1 = bundle.getString("ImageUri1");
////        imageUri2 = bundle.getString("ImageUri2");
////        imageUri3 = bundle.getString("ImageUri3");
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
//
//
//
//
//
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_create_advert_category, container, false);
//
//        frameLayout = getActivity().findViewById(R.id.create_advert_container);
//
//        closeBtn = view.findViewById(R.id.add_advert_category_close);
//
//        bikeBtn = view.findViewById(R.id.add_advert_category_bike);
//        carBtn = view.findViewById(R.id.add_advert_category_car);
//        sportBikeBtn = view.findViewById(R.id.add_advert_category_sport_bike);
//        bigCarBtn = view.findViewById(R.id.add_advert_category_big_car);
//        cycleBtn = view.findViewById(R.id.add_advert_category_cycle);
//        otherBtn = view.findViewById(R.id.add_advert_category_other);
//        add_advert_category_other
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        closeBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                mainIntent();
//            }
//        });
//        bikeBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                openCreateAdvertFragment(new CreateAdvertFragment(),0,"Bike",R.drawable.ic_directions_car_blue);
//            }
//        });
//        carBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                openCreateAdvertFragment(new CreateAdvertFragment(),1,"Car",R.drawable.ic_directions_car_blue);
//            }
//        });
//        sportBikeBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                openCreateAdvertFragment(new CreateAdvertFragment(),2,"Sport bike",R.drawable.ic_directions_car_blue);
//            }
//        });
//        bigCarBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                openCreateAdvertFragment(new CreateAdvertFragment(),3,"Big car",R.drawable.ic_directions_car_blue);
//            }
//        });
//        cycleBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                openCreateAdvertFragment(new CreateAdvertFragment(),4,"Cycle",R.drawable.ic_directions_car_blue);
//            }
//        });
//        otherBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                openCreateAdvertFragment(new CreateAdvertFragment(),5,"Other",R.drawable.ic_directions_car_blue);
//            }
//        });
//    }
//
//    private void mainIntent(){
//        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
//        startActivity(mainIntent);
//        getActivity().finish();
//    }
//
//    private void openCreateAdvertFragment(Fragment fragment, int id, String title, int icon) {
//        Bundle bundle = new Bundle();
//        bundle.putString("Title",title);
//        bundle.putInt("Id",id);
//        bundle.putInt("Icon",icon);
//
//        // Save params
//
//        bundle.putString("Country", country);
//        bundle.putString("City", city);
//        bundle.putString("Tittle", tittle);
//        bundle.putString("Price", price);
//        bundle.putString("Phone", phone);
//        bundle.putString("Description", description);
//
////        bundle.putString("ImageUri", null);
////        bundle.putString("ImageUri1", null);
////        bundle.putString("ImageUri2", null);
////        bundle.putString("ImageUri3", null);
////
////        bundle.putString("StringUriMain", null);
////        bundle.putString("StringUri1", null);
////        bundle.putString("StringUri2", null);
////        bundle.putString("StringUri3", null);
////
////        bundle.putString("DownloadStringUriMain", null);
////        bundle.putString("DownloadStringUri1", null);
////        bundle.putString("DownloadStringUri2", null);
////        bundle.putString("DownloadStringUri3", null);
//
//
//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragment.setArguments(bundle);
//        fragmentTransaction.replace(frameLayout.getId(), fragment);
//        fragmentTransaction.commit();
//    }
//
//}
