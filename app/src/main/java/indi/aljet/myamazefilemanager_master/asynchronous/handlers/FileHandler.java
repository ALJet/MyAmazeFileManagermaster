package indi.aljet.myamazefilemanager_master.asynchronous.handlers;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.File;
import java.lang.ref.WeakReference;

import indi.aljet.myamazefilemanager_master.adapter.RecyclerAdapter;
import indi.aljet.myamazefilemanager_master.filesystem.CustomFileObserver;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFile;
import indi.aljet.myamazefilemanager_master.fragments.MainFragment;
import indi.aljet.myamazefilemanager_master.utils.provider.UtilitiesProviderInterface;

/**
 * Created by PC-LJL on 2018/2/1.
 */

public class FileHandler extends Handler {

    private WeakReference<MainFragment> mainFragment;

    private UtilitiesProviderInterface utilsProvider;

    private RecyclerView listView;


    public FileHandler(MainFragment mainFragment, UtilitiesProviderInterface utilsProvider,
                       RecyclerView listView) {
        super(Looper.getMainLooper());
        this.mainFragment = new WeakReference<>(mainFragment);
        this.utilsProvider = utilsProvider;
        this.listView = listView;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        MainFragment main = mainFragment.get();

        String path = (String) msg.obj;

        switch (msg.what) {
            case CustomFileObserver.GOBACK:
                main.goBack();
                break;
            case CustomFileObserver.NEW_ITEM:
                HybridFile fileCreated = new HybridFile(main.openMode,
                        main.getCurrentPath() + "/" + path);
                main.getElementsList().add(fileCreated.generateLayoutElement(main,
                        utilsProvider));
                break;
            case CustomFileObserver.DELETED_ITEM:
                for (int i = 0; i < main.getElementsList().size(); i++) {
                    File currentFile = new File(main.getElementsList().get(i).desc);

                    if (currentFile.getName().equals(path)) {
                        main.getElementsList().remove(i);
                        break;
                    }
                }
                break;
            default://Pass along other messages from the UI
                super.handleMessage(msg);
                return;
        }

        if (listView.getVisibility() == View.VISIBLE) {
            if (main.getElementsList().size() == 0) {
                // no item left in list, recreate views
                main.reloadListElements(true, main.results, !main.IS_LIST);
            } else {
                // we already have some elements in list view, invalidate the adapter
                ((RecyclerAdapter) listView.getAdapter()).setItems(listView, main.getElementsList());
            }
        } else {
            // there was no list view, means the directory was empty
            main.loadlist(main.getCurrentPath(), true, main.openMode);
        }

        main.computeScroll();

    }
}
