package software_project.com.hoarder.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.simplify.android.sdk.CardEditor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import software_project.com.hoarder.Adapter.CheckoutArrayAdapter;
import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.R;

/**
 * Created by Niall on 30/11/2016.
 */
public class PaymentActivity extends AppCompatActivity{
    String serverUrl = "http://hoarder-app.herokuapp.com/payment";
    double credit,total;
    String email;
    TextView totalCostAmnt,totalCreditAmnt;
    ArrayList<Item> itemArray;
    JSONArray jsArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        final Button checkoutButton = (Button) findViewById(R.id.payBtn);
        final CardEditor cardEditor = (CardEditor) findViewById(R.id.card_editor);
        //Get listView item
        ListView checkoutList = (ListView) findViewById(R.id.checkoutList);

        email = (String) getIntent().getSerializableExtra("email");
        total = (Double) getIntent().getSerializableExtra("total");
        credit = total * 0.01;

        totalCostAmnt = (TextView)findViewById(R.id.costTxtCheckout);
        totalCreditAmnt = (TextView)findViewById(R.id.loyaltyCreditTxt);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        totalCostAmnt.setText(String.valueOf(currencyFormatter.format(total)));
        totalCreditAmnt.setText(String.valueOf(currencyFormatter.format(credit)));

        checkoutList.setDivider(null);

        itemArray = (ArrayList<Item>) getIntent().getSerializableExtra("mylist");

        jsArray = new JSONArray();
        for(int i = 0; i < itemArray.size(); i++) {
            try {
                JSONObject object=new JSONObject();
                object.put("productName",itemArray.get(i).getName());
                object.put("productPrice",itemArray.get(i).getPrice());
                jsArray.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Create the adapter
        final CheckoutArrayAdapter checkoutAdapter = new CheckoutArrayAdapter(this,itemArray);

        // Attach the adapter to the list view
        checkoutList.setAdapter(checkoutAdapter);



        // add state change listener
        cardEditor.addOnStateChangedListener(new CardEditor.OnStateChangedListener() {
            @Override
            public void onStateChange(CardEditor cardEditor) {
                // isValid() == true : card editor contains valid and complete card information
                checkoutButton.setEnabled(cardEditor.isValid());
            }
        });

        //Setup payment http request
        final RequestQueue requestQueue = Volley.newRequestQueue(PaymentActivity.this);
        // add checkout button click listener
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                Map<String, String> jsonParams = new HashMap<String, String>();

                final String number = cardEditor.getCard().getNumber().toString().trim();
                final String cvc = cardEditor.getCard().getCvc().toString().trim();
                final String expMonth = cardEditor.getCard().getExpMonth().toString().trim();
                final String expYear = cardEditor.getCard().getExpYear().toString().trim();

                jsonParams.put("amount", "400");
                jsonParams.put("expMonth", expMonth);
                jsonParams.put("expYear", expYear);
                jsonParams.put("cvc", cvc);
                jsonParams.put("number", number);

                JsonObjectRequest paymentRequest = new JsonObjectRequest( Request.Method.POST, serverUrl,

                        new JSONObject(jsonParams),
                        new Response.Listener<JSONObject>() {
                            @Override
                            //Success response from Payment(Credit loading and receipt generation)
                            public void onResponse(JSONObject response) {
                                String creditUrl = "https://hoarder-app.herokuapp.com/credit/"+email;
                                final String receiptUrl = "https://hoarder-app.herokuapp.com/receipt/"+email;
                                Map<String, Double> updateCredit = new HashMap<>();
                                final Map<String, JSONArray> createReceipt = new HashMap<>();

                                updateCredit.put("credit", credit);
                                createReceipt.put("items", jsArray);

                                /**
                                 * Add credit for transaction
                                 */
                                JsonObjectRequest creditRequest = new JsonObjectRequest( Request.Method.POST, creditUrl,new JSONObject(updateCredit),
                                        //Success response from credit loading
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                /**
                                                 * Generate receipt for transaction
                                                 */
                                                JsonObjectRequest receiptRequest = new JsonObjectRequest( Request.Method.POST, receiptUrl,new JSONObject(createReceipt),
                                                        //Success response from credit loading
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                Toast toast = Toast.makeText(getApplicationContext(),
                                                                        "Payment Successful!!", Toast.LENGTH_SHORT);
                                                                toast.show();
                                                                Intent paymentSuccessIntent = new Intent(PaymentActivity.this, MainActivity.class);
                                                                paymentSuccessIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                paymentSuccessIntent.putExtra("email", email);
                                                                startActivity(paymentSuccessIntent);

                                                            }
                                                        },
                                                        //Error response from credit load
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast toast = Toast.makeText(getApplicationContext(),
                                                                        "There was an error generating the receipt", Toast.LENGTH_SHORT);
                                                                toast.show();
                                                                error.printStackTrace();
                                                            }
                                                        }) {
                                                    @Override
                                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                                        HashMap<String, String> headers = new HashMap<>();
                                                        headers.put("Content-Type", "application/json; charset=utf-8");
                                                        return headers;
                                                    }
                                                };
                                                requestQueue.add(receiptRequest);
                                                /**
                                                 * generate receipt for transaction end
                                                 */
                                            }
                                        },
                                        //Error response from credit load
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast toast = Toast.makeText(getApplicationContext(),
                                                        "There was an error loading your store credit for this transaction. The payment has been cancelled please try again!!", Toast.LENGTH_LONG);
                                                toast.show();
                                                error.printStackTrace();
                                            }
                                        }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        HashMap<String, String> headers = new HashMap<>();
                                        headers.put("Content-Type", "application/json; charset=utf-8");
                                        return headers;
                                    }
                                };
                                requestQueue.add(creditRequest);
                                /**
                                 * Add credit for transaction end
                                 */
                            }
                        },
                        //Error response from payment
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "There was an error updating with your payment", Toast.LENGTH_SHORT);
                                toast.show();
                                error.printStackTrace();
                            }}) {

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }
                        };
                requestQueue.add(paymentRequest);
                /**
                 * Payment request for transaction end
                 */
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
}
