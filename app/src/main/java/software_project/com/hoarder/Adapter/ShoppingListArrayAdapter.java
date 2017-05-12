package software_project.com.hoarder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.Object.List;
import software_project.com.hoarder.R;

public class ShoppingListArrayAdapter extends ArrayAdapter<List> {


    public ShoppingListArrayAdapter(Context context, ArrayList<List> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        // Get the item data for this position
        List list = getItem(position);

        // Inflate the view only if an existing view is not being reused
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
        }

        View divider = convertView.findViewById(R.id.rowDivider);

        if(position==0){ // for two rows if(position==0 || position==1)
            divider.setVisibility(View.GONE);
        }

        // Find views
        TextView nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
        TextView priceTxt = (TextView) convertView.findViewById(R.id.priceTxt);

        // Populate the corresponding fields for each item
        nameTxt.setText(String.valueOf(list.getName()));
        priceTxt.setText(String.valueOf(currencyFormatter.format(Double.parseDouble(list.getPrice()))));

        return convertView;
    }
}
