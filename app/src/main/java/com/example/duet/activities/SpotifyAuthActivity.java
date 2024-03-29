package com.example.duet.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.duet.MSP;
import com.example.duet.R;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SpotifyAuthActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor;

    private RequestQueue queue;

    String CLIENT_ID = "20a43da3a3d6461a939c9b70211eff0b";
    private static final int REQUEST_CODE = 1337;
    private static final boolean SHOW_DIALOG = true;
    private static final String REDIRECT_URI = "com.example.duet://callback";
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";
    private String songName="";
    private String []chosenArtists=new String[3];
    Intent newintent = new Intent(SpotifyAuthActivity.this, MatchActivity.class);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_spotify_auth);
        authenticateSpotify();
        queue = Volley.newRequestQueue(this);

    }


    private void authenticateSpotify() {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES}).setShowDialog(SHOW_DIALOG);
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    editor = getSharedPreferences("SP_FILE", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    editor.apply();
                    getArtists();
                    getSong();
                    startMainActivity();
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d("ccc","Didn't get spotify token");
                    // Handle error response
                    break;
                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }




    private void startMainActivity(){
        String ENDPOINT = "https://api.spotify.com/v1/me";
        RequestQueue mqueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ENDPOINT, null, response -> {
            String email=response.optString("email");
            getUser(email);

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = MSP.getMe().getString("token","");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        mqueue.add(jsonObjectRequest);
    }

    private void getSong(){
        String ENDPOINT = "https://api.spotify.com/v1/me/top/tracks";
        RequestQueue mqueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ENDPOINT, null, response -> {

            try {
                JSONArray items=response.getJSONArray("items");
                JSONObject song=items.getJSONObject(Integer.parseInt("0"));
                JSONArray artists=song.getJSONArray("artists");
                JSONObject artist1=artists.getJSONObject(Integer.parseInt("0"));
                String artistName=artist1.getString("name");
                songName=song.getString("name")+" by "+artistName;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = MSP.getMe().getString("token","");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        mqueue.add(jsonObjectRequest);
    }

    private void getArtists(){
        String ENDPOINT = "https://api.spotify.com/v1/me/top/artists";
        RequestQueue mqueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ENDPOINT, null, response -> {

            try {
                JSONArray items=response.getJSONArray("items");
                JSONObject artist1=items.getJSONObject(Integer.parseInt("0"));
                String artistName1=artist1.getString("name");
                JSONObject artist2=items.getJSONObject(Integer.parseInt("1"));
                String artistName2=artist2.getString("name");
                JSONObject artist3=items.getJSONObject(Integer.parseInt("2"));
                String artistName3=artist3.getString("name");
                chosenArtists= new String[]{artistName1, artistName2, artistName3};

                //Log.d("getArtists",chosenArtists[0]+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = MSP.getMe().getString("token","");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        mqueue.add(jsonObjectRequest);
    }

    private void getUser(String email) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String endpoint = "http://"+getString(R.string.ip)+":8085/iob/users/login/2022b.Yaeli.Bar.Gimelshtei/" + email;
        StringRequest request = new StringRequest(Request.Method.GET, endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // on below line we are displaying a success toast message.
                        try {
                            //response json
                            JSONObject respObj = new JSONObject(response);
                            newintent = new Intent(SpotifyAuthActivity.this, MatchActivity.class);
                            newintent.putExtra("email",email);
                            startActivity(newintent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newintent = new Intent(SpotifyAuthActivity.this, RegistrationFormForSpotifyUser.class);
                newintent.putExtra("email",email);
                newintent.putExtra("chosenArtists",chosenArtists);
                newintent.putExtra("song",songName);
                startActivity(newintent);
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
        queue.add(request);
    }

}