package indi.aljet.myamazefilemanager_master.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.database.CloudContract;
import indi.aljet.myamazefilemanager_master.ui.dialogs.SmbSearchDialog;
import indi.aljet.myamazefilemanager_master.utils.OpenMode;
import indi.aljet.myamazefilemanager_master.utils.Utils;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

/**
 * Created by PC-LJL on 2018/1/12.
 */


public class CloudSheetFragment extends
        BottomSheetDialogFragment implements
        View.OnClickListener {

    View rootView;

    private Unbinder unbinder;

    @BindView(R.id.linear_layout_smb)
    LinearLayout mSmbLayout;
    @BindView(R.id.linear_layout_box)
    LinearLayout mBoxLayout;
    @BindView(R.id.linear_layout_dropbox)
    LinearLayout mDropboxLayout;
    @BindView(R.id.linear_layout_google_drive)
    LinearLayout mGoogleDriveLayout;
    @BindView(R.id.linear_layout_onedrive)
    LinearLayout mOnedriveLayout;
    @BindView(R.id.linear_layout_get_cloud)
    LinearLayout mGetCloudLayout;

    public static final String TAG_FRAGMENT = "cloud_fragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        rootView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_sheet_cloud,null);

        if (((MainActivity) getActivity()).getAppTheme().equals(AppTheme.DARK)) {
            rootView.setBackgroundColor(Utils.getColor(getContext(), R.color.holo_dark_background));
        } else if (((MainActivity) getActivity()).getAppTheme().equals(AppTheme.BLACK)) {
            rootView.setBackgroundColor(Utils.getColor(getContext(), android.R.color.black));
        } else {
            rootView.setBackgroundColor(Utils.getColor(getContext(), android.R.color.white));
        }

        if (isCloudProviderAvailable(getContext())) {

            mBoxLayout.setVisibility(View.VISIBLE);
            mDropboxLayout.setVisibility(View.VISIBLE);
            mGoogleDriveLayout.setVisibility(View.VISIBLE);
            mOnedriveLayout.setVisibility(View.VISIBLE);
            mGetCloudLayout.setVisibility(View.GONE);
        }

        mSmbLayout.setOnClickListener(this);
        mBoxLayout.setOnClickListener(this);
        mDropboxLayout.setOnClickListener(this);
        mGoogleDriveLayout.setOnClickListener(this);
        mOnedriveLayout.setOnClickListener(this);
        mGetCloudLayout.setOnClickListener(this);

        dialog.setContentView(rootView);
    }


    public static final boolean isCloudProviderAvailable
            (Context context){
        PackageManager pm = context.getPackageManager();
        try{
            pm.getPackageInfo(CloudContract
            .APP_PACKAGE_NAME,PackageManager
            .GET_ACTIVITIES);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_layout_smb:
                dismiss();
                SmbSearchDialog smbDialog=new SmbSearchDialog();
                smbDialog.show(getActivity().getFragmentManager(), "tab");
                return;
            case R.id.linear_layout_box:
                ((MainActivity) getActivity()).addConnection(OpenMode.BOX);
                break;
            case R.id.linear_layout_dropbox:
                ((MainActivity) getActivity()).addConnection(OpenMode.DROPBOX);
                break;
            case R.id.linear_layout_google_drive:
                ((MainActivity) getActivity()).addConnection(OpenMode.GDRIVE);
                break;
            case R.id.linear_layout_onedrive:
                ((MainActivity) getActivity()).addConnection(OpenMode.ONEDRIVE);
                break;
            case R.id.linear_layout_get_cloud:
                Intent cloudPluginIntent = new Intent(Intent.ACTION_VIEW);
                cloudPluginIntent.setData(Uri.parse("market://details?id=com.filemanager.amazecloud"));
                startActivity(cloudPluginIntent);
                break;
        }

        // dismiss this sheet dialog
        dismiss();
    }

    public interface CloudConnectionCallbacks{
        void addConnection(OpenMode service);
        void deleteConnection(OpenMode service);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sheet_cloud,container);
        unbinder = ButterKnife.bind(this,view);
        return view;
//        return super.onCreateView(inflater, container, savedInstanceState);
    }



}
