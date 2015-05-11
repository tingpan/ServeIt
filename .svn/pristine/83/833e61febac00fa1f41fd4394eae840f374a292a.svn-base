package com.example.tingpan.fileStore;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by TingPan on 3/24/15.
 */
public interface IFileStore {
    public enum FileStoreStates {NEW, INIT, READY, FAILED}
    public FileStoreStates getFileStoreState();
    public List<Map<String, Object>> readFile();
    public void selectFile(String fileName);
    public String getFileStoreType();
    public File getPortDir();
}
