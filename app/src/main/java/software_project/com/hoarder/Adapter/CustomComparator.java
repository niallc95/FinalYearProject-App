package software_project.com.hoarder.Adapter;

import java.util.Comparator;

import software_project.com.hoarder.Object.Receipt;

public class CustomComparator implements Comparator<Receipt> {// may be it would be Model
    @Override
    public int compare(Receipt obj1, Receipt obj2) {
        return obj1.getDate().compareTo(obj2.getDate());// compare two objects
    }
}