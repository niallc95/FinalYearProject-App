package Testing;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import software_project.com.hoarder.Activity.LoginActivity;
import software_project.com.hoarder.Activity.MainActivity;
import software_project.com.hoarder.R;

import static org.junit.Assert.assertNotNull;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;


    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void LaunchTest1(){
        View cartList = mActivity.findViewById(R.id.cartList);

        assertNotNull(cartList);
    }

    @Test
    public void LaunchTest2(){
        View emptyView = mActivity.findViewById(R.id.empty_view);

        assertNotNull(emptyView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}