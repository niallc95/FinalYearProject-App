package software_project.com.hoarder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.R;

/**
 * Created by Niall on 22/11/2016.
 */

public class CheckoutArrayAdapter extends ArrayAdapter<Item> {


    public CheckoutArrayAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the item data for this position
        Item item = getItem(position);
        final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

        // Inflate the view only if an existing view is not being reused
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkout_row, parent, false);
        }

        // Find views
        TextView nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
        TextView priceTxt = (TextView) convertView.findViewById(R.id.priceTxt);
        TextView quantityCountTxt = (TextView) convertView.findViewById(R.id.quantityCountTxt);


        // Populate the corresponding fields for each item
        nameTxt.setText(String.valueOf(item.getName()));
        priceTxt.setText(String.valueOf(currencyFormatter.format(item.getPrice()*item.getQuantity())));
        quantityCountTxt.setText("X"+String.valueOf(item.getQuantity()));

        return convertView;
    }
}
