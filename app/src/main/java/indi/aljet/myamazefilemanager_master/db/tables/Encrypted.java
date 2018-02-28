package indi.aljet.myamazefilemanager_master.db.tables;

import org.litepal.crud.DataSupport;

/**
 * Created by PC-LJL on 2018/1/11.
 */

public class Encrypted extends DataSupport {

    private int _id;

    private String path;

    private String password;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
