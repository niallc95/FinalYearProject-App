package software_project.com.hoarder.Activity;

import android.Manifest;
import android.app.Dialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import software_project.com.hoarder.Adapter.CartArrayAdapter;
import software_project.com.hoarder.Object.Item;
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
    String accountUrl = "https://hoarder-app.herokuapp.com/user/";
    ArrayList<Item> itemArray;
    ArrayList<Receipt> receiptArray;
    View emptyView;
    String email,orderCount,credit,creationDate,phoneNumber,name;
    double totalCost,vatValue;
    Intent receiptsIntent,profileIntent;
    public static final String SESSION_NAME = "session";
    CartArrayAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemArray = new ArrayList<>();
        receiptArray = new ArrayList<>();
        //Get listView item
        cartList = (ListView) findViewById(R.id.cartList);
        //Add divider for each listView item
        cartList.setDivider(null);
        content = (TextView) findViewById(R.id.counterTxt);
        cost = (TextView) findViewById(R.id.costTxt);
        vat = (TextView) findViewById(R.id.vatTxt);

        receiptsIntent = new Intent(MainActivity.this, ReceiptsActivity.class);
        profileIntent = new Intent(MainActivity.this, ProfileActivity.class);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        emailHeader = (TextView)header.findViewById(R.id.emailTextHeader);
        final SharedPreferences session = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        email = session.getString("email", null);
        emailHeader.setText(email);

        // Create the adapter
        itemAdapter = new CartArrayAdapter(this,itemArray);

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

        //Set buttons for clearing cart and checking out

//        clear = (Button) findViewById(R.id.clearBtn);
//        clear.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if(itemArray.size() != 0){
//                    itemAdapter.clear();
//                    content.setText("(0 items):");
//                    cost.setText("€0.00");
//                }else {
//                    Toast toast = Toast.makeText(getApplicationContext(),
//                            "Your cart is empty!!", Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            }
//        });

        checkout = (Button) findViewById(R.id.checkoutBtn);
        checkout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(itemArray.size() != 0){
                    Intent checkoutIntent = new Intent(MainActivity.this, PaymentActivity.class);
                    SharedPreferences.Editor checkoutEditor = getSharedPreferences(SESSION_NAME, MODE_PRIVATE).edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(itemArray);
                    checkoutEditor.putString("items", json);
                    checkoutEditor.putString("email",email);
                    checkoutEditor.putString("total",String.valueOf(totalCost));
                    checkoutEditor.commit();
                    startActivity(checkoutIntent);
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Your cart is empty!!", Toast.LENGTH_SHORT);
                    toast.show();
                }
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
            //startActivity(new Intent(MainActivity.this, MainActivity.class));
            //return true;
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
                                String price = itemObject.optString("productPrice");
                                String category = itemObject.optString("productCategory");

                                Item item = new Item(name,price,category);
                                emptyView.setVisibility(emptyView.GONE);
                                cartList.setEmptyView(null);

                                itemArray.add(item);

                                getValues();

                                Toast toast = Toast.makeText(getApplicationContext(),
                                        name+" successfully added to cart!", Toast.LENGTH_SHORT);
                                toast.show();


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
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject itemObject = jsonArray.getJSONObject(i);
                                String date = itemObject.optString("date");
                                String time = itemObject.optString("time");
                                String referenceNumber = itemObject.optString("referenceNumber");
                                String totalCost = itemObject.optString("totalCost");
                                String itemCount = itemObject.optString("itemCount");
                                Receipt receipt = new Receipt(date,time,itemCount,totalCost,referenceNumber);
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

    public void getUserInformation(){
        /**
         * Retrieve user information from server
         */
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest receiptRequest = new StringRequest(Request.Method.GET, accountUrl+email,
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
        requestQueue.add(receiptRequest);
        /**
         * Retrieve receipts from server end
         */
    }

    public void getValues(){
        if(itemArray.size()==1) {
            content.setText(Integer.toString(itemArray.size()) + " item");
        }else {
            content.setText(Integer.toString(itemArray.size()) + " items");
        }
        //Find sum of all prices and display in the subTotal field
        totalCost = 0;
        vatValue = 0;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        for(int i = 0; i < itemArray.size(); i++) {
            Float value=Float.parseFloat(itemArray.get(i).getPrice());
            totalCost += value;
        }
        vatValue = totalCost*0.23;
        cost.setText(String.valueOf(currencyFormatter.format(totalCost)));

        vat.setText(String.valueOf(currencyFormatter.format(vatValue)));
    }

    class itemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
            ViewGroup vg=(ViewGroup)view;
            TextView itemName =(TextView)vg.findViewById(R.id.nameTxt);
            String name = itemName.getText().toString();
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.item_dialog);
            dialog.setTitle("Hoarder");

            Button addBtn = (Button) dialog.findViewById(R.id.addListBtn);
            Button deleteBtn = (Button) dialog.findViewById(R.id.deleteListBtn);

            addBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Object toAdd = itemArray.get(position);
                    Toast.makeText(MainActivity.this, toAdd.toString(),Toast.LENGTH_SHORT).show();
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
