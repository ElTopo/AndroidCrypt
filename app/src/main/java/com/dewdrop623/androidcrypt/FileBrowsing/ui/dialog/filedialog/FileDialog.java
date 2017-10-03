package com.dewdrop623.androidcrypt.FileBrowsing.ui.dialog.filedialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.dewdrop623.androidcrypt.FileBrowsing.ui.MainActivity;
import com.dewdrop623.androidcrypt.FileBrowsing.ui.fileviewer.FileViewer;
import com.dewdrop623.androidcrypt.R;

import java.io.File;

/**
 * super class for dialogs that interact with files
 */

public class FileDialog extends DialogFragment {
    public static final String PATH_ARGUMENT = "com.dewdrop623.androidcrypt.FileBrowsing.ui.dialog.PATH_ARGUMENT";
    protected FileViewer fileViewer;
    protected File file;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFromArguments();

        //FileDialog was recreated (probably a screen rotation) so we need to get the reference to fileViewer back
        if (savedInstanceState!=null) {
            fileViewer = ((MainActivity)getActivity()).getFileViewer();
        }
    }

    private void initFromArguments() {
        file = new File(getArguments().getString(PATH_ARGUMENT));
    }

    protected Dialog createDialog(String title, View view, String positiveButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setNegativeButton(R.string.cancel, null);

        if (positiveButtonText != null) {
            builder.setPositiveButton(positiveButtonText, positiveOnClickListener);
        }
        if(view != null) {
            builder.setView(view);
        }
        return builder.create();
    }
    protected View inflateLayout(int layoutId) {
        return ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layoutId, null);
    }

    public void setFileViewer(FileViewer fileViewer) {
        this.fileViewer = fileViewer;
    }

    Dialog.OnClickListener positiveOnClickListener = new Dialog.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            positiveButtonOnClick();
        }
    };

    protected void positiveButtonOnClick() {
        if (fileViewer == null) {
            Log.e("DebugFileOptionDialog", "You must call fileDialog.setFileViewer(FileViewer)");
            return;
        }
        return;
    }
    protected void showNewDialogAndDismiss(FileDialog fileDialog) {
        showNewDialogAndDismiss(fileDialog, new Bundle());
    }
    protected void showNewDialogAndDismiss(FileDialog fileDialog, Bundle newArguments) {
        newArguments.putAll(getArguments());
        fileDialog.setArguments(newArguments);
        fileDialog.setFileViewer(fileViewer);
        ((MainActivity)getActivity()).showDialogFragment(fileDialog);
        dismiss();
    }
}