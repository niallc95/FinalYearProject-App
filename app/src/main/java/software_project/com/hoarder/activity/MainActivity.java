package software_project.com.hoarder.Activity;

import android.Manifest;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import software_project.com.hoarder.Adapter.CartArrayAdapter;
import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.Object.List;
import software_project.com.hoarder.Object.Receipt;
import software_project.com.hoarder.R;

/**
 * Created by Niall on 27/09/2016.
 * Main screen for the application which holds the scanning functionality
 * This activity utilises a theme from styles.xml to insure the user is not waiting while the app is loading.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    TextView cost,content,vat,emailHeader;
    ListView cartList;
    Button checkout;
    String findItemUrl = "http://hoarder-app.herokuapp.com/findItem/";
    String receiptUrl = "https://hoarder-app.herokuapp.com/findReceipt/";
    String findListUrl = "https://hoarder-app.herokuapp.com/findList/";
    String addListUrl = "https://hoarder-app.herokuapp.com/list/";
    String accountUrl = "https://hoarder-app.herokuapp.com/user/";
    ArrayList<Item> itemArray;
    ArrayList<Receipt> receiptArray;
    ArrayList<List> listItemArray;
    View emptyView;
    Item getItem;
    String email,orderCount,credit,creationDate,phoneNumber,name,productName;
    double totalCost,vatValue,productPrice;
    int cartCount;
    Intent receiptsIntent,profileIntent,listIntent;
    public static final String SESSION_NAME = "session";
    CartArrayAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemArray = new ArrayList<>();
        receiptArray = new ArrayList<>();
        listItemArray = new ArrayList<>();
        //Get listView item
        cartList = (ListView) findViewById(R.id.cartList);
        //Add divider for each listView item
        cartList.setDivider(null);
        content = (TextView) findViewById(R.id.counterTxt);
        cost = (TextView) findViewById(R.id.costTxt);
        vat = (TextView) findViewById(R.id.vatTxt);

        receiptsIntent = new Intent(MainActivity.this, ReceiptsListActivity.class);
        profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        listIntent = new Intent(MainActivity.this, software_project.com.hoarder.Activity.ListActivity.class);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        emailHeader = (TextView)header.findViewById(R.id.emailTextHeader);
        final SharedPreferences session = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        email = session.getString("email", null);
        emailHeader.setText(email);

        // Create the adapter
        itemAdapter = new CartArrayAdapter(this,R.layout.item_row,itemArray);

        // Attach the adapter to the list view
        cartList.setAdapter(itemAdapter);

        cartList.setOnItemClickListener(new itemClick());

        emptyView = findViewById(R.id.empty_view);
        cartList.setEmptyView(emptyView);

        //Check if camera is enabled to allow barcode scanning and prompt the user if it is not enabled
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

        if (isNetworkConnected()==false){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Error, Cannot connect to internet", Toast.LENGTH_SHORT);
            toast.show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Setting up the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //Set logo for app bar
        android.support.v7.app.ActionBar appBar = getSupportActionBar();
        appBar.setDisplayHomeAsUpEnabled(false);
        appBar.setDisplayShowTitleEnabled(false);
        appBar.setCustomView(R.layout.app_bar_top);
        appBar.setDisplayShowCustomEnabled(true);

        checkout = (Button) findViewById(R.id.checkoutBtn);
        checkout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getPaymentInformation();
            }
        });
    }
    /**
     * Check your device is connected to a network
     */
    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    /**
     * Navigation drawer functions start
     */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    /**
     * Navigation intents for nav side bar
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shopping_cart) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "You are already here!", Toast.LENGTH_SHORT);
            toast.show();
        }else if (id == R.id.nav_deals) {

        } else if (id == R.id.nav_signout) {
            Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logoutIntent);
        } else if (id == R.id.nav_receipts) {
            getReceipts();
        } else if (id == R.id.nav_profile) {
            getUserInformation();
        } else if (id == R.id.nav_shopping_list) {
            getLists();
        } else if (id == R.id.nav_customer_service) {

        }else if (id == R.id.nav_about) {

        }else if (id == R.id.nav_locations) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * Navigation drawer functions end
     */

    /**
     * Scanning Function start
     */
    //Grab the data once scanning is complete, compare with database and populate the fields
    public void onActivityResult(int requestCode, final int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            final String scanContent = scanningResult.getContents();

            /**
             * Send scan data to server
             */
            final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, findItemUrl +scanContent,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject itemObject = jsonArray.getJSONObject(0);
                                String name = itemObject.optString("productName");
                                double price = Double.parseDouble(itemObject.optString("productPrice"));
                                String category = itemObject.optString("productCategory");

                                Item item = new Item(name, price, category, 1);
                                emptyView.setVisibility(emptyView.GONE);
                                cartList.setEmptyView(null);
                                if(itemArray.size()>0) {
                                    boolean isNew = true;
                                    for (Item items : itemArray){
                                        if (items.getName().equals(name)) {
                                            int quantity = items.getQuantity();
                                            items.setQuantity(quantity + 1);
                                            isNew = false;
                                            getValues();
                                            Toast toast = Toast.makeText(getApplicationContext(),
                                                    name + " has been added to cart!!", Toast.LENGTH_SHORT);
                                            toast.show();
                                            break;
                                        }
                                    }
                                    if(isNew){
                                        itemArray.add(item);
                                        getValues();
                                        Toast toast = Toast.makeText(getApplicationContext(),
                                                name + " has been added to cart!!", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }else{
                                    itemArray.add(item);
                                    getValues();
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            name + "Added to cart!!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                itemAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            requestQueue.stop();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Barcode not recognised or item does not exist. Please try again!!", Toast.LENGTH_SHORT);
                    toast.show();
                    error.printStackTrace();
                    requestQueue.stop();
                }
            });

            requestQueue.add(stringRequest);

            /**
             * send data to server end
             */
        }
        //Cannot find data toast
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Barcode not recognised. Please try again!!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    //Setting up OnClick for the scan button
    public void scanner(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt(String.valueOf("scan bar code"));
        integrator.setResultDisplayDuration(0);
        integrator.setScanningRectangle(900,600); // Wide box for barcode scanning added to camera view
        integrator.setCameraId(0); //Use camera of current device
        integrator.initiateScan();
    }

    /**
     * Scanning function end
     */

    public void getReceipts(){
        /**
         * Retrieve receipts from server
         */
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest receiptRequest = new StringRequest(Request.Method.GET, receiptUrl+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            receiptArray.clear();
                            itemAdapter.notifyDataSetChanged();
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
                        startActivity(receiptsIntent);
                        requestQueue.stop();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "You have no previous receipts at this store!!", Toast.LENGTH_SHORT);
                toast.show();
                error.printStackTrace();
                startActivity(receiptsIntent);
                requestQueue.stop();
            }
        });
        requestQueue.add(receiptRequest);
        /**
         * Retrieve receipts from server end
         */
    }

    public void addToList(){
        JSONObject listRequest=new JSONObject();
        try{
            listRequest.put("productName",productName);
            listRequest.put("productPrice",productPrice);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        final Map<String, Object> addToList = new HashMap<>();
        addToList.put("items", listRequest);

        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest requestList = new JsonObjectRequest(Request.Method.POST, addListUrl+email , new JSONObject(addToList),
                //Success response from add to list
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Your shopping list has been updated", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                },
                //Error response from add to list
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "There was an error adding this item to your shopping list!!", Toast.LENGTH_SHORT);
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
        requestQueue.add(requestList);
        /**
         * generate receipt for transaction end
         */
    }

    public void getLists(){
        listItemArray.clear();
        itemAdapter.notifyDataSetChanged();
        /**
         * Retrieve shopping list from server
         */
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
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
                        startActivity(listIntent);
                        requestQueue.stop();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Your shopping list is empty!!", Toast.LENGTH_SHORT);
                toast.show();
                error.printStackTrace();
                startActivity(listIntent);
                requestQueue.stop();
            }
        });
        requestQueue.add(getListRequest);
        /**
         * Retrieve shopping list from server end
         */
    }

    public void getUserInformation(){
        /**
         * Retrieve user information from server
         */
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest profileRequest = new StringRequest(Request.Method.GET, accountUrl+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject profile = new JSONObject(response);
                            orderCount = String.valueOf(profile.optInt("orders"));
                            credit = String.valueOf(profile.optDouble("credit"));
                            creationDate = profile.optString("date");
                            phoneNumber = profile.optString("phoneNumber");
                            name = profile.optString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor sessionEditor = getSharedPreferences(SESSION_NAME, MODE_PRIVATE).edit();

                        sessionEditor.putString("orders", orderCount);
                        sessionEditor.putString("credit", credit);
                        sessionEditor.putString("creationDate", creationDate);
                        sessionEditor.putString("phoneNumber", phoneNumber);
                        sessionEditor.putString("name", name);

                        sessionEditor.commit();
                        startActivity(profileIntent);
                        requestQueue.stop();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "You have no previous receipts at this store!!", Toast.LENGTH_SHORT);
                toast.show();
                error.printStackTrace();
                startActivity(profileIntent);
                requestQueue.stop();
            }
        });
        requestQueue.add(profileRequest);
        /**
         * Retrieve receipts from server end
         */
    }

    public void getPaymentInformation(){
        /**
         * Retrieve user information from server
         */
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest userRequest = new StringRequest(Request.Method.GET, accountUrl+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject profile = new JSONObject(response);
                            credit = String.valueOf(profile.optDouble("credit"));
                            name = profile.optString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(itemArray.size() != 0){
                            Intent checkoutIntent = new Intent(MainActivity.this, PaymentActivity.class);
                            SharedPreferences.Editor checkoutEditor = getSharedPreferences(SESSION_NAME, MODE_PRIVATE).edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(itemArray);
                            checkoutEditor.putString("items", json);
                            checkoutEditor.putString("email",email);
                            checkoutEditor.putString("itemCount",String.valueOf(cartCount));
                            checkoutEditor.putString("total",String.valueOf(totalCost));
                            checkoutEditor.putString("userAccountCredit", credit);
                            checkoutEditor.commit();
                            startActivity(checkoutIntent);
                        }else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Your cart is empty!!", Toast.LENGTH_SHORT);
                            toast.show();
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

    public void getValues(){
        cartCount = 0;
        for(int i = 0; i < itemArray.size(); i++) {
            int count = itemArray.get(i).getQuantity();
            if(itemArray.get(i).getQuantity()==0){
                itemArray.remove(i);
                itemAdapter.notifyDataSetChanged();
                if(itemArray.size()==0){
                    cartList.setEmptyView(emptyView);
                }
            }
            cartCount += count;
        }
        if(cartCount==1) {
            content.setText(Integer.toString(cartCount) + " item");
        }else {
            content.setText(Integer.toString(cartCount) + " items");
        }
        //Find sum of all prices and display in the subTotal field
        totalCost = 0;
        vatValue = 0;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        for(int i = 0; i < itemArray.size(); i++) {
            double value= itemArray.get(i).getPrice()* itemArray.get(i).getQuantity();
            totalCost += value;
        }
        vatValue = totalCost*0.23;
        cost.setText(String.valueOf(currencyFormatter.format(totalCost)));

        vat.setText(String.valueOf(currencyFormatter.format(vatValue)));
    }

    class itemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.item_dialog);

            Button addBtn = (Button) dialog.findViewById(R.id.AddToListBtn);
            Button deleteBtn = (Button) dialog.findViewById(R.id.deleteListBtn);

            addBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getItem = itemArray.get(position);
                    productName = getItem.getName();
                    productPrice = getItem.getPrice();
                    addToList();
                    dialog.dismiss();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Object toRemove = itemArray.get(position);
                    itemArray.remove(toRemove);
                    itemAdapter.notifyDataSetChanged();
                    getValues();
                    if(itemArray.size()==0){
                        cartList.setEmptyView(emptyView);
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
