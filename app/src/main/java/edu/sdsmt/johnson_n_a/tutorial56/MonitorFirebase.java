package edu.sdsmt.johnson_n_a.tutorial56;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MonitorFirebase {
    public final static MonitorFirebase INSTANCE = new MonitorFirebase();
    private static final String USER = "name";
    private static final String EMAIL = "fake@email.com";
    private static final String PASSWORD = "12345678";
    private static final String TAG = "monitor";

    // Firebase instance variables
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;
    private final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
    private boolean authenticated = false;

    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * private to defeat instantiation.
     */
    private MonitorFirebase() {
        createUser();
    }

    /**
     * Create a new user if possible
     */
    private void createUser() {
        Task<AuthResult> result = userAuth.createUserWithEmailAndPassword(EMAIL, PASSWORD);
        result.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                    firebaseUser = userAuth.getCurrentUser();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("/screenName/" + USER, true);
                    result.put("/" + firebaseUser.getUid() + "/name", USER);
                    result.put("/" + firebaseUser.getUid() + "/password", PASSWORD);
                    result.put("/" + firebaseUser.getUid() + "/email", EMAIL);
                    userRef.updateChildren(result);
                    signIn();
                } else {
                    Log.d(TAG, "Problem: " + task.getException().getMessage());
                    signIn();
                }
            }
        });
    }

    /**
     * Sign a user in with the given email and password
     */
    private void signIn() {
        // use "username" already exists
        Task<AuthResult> result = userAuth.signInWithEmailAndPassword(EMAIL, PASSWORD);
        result.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                    authenticated = true;
                    firebaseUser = userAuth.getCurrentUser();
                } else {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    authenticated = false;
                }
            }
        });
    }
}
