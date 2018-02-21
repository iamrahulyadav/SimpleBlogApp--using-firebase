package com.somesh.myproject.android.simpleblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText username_et, email_et, password_et;
    Button signup_bt;
    TextView login_tv;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    TextInputLayout username_til, email_til, password_til;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //............................................................................................

        username_et = findViewById(R.id.username_et);
        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        username_til = findViewById(R.id.username_til);
        email_til = findViewById(R.id.email_til);
        password_til = findViewById(R.id.password_til);
        signup_bt = findViewById(R.id.signup_bt);
        login_tv = findViewById(R.id.login_tv);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        //............................................................................................

        username_til.setHint("Username");
        email_til.setHint("Email");
        password_til.setHint("Password");
        signup_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

    }

    private void signup() {
        final String username = username_et.getText().toString();
        String email = email_et.getText().toString();
        String password = password_et.getText().toString();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressDialog.setMessage("Signing Up");
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        String user_id = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference child_ref = databaseReference.child(user_id);
                        child_ref.child("UserName").setValue(username);
                        child_ref.child("Image").setValue("Default");
                        progressDialog.dismiss();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }


                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CANCELED) {
            finish();
        }
    }
}
