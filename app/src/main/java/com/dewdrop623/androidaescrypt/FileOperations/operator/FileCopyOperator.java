package com.dewdrop623.androidaescrypt.FileOperations.operator;

import android.os.Bundle;

import com.dewdrop623.androidaescrypt.FileOperations.FileModifierService;
import com.dewdrop623.androidaescrypt.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * copies files
 */

public class FileCopyOperator extends FileOperator {
    public static final String FILE_COPY_DESTINATION_ARG = "com.dewdrop623.androidaescrypt.FileOperations.operator.FileCopyOperator.FILE_COPY_DESTINATION_ARG";

    private boolean done = false;
    private boolean conflict = false;
    private File outputFile;

    public FileCopyOperator(File file, Bundle args, FileModifierService fileModifierService) {
        super(file, args, fileModifierService);
    }

    @Override
    public int getProgress() {
        if (done) {//TODO real updates
            return 100;
        }
        return 0;
    }

    @Override
    protected void initMemVarFromArgs() {
        outputFile = new File(args.getString(FILE_COPY_DESTINATION_ARG) + "/" + file.getName());
    }

    @Override
    protected void handleYesNoResponse(boolean yes) {
        if (yes) {
            finishTakingInput();
        } else {
            cancelOperation();
        }
    }

    @Override
    protected void handleYesNoRememberAnswerResponse(boolean yes, boolean remember) {

    }

    @Override
    protected void handleTextOrCancelResponse(String response) {

    }

    @Override
    protected void doOperation() {
        try {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            FileChannel sourceChannel = null;
            FileChannel destinationChannel = null;
            try {
                sourceChannel = new FileInputStream(file).getChannel();
                destinationChannel = new FileOutputStream(outputFile).getChannel();
                destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } catch (IOException ioe) {
                ioe.printStackTrace(); //TODO handle this
            } finally {
                if (sourceChannel != null) {
                    sourceChannel.close();
                }
                if (destinationChannel != null) {
                    destinationChannel.close();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        done = true;
    }

    @Override
    protected void prepareAndValidate() {
        if (!file.exists()) {
            fileModifierService.showToast(fileModifierService.getString(R.string.file_does_not_exist)+": "+file.getName());
            cancelOperation();
            return;
        }
        if (!file.canRead()) {
            fileModifierService.showToast(fileModifierService.getString(R.string.file_not_readable)+": "+file.getName());
            cancelOperation();
            return;
        }
        if (!outputFile.getParentFile().exists()) {
            fileModifierService.showToast(fileModifierService.getString(R.string.could_not_find_directory)+": "+file.getParentFile().getName());
            cancelOperation();
            return;
        }
        if (!outputFile.getParentFile().canWrite()) {
            fileModifierService.showToast(fileModifierService.getString(R.string.directory_not_writable)+": "+outputFile.getParentFile().getName());
            cancelOperation();
            return;
        }
        conflict = outputFile.exists();
    }

    @Override
    protected void getInfoFromUser() {
        if (conflict) {
            askYesNo("Overwrite "+outputFile.getName()+"?");
        } else {
            finishTakingInput();
        }
    }
    public void doOperationWithoutThreadOrUserQuestions() {
        initMemVarFromArgs();
        doOperation();
    }
}
