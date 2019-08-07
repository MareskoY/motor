package c.motor.motor;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import c.motor.motor.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference mDatabase;

    private RelativeLayout frameLayout;

    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confPasswordInput;

    private ImageButton closeBtn;
    private Button signUpBtn;
    private Button loginLink;

    private ProgressDialog loadingBar;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        frameLayout = getActivity().findViewById(R.id.register_container);

        nameInput = view.findViewById(R.id.sign_up_name_input);
        emailInput = view.findViewById(R.id.sign_up_email_input);
        passwordInput = view.findViewById(R.id.sign_up_password_input);
        confPasswordInput = view.findViewById(R.id.sign_up_confirm_password_input);

        closeBtn = view.findViewById(R.id.sign_up_close);
        signUpBtn = view.findViewById(R.id.sign_up_signup_btn);
        loginLink = view.findViewById(R.id.sign_up_login_link);

        loadingBar = new ProgressDialog(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEmailAndPassword()){
                    createNewAccount();
                }
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });
        nameInput.addTextChangedListener(new TextWatcher() {
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
        confPasswordInput.addTextChangedListener(new TextWatcher() {
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
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
    private void checkInputs(){
        if(!TextUtils.isEmpty(emailInput.getText())){
            if(!TextUtils.isEmpty(nameInput.getText())){
                if(!TextUtils.isEmpty(passwordInput.getText()) && passwordInput.length() >= 6){
                    if(!TextUtils.isEmpty(confPasswordInput.getText())){
                        signUpBtn.setEnabled(true);
                        signUpBtn.setTextColor(Color.rgb(255,255,255));
                    }else{
                        signUpBtn.setEnabled(false);
                        signUpBtn.setTextColor(Color.argb(70,255,255,255));
                    }
                }else{
                    signUpBtn.setEnabled(false);
                    signUpBtn.setTextColor(Color.argb(70,255,255,255));
                }
            }else{
                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(Color.argb(70,255,255,255));
            }
        }else{
            signUpBtn.setEnabled(false);
            signUpBtn.setTextColor(Color.argb(70,255,255,255));
        }
    }
    private boolean checkEmailAndPassword(){

        boolean result = false;
        Drawable customErrorIcon = getResources().getDrawable(R.drawable.ic_error_outline_black_24dp);
        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());

        if (emailInput.getText().toString().matches(emailPattern)){
            if (passwordInput.getText().toString().equals(confPasswordInput.getText().toString())){

                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(Color.argb(70,255,255,255));
                result = true;

            }else{
                confPasswordInput.setError("Password doesn't matched!",customErrorIcon);
            }
        }else{
            emailInput.setError("Invalid Email!", customErrorIcon);
        }
        return result;
    }
    private void createNewAccount(){
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();
        final String name = nameInput.getText().toString();

        loadingBar.setTitle("Creating new Account");
        loadingBar.setMessage("Please wait, while we are creating your new account");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            loadingBar.cancel();
                            setUserInformation(name,email);
                        }else{
                            signUpBtn.setEnabled(false);
                            signUpBtn.setTextColor(Color.argb(70,255,255,255));
                            loadingBar.cancel();
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void mainIntent(){
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
        getActivity().finish();
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
                        }else{
                            signUpBtn.setEnabled(false);
                            signUpBtn.setTextColor(Color.argb(70,255,255,255));
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

}
