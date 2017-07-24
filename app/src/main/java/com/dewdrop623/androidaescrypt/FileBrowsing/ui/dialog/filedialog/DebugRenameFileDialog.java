package com.dewdrop623.androidaescrypt.FileBrowsing.ui.dialog.filedialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.dewdrop623.androidaescrypt.FileOperations.FileModifierService;
import com.dewdrop623.androidaescrypt.FileOperations.FileOperationType;
import com.dewdrop623.androidaescrypt.FileOperations.operator.FileMoveOperator;
import com.dewdrop623.androidaescrypt.R;

/**
 * ask user for filename
 */

public class DebugRenameFileDialog extends FileDialog {

    EditText fileNameEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = inflateLayout(R.layout.dialogfragment_debug_rename_file);
        fileNameEditText = (EditText) view.findViewById(R.id.fileNameEditText);
        return createDialog(getString(R.string.rename)+" "+file.getName(), view, getString(R.string.ok));
    }

    @Override
    protected void positiveButtonOnClick() {
        super.positiveButtonOnClick();
        Bundle args = new Bundle();
        args.putString(FileMoveOperator.FILE_MOVE_DESTINATION_ARG, file.getParent().toString()+"/"+fileNameEditText.getText().toString());
        args.putString(FileModifierService.FILEMODIFIERSERVICE_FILE, file.getAbsolutePath());
        args.putInt(FileModifierService.FILEMODIFIERSERVICE_OPERATIONTYPE, FileOperationType.MOVE);
        fileViewer.sendFileCommandToFileBrowser(args);
    }
}