package software_project.com.hoarder.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import software_project.com.hoarder.R;

public class DealsActivity extends AppCompatActivity {

    String addListUrl = "https://hoarder-app.herokuapp.com/list/";
    double dealCost;
    String dealName,email;
    Button deal1Btn,deal2Btn,deal3Btn;
    TextView deal1Name,deal2Name,deal3Name,deal1Cost,deal2Cost,deal3Cost;
    public static final String SESSION_NAME = "session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);

        final SharedPreferences session = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        email = session.getString("email", null);

        deal1Name = (TextView) findViewById(R.id.title1);
        deal2Name = (TextView) findViewById(R.id.title2);
        deal3Name = (TextView) findViewById(R.id.title3);

        deal1Cost = (TextView) findViewById(R.id.nowCost1);
        deal2Cost = (TextView) findViewById(R.id.nowCost2);
        deal3Cost = (TextView) findViewById(R.id.nowCost3);

        deal1Btn = (Button) findViewById(R.id.addDealToListBtn1);
        deal1Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dealName = String.valueOf(deal1Name.getText());
                dealCost = 1.99;
                addToList();
            }
        });

        deal2Btn = (Button) findViewById(R.id.addDealToListBtn2);
        deal2Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dealName = String.valueOf(deal2Name.getText());
                dealCost = 2.99;
                addToList();
            }
        });

        deal3Btn = (Button) findViewById(R.id.addDealToListBtn3);
        deal3Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dealName = String.valueOf(deal3Name.getText());
                dealCost = 3.00;
                addToList();
            }
        });
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
    public void onBackPressed() {
        finish();
    }

    public void addToList(){
        JSONObject listRequest=new JSONObject();
        try{
            listRequest.put("productName",dealName);
            listRequest.put("productPrice",dealCost);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        final Map<String, Object> addToList = new HashMap<>();
        addToList.put("items", listRequest);

        final RequestQueue requestQueue = Volley.newRequestQueue(DealsActivity.this);
        JsonObjectRequest requestList = new JsonObjectRequest(Request.Method.POST, addListUrl+email , new JSONObject(addToList),
                //Success response from add to list
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Your shopping list has been updated", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                },
                //Error response from add to list
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "There was an error adding this item to your shopping list!!", Toast.LENGTH_SHORT);
                        toast.show();
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(requestList);
        /**
         * generate receipt for transaction end
         */
    }
}
