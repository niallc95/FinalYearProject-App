package software_project.com.hoarder.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

import software_project.com.hoarder.Adapter.CustomComparator;
import software_project.com.hoarder.Adapter.ReceiptArrayAdapter;
import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.Object.Receipt;
import software_project.com.hoarder.R;


public class ReceiptsListActivity extends AppCompatActivity{
    int receiptCount = 0;
    TextView receiptCountTxt,recentOrderTxt;
    public static final String SESSION_NAME = "session";
    ArrayList<Receipt> receiptArray;
    ArrayList<Item> receiptItemsList;
    ListView receiptList;
    View emptyView;
    String date,time,refNo,itemCount,total,discount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        receiptCountTxt = (TextView) findViewById(R.id.receiptCountTxt);
        recentOrderTxt = (TextView) findViewById(R.id.recentOrderTxt);

        receiptArray = new ArrayList<>();
        receiptList = (ListView) findViewById(R.id.receiptList);
        receiptList.setDivider(null);

        emptyView = findViewById(R.id.empty_view);
        receiptList.setEmptyView(emptyView);

        SharedPreferences session = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = session.getString("receipt", null);
        if (json != null) {
            java.lang.reflect.Type type = new TypeToken<ArrayList<Receipt>>() {
            }.getType();
            receiptArray = gson.fromJson(json, type);

            if (receiptArray.size() > 0) {
                emptyView.setVisibility(emptyView.GONE);
                receiptList.setEmptyView(null);
                receiptCount = receiptArray.size();
            }

            Collections.sort(receiptArray, new CustomComparator());
            // Create the adapter
            ReceiptArrayAdapter receiptArrayAdapter = new ReceiptArrayAdapter(this,receiptArray);
            // Attach the adapter to the list view
            receiptList.setAdapter(receiptArrayAdapter);

            receiptList.setOnItemClickListener(new itemClick());
        }
        String recentOrder= receiptArray.get(0).getDate();
        receiptCountTxt.setText(String.valueOf(receiptCount));
        recentOrderTxt.setText(recentOrder);

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

    class itemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            Receipt receipt = receiptArray.get(position);
            refNo = receipt.getReferenceNumber();
            date = receipt.getDate();
            time = receipt.getTime();
            total = receipt.getTotalCost();
            itemCount = receipt.getItemCount();
            receiptItemsList = receipt.getReceiptItems();
            discount = receipt.getDiscount();

            SharedPreferences.Editor sessionEditor = getSharedPreferences(SESSION_NAME, MODE_PRIVATE).edit();

            sessionEditor.putString("receiptRefNo", refNo);
            sessionEditor.putString("receiptDate", date);
            sessionEditor.putString("receiptTime", time);
            sessionEditor.putString("receiptTotal", total);
            sessionEditor.putString("receiptCount", itemCount);
            sessionEditor.putString("receiptDiscount", discount);

            Gson gson = new Gson();
            String json = gson.toJson(receiptItemsList);
            sessionEditor.putString("receiptItems", json);
            sessionEditor.commit();
            Intent receiptViewIntent = new Intent(ReceiptsListActivity.this, ReceiptsViewActivity.class);
            startActivity(receiptViewIntent);

        }
    }

}
