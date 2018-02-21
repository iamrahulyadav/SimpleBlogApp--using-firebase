package com.somesh.myproject.android.simpleblogapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<Blog> firebaseRecyclerOptions;
    Query query;
    FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //.......................................................................................................................

        recyclerView = findViewById(R.id.RecylerView_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        query = FirebaseDatabase.getInstance().getReference().child("Blog");
        firebaseAuth = FirebaseAuth.getInstance();

        //.......................................................................................................................

        query.keepSynced(true);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }
            }
        };
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Blog>()
                .setQuery(query, Blog.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull Blog model) {
                holder.setmView(model.getTitle(), model.getDesc(), model.getImage(), getApplicationContext());
            }

            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);
                return new BlogViewHolder(V);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ClickIcon:
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                return true;
            case R.id.SettingsMenu:
                return true;
            case R.id.LogOut_option:
                logout();
                return true;
            default:
        return super.onOptionsItemSelected(item);
    }}

    private void logout() {
        firebaseAuth.signOut();
    }

    public static final class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;

        private BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setmView(String title, String desc, final String imageUri, final Context context) {
            final ImageView imageView = mView.findViewById(R.id.recycler_image_iv);
            TextView tv1 = mView.findViewById(R.id.recycler_title_tv);
            TextView tv2 = mView.findViewById(R.id.recycler_desc_tv);
            tv1.setText(title);
            tv2.setText(desc);
            Picasso.with(context).load(imageUri).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(imageUri).into(imageView);
                }
            });

        }
    }
}

