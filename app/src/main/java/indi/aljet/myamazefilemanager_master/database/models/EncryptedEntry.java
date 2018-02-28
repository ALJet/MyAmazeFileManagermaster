package indi.aljet.myamazefilemanager_master.database.models;

/**
 * Created by PC-LJL on 2018/1/9.
 */

public class EncryptedEntry {

    private int id;
    private String path,password;

    public EncryptedEntry() {
    }


    public EncryptedEntry(String path, String password) {
        this.path = path;
        this.password = password;
    }


    public int getId() {
        return id;
    }

    public void setId(int _id) {
        this.id = _id;
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
