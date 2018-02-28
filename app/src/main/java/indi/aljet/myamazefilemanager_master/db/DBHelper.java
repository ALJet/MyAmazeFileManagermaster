package indi.aljet.myamazefilemanager_master.db;

import android.os.Environment;
import android.util.Log;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import indi.aljet.myamazefilemanager_master.database.models.EncryptedEntry;
import indi.aljet.myamazefilemanager_master.db.tables.Bookmarks;
import indi.aljet.myamazefilemanager_master.db.tables.Cloud;
import indi.aljet.myamazefilemanager_master.db.tables.Encrypted;
import indi.aljet.myamazefilemanager_master.db.tables.Grid;
import indi.aljet.myamazefilemanager_master.db.tables.Hidden;
import indi.aljet.myamazefilemanager_master.db.tables.History;
import indi.aljet.myamazefilemanager_master.db.tables.Smb;
import indi.aljet.myamazefilemanager_master.db.tables.Tab;
import indi.aljet.myamazefilemanager_master.utils.OpenMode;

/**
 * Created by PC-LJL on 2018/1/11.
 */

public class DBHelper {




    private static String TAG = "DBHelper";
    public static void addTab(Tab tab){
        Log.d(TAG,"insert  tab ");
        tab.save();
    }


    public static void clearTab(){
        DataSupport.deleteAll(Tab.class,"tab_no = ? " ,String.valueOf(1));
        DataSupport.deleteAll(Tab.class,"tab_no = ? " ,String.valueOf(2));

//        DataSupport.delete(Tab.class,1);
//        DataSupport.delete(Tab.class,2);
    }


    public static Tab findTab(int tabNo){
        return (Tab) DataSupport.select("tab_no","path","home")
                .where("tab_no = ?",String.valueOf(tabNo))
                .find(Tab.class);
    }


    public static List<Tab> getAllTabs(){
        return DataSupport.findAll(Tab.class);
    }

    public static void addEntry(EncryptedEntry encryptedEntry){

        Encrypted encrypted = new Encrypted();
        encrypted.setPassword(encryptedEntry.getPassword());
        encrypted.setPath(encryptedEntry.getPassword());
        encrypted.save();
    }

    public static void clearEncrypted(String path){
        DataSupport.deleteAll(Encrypted.class,
                "path = ? ",path);
    }


    public static Encrypted findEntrypted(String path) {
        return (Encrypted) DataSupport.select("_id,","path","password")
                .where("path = ?",path)
                .find(Encrypted.class);
    }

    public static List<Cloud> getAllEntries(){
        return DataSupport.findAll(Cloud.class);
    }

    public static Encrypted findEntrypted(OpenMode openMode) {
        return (Encrypted) DataSupport.select("_id,","path","password")
                .where("service = ?",String.valueOf(openMode.ordinal()))
                .find(Encrypted.class);
    }

    public static List<Encrypted> getAllEntrpteds(){
        return DataSupport.findAll(Encrypted.class);
    }


    public static void updateEntrypted(Encrypted oldEncrypted,Encrypted newEncrypted){
        int id = oldEncrypted.get_id();
        newEncrypted.update(id);

    }


    public static void addCommonBookmarks(){
        String sd = Environment.getExternalStorageDirectory() + "/";

        String[] dirs = new String[] {
                sd + Environment.DIRECTORY_DCIM,
                sd + Environment.DIRECTORY_DOWNLOADS,
                sd + Environment.DIRECTORY_MOVIES,
                sd + Environment.DIRECTORY_MUSIC,
                sd + Environment.DIRECTORY_PICTURES
        };

        for (String dir : dirs) {

            addBookmark(new File(dir).getName(), dir);
        }
    }


    public static void addHistory(String path) {
        History history = new History();
        history.setPath(path);
        history.save();
    }

    public static void addHidden(String path) {
        Hidden hidden = new Hidden();
        hidden.setPath(path);
        hidden.save();
    }

    public static void addListView(String path) {
        indi.aljet.myamazefilemanager_master.db.tables.List list = new indi.aljet.myamazefilemanager_master.db.tables.List();
        list.setPath(path);
        list.save();
    }

    public static void addGridView(String path) {
        Grid grid = new Grid();
        grid.setPath(path);
        grid.save();
    }

