package software_project.com.hoarder.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.simplify.android.sdk.CardEditor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import software_project.com.hoarder.Adapter.CheckoutArrayAdapter;
import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.R;

/**
 * Author: Niall Curran
 * Student Number: x13440572
 * Description: This screen shows the user their checkout summary as well as allowing them to use their store credit to lessen
 *              the total cost of the transaction before making the payment.
 */

public class PaymentActivity extends AppCompatActivity{
    String serverUrl = "http://hoarder-app.herokuapp.com/payment";
    double credit,total,discount,accountCredit, remainingCredit;
    String email,totalCost,cartContent;
    TextView totalCostAmnt,totalCreditAmnt,discountText;
    ArrayList<Item> itemArray;
    JSONArray jsArray;
    CardEditor cardEditor;
    Button checkoutButton;
    NumberFormat currencyFormatter;
    FloatingActionButton loyaltyPromptBtn;
    public static final String SESSION_NAME = "session";
    SharedPreferences session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        checkoutButton = (Button) findViewById(R.id.payBtn);
        loyaltyPromptBtn = (FloatingActionButton) findViewById(R.id.loyaltyPromptBtn);
        //Get listView item
        ListView checkoutList = (ListView) findViewById(R.id.checkoutList);

        session = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = session.getString("items", null);
        java.lang.reflect.Type type = new TypeToken<ArrayList<Item>>(){}.getType();
        itemArray = gson.fromJson(json, type);

        email = session.getString("email", null);
        cartContent = session.getString("itemCount",null);
        total = Double.parseDouble(session.getString("total", null));
        accountCredit = Double.parseDouble(session.getString("userAccountCredit", null));
        remainingCredit = Double.parseDouble(session.getString("userAccountCredit", null));
        credit = total * 0.01;
        totalCost = String.valueOf(total*100);

        totalCostAmnt = (TextView)findViewById(R.id.totalTxt);
        totalCreditAmnt = (TextView)findViewById(R.id.loyaltyTxt);
        discountText = (TextView) findViewById(R.id.discountTxt);

        currencyFormatter = NumberFormat.getCurrencyInstance();
        totalCostAmnt.setText(String.valueOf(currencyFormatter.format(total)));
        totalCreditAmnt.setText(String.valueOf(currencyFormatter.format(credit)));
        discountText.setText(String.valueOf(currencyFormatter.format(0.00)));

        cardEditor = (CardEditor) findViewById(R.id.card_editor);
        // add state change listener
        cardEditor.addOnStateChangedListener(new CardEditor.OnStateChangedListener() {
            @Override
            public void onStateChange(CardEditor cardEditor) {
                // isValid() == true : card editor contains valid and complete card information
                checkoutButton.setEnabled(cardEditor.isValid());
            }
        });

        checkoutList.setDivider(null);

