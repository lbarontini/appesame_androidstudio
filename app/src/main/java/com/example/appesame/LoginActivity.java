package com.example.appesame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 300;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SignInButton signInButton;
    private Button logOutButton;
    private TextView usernameTv;
    private ImageView proPicView;
    private ImageButton closeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInButton = findViewById(R.id.sign_in_button);
        usernameTv = findViewById(R.id.username);
        proPicView = findViewById(R.id.profile_picture);
        logOutButton= findViewById(R.id.logout_button);
        closeBtn= findViewById(R.id.dismiss);

        //handling the dismiss click
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();
            }
        });

        //handling the logout click
        logOutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                updateUI(null);
            }
        });
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(getApplicationContext())) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });
        updateUI(mAuth.getCurrentUser());
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("googlesignin", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        Toast.makeText(getApplicationContext(), R.string.welcome_message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, ExamChooserActivity.class);
                        startActivity(intent);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("googlesignin", "signInWithCredential:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
    }

    private void updateUI(FirebaseUser account) {
        if (account!=null) {
            closeBtn.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
            logOutButton.setVisibility(View.VISIBLE);
            usernameTv.setText(account.getDisplayName());
            Glide.with(this)
                    .load(account.getPhotoUrl().toString())
                    .placeholder(R.drawable.ic_account_circle_light)
                    .centerCrop()
                    .circleCrop()
                    .into(proPicView);
        }else {
            closeBtn.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            logOutButton.setVisibility(View.INVISIBLE);
            usernameTv.setText(R.string.alert_login);
            proPicView.setImageResource(R.drawable.ic_account_circle_light);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("googlesignin", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("googlesignin", "Google sign in failed", e);
            }
        }
    }
    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
