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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import software_project.com.hoarder.R;

/**
 * Author: Niall Curran
 * Student Number: x13440572
 * Description: Login screen for the user
 */

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;
    Button loginBtn;
    TextView signupLink;
    EditText passwordText;
    EditText emailText;
    String serverUrl = "http://hoarder-app.herokuapp.com/login";
    String email;
    public static String statusCode;
    ProgressDialog progressDialog;
    public static final String SESSION_NAME = "session";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        clear();

        emailText = (EditText) findViewById(R.id.login_email_address);
        passwordText = (EditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        signupLink = (TextView) findViewById(R.id.signupLink);

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
                Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signupIntent);
                finish();
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
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "You are successfully logged in!!", Toast.LENGTH_SHORT);
                        toast.show();
                        statusCode = "200";
                        progressDialog.dismiss();
                        SharedPreferences.Editor sessionEditor = getSharedPreferences(SESSION_NAME, MODE_PRIVATE).edit();
                        sessionEditor.putString("email", email);
                        sessionEditor.commit();
                        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Login credentials incorrect. Please try again!!", Toast.LENGTH_SHORT);
                        toast.show();
                        error.printStackTrace();
                        statusCode = "200";
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

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed. Please try again!", Toast.LENGTH_LONG).show();
        loginBtn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty()||!email.contains("@")){
            emailText.setError("Please enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 13) {
            passwordText.setError("Invalid password.Password must be between 6 and 13 alphanumeric characters");
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
}