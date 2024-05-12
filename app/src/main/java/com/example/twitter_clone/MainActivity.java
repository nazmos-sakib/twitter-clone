package com.example.twitter_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.twitter_clone.databinding.ActivityMainBinding;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());

        binding.button.setOnClickListener(View->{
            ParseUser.logInInBackground(binding.evUserName.getText().toString(), binding.evPasswords.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e==null){
                        Log.i("Login", ": Success");
                        redirectUser();
                    } else {
                        ParseUser newUser = new ParseUser();
                        newUser.setUsername(binding.evUserName.getText().toString());
                        newUser.setPassword(binding.evPasswords.getText().toString());
                        //newUser.put("isFollowing", Pars);

                        newUser.signUpInBackground(exception->{
                            if (exception==null){
                                Log.i("Signup", ": success");
                                redirectUser();
                            } else {
                                Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        });


    }

    void redirectUser(){
        if (ParseUser.getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}