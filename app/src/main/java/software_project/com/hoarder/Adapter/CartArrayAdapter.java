package software_project.com.hoarder.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;

import software_project.com.hoarder.Activity.MainActivity;
import software_project.com.hoarder.Object.Item;
import software_project.com.hoarder.R;

public class CartArrayAdapter extends ArrayAdapter<Item> {
    Context context;
    int layoutId;
    ArrayList<Item> itemArray = new ArrayList<Item>();

    public CartArrayAdapter(Context context, int layoutId,ArrayList<Item> itemArray) {
        super(context, layoutId, itemArray);
        this.layoutId = layoutId;
        this.context = context;
        this.itemArray = itemArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemHolder holder;
        View row = convertView;
        final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutId, parent, false);
            holder = new ItemHolder();
            holder.nameTxt = (TextView) row.findViewById(R.id.nameTxt);
            holder.priceTxt = (TextView) row.findViewById(R.id.priceTxt);
            holder.descTxt = (TextView) row.findViewById(R.id.descriptionTxt);
            holder.quantityTxt = (TextView) row.findViewById(R.id.quantityTxt);
            holder.categoryView = (ImageView) row.findViewById(R.id.categoryView);
            holder.addBtn = (ImageView) row.findViewById(R.id.plus_btn);
            holder.negBtn = (ImageView) row.findViewById(R.id.minus_btn);
            row.setTag(holder);
        } else {
            holder = (ItemHolder) row.getTag();
        }
        final Item item = itemArray.get(position);
        holder.quantityTxt.setText(String.valueOf(item.getQuantity()));
        holder.priceTxt.setText(String.valueOf(currencyFormatter.format(item.getPrice()*item.getQuantity())));
        holder.nameTxt.setText(item.getName());
        holder.descTxt.setText(item.getCat());

        if(String.valueOf(item.getCat()).contains("Crisps")) {
            holder.categoryView.setImageResource(R.drawable.ic_icon_crisps);
        }else if(String.valueOf(item.getCat()).contains("Energy Drink")) {
            holder.categoryView.setImageResource(R.drawable.ic_icon_energy);
        }else if(String.valueOf(item.getCat()).contains("Soda")) {
            holder.categoryView.setImageResource(R.drawable.ic_icon_soda);
        }else if(String.valueOf(item.getCat()).contains("Beverage")) {
            holder.categoryView.setImageResource(R.drawable.ic_icon_beverage);
        }else if(String.valueOf(item.getCat()).contains("Chocolate")) {
            holder.categoryView.setImageResource(R.drawable.ic_icon_choc);
        }else {
            holder.categoryView.setImageResource(R.drawable.ic_no_image);
        }

        holder.addBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                item.setQuantity(item.getQuantity()+1);
                holder.quantityTxt.setText(String.valueOf(item.getQuantity()));
                holder.priceTxt.setText(String.valueOf(currencyFormatter.format(item.getPrice()*item.getQuantity())));
                if(context instanceof MainActivity){
                    ((MainActivity)context).getValues();
                }
            }
        });

        holder.negBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(item.getQuantity()>0) {
                    item.setQuantity(item.getQuantity() - 1);
                    holder.quantityTxt.setText(String.valueOf(item.getQuantity()));
                    holder.priceTxt.setText(String.valueOf(currencyFormatter.format(item.getPrice()*item.getQuantity())));
                    if(context instanceof MainActivity){
                        ((MainActivity)context).getValues();
                    }
                }else{
                    item.setQuantity(0);
                    holder.quantityTxt.setText(String.valueOf(item.getQuantity()));
                    holder.priceTxt.setText(String.valueOf(currencyFormatter.format(item.getPrice()*item.getQuantity())));
                    if(context instanceof MainActivity){
                        ((MainActivity)context).getValues();
                    }
                }
            }
        });
        return row;

    }

    static class ItemHolder {
        TextView nameTxt;
        TextView descTxt;
        TextView quantityTxt;
        TextView priceTxt;
        ImageView categoryView;
        ImageView addBtn;
        ImageView negBtn;
    }
}