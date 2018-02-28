package indi.aljet.myamazefilemanager_master.utils.files;

import java.util.Comparator;

import indi.aljet.myamazefilemanager_master.adapter.data.LayoutElementParcelable;

/**
 * Created by PC-LJL on 2018/1/25.
 */

public class FileListSorter implements
        Comparator<LayoutElementParcelable> {

    private int dirsOnTop = 0;
    private int asc = 1;
    private int sort = 0;

    public FileListSorter(int dir, int sort, int asc) {
        this.dirsOnTop = dir;
        this.asc = asc;
        this.sort = sort;
    }

    private boolean isDirectory(LayoutElementParcelable path){
        return path.isDirectory;
    }


    @Override
    public int compare(LayoutElementParcelable file1,
                       LayoutElementParcelable file2) {
        if(dirsOnTop == 0){
            if(isDirectory(file1) && !isDirectory(file2)){
                return -1;
            }else if(isDirectory(file2) && !isDirectory(file1)){
                return 1;
            }
        }else if(dirsOnTop == 1){
            if(isDirectory(file1) && !isDirectory(file2)){
                return 1;
            }else if(isDirectory(file2) && !isDirectory(file1)){
                return -1;
            }
        }
        if(sort == 0){
            return asc * file1.title.compareToIgnoreCase(file2.title);
        }else if(sort == 1){
            return asc * Long.valueOf(file1.date)
                    .compareTo(file2.date);
        }else if(sort == 2){
            if(!file1.isDirectory && !file2.isDirectory){
                return asc * Long.valueOf(file1.longSize).compareTo(file2.longSize);
            }else{
                return file1.title.compareToIgnoreCase(file2.title);
            }
        }else if(sort == 3){
            // sort by type
            if(!file1.isDirectory && !file2.isDirectory) {

                final String ext_a = getExtension(file1.title);
                final String ext_b = getExtension(file2.title);


                final int res = asc*ext_a.compareTo(ext_b);
                if (res == 0) {
                    return asc * file1.title.compareToIgnoreCase(file2.title);
                }
                return res;
            } else {
                return  file1.title.compareToIgnoreCase(file2.title);
            }
        }

        return 0;
    }



    private static String getExtension(String a){
        return a.substring(a.lastIndexOf(".") + 1)
                .toLowerCase();
    }
}
