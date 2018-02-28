package indi.aljet.myamazefilemanager_master.utils;

/**
 * Created by PC-LJL on 2018/1/18.
 */

public class MapEntry extends
        ImmutableEntry<ImmutableEntry<Integer
        ,Integer>,Integer> {

    public MapEntry(ImmutableEntry<Integer,Integer> key,
                    Integer value){
        super(key,value);
    }

}