        jsArray = new JSONArray();
        for(int i = 0; i < itemArray.size(); i++) {
            try {
                JSONObject object=new JSONObject();
                object.put("productName",itemArray.get(i).getName());
                object.put("productPrice",itemArray.get(i).getPrice());
                object.put("productQuantity",itemArray.get(i).getQuantity());
                jsArray.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Create the adapter
        final CheckoutArrayAdapter checkoutAdapter = new CheckoutArrayAdapter(this,itemArray);

        // Attach the adapter to the list view
        checkoutList.setAdapter(checkoutAdapter);

        // add checkout button click listener
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                makePayment();
            }
        });

        // add loyalty button click listener
        loyaltyPromptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if(accountCredit > 0){
                    final Dialog dialog = new Dialog(PaymentActivity.this);
                    dialog.setContentView(R.layout.discount_dialog);

                    Button yesBtn = (Button) dialog.findViewById(R.id.yesBtn);
                    Button noBtn = (Button) dialog.findViewById(R.id.noBtn);

                    noBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            discount = 0.00;
                            discountText.setText(String.valueOf(currencyFormatter.format(discount)));
                            remainingCredit = accountCredit - discount;
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "You have " + String.valueOf(currencyFormatter.format(remainingCredit)) + " remaining", Toast.LENGTH_LONG);
                            toast.show();
                            getDiscountedCost();
                            dialog.dismiss();
                        }
                    });
                    yesBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if(accountCredit > total) {
                                discount = total;
                                discountText.setText("-"+String.valueOf(currencyFormatter.format(discount)));
                                remainingCredit = accountCredit - discount;
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "You have " + String.valueOf(currencyFormatter.format(remainingCredit)) + " remaining", Toast.LENGTH_LONG);
                                toast.show();
                                getDiscountedCost();
                                dialog.dismiss();
                            }else{
                                discount = accountCredit;
                                remainingCredit = accountCredit - discount;
                                discountText.setText("-"+String.valueOf(currencyFormatter.format(discount)));
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "You have " + String.valueOf(currencyFormatter.format(remainingCredit)) + " remaining", Toast.LENGTH_LONG);
                                toast.show();
                                getDiscountedCost();
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "There is no credit associated with this account!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public void makePayment() {
        checkoutButton.setEnabled(false);

        final ProgressDialog progressDialog = ProgressDialog.show(this, null, null, true, false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Contacting Simplify...");
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.show();

        //Setup payment http request
        final RequestQueue requestQueue = Volley.newRequestQueue(PaymentActivity.this);
        final Map<String, String> jsonParams = new HashMap<String, String>();
        if (cardEditor.getCard().getNumber() == null || cardEditor.getCard().getCvc() == null || cardEditor.getCard().getExpMonth() == null || cardEditor.getCard().getExpYear() == null) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Please make sure no field is empty!", Toast.LENGTH_SHORT);
            toast.show();
            progressDialog.dismiss();
            checkoutButton.setEnabled(true);
        } else {
            final String number = cardEditor.getCard().getNumber().toString().trim();
            final String cvc = cardEditor.getCard().getCvc().toString().trim();
            final String expMonth = cardEditor.getCard().getExpMonth().toString().trim();
            final String expYear = cardEditor.getCard().getExpYear().toString().trim();

            jsonParams.put("amount", totalCost);
            jsonParams.put("expMonth", expMonth);
            jsonParams.put("expYear", expYear);
            jsonParams.put("cvc", cvc);
            jsonParams.put("number", number);

            JsonObjectRequest paymentRequest = new JsonObjectRequest(Request.Method.POST, serverUrl,

                    new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        //Success response from Payment(Credit loading and receipt generation)
                        public void onResponse(JSONObject response) {
                            String creditUrl = "https://hoarder-app.herokuapp.com/credit/" + email;
                            final String receiptUrl = "https://hoarder-app.herokuapp.com/receipt/" + email;
                            Map<String, Object> updateCredit = new HashMap<>();
                            final Map<String, Object> createReceipt = new HashMap<>();

                            Random generateId = new Random();
                            int refNumber = generateId.nextInt(900000 - 100000) + 100000;
                            updateCredit.put("credit", credit+remainingCredit);
                            createReceipt.put("itemCount", cartContent);
                            createReceipt.put("discount", discount);
                            createReceipt.put("totalCost", String.valueOf(total));
                            createReceipt.put("referenceNumber", String.valueOf(refNumber));
                            createReceipt.put("items", jsArray);

                            /**
                             * Add credit for transaction
                             */
                            JsonObjectRequest creditRequest = new JsonObjectRequest(Request.Method.POST, creditUrl, new JSONObject(updateCredit),
                                    //Success response from credit loading
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            /**
                                             * Generate receipt for transaction
                                             */
                                            JsonObjectRequest receiptRequest = new JsonObjectRequest(Request.Method.POST, receiptUrl, new JSONObject(createReceipt),
                                                    //Success response from credit loading
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            Toast toast = Toast.makeText(getApplicationContext(),
                                                                    "Payment Successful!", Toast.LENGTH_SHORT);
                                                            toast.show();
                                                            Intent paymentSuccessIntent = new Intent(PaymentActivity.this, MainActivity.class);
                                                            paymentSuccessIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            progressDialog.dismiss();
                                                            startActivity(paymentSuccessIntent);
                                                            finish();
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
                                                            progressDialog.dismiss();
                                                            checkoutButton.setEnabled(true);
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
                                                    "There was an error loading your store credit for this transaction. The payment has been cancelled please try again!", Toast.LENGTH_LONG);
                                            toast.show();
                                            error.printStackTrace();
                                            progressDialog.dismiss();
                                            checkoutButton.setEnabled(true);
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
                                    error.networkResponse.toString(), Toast.LENGTH_SHORT);
                            toast.show();
                            error.printStackTrace();
                            progressDialog.dismiss();
                            checkoutButton.setEnabled(true);
                        }
                    }) {

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


    public void getDiscountedCost(){
        double cost = total - discount;
        totalCostAmnt.setText(String.valueOf(currencyFormatter.format(cost)));
    }

}
