package software_project.com.hoarder.Object;

import java.io.Serializable;
import java.util.ArrayList;

public class Receipt implements Serializable {
    private String date;
    private String time;
    private String itemCount;
    private String totalCost;
    private String discount;
    private String referenceNumber;
    private ArrayList<Item> receiptItems;

    public Receipt(String date, String time,String itemCount,String totalCost,String referenceNumber,String discount,ArrayList<Item> receiptItems){
        this.date = date;
        this.time = time;
        this.itemCount = itemCount;
        this.discount = discount;
        this.totalCost = totalCost;
        this.referenceNumber = referenceNumber;
        this.receiptItems = receiptItems;
    }

    public String getTime() {
        return time;
    }
    public String getDate() {
        return date;
    }
    public String getItemCount() {
        return itemCount;
    }
    public String getDiscount() {
        return discount;
    }
    public String getTotalCost() {
        return totalCost;
    }
    public String getReferenceNumber() {
        return referenceNumber;
    }
    public ArrayList<Item> getReceiptItems() {
        return receiptItems;
    }
}
