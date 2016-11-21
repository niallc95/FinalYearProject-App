package software_project.com.hoarder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Niall on 27/09/2016.
 * Splash/load screen for start of application (First screen of application)
 * This activity utilises a theme from styles.xml to insure the user is not waiting while the app is loading.
 */

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Launch Login once loaded
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}