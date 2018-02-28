package indi.aljet.myamazefilemanager_master.database.models;

import indi.aljet.myamazefilemanager_master.utils.OpenMode;

/**
 * Created by PC-LJL on 2018/1/9.
 */

public class CloudEntry {

    private int id;
    private OpenMode serviceType;
    private String persistData;

    public CloudEntry() {
    }

    public CloudEntry(OpenMode serviceType, String persistData) {
        this.serviceType = serviceType;
        this.persistData = persistData;
    }


    public int getId() {
        return id;
    }

    public void setId(int _id) {
        this.id = _id;
    }

    public OpenMode getServiceType() {
        return serviceType;
    }

    public void setServiceType(OpenMode serviceType) {
        this.serviceType = serviceType;
    }

    public String getPersistData() {
        return persistData;
    }

    public void setPersistData(String persistData) {
        this.persistData = persistData;
    }
}
