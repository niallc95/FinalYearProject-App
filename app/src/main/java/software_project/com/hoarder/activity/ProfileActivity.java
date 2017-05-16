package software_project.com.hoarder.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.Object.List;
import software_project.com.hoarder.Object.Receipt;
import software_project.com.hoarder.R;

/**
 * Author: Niall Curran
 * Student Number: x13440572
 * Description: This screen shows all user data including loyalty credit
 */

public class ProfileActivity extends AppCompatActivity {
    TextView emailTxt,dateTxt,mobileTxt,nameTxt,storeCreditTxt,ordersTxt;
    String email,date,mobile,name,orders,newPassword,oldPassword;
    TableRow receiptLink,profileLink,listLink;
    Intent receiptProfileIntent,shoppingListProfileIntent;
    double storeCredit;
    public static final String SESSION_NAME = "session";
    String receiptUrl = "https://hoarder-app.herokuapp.com/findReceipt/";
    String findListUrl = "https://hoarder-app.herokuapp.com/findList/";
    String editPasswordUrl = "https://hoarder-app.herokuapp.com/password/";
    ArrayList<Receipt> receiptArray;
    ArrayList<List> listItemArray;
    ProgressDialog progressDialog;
    EditText newPass, oldPass;
    Button confirm,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

        receiptArray = new ArrayList<>();
        listItemArray = new ArrayList<>();

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

        receiptLink = (TableRow) findViewById(R.id.receiptListRowProfile);
        receiptLink.setClickable(true);
        receiptLink.setOnClickListener(receiptClick);

        profileLink = (TableRow) findViewById(R.id.editRowProfile);
        profileLink.setClickable(true);
        profileLink.setOnClickListener(profileClick);

        listLink = (TableRow) findViewById(R.id.shoppingListRowProfile);
        listLink.setClickable(true);
        listLink.setOnClickListener(listClick);

        receiptProfileIntent = new Intent(ProfileActivity.this, ReceiptsListActivity.class);
        shoppingListProfileIntent = new Intent(ProfileActivity.this, ListActivity.class);
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

    View.OnClickListener receiptClick= new View.OnClickListener() {
        public void onClick(View v) {
            getReceiptsProfile();
        }
    };

    View.OnClickListener profileClick= new View.OnClickListener() {
        public void onClick(View v) {
            changePassword();
        }
    };

    View.OnClickListener listClick= new View.OnClickListener() {
        public void onClick(View v) {
            getProfileLists();
        }
    };

    public void changePassword(){
        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.edit_password_dialog);

        cancel = (Button) dialog.findViewById(R.id.editPasswordCancel);
        confirm = (Button) dialog.findViewById(R.id.editPasswordConfirm);

