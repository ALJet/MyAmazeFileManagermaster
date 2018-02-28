package indi.aljet.myamazefilemanager_master.db.tables;

import org.litepal.crud.DataSupport;

/**
 * Created by PC-LJL on 2018/1/11.
 */

public class Bookmarks extends DataSupport {

    private int _id;

    private String name;

    private String path;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
