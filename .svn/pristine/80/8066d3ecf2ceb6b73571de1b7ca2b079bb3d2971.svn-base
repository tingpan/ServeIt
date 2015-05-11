package com.example.tingpan.fileStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TingPan on 3/24/15.
 */
public class MockFileStore implements IFileStore {

    private static final MockFileStore fileStore = new MockFileStore();
    private MockFileStore(){}
    private FileStoreStates fileStoreStates = FileStoreStates.FAILED;
    private String[] fileNames = new String[0];
    private File portDir = new File("test");

    @Override
    public FileStoreStates getFileStoreState() {
        return fileStoreStates;
    }

    @Override
    public List<Map<String, Object>> readFile() {
        List<Map<String, Object>> fileList = new ArrayList<>();
        for (String fileName : fileNames) {
            Map<String, Object> gameItem = new HashMap<>();
            gameItem.put("title", fileName);
            gameItem.put("date", "date");
            fileList.add(gameItem);
        }
        return fileList;
    }

    @Override
    public void selectFile(String fileName) {

    }

    @Override
    public String getFileStoreType() {
        return "Mock";
    }

    @Override
    public File getPortDir() {
        return portDir;
    }

    public void setFileStoreStates (FileStoreStates states){
        this.fileStoreStates = states;
    }

    public static MockFileStore getInstance(){
        return fileStore;
    }

    public void setFileList (String[] list) {
        this.fileNames = list;
    }

    public String[] getFileList () {
        return fileNames;
    }
}
