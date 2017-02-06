package software_project.com.hoarder.Object;

import java.io.Serializable;

/**
 * Created by Niall on 02/02/2017.
 */
public class Receipt implements Serializable {
    private String date;
    private String time;
    private String itemCount;
    private String totalCost;
    private String referenceNumber;

    public Receipt(String date, String time,String itemCount,String totalCost,String referenceNumber){
        this.date = date;
        this.time = time;
        this.itemCount = itemCount;
        this.totalCost = totalCost;
        this.referenceNumber = referenceNumber;
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
    public String getTotalCost() {
        return totalCost;
    }
    public String getReferenceNumber() {
        return referenceNumber;
    }
}
