package software_project.com.hoarder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import software_project.com.hoarder.R;

/**
 * Author: Niall Curran
 * Student Number: x13440572
 * Description: Introduction screen for the user
 */

public class IntroductionActivity extends AppCompatActivity {

    CarouselView slideShowView;
    Button loginBtn, signupBtn;
    int[] carouselImages = {R.drawable.carousel_1, R.drawable.carousel_2, R.drawable.carousel_3, R.drawable.carousel_4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        slideShowView = (CarouselView) findViewById(R.id.slideShow);
        slideShowView.setPageCount(carouselImages.length);

        slideShowView.setImageListener(IntroImageListener);

        loginBtn = (Button)findViewById(R.id.linkToLoginIntro);
        signupBtn = (Button)findViewById(R.id.linkToSignupIntro);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(IntroductionActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(IntroductionActivity.this, SignupActivity.class);
                startActivity(signupIntent);
                finish();
            }
        });
    }

    ImageListener IntroImageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(carouselImages[position]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    };
}
