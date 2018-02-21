package com.somesh.myproject.android.simpleblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {
    private static final int IMAGE_PICK = 2;
    Button submit_bt;
    EditText postTitle_et, postDescription_et;
    ImageView selectImage_iv;
    Uri imageUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        //.................................................................
        submit_bt = findViewById(R.id.submit_post_bt);
        selectImage_iv = findViewById(R.id.select_image_view);
        postTitle_et = findViewById(R.id.post_name_et);
        postDescription_et = findViewById(R.id.post_description_et);
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        //..................................................................
        selectImage_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i,IMAGE_PICK);
            }
        });
        submit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private void startPosting() {
        progressDialog.setMessage("Posting to blog");
        progressDialog.show();
        final String post_title = postTitle_et.getText().toString();
        final String post_description = postDescription_et.getText().toString();
        if (!TextUtils.isEmpty(post_title) && !TextUtils.isEmpty(post_description) && imageUri != null) {
            StorageReference filepath = storageReference.child("Blog photos").child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    DatabaseReference childref = databaseReference.push();
                    childref.child("title").setValue(post_title);
                    childref.child("desc").setValue(post_description);
                    childref.child("image").setValue(downloadUri.toString());
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK && resultCode == RESULT_OK) {
            imageUri = data.getData();
            selectImage_iv.setImageURI(imageUri);
        }
    }
}
