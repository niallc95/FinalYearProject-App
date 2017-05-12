package software_project.com.hoarder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Author: Niall Curran
 * Student Number: x13440572
 * Description: Splash/load screen for start of application (First screen of application)
 *              This activity utilises a theme from styles.xml to insure the user is not
 *              waiting while the app is loading.
 */

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Launch Login once loaded
        Intent intent = new Intent(this, IntroductionActivity.class);
        startActivity(intent);
        finish();
    }
}