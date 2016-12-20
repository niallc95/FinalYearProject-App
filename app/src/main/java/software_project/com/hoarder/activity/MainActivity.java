package software_project.com.hoarder.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import software_project.com.hoarder.Adapter.ItemArrayAdapter;
import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.R;

/**
 * Created by Niall on 27/09/2016.
 * Main screen for the application which holds the scanning functionality
 * This activity utilises a theme from styles.xml to insure the user is not waiting while the app is loading.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    TextView cost,content;
    ListView cartList;
    Button checkout, clear;
    String serverUrl = "http://hoarder-app.herokuapp.com/findItem/";
    ArrayList<Item> itemArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemArray = new ArrayList<Item>();
        //Get listView item
        cartList = (ListView) findViewById(R.id.cartView);
        //Add divider for each listView item
        cartList.setDivider(null);

        content = (TextView) findViewById(R.id.contentTxt);
        cost = (TextView) findViewById(R.id.totalTxt);

        // Create the adapter
        final ItemArrayAdapter itemAdapter = new ItemArrayAdapter(this,itemArray);

        // Attach the adapter to the list view
        cartList.setAdapter(itemAdapter);


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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set logo for app bar
        android.support.v7.app.ActionBar appBar = getSupportActionBar();
        appBar.setDisplayHomeAsUpEnabled(false);
        appBar.setDisplayShowTitleEnabled(false);
        appBar.setCustomView(R.layout.app_bar_top);
        appBar.setDisplayShowCustomEnabled(true);

        //Set buttons for clearing cart and checking out

        clear = (Button) findViewById(R.id.clearBtn);
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(itemArray.size() != 0){
                    itemAdapter.clear();
                    content.setText("(0 items):");
                    cost.setText("€0.00");
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Your cart is empty!!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        checkout = (Button) findViewById(R.id.checkoutBtn);
        checkout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(itemArray.size() != 0){
                    startActivity(new Intent(MainActivity.this, PaymentActivity.class));
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Navigation intents for nav side bar
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //startActivity(new Intent(MainActivity.this, MainActivity.class));
            //return true;
        } else if (id == R.id.nav_shopping_list) {

        } else if (id == R.id.nav_shopping_cart) {

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_receipts) {

        } else if (id == R.id.nav_loyalty) {

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
            StringRequest stringRequest = new StringRequest(Request.Method.GET, serverUrl+scanContent,
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

                                itemArray.add(item);
                                if(itemArray.size()==1) {
                                    content.setText("(" + Integer.toString(itemArray.size()) + " item):");
                                }else {
                                    content.setText("(" + Integer.toString(itemArray.size()) + " items):");
                                }
                                //Find sum of all prices and display in the subTotal field
                                double totalCost = 0;
                                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
                                for(int i = 0; i < itemArray.size(); i++)
                                {
                                    Float value=Float.parseFloat(itemArray.get(i).getPrice());
                                    totalCost += value;
                                }



                                cost.setText(String.valueOf(currencyFormatter.format(totalCost)));

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

}
