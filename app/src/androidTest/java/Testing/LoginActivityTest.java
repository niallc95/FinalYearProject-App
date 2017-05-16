package Testing;

import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import software_project.com.hoarder.Activity.LoginActivity;
import software_project.com.hoarder.R;

import static org.junit.Assert.*;

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    private LoginActivity mActivity = null;


    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void LaunchTest1(){
        View logo = mActivity.findViewById(R.id.logoImg);

        assertNotNull(logo);
    }

    @Test
    public void LaunchTest2(){
        View emailField = mActivity.findViewById(R.id.login_email_address);

        assertNotNull(emailField);
    }

    @Test
    public void LaunchTest3(){
        View passwordField = mActivity.findViewById(R.id.login_password);

        assertNotNull(passwordField);
    }

    @Test
    public void LaunchTest4(){
        View loginBtn = mActivity.findViewById(R.id.loginBtn);
        View signupLink = mActivity.findViewById(R.id.signupLink);

        assertNotNull(loginBtn);
        assertNotNull(signupLink);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}