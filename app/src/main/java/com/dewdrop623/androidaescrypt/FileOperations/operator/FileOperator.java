package com.dewdrop623.androidaescrypt.FileOperations.operator;

import java.io.File;

/**
 * interface for classes that do work on files
 */

public abstract class FileOperator {
    protected File file;
    protected String[] args;
    public FileOperator(File file, String[] args) {
        this.file = file;
        this.args = args;
    }
    public abstract int getProgress();
    public abstract void execute();
}