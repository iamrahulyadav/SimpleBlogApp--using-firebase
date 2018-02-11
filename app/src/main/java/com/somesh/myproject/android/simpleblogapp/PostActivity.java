package com.somesh.myproject.android.simpleblogapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PostActivity extends AppCompatActivity {
    Button submit_bt;
    ImageView selectimage_bt;
    private static final int IMAGE_PICK = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        submit_bt = findViewById(R.id.submit_post_bt);
        selectimage_bt  =findViewById(R.id.select_image_view);
        submit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selectimage_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i,IMAGE_PICK);
            }
        });
    }
}
