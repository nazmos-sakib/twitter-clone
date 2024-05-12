package com.example.twitter_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;

import com.example.twitter_clone.databinding.ActivityFeedBinding;
import com.example.twitter_clone.databinding.ActivityMainBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    String TAG = "FeedActivity";
    ActivityFeedBinding binding;
    ArrayList<Map<String,String>> tweetData = new ArrayList<>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Tweet Feed");
        ParseQuery<ParseObject> quary = ParseQuery.getQuery("Tweet");
        quary.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));
        quary.orderByDescending("createdAt");
        quary.setLimit(20);

        quary.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    for (ParseObject tweet: objects){
                        Map<String,String> tweetInfo = new HashMap<>();
                        tweetInfo.put("Content",tweet.getString("tweet"));
                        tweetInfo.put("username",tweet.getString("username"));
                        tweetData.add(tweetInfo);
                    }

                    SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(),tweetData,
                            android.R.layout.simple_list_item_2,
                            new String[]{"Content","username"},
                            new int[]{android.R.id.text1,android.R.id.text2});
                    binding.tweetList.setAdapter(simpleAdapter);
                }
            }
        });



    }
}