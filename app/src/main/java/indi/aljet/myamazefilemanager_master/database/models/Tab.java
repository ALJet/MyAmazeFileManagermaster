package indi.aljet.myamazefilemanager_master.database.models;

/**
 * Created by PC-LJL on 2018/1/9.
 */

public class Tab {

    private int _id;
    private int _tab_no;
    private String _label;
    private String _path;
    private String _home;

    public Tab() {
    }


    public Tab(int _tab_no, String _label, String _path, String _home) {
        this._tab_no = _tab_no;
        this._label = _label;
        this._path = _path;
        this._home = _home;
    }


    public void setTab(int tab_no) {
        this._tab_no = tab_no;
    }

    public int getTab() {
        return this._tab_no;
    }
    public void setHome(String tab_no) {
        this._home=tab_no;
    }

    public String getHome() {
        return this._home;
    }

    public void setLabel(String label) {
        this._label = label;
    }

    public String getLabel() {
        return this._label;
    }

    public void setPath(String path) {
        this._path = path;
    }

    public String getPath() {
        return this._path;
    }


    public String getOriginalPath(boolean savePaths){
        if(savePaths)
            return getPath();
        else
            return getHome();
    }
}
