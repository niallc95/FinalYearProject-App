package Testing;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import software_project.com.hoarder.Activity.LoginActivity;
import software_project.com.hoarder.Activity.SignupActivity;
import software_project.com.hoarder.R;

import static org.junit.Assert.assertNotNull;

public class SignupActivityTest {

    @Rule
    public ActivityTestRule<SignupActivity> mActivityTestRule = new ActivityTestRule<SignupActivity>(SignupActivity.class);

    private SignupActivity mActivity = null;


    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void LaunchTest1(){
        View fnameField = mActivity.findViewById(R.id.input_fname);
        View lnameField = mActivity.findViewById(R.id.input_lname);

        assertNotNull(fnameField);
        assertNotNull(lnameField);
    }

    @Test
    public void LaunchTest2(){
        View emailField = mActivity.findViewById(R.id.input_email);

        assertNotNull(emailField);
    }

    @Test
    public void LaunchTest3(){
        View phoneField = mActivity.findViewById(R.id.input_phone);

        assertNotNull(phoneField);
    }

    @Test
    public void LaunchTest4(){
        View passwordField = mActivity.findViewById(R.id.input_password);

        assertNotNull(passwordField);
    }

    @Test
    public void LaunchTest5(){
        View signupBtn = mActivity.findViewById(R.id.signupBtn);
        View loginLink = mActivity.findViewById(R.id.loginLink);

        assertNotNull(signupBtn);
        assertNotNull(loginLink);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}