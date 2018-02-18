package software_project.com.hoarder.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import software_project.com.hoarder.R;

/**
 * Maps screen for google maps integration when displaying the NCT locations
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng TescoClairehall = new LatLng(53.4010673, -6.1821907);
        LatLng TescoSwords = new LatLng(53.4447326, -6.2459823);
        LatLng SupervaluBoroimhe = new LatLng(53.446585, -6.2403167);
        LatLng SupervaluPavillions = new LatLng(53.4543029, -6.2214465);
        LatLng SupervaluEnfield = new LatLng(53.4141395, -6.8301169);
        LatLng TescoEnfield = new LatLng(53.4081545, -6.8196891);
        LatLng SupervaluRatoath = new LatLng(53.506979, -6.4669167);
        LatLng TescoRatoath = new LatLng(53.5047564, -6.4691311);


        //Add markers
        mMap.addMarker(new MarkerOptions().position(TescoSwords).title("Tesco Express,Forrest Rd, Fosterstown North, Swords, Co. Dublin"));
        mMap.addMarker(new MarkerOptions().position(TescoClairehall).title("Tesco, Clarehall Shopping Centre, Malahide Rd, Northern Cross, Dublin 17"));
        mMap.addMarker(new MarkerOptions().position(SupervaluBoroimhe).title("SuperValu Swords - O'Ciobhain's,Boroimhe Shopping Centre, Boroimhe, Swords, Co. Dublin"));
        mMap.addMarker(new MarkerOptions().position(SupervaluPavillions).title("SuperValu, Malahide Rd, Swords Demesne, Swords, Co. Dublin"));
        mMap.addMarker(new MarkerOptions().position(SupervaluEnfield).title("Hannon's Supervalu,Johnstown, Enfield, Co. Meath"));
        mMap.addMarker(new MarkerOptions().position(TescoEnfield).title("Tesco Express, Enfield Garda Station, Main St, Johnstown, Enfield, Co. Meath"));
        mMap.addMarker(new MarkerOptions().position(SupervaluRatoath).title("Tesco Express, Enfield Garda Station, Main St, Johnstown, Enfield, Co. Meath"));
        mMap.addMarker(new MarkerOptions().position(TescoRatoath).title("SuperValu Ratoath, Fairyhouse Rd, Ratoath, Co. Meath"));

        //Set initial focus of map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(TescoSwords));
    }
}
