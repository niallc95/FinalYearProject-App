package software_project.com.hoarder.Object;

/**
 * Created by Niall on 21/11/2016.
 */


public class Item {

    private String productName;
    private String productPrice;
    private String productCategory;
    private String scanContent;


    public Item(String productName, String productPrice, String productCategory, String scanContent){

        this.productName = productName;
        this.productPrice = productPrice;
        this.productCategory = productCategory;
        this.scanContent = scanContent;
    }


    public String getName() {
        return productName;
    }
    public String getPrice() {
        return productPrice;
    }
    public String getCat() {
        return productCategory;
    }



}