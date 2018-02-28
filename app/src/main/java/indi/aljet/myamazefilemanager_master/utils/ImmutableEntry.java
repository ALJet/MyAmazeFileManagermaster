package indi.aljet.myamazefilemanager_master.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

/**
 * Created by PC-LJL on 2018/1/17.
 */

public class ImmutableEntry<K,V> implements Map.Entry<K,V> {

    private final K key;
    private final V value;


    public ImmutableEntry(@Nullable K key,@Nullable V value) {
        this.key = key;
        this.value = value;
    }


    @Nullable
    @Override
    public final K getKey() {
        return key;
    }

    @Nullable
    @Override
    public final V getValue() {
        return value;
    }


    @Override
    public final V setValue(V value){
        throw new UnsupportedOperationException();
    }

}