        newPass = (EditText) dialog.findViewById(R.id.edit_password_text);
        oldPass = (EditText) dialog.findViewById(R.id.edit_password_old_text);

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!validate()) {
                    onPasswordChangeFailed();
                    return;
                }

                progressDialog = ProgressDialog.show(ProfileActivity.this, null, null, true, false);
                progressDialog.setIndeterminate(true);
                progressDialog.setContentView(R.layout.progress_layout);
                progressDialog.show();

                newPassword = newPass.getText().toString();
                oldPassword = oldPass.getText().toString();

                Map<String, Object> updatePassword = new HashMap<>();
                updatePassword.put("newPassword", newPassword);
                updatePassword.put("oldPassword", oldPassword);

                final RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
                JsonObjectRequest updatePasswordRequest = new JsonObjectRequest(Request.Method.POST, editPasswordUrl+email , new JSONObject(updatePassword),
                        //Success response from password change
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Your password has been successfully changed!", Toast.LENGTH_SHORT);
                                toast.show();
                                progressDialog.dismiss();
                                dialog.dismiss();
                            }
                        },
                        //Error response from password change
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Error changing password. Please try again!", Toast.LENGTH_SHORT);
                                toast.show();
                                error.printStackTrace();
                                progressDialog.dismiss();
                                dialog.dismiss();
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
                requestQueue.add(updatePasswordRequest);
            }
        });
        dialog.show();
    }

    public void onPasswordChangeFailed() {
        Toast.makeText(getBaseContext(), "Error changing password. Please try again!", Toast.LENGTH_LONG).show();
        confirm.setEnabled(true);
    }

    public void getProfileLists(){
        listItemArray.clear();
        /**
         * Retrieve shopping list from server
         */
        final RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        StringRequest getListRequest = new StringRequest(Request.Method.GET, findListUrl+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray responseObject = new JSONArray(response);
                            JSONObject itemObject = responseObject.getJSONObject(0);
                            String items = itemObject.optString("items");
                            JSONArray responseArray = new JSONArray(items);
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject item = responseArray.getJSONObject(i);
                                String productName = item.optString("productName");
                                String productPrice = item.optString("productPrice");
                                List list = new List(productName,productPrice);
                                listItemArray.add(list);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor sessionEditor = getSharedPreferences(SESSION_NAME, MODE_PRIVATE).edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(listItemArray);

                        sessionEditor.putString("list", json);
                        sessionEditor.commit();
                        startActivity(shoppingListProfileIntent);
                        requestQueue.stop();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Your shopping list is empty!!", Toast.LENGTH_SHORT);
                toast.show();
                error.printStackTrace();
                startActivity(shoppingListProfileIntent);
                requestQueue.stop();
            }
        });
        requestQueue.add(getListRequest);
        /**
         * Retrieve shopping list from server end
         */
    }

    public void getReceiptsProfile(){
        /**
         * Retrieve receipts from server
         */
        final RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        StringRequest receiptRequest = new StringRequest(Request.Method.GET, receiptUrl+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            receiptArray.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ArrayList<Item> itemList = new ArrayList<>();
                                JSONObject receiptObject = jsonArray.getJSONObject(i);
                                String date = receiptObject.optString("date");
                                String time = receiptObject.optString("time");
                                String referenceNumber = receiptObject.optString("referenceNumber");
                                String totalCost = receiptObject.optString("totalCost");
                                String itemCount = receiptObject.optString("itemCount");
                                String discount = receiptObject.optString("discount");
                                JSONArray receiptItems = receiptObject.optJSONArray("items");
                                for(int j = 0; j < receiptItems.length(); j++) {
                                    JSONObject itemObject = receiptItems.getJSONObject(j);
                                    String name = itemObject.optString("productName");
                                    double price = Double.parseDouble(itemObject.optString("productPrice"));
                                    int quantity = Integer.valueOf(itemObject.optString("productQuantity"));
                                    Item item = new Item(name, price, "", quantity);
                                    itemList.add(item);
                                }
                                Receipt receipt = new Receipt(date,time,itemCount,totalCost,referenceNumber,discount,itemList);
                                receiptArray.add(receipt);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor sessionEditor = getSharedPreferences(SESSION_NAME, MODE_PRIVATE).edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(receiptArray);

                        sessionEditor.putString("receipt", json);
                        sessionEditor.commit();
                        startActivity(receiptProfileIntent);
                        requestQueue.stop();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "You have no previous receipts at this store!!", Toast.LENGTH_SHORT);
                toast.show();
                error.printStackTrace();
                startActivity(receiptProfileIntent);
                requestQueue.stop();
            }
        });
        requestQueue.add(receiptRequest);
        /**
         * Retrieve receipts from server end
         */
    }

    public boolean validate() {
        boolean valid = true;

        String newPassword = newPass.getText().toString();
        String oldPassword = oldPass.getText().toString();

        if (newPassword.isEmpty()|| newPassword.length() < 6 || newPassword.length() > 13){
            newPass.setError("New Password must be between 6 and 13 alphanumeric characters");
            valid = false;
        } else {
            newPass.setError(null);
        }

        if (oldPassword.isEmpty() || oldPassword.length() < 6 || oldPassword.length() > 13) {
            oldPass.setError("Invalid password");
            valid = false;
        } else {
            oldPass.setError(null);
        }
        return valid;
    }
}
