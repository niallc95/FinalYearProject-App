package software_project.com.hoarder.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.NumberFormat;
import java.util.ArrayList;

import software_project.com.hoarder.Adapter.ReceiptItemAdapter;
import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.R;

/**
 * Created by Niall on 04/04/2017.
 */

public class ReceiptsViewActivity extends AppCompatActivity {

    public static final String SESSION_NAME = "session";
    String date,time,refNo,itemCount,total,discount;
    ArrayList<Item> receiptItems;
    ListView receiptItemList;
    double subtotal,vat;
    TextView refNoTxt,dateTxt,timeTxt,headerTotalTxt,countTxt,subtotalTxt,totalTxt,discountTxt,vatTxt;
    NumberFormat currencyFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_view);
        Gson gson = new Gson();

        currencyFormatter = NumberFormat.getCurrencyInstance();

        receiptItemList = (ListView)findViewById(R.id.receiptItemList);
        refNoTxt = (TextView)findViewById(R.id.receiptRefNoTxt);
        dateTxt = (TextView)findViewById(R.id.receiptDateHeaderTxt);
        timeTxt = (TextView)findViewById(R.id.receiptTimeHeaderTxt);
        headerTotalTxt = (TextView)findViewById(R.id.receiptTotalHeaderTxt);
        countTxt = (TextView)findViewById(R.id.receiptItemCountHeaderTxt);
        subtotalTxt = (TextView)findViewById(R.id.receiptSubtotalTxt);
        totalTxt= (TextView)findViewById(R.id.receiptTotalTxt);
        discountTxt = (TextView)findViewById(R.id.receiptDiscountTxt);
        vatTxt = (TextView)findViewById(R.id.receiptVatTxt);

        SharedPreferences session = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        refNo = session.getString("receiptRefNo", null);
        date = session.getString("receiptDate", null);
        time = session.getString("receiptTime", null);
        itemCount = session.getString("receiptCount", null);
        total = session.getString("receiptTotal", null);
        discount = session.getString("receiptDiscount", null);

        String json = session.getString("receiptItems", null);
        refNoTxt.setText(refNo);
        dateTxt.setText(date);
        timeTxt.setText(time);
        headerTotalTxt.setText(currencyFormatter.format(Double.parseDouble(total)));
        countTxt.setText("("+itemCount+")");

        subtotal = Double.parseDouble(total)/123*100;
        vat = Double.parseDouble(total)-subtotal;

        subtotalTxt.setText(currencyFormatter.format(subtotal));
        totalTxt.setText(currencyFormatter.format(Double.parseDouble(total)-Double.parseDouble(discount)));
        discountTxt.setText("-"+currencyFormatter.format(Double.parseDouble(discount)));
        vatTxt.setText("+"+currencyFormatter.format(vat));

        if (json != null) {
            java.lang.reflect.Type type = new TypeToken<ArrayList<Item>>() {}.getType();
            receiptItems = gson.fromJson(json, type);
            // Create the adapter
            ReceiptItemAdapter receiptItemAdapter = new ReceiptItemAdapter(this,receiptItems);
            // Attach the adapter to the list view
            receiptItemList.setAdapter(receiptItemAdapter);
        }
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
}
