package software_project.com.hoarder.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.NumberFormat;

import software_project.com.hoarder.R;

/**
 * Created by Niall on 02/02/2017.
 */
public class ProfileActivity extends AppCompatActivity {
    TextView emailTxt,dateTxt,mobileTxt,nameTxt,storeCreditTxt,ordersTxt;
    String email,date,mobile,name,orders;
    double storeCredit;
    public static final String SESSION_NAME = "session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

        SharedPreferences session = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        email = session.getString("email", null);
        date = session.getString("creationDate", null);
        mobile = session.getString("phoneNumber", null);
        name = session.getString("name", null);
        storeCredit = Double.parseDouble(session.getString("credit", null));
        orders = session.getString("orders", null);

        nameTxt = (TextView) findViewById(R.id.nameTxt);
        emailTxt = (TextView) findViewById(R.id.emailTxt);
        dateTxt = (TextView) findViewById(R.id.creationTxt);
        mobileTxt = (TextView) findViewById(R.id.mobileNumberText);
        storeCreditTxt = (TextView) findViewById(R.id.storeCreditAmntTxt);
        ordersTxt = (TextView) findViewById(R.id.ordersCompleteTxt);

        nameTxt.setText(name);
        emailTxt.setText(email);
        dateTxt.setText(date);
        mobileTxt.setText(mobile);
        storeCreditTxt.setText(String.valueOf(currencyFormatter.format(storeCredit)));
        ordersTxt.setText(orders);

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
