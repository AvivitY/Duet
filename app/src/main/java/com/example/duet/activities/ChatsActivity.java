package com.example.duet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duet.ChatAdapter;
import com.example.duet.R;
import com.example.duet.data.Chat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class ChatsActivity extends AppCompatActivity {
    private RecyclerView chats_LST_chats;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        findviews();
        initNavigation();
        setChats();
    }

    private void initNavigation() {
        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.home:
                        //move to home activity
                        finish();
                        return true;

                    case R.id.chats:
                        //stay in this activity
                        return true;
                    case R.id.person:
                        //move to profile activity
                        Intent profile = new Intent(getApplicationContext(),ProfileActivity.class);
                        profile.putExtra("email",getIntent().getStringExtra("email"));
                        profile.putExtra("avatar",getIntent().getStringExtra("avatar"));
                        startActivity(profile);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    private void setChats() {
        ArrayList<Chat> chats = new ArrayList<>();
        chats.add(new Chat());
        chats.add(new Chat());
        chats.add(new Chat());
        chats.add(new Chat());
        chats.add(new Chat());
        chats.add(new Chat());
        chats.add(new Chat());
        ChatAdapter chatAdapter = new ChatAdapter(this, chats);
        chats_LST_chats.setLayoutManager(new LinearLayoutManager(this));
        chats_LST_chats.setHasFixedSize(true);
        chats_LST_chats.setAdapter(chatAdapter);
    }

    private void findviews() {
        chats_LST_chats = findViewById(R.id.chats_LST_chats);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.chats);
        bottomNavigationView.setItemIconSize(70);
    }
}