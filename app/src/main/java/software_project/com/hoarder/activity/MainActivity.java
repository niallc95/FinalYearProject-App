package software_project.com.hoarder.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import software_project.com.hoarder.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private TextView nameTxt, priceTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTxt = (TextView) findViewById(R.id.scanContent);
        priceTxt = (TextView) findViewById(R.id.scanFormat);

        //Check if camera is enabled to allow barcode scanning and prompt the user if it is not enabled
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            return true;
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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            //Add database integration here to grab scan data and relate to db to find name and price of item
            if(scanContent != null && scanFormat != null) {
                nameTxt.setText("item: "+scanContent);
                priceTxt.setText("item:" +scanFormat);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Item " + scanContent + " successfully added to cart!!", Toast.LENGTH_SHORT);
                toast.show();
            }

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
