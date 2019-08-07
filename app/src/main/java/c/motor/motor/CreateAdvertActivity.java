package c.motor.motor;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class CreateAdvertActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        frameLayout = findViewById(R.id.create_advert_container);
//        setFragment(new CreateAdvertCategoryFragment());
    }
    private void setFragment(Fragment fragment) {

        Bundle bundle = new Bundle();

        // Save params

        bundle.putString("Country", "Your Country*");
        bundle.putString("City", "Your City*");
        bundle.putString("Tittle", "");
        bundle.putString("Price", "");
        bundle.putString("Phone", "");
        bundle.putString("Description", "");

//        bundle.putString("ImageUri", null);
//        bundle.putString("ImageUri1", null);
//        bundle.putString("ImageUri2", null);
//        bundle.putString("ImageUri3", null);
//
//        bundle.putString("StringUriMain", null);
//        bundle.putString("StringUri1", null);
//        bundle.putString("StringUri2", null);
//        bundle.putString("StringUri3", null);
//
//        bundle.putString("DownloadStringUriMain", null);
//        bundle.putString("DownloadStringUri1", null);
//        bundle.putString("DownloadStringUri2", null);
//        bundle.putString("DownloadStringUri3", null);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }
}