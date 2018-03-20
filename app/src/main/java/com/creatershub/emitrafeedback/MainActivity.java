package com.creatershub.emitrafeedback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserDetails", 0);

        if (!sharedPreferences.getString("username", "").isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), GiveFeedback.class);
            startActivity(intent);
            finish();
        }

    }

    public void onClickLogin(View view) throws JSONException {
        TextView loginIdTextView = findViewById(R.id.loginid);
        TextView passwordTextView = findViewById(R.id.password);
        String loginId = loginIdTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://winning-journey.herokuapp.com/android/login";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", loginId);
        jsonBody.put("password", password);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.get("status").toString();
                            switch (status) {
                                case "200":
                                    Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                    JSONObject data = (JSONObject) response.get("data");
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserDetails", 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("id", data.getString("_id"));
                                    editor.putString("username", data.getString("username"));
                                    editor.putString("password", data.getString("password"));
                                    editor.apply();
                                    Intent intent = new Intent(getApplicationContext(), GiveFeedback.class);
                                    startActivity(intent);
                                    break;
                                case "404":
                                case "401":
                                    Toast.makeText(MainActivity.this, "Wrong credentials!", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Log.e("Error", response.get("status").toString());
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse == null) {
                            Toast.makeText(MainActivity.this, "Internet Service not available!", Toast.LENGTH_SHORT).show();
                        }

                        else {
                            Log.e("Error", error.toString());
                        }
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}