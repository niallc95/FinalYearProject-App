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
import software_project.com.hoarder.R;

/**
 * Created by Niall on 22/11/2016.
 */

public class CartArrayAdapter extends ArrayAdapter<Item> {


    public CartArrayAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the item data for this position
        Item item = getItem(position);

        // Inflate the view only if an existing view is not being reused
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_row, parent, false);
        }

        // Find views
        TextView nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
        TextView priceTxt = (TextView) convertView.findViewById(R.id.priceTxt);
        TextView categoryTxt = (TextView) convertView.findViewById(R.id.descriptionTxt);
        ImageView categoryView = (ImageView) convertView.findViewById(R.id.categoryView);
        Button listAdd = (Button) convertView.findViewById(R.id.addToList);
        Button clearBtn = (Button) convertView.findViewById(R.id.clearFromList);

        // Populate the corresponding fields for each item
        nameTxt.setText(String.valueOf(item.getName()));
        priceTxt.setText("€"+String.valueOf(item.getPrice()));
        categoryTxt.setText(String.valueOf(item.getCat()));


        if(String.valueOf(item.getCat()).contains("Crisps")) {
            categoryView.setImageResource(R.drawable.ic_icon_crisps);
        }else if(String.valueOf(item.getCat()).contains("Energy Drink")) {
            categoryView.setImageResource(R.drawable.ic_icon_energy);
        }else if(String.valueOf(item.getCat()).contains("Soda")) {
            categoryView.setImageResource(R.drawable.ic_icon_soda);
        }else if(String.valueOf(item.getCat()).contains("Beverage")) {
            categoryView.setImageResource(R.drawable.ic_icon_beverage);
        }else if(String.valueOf(item.getCat()).contains("Chocolate")) {
            categoryView.setImageResource(R.drawable.ic_icon_choc);
        }else {
            categoryView.setImageResource(R.drawable.ic_no_image);
        }

        return convertView;
    }
}
