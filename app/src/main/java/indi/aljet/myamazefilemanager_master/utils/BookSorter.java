package indi.aljet.myamazefilemanager_master.utils;

import java.util.Comparator;

/**
 * Created by PC-LJL on 2018/1/12.
 */

public class BookSorter implements Comparator<String[]> {

    @Override
    public int compare(String[] strings, String[] t1) {
        int result = strings[0].compareToIgnoreCase(t1[0]);
        if(result == 0){
            result = strings[1].compareToIgnoreCase(t1[1]);
        }
        return result;
    }
}
