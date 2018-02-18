package software_project.com.hoarder.Activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.Type;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import software_project.com.hoarder.Adapter.CheckoutArrayAdapter;
import software_project.com.hoarder.Adapter.ReceiptArrayAdapter;
import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.Object.Receipt;
import software_project.com.hoarder.R;


public class ReceiptsActivity extends AppCompatActivity {
    int receiptCount = 0;
    TextView receiptCountTxt,recentOrderTxt;
    public static final String SESSION_NAME = "session";
    ArrayList<Receipt> receiptArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        receiptCountTxt = (TextView) findViewById(R.id.receiptCountTxt);
        recentOrderTxt = (TextView) findViewById(R.id.recentOrderTxt);
        ListView receiptList = (ListView) findViewById(R.id.receiptList);
        receiptList.setDivider(null);

        SharedPreferences session = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = session.getString("receipt", null);
        if (json != null) {
            java.lang.reflect.Type type = new TypeToken<ArrayList<Receipt>>() {
            }.getType();
            receiptArray = gson.fromJson(json, type);

            if (receiptArray.size() > 0) {
                receiptCount = receiptArray.size();
            }

            // Create the adapter
            final ReceiptArrayAdapter receiptArrayAdapter = new ReceiptArrayAdapter(this,receiptArray);
            // Attach the adapter to the list view
            receiptList.setAdapter(receiptArrayAdapter);
        }

        receiptCountTxt.setText(String.valueOf(receiptCount));
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
