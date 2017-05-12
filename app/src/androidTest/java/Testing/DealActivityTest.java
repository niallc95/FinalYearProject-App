package Testing;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import software_project.com.hoarder.Activity.DealsActivity;
import software_project.com.hoarder.Activity.ProfileActivity;
import software_project.com.hoarder.R;

import static org.junit.Assert.assertNotNull;

public class DealActivityTest {

    @Rule
    public ActivityTestRule<DealsActivity> mActivityTestRule = new ActivityTestRule<DealsActivity>(DealsActivity.class);

    private DealsActivity mActivity = null;


    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void LaunchTest1(){
        View deal1 = mActivity.findViewById(R.id.deal_1);

        assertNotNull(deal1);
    }

    @Test
    public void LaunchTest2(){
        View deal2 = mActivity.findViewById(R.id.deal_2);

        assertNotNull(deal2);
    }

    @Test
    public void LaunchTest3(){
        View deal3 = mActivity.findViewById(R.id.deal_3);

        assertNotNull(deal3);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}