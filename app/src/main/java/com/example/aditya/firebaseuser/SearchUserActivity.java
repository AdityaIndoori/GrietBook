package com.example.aditya.firebaseuser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class SearchUserActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private EditText editTextSearch;
    private Button buttonSearch;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference databaseReference;
    private Query query;
    private int count;
    private ArrayList<String> userNameArray;
    private Toast toast;
    private TextView textViewSearch;
    private FirebaseRecyclerAdapter<UserInformation,SearchViewHolder> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        editTextSearch = (EditText) findViewById(R.id.editTextSearchSU);
        buttonSearch = (Button) findViewById(R.id.buttonSearchSU);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSU);
        databaseReference = FirebaseDatabase.getInstance().getReference("User/");
        buttonSearch.setOnClickListener(this);
        textViewSearch = (TextView)findViewById(R.id.textViewSearch);
        count = 0;
        userNameArray = new ArrayList<String>();
        //RecyclerView:
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onClick(final View view) {
        if (view == buttonSearch){
            count=0;
            textViewSearch.setText("");
             final String userName = editTextSearch.getText().toString();
            if (userName== null || userName.equals("")){
                recyclerView.setVisibility(View.GONE);
                return;
            }
            recyclerView.setVisibility(View.VISIBLE);
            query = databaseReference.orderByChild("name");
            mAdapter = new FirebaseRecyclerAdapter<UserInformation, SearchViewHolder>
                    (UserInformation.class,R.layout.recycler_view_item_su,SearchViewHolder.class,query)
            {
                @Override
                protected void populateViewHolder(SearchViewHolder viewHolder, final UserInformation model, int position) {
                    Log.v("Contains","String 1: " + model.getName().trim().toLowerCase() + " Contains, String 2: " + userName.trim().toLowerCase());
                    if (model.getName().trim().toLowerCase().contains(userName.trim().toLowerCase()) && !model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        Log.v("UID","Logged IN: " );
                        viewHolder.setTextViewUserName(model.getName(), model.getEmailID());
                    }
                    else
                        viewHolder.dontShow();
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Intent intent = new Intent(getApplicationContext(),OtherProfilePostSearchActivity.class);
                            intent.putExtra("UserInfo",model);
                            startActivity(intent);
                            overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                        }
                    });

                }
            };
            recyclerView.setAdapter(mAdapter);
        }
    }

}
