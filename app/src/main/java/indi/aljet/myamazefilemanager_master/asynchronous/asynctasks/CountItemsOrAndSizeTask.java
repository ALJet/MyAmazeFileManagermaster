package indi.aljet.myamazefilemanager_master.asynchronous.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Pair;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.utils.OnFileFound;
import indi.aljet.myamazefilemanager_master.utils.OnProgressUpdate;
import indi.aljet.myamazefilemanager_master.utils.files.FileUtils;

/**
 * Created by PC-LJL on 2018/1/30.
 */

public class CountItemsOrAndSizeTask extends AsyncTask<Void,
        Pair<Integer,Long>,String> {

    private Context context;
    private TextView itemsText;
    private HybridFileParcelable file;
    private boolean isStorage;

    public CountItemsOrAndSizeTask(Context context, TextView itemsText, HybridFileParcelable file, boolean isStorage) {
        this.context = context;
        this.itemsText = itemsText;
        this.file = file;
        this.isStorage = isStorage;
    }


    @Override
    protected String doInBackground(Void[] voids) {
        String items = "";
        long fileLength = file.length(context);
        if(file.isDirectory(context)){
            final AtomicInteger x = new AtomicInteger(0);
            file.forEachChildrenFile(context,false,new
                    OnFileFound(){
                        @Override
                        public void onFileFound(HybridFileParcelable file) {
                            x.incrementAndGet();
                        }
                    });
            final int folderLength = x.intValue();
            long folderSize;
            if(isStorage){
                folderSize = file.getUsableSpace();
            }else{
                folderSize = FileUtils.folderSize(file,
                        new OnProgressUpdate<Long>() {
                            @Override
                            public void onUpdate(Long data) {
                                publishProgress(new Pair<>(folderLength,
                                        data));
                            }
                        });
            }
            items = getText(folderLength,folderSize,
                    false);

        }else{
            items = Formatter.formatFileSize(context,
                    fileLength) + ("(" + fileLength + " "
                    +context.getResources()
                    .getQuantityString(R.plurals.bytes,
                            (int) fileLength)
                    + ")");
        }


        return items;
    }

    @Override
    protected void onProgressUpdate(Pair<Integer, Long>[] values) {
        Pair<Integer,Long> data = values[0];
        itemsText.setText(getText(data.first,data.second,
                true));
    }

    private String getText(int filesInFolder,
                           long length,
                           boolean loading){
        String numOfItems = (filesInFolder != 0 ?
        filesInFolder + " " : "") +
                context.getResources().getQuantityString(R
                .plurals.items,filesInFolder);
        return numOfItems + "; " + (loading ? ">" : "") +
                Formatter.formatFileSize(context,length);
    }

    protected void onPostExecute(String items) {
        itemsText.setText(items);
    }
}