    public static void addBookmark(String name, String path) {
        Bookmarks bookmarks = new Bookmarks();
        bookmarks.setPath(path);
        bookmarks.setName(name);
        bookmarks.save();
    }

    public static void addSmb(String name, String path) {
        Smb smb = new Smb();
        smb.setPath(path);
        smb.setName(name);
        smb.save();
    }


    public  static List<String> getHistoryLinkedList(){
        List<History> history =  DataSupport.findAll(History.class);
        List<String> strsr = new ArrayList<>();
        for(int i = 0 ;i < history.size();i++){
            strsr.set(i, history.get(i).getPath());
        }
        return strsr;
    }


//    public ConcurrentRadixTree<VoidValue> getHiddenFilesConcurrentRadixTree()



    public static List<String> getListViewList(){
        List<indi.aljet.myamazefilemanager_master.db.tables.List> lists =
                DataSupport.findAll(indi.aljet.myamazefilemanager_master.db.tables.List.class);
        List<String> strsr = new ArrayList<>();
        for(int i = 0 ;i < lists.size();i++){
            strsr.set(i, lists.get(i).getPath());
        }
        return strsr;

    }

    public static List<String> getGridViewList(){
        List<Grid> lists = DataSupport
                .findAll(Grid.class);
        List<String> strsr = new ArrayList<>();
        for(int i = 0 ;i < lists.size();i++){
            strsr.set(i, lists.get(i).getPath());
        }
        return strsr;
    }


    public static List<String[]> getBookmarksList(){
        List<Bookmarks> lists = DataSupport
                .findAll(Bookmarks.class);
        List<String[]> row = new ArrayList<>();
        for(int i = 0 ;i < lists.size();i++){
            row.add(new String[]{lists.get(i).getName(),
            lists.get(i).getPath()});
        }
        return row;
    }



    public static List<String[]> getSmbList(){
        List<Smb> lists = DataSupport
                .findAll(Smb.class);
        List<String[]> row = new ArrayList<>();
        for(int i = 0 ;i < lists.size();i++){
            row.add(new String[]{ lists.get(i)
            .getName(),lists.get(i).getPath()});
        }
        return row;
    }


    public static void removeHistoryPath(String path){
        DataSupport.deleteAll(History.class,"path = ?",
                path);
    }

    public static void removeHiddenPath(String path){
        DataSupport.deleteAll(Hidden.class,"path = ?",
                path);
    }

    public static void removeListViewPath(String path){
        DataSupport.deleteAll(indi.aljet.myamazefilemanager_master.db.tables.List
        .class,"path = ?",path);
    }

    public static void removeGridViewPath(String path){
        DataSupport.deleteAll(Grid.class,"path = ?",
                path);
    }

    public static void removeBookmarksPath(String name,String path){
        DataSupport.deleteAll(Bookmarks.class,"name = ? and path = ?",
                name,path);
    }

    public static void removeSmbPath(String name,String path){
        DataSupport.deleteAll(Smb.class,"name = ? and path = ?",
                name,path);
    }

    public static void clearHistoryTable(){
        DataSupport.deleteAll(History.class);
    }

    public static void clearHiddenTable() {
        DataSupport.deleteAll(Hidden.class);
    }

    public static void clearListViewTable() {
        DataSupport.deleteAll(indi.aljet.myamazefilemanager_master.db.tables.List.class);
    }

    public static void clearGridViewTable() {
        DataSupport.deleteAll(Grid.class);
    }

    public static void clearBookmarksTable() {
        DataSupport.deleteAll(Bookmarks.class);
    }

    public static void clearSmbTable() {
        DataSupport.deleteAll(Smb.class);
    }


    public static void renameBookmark(String oldName,
                                      String oldPath,
                                      String newName,
                                      String newPath){
        Bookmarks bookmarks = new Bookmarks();
        bookmarks.setName(newName);
        bookmarks.setPath(newPath);
        bookmarks.updateAll("name = ? and path = ?",
                oldName,oldPath);
    }


    public static void renameSMB(String oldName,
                                 String oldPath,
                                 String newName,
                                 String newPath){
        Smb smb = new Smb();
        smb.setName(newName);
        smb.setPath(newPath);
        smb.updateAll("name = ? and path = ?",
                oldName,oldPath);
    }
















}
