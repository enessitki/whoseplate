package com.example.whoseplate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends AppCompatActivity {
    AlphaAnimation signAnimation;
    AlphaAnimation signAnimationReverse;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private long databaseCount;

    @BindView(R.id.textSignUp)
    TextView signUpView;
    @BindView(R.id.textLogin)
    TextView loginView;

    @BindView(R.id.loginButton)
    Button loginButton;
    @BindView(R.id.signUpButton)
    Button signUpButton;

    @BindView(R.id.signin_view)
    View signInPage;
    @BindView(R.id.signup_view)
    View signUpPage;

    @BindView(R.id.txtUsername)
    TextInputLayout usernameText;
    @BindView(R.id.txtPassword)
    TextInputLayout passwordText;

    @BindView(R.id.txtSignUpMail)
    TextInputLayout emailSignUpText;
    @BindView(R.id.txtSignUpPlate)
    TextInputLayout plateSignUpText;
    @BindView(R.id.txtSignUpPassword)
    TextInputLayout passwordSignUpText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setAnimations();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        signUpView.setOnClickListener(v -> {
            signInPage.startAnimation(signAnimation);
            signInPage.setVisibility(View.GONE);

            signUpPage.startAnimation(signAnimationReverse);
            signUpPage.setVisibility(View.VISIBLE);

        });
        loginView.setOnClickListener(v -> {
            signUpPage.startAnimation(signAnimation);
            signUpPage.setVisibility(View.GONE);

            signInPage.startAnimation(signAnimationReverse);
            signInPage.setVisibility(View.VISIBLE);


        });

        loginButton.setOnClickListener(v -> {
            try {
                mAuth.signInWithEmailAndPassword(usernameText.getEditText().getText().toString().trim(), passwordText.getEditText().getText().toString().trim())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                mDatabase.child("users").child(user != null ? user.getUid() : null).child("plate").get().addOnCompleteListener(task1 -> {
                                    if (!task1.isSuccessful()) {

                                        Toast.makeText(MainActivity.this, "Error",
                                                Toast.LENGTH_SHORT).show();

                                    } else {

                                Toast.makeText(MainActivity.this, "Welcome "+ task1.getResult().getValue(),
                                        Toast.LENGTH_SHORT).show();

                                    }
                                });

                            } else {

                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Hata",
                        Toast.LENGTH_SHORT).show();

            }

//
//            mDatabase.child("users").child("9").child("plate").get().addOnCompleteListener(task -> {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                }
//            });


        });

        signUpButton.setOnClickListener(v -> {
            try {
                mAuth.createUserWithEmailAndPassword(emailSignUpText.getEditText().getText().toString().trim(), passwordSignUpText.getEditText().getText().toString().trim())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Authentication added.",
                                        Toast.LENGTH_SHORT).show();

                                FirebaseUser user = mAuth.getCurrentUser();
                                writeNewUser(user != null ? user.getUid() : null, plateSignUpText.getEditText().getText().toString().trim(), passwordSignUpText.getEditText().getText().toString().trim());

                            } else {
                                Log.i("err", String.valueOf(task.getException()));
                                if (String.valueOf(task.getException()).contains("The email address is already in use by another account.")){
                                    emailSignUpText.setError("The email address is already in use.");
                                }
//                                    Toast.makeText(MainActivity.this, "The email adress is already in use by another account.",
//                                            Toast.LENGTH_SHORT).show();

                            }
                        });
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Failed.",
                        Toast.LENGTH_SHORT).show();
            }

        });
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                databaseCount = snapshot.getChildrenCount();
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                databaseCount = snapshot.getChildrenCount();
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                databaseCount = snapshot.getChildrenCount();

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                databaseCount = snapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void writeNewUser(String userId, String plate, String password) {
        User user = new User(plate, password);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private void setAnimations() {
        signAnimation = new AlphaAnimation(1, 0f);
        signAnimation.setDuration(1000);
        signAnimation.setFillAfter(false);

        signAnimationReverse = new AlphaAnimation(0, 1f);
        signAnimationReverse.setDuration(1000);
        signAnimationReverse.setFillAfter(false);
    }


}