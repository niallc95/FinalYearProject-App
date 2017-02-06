package software_project.com.hoarder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.Object.Receipt;
import software_project.com.hoarder.R;

/**
 * Created by Niall on 22/11/2016.
 */

public class ReceiptArrayAdapter extends ArrayAdapter<Receipt> {


    public ReceiptArrayAdapter(Context context, ArrayList<Receipt> receipts) {
        super(context, 0, receipts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the item data for this position
        Receipt receipt = getItem(position);

        // Inflate the view only if an existing view is not being reused
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.receipt_row, parent, false);
        }

        // Find views
        TextView timeTxt = (TextView) convertView.findViewById(R.id.timeTxt);
        TextView dateTxt = (TextView) convertView.findViewById(R.id.dateTxt);
        TextView referenceNoTxt = (TextView) convertView.findViewById(R.id.refNoTxt);
        TextView totalCostTxt = (TextView) convertView.findViewById(R.id.totalCostTxt);
        TextView itemCountTxt = (TextView) convertView.findViewById(R.id.itemCountTxt);


        // Populate the corresponding fields for each item
        timeTxt.setText(String.valueOf(receipt.getTime()));
        dateTxt.setText(String.valueOf(receipt.getDate()));
        referenceNoTxt.setText(String.valueOf(receipt.getReferenceNumber()));
        totalCostTxt.setText(String.valueOf(receipt.getTotalCost()));
        itemCountTxt.setText(String.valueOf(receipt.getItemCount()));

        return convertView;
    }
}
