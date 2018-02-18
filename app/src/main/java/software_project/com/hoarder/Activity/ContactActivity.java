package software_project.com.hoarder.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import software_project.com.hoarder.R;

/**
 * Author: Niall Curran
 * Student Number: x13440572
 * Description: Contact screen for the user
 */

public class ContactActivity extends AppCompatActivity {

    Button send;
    EditText name,email,phone,message;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        send = (Button) findViewById(R.id.contactSubmitBtn);
        name = (EditText)findViewById(R.id.nameFieldContact);
        email = (EditText)findViewById(R.id.emailFieldContact);
        phone = (EditText)findViewById(R.id.phoneFieldContact);
        message = (EditText)findViewById(R.id.messageFieldContact);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String nameText = name.getText().toString();
        String emailText = email.getText().toString();
        String phoneText = phone.getText().toString();
        String messageText = message.getText().toString();

        if (emailText.isEmpty()||!emailText.contains("@")){
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (nameText.isEmpty()) {
            name.setError("Please input your name");
            valid = false;
        } else {
            name.setError(null);
        }

        if (phoneText.isEmpty()) {
            phone.setError("Please input your phone number");
            valid = false;
        } else {
            phone.setError(null);
        }

        if (messageText.isEmpty()) {
            message.setError("The message field cannot be blank");
            valid = false;
        } else {
            message.setError(null);
        }
        return valid;
    }

    public void send() {
        if (!validate()) {
            onSendFailed();
            return;
        }
        send.setEnabled(false);

        progressDialog = ProgressDialog.show(this, null, null, true, false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.show();

        Toast toast = Toast.makeText(getApplicationContext(),
                "Your email has been sent!", Toast.LENGTH_SHORT);
        toast.show();

        progressDialog.dismiss();
        finish();
    }

    public void onSendFailed() {
        Toast.makeText(getBaseContext(), "Email sending failed. Please try again!", Toast.LENGTH_LONG).show();
        send.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
