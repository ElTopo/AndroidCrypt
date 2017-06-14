package com.dewdrop623.androidaescrypt.FileBrowsing;

import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;

import java.io.File;

/**
 * keeps track of current directory, monitors files for changes, takes commands from FileViewer and executes them
 */

public class FileBrowser {

    private File currentPath;
    private FileViewer fileViewer;
    private FileObserver fileObserver;

    public static final File parentDirectory = new File("..");
    public static final File topLevelInternal = new File(Environment.getExternalStorageDirectory().toString());

    public FileBrowser() {
        //TODO get path from settings, for now just use default external storage path
        currentPath = topLevelInternal;
        monitorCurrentPathForChanges();
    }
    public void setFileViewer(FileViewer fileViewer) {
        this.fileViewer = fileViewer;
        fileViewer.setFileBrowser(this);
        updateFileViewer();
    }
    private void updateFileViewer() {
        File[] files = currentPath.listFiles();
        if (files == null) {
            files = new File[]{};
        }
        fileViewer.setFileList(files);
    }
    private void monitorCurrentPathForChanges() {
        if (fileObserver != null) {
            fileObserver.stopWatching();
        }
        fileObserver = new FileObserver(currentPath.getAbsolutePath()) {
            @Override
            public void onEvent(int event, String path) {
                event &= FileObserver.ALL_EVENTS;
                if (event == FileObserver.CLOSE_WRITE || event == FileObserver.CREATE || event == FileObserver.MODIFY || event == FileObserver.DELETE || event == FileObserver.MOVED_FROM
                        || event == FileObserver.MOVED_TO || event == FileObserver.ATTRIB)

                    new Handler(Looper.getMainLooper()).post(new Runnable() {//create handler and post runnable so that updateFileViewer is called from UI thread and can change UI
                        @Override
                        public void run() {
                            updateFileViewer();
                        }
                    });
            }
        };
        fileObserver.startWatching();
    }
    public File getCurrentPath() {
        return currentPath;
    }
    public void changePath(File newPath) {
        if(!newPath.isDirectory()) {
            return;
        }
        if (newPath == parentDirectory) {
            currentPath = currentPath.getParentFile();
        } else {
            currentPath = newPath;
        }
        monitorCurrentPathForChanges();
        updateFileViewer();
    }
    public void modifyFile(/*FileCommand fileCommand*/) {
        //TODO execute file commands
    }
}
