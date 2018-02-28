package indi.aljet.myamazefilemanager_master.db.tables;

import org.litepal.crud.DataSupport;

/**
 * Created by PC-LJL on 2018/1/11.
 */

public class Cloud extends DataSupport {

    private int _id;

    private int service;

    private String persist;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public String getPersist() {
        return persist;
    }

    public void setPersist(String persist) {
        this.persist = persist;
    }
}
