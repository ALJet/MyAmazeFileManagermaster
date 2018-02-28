package indi.aljet.myamazefilemanager_master.db.tables;

import org.litepal.crud.DataSupport;

/**
 * Created by PC-LJL on 2018/1/11.
 */

public class Tab extends DataSupport {


    public Tab() {
    }

    public Tab(int tab_no,String temp, String path, String home) {
        this.tab_no = tab_no;
        this.path = path;
        this.home = home;
    }

    public Tab(int tab_no, String path, String home) {
        this.tab_no = tab_no;
        this.path = path;
        this.home = home;
    }

    private int tab_no;

    private String path;

    private String home;

    public int getTab_no() {
        return tab_no;
    }

    public void setTab_no(int tab_no) {
        this.tab_no = tab_no;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }


    public String getOriginalPath(boolean savePaths){
        if(savePaths )
            return getPath();
        else
            return getHome();
    }
}
