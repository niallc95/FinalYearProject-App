package software_project.com.hoarder.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import software_project.com.hoarder.R;

public class SignupActivity extends AppCompatActivity {
    Button signupBtn;
    TextView loginLink;
    EditText passwordText;
    EditText emailText;
    EditText fnameText;
    EditText lnameText;
    EditText mobileText;
    String serverUrl = "http://hoarder-app.herokuapp.com/user";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        fnameText = (EditText) findViewById(R.id.input_fname);
        lnameText = (EditText) findViewById(R.id.input_lname);
        mobileText = (EditText) findViewById(R.id.input_phone);
        signupBtn = (Button) findViewById(R.id.btn_signup);
        loginLink = (TextView) findViewById(R.id.link_login);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    //Signup request to server functionality
    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupBtn.setEnabled(false);

        final ProgressDialog progressDialog = ProgressDialog.show(this, null, null, true, false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account");
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.show();

        final RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);
        String fname = fnameText.getText().toString();
        String lname = lnameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String mobile = mobileText.getText().toString();

        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("name", fname+" "+lname);
        jsonParams.put("password", password);
        jsonParams.put("email", email);
        jsonParams.put("phoneNumber", mobile);

        JsonObjectRequest postRequest = new JsonObjectRequest( Request.Method.POST, serverUrl,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Account successfully created!!", Toast.LENGTH_SHORT);
                        toast.show();
                        progressDialog.dismiss();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Account with this email already exists. Please try again!!", Toast.LENGTH_SHORT);
                        toast.show();
                        error.printStackTrace();
                        progressDialog.dismiss();
                        signupBtn.setEnabled(true);
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

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        signupBtn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String fname = fnameText.getText().toString();
        String lname = lnameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (fname.isEmpty() || fname.length() < 3) {
            fnameText.setError("at least 3 characters");
            valid = false;
        } else {
            fnameText.setError(null);
        }

        if (lname.isEmpty() || lname.length() < 3) {
            lnameText.setError("at least 3 characters");
            valid = false;
        } else {
            lnameText.setError(null);
        }

        if (email.isEmpty()||!email.contains("@")) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 13 ) {
            passwordText.setError("Password must be between 6 and 13 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}