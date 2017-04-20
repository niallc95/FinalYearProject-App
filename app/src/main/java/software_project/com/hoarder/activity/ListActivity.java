package software_project.com.hoarder.Activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import software_project.com.hoarder.Adapter.ShoppingListArrayAdapter;
import software_project.com.hoarder.Object.List;
import software_project.com.hoarder.R;

/**
 * Created by Niall on 06/02/2017.
 */

public class ListActivity extends AppCompatActivity {
    public static final String SESSION_NAME = "session";
    ArrayList<List> listArray;
    ListView shoppingList;
    ShoppingListArrayAdapter shoppingListArrayAdapter;
    TextView ItemCount;
    FloatingActionButton clearAll;
    String clearListUrl = "https://hoarder-app.herokuapp.com/listReplace/";
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listArray = new ArrayList<>();
        shoppingList = (ListView) findViewById(R.id.shoppingListView);
        shoppingList.setDivider(null);

        ItemCount=(TextView)findViewById(R.id.itemCountListTxt);
        clearAll = (FloatingActionButton) findViewById(R.id.deleteAllBtn);

        SharedPreferences session = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        email = session.getString("email", null);
        Gson gson = new Gson();
        String json = session.getString("list", null);
        if (json != null) {
            java.lang.reflect.Type type = new TypeToken<ArrayList<List>>() {
            }.getType();
            listArray = gson.fromJson(json, type);
            // Create the adapter
            shoppingListArrayAdapter = new ShoppingListArrayAdapter(this,listArray);
            // Attach the adapter to the list view
            shoppingList.setAdapter(shoppingListArrayAdapter);
            shoppingList.setOnItemClickListener(new itemClick());
        }
        // Clear all items from list button on click
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listArray.size()==0) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Your shopping list is empty!!", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    clearAll();
                }
            }
        });
        getValues();
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

    public void getValues(){
        if(listArray.size()==1) {
            ItemCount.setText(Integer.toString(listArray.size()) + " item");
        }else {
            ItemCount.setText(Integer.toString(listArray.size()) + " items");
        }
    }

    public void clearAll(){
        final Dialog dialog = new Dialog(ListActivity.this);
        dialog.setContentView(R.layout.confirm_dialog);

        Button cancelBtn = (Button) dialog.findViewById(R.id.noBtn);
        Button deleteBtn = (Button) dialog.findViewById(R.id.deleteBtn);
        TextView confirmation = (TextView) dialog.findViewById(R.id.confirmationTxt);

        confirmation.setText("Are you sure?");
        deleteBtn.setText("Yes");
        cancelBtn.setText("No");
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listArray.clear();
                shoppingListArrayAdapter.notifyDataSetChanged();
                getValues();
                clearList();
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    class itemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
            final Dialog dialog = new Dialog(ListActivity.this);
            dialog.setContentView(R.layout.confirm_dialog);

            Button cancelBtn = (Button) dialog.findViewById(R.id.noBtn);
            Button deleteBtn = (Button) dialog.findViewById(R.id.deleteBtn);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Object toRemove = listArray.get(position);
                    listArray.remove(toRemove);
                    shoppingListArrayAdapter.notifyDataSetChanged();
                    getValues();
                    dialog.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Item has been successfully removed from list!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            dialog.show();
        }
    }

    public void clearList(){
        final Map<String, Object> clearList = new HashMap<>();

        final RequestQueue requestQueue = Volley.newRequestQueue(ListActivity.this);
        JsonObjectRequest clearListRequest = new JsonObjectRequest(Request.Method.POST, clearListUrl+email , new JSONObject(clearList),
                //Success response from list clear
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Your shopping list has been cleared", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                },
                //Error response from list clear
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                error.toString(), Toast.LENGTH_SHORT);
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
        requestQueue.add(clearListRequest);
        /**
         * generate receipt for transaction end
         */
    }

}
