package c.motor.motor;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.Executor;

import c.motor.motor.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {


    public SignInFragment() {
        // Required empty public constructor
    }

    static final int GOOGLE_SIGN_IN = 123;

    private RelativeLayout frameLayout;

    private EditText emailInput;
    private EditText passwordInput;

    private Button loginBtn;
    private Button signupLink;
    private ImageButton closeBtn;

    private LinearLayout googleBtn;
    private ImageView facebookBtn, twitterBtn;

    private ProgressDialog loadingBar;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference mDatabase;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        frameLayout = getActivity().findViewById(R.id.register_container);

        emailInput = view.findViewById(R.id.sign_in_email_input);
        passwordInput = view.findViewById(R.id.sign_in_password_input);

        loginBtn = view.findViewById(R.id.sign_in_login_btn);
        signupLink = view.findViewById(R.id.sign_in_signup_link);
        googleBtn = view.findViewById(R.id.sign_in_by_google);

        closeBtn = view.findViewById(R.id.sign_in_close);

        loadingBar = new ProgressDialog(getActivity());

        firebaseAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());
            }
        });
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProcess();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {

            loadingBar.setTitle("Login ....");
            loadingBar.setMessage("Please wait.....");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            firebaseAuthWithGoogle(task);
        }
    }


    private void firebaseAuthWithGoogle(Task<GoogleSignInAccount> completedTask){
        try{
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account != null){
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            setUserInformation(account.getDisplayName(), account.getEmail());
                        }
                    }
                });
            }

        }catch (ApiException e){
            //error
        }
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void mainIntent(){
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
        getActivity().finish();
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
    private void checkInputs() {
        if(!TextUtils.isEmpty(emailInput.getText())){
            if(!TextUtils.isEmpty(passwordInput.getText())){
                loginBtn.setEnabled(true);
                loginBtn.setTextColor(Color.rgb(255,255,255));
            }else{
                loginBtn.setEnabled(false);
                loginBtn.setTextColor(Color.argb(70,255,255,255));
            }
        }else{
            loginBtn.setEnabled(false);
            loginBtn.setTextColor(Color.argb(70,255,255,255));
        }
    }
    private void loginProcess() {
        if(emailInput.getText().toString().matches(emailPattern)){
            if(passwordInput.length() >= 6 ){

                loadingBar.setTitle("Login ....");
                loadingBar.setMessage("Please wait.....");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);

                loginBtn.setEnabled(false);
                loginBtn.setTextColor(Color.argb(70,255,255,255));

                firebaseAuth.signInWithEmailAndPassword(emailInput.getText().toString(), passwordInput.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    loadingBar.cancel();
                                    mainIntent();
                                }else{
                                    loginBtn.setEnabled(false);
                                    loginBtn.setTextColor(Color.argb(70,255,255,255));
                                    loadingBar.cancel();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else{
                Toast.makeText(getActivity(), "Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(), "Incorrect email or password", Toast.LENGTH_SHORT).show();
        }

    }

    private void setUserInformation(String name, String email){
        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        User user = new User(name,email);
        Map<String,String> userdata = user.toMap();

        firebaseFirestore.collection("USERS").document(currentUserId)
                .set(userdata)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mainIntent();
                            loadingBar.cancel();
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            loadingBar.cancel();
                        }
                    }
                });


    }
}
