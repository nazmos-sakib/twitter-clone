package com.example.twitter_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.twitter_clone.databinding.ActivityUserBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {
    String TAG = "UserActivity";
    ActivityUserBinding binding;
    ArrayList<String> users = new ArrayList<>();
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("User List");
        users.add("nick");
        users.add("sara");
        binding.userList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_checked,users);

        binding.userList.setAdapter(adapter);
        binding.userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView checkedTextView = (CheckedTextView) view;

                if (checkedTextView.isChecked()){
                    Log.i(TAG, "onItemClick: info -> checked");
                    ParseUser.getCurrentUser().add("isFollowing",users.get(i));
                } else {
                    Log.i(TAG, "onItemClick: info -> not checked");
                    Objects.requireNonNull(ParseUser.getCurrentUser().getList("isFollowing")).remove(users.get(i));
                    List<String> temUser = ParseUser.getCurrentUser().getList("isFollowing");
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing",temUser);
                }

                ParseUser.getCurrentUser().saveInBackground();
            }

        });

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null && objects.size()>0){
                    for (ParseUser user: objects){
                        users.add(user.getUsername());
                    }
                    adapter.notifyDataSetChanged();

                    try {
                        for (String userName:users){
                            if (ParseUser.getCurrentUser().getList("isFollowing").contains(userName)){
                                binding.userList.setItemChecked(users.indexOf(userName),true);
                            }
                        }
                    } catch (Exception exception){
                        return;
                    }
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tweet_menu,menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    //to identify which menu is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tweet_menuItem:
                EditText tweetEditText = new EditText(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Send a Tweet")
                        .setNegativeButton("cancel",(dialogInterface,i)->{
                            dialogInterface.cancel();
                        }).setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ParseObject tweet = new ParseObject("Tweet");
                                tweet.put("tweet",tweetEditText.getText().toString());
                                tweet.put("username",ParseUser.getCurrentUser().getUsername());
                                tweet.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e==null){
                                            Toast.makeText(getApplicationContext(),"Tweet send",Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(getApplicationContext(),"Tweet Failed :(",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });


                builder.setView(tweetEditText);
                builder.show();

                break;
            case R.id.logout_menuItem:
                ParseUser.logOut();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.viewTweet_menuItem:
                startActivity(new Intent(this, FeedActivity.class));
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}