package software_project.com.hoarder.Object;

import java.io.Serializable;

public class Item implements Serializable{

    private String productName;
    private String productCategory;
    private double productPrice;
    private int productQuantity;


    public Item(String productName, double productPrice, String productCategory,int productQuantity){
        this.productName = productName;
        this.productPrice = productPrice;
        this.productCategory = productCategory;
        this.productQuantity = productQuantity;
    }


    public String getName() {
        return productName;
    }
    public double getPrice() {
        return productPrice;
    }
    public String getCat() {
        return productCategory;
    }
    public int getQuantity() { return productQuantity;}

    public void setQuantity(int quantity){
        this.productQuantity = quantity;
    }
}
