package software_project.com.hoarder.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.simplify.android.sdk.CardEditor;
import com.simplify.android.sdk.CardToken;
import com.simplify.android.sdk.Simplify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.R;
import software_project.com.hoarder.Request.CustomRequest;

/**
 * Created by Niall on 30/11/2016.
 */

public class PaymentActivity extends AppCompatActivity {

    Simplify simplify;
    String serverUrl = "http://hoarder-app.herokuapp.com/payment";
    CardEditor cardEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);

        simplify = new Simplify();
        simplify.setApiKey(getString(R.string.simplify_api_key));

        // init card editor
        cardEditor = (CardEditor) findViewById(R.id.card_editor);
        final Button checkoutButton = (Button) findViewById(R.id.payBtn);
        // add state change listener
        cardEditor.addOnStateChangedListener(new CardEditor.OnStateChangedListener() {
            @Override
            public void onStateChange(CardEditor cardEditor) {
                checkoutButton.setEnabled(cardEditor.isValid());
            }
        });

        // add checkout button click listener
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a card token
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(PaymentActivity.this);
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("amount", "400");
                    jsonBody.put("expMonth", "12");
                    jsonBody.put("expYear", "19");
                    jsonBody.put("number", "5555555555554444");
                    jsonBody.put("cvc", "123");
                    final String mRequestBody = jsonBody.toString();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("VOLLEY", response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", error.toString());
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }
                    };

                    requestQueue.add(stringRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

    private void sendPayment() throws JSONException {
        final String amount = "200";
        final String number = cardEditor.getCard().getNumber().toString().trim();
        final String cvc = cardEditor.getCard().getCvc().toString().trim();
        final String expMonth = cardEditor.getCard().getExpMonth().toString().trim();
        final String expYear = cardEditor.getCard().getExpYear().toString().trim();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("amount", "400");
        jsonBody.put("expMonth", "12");
        jsonBody.put("expYear", "19");
        jsonBody.put("number", "5555555555554444");
        jsonBody.put("cvc", "123");
    }
}
