package com.creatershub.emitrafeedback;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class GiveFeedback extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://winning-journey.herokuapp.com/");
        } catch (URISyntaxException e) {}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_feedback);
        mSocket.connect();
    }

    public void onClickSubmitFeedback(View view) {
        EditText kioskIdField = findViewById(R.id.kioskIdField);
        EditText feedbackField = findViewById(R.id.feedbackText);
        String kioskId = kioskIdField.getText().toString();
        String feedback = feedbackField.getText().toString();
        RatingBar ratingBar = findViewById(R.id.kioskRatingBar);
        int rating = ratingBar.getNumStars();
        if (kioskId.isEmpty()) {
            Toast.makeText(this, "Please enter the kiosk ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        else if (feedback.isEmpty()) {
            Toast.makeText(this, "Please enter the feedback description!", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = "EM " + kioskId + " " + Integer.toString(rating) + " " + feedback;
        JSONObject json = new JSONObject();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserDetails", 0);
        String userid = sharedPreferences.getString("username", "");
        try {
            json.put("message", message);
            json.put("username", userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("storeuserFeedback", json);
        Toast.makeText(this, "Thankyou for submitting the feedback!!", Toast.LENGTH_SHORT).show();
    }
}
