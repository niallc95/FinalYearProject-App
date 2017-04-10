package software_project.com.hoarder.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import software_project.com.hoarder.R;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;
    Button loginBtn;
    TextView signupLink;
    EditText passwordText;
    EditText emailText;
    String serverUrl = "http://hoarder-app.herokuapp.com/login";
    String accountUrl = "https://hoarder-app.herokuapp.com/user/";
    String email,name,accountCredit;
    ProgressDialog progressDialog;
    public static final String SESSION_NAME = "session";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        clear();

        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        loginBtn = (Button) findViewById(R.id.btn_login);
        signupLink = (TextView) findViewById(R.id.link_signup);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    //Login request to server functionality
    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        loginBtn.setEnabled(false);

        progressDialog = ProgressDialog.show(this, null, null, true, false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.show();

        final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        Map<String, String> jsonParams = new HashMap<>();
            jsonParams.put("email", email);
            jsonParams.put("password", password);

        JsonObjectRequest postRequest = new JsonObjectRequest( Request.Method.POST, serverUrl,

                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getUserInformation();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Login credentials incorrect. Please try again!!", Toast.LENGTH_SHORT);
                        toast.show();
                        error.printStackTrace();
                        progressDialog.dismiss();
                        loginBtn.setEnabled(true);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        // back press disable
        moveTaskToBack(true);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed. Please try again!!", Toast.LENGTH_LONG).show();
        loginBtn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty()||!email.contains("@")){
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }
        return valid;
    }

    public void clear(){
        SharedPreferences.Editor editor = getSharedPreferences(SESSION_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public void getUserInformation(){
        /**
         * Retrieve user information from server
         */
        final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest userRequest = new StringRequest(Request.Method.GET, accountUrl+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject profile = new JSONObject(response);
                            accountCredit = String.valueOf(profile.optDouble("credit"));
                            name = profile.optString("name");

                            Toast toast = Toast.makeText(getApplicationContext(),
                                    accountCredit +"You are successfully logged in!!", Toast.LENGTH_SHORT);
                            toast.show();
                            progressDialog.dismiss();
                            SharedPreferences.Editor sessionEditor = getSharedPreferences(SESSION_NAME, MODE_PRIVATE).edit();
                            sessionEditor.putString("email", email);
                            sessionEditor.putString("userAccountCredit", accountCredit);
                            sessionEditor.commit();
                            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(loginIntent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        requestQueue.stop();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Error loading profile", Toast.LENGTH_SHORT);
                toast.show();
                error.printStackTrace();
                requestQueue.stop();
            }
        });
        requestQueue.add(userRequest);
        /**
         * Retrieve receipts from server end
         */
    }
}