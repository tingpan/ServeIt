package com.example.tingpan.fileStore;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.tingpan.application.SplashActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TingPan on 3/8/15.
 */
public class FileStore implements IFileStore{
    public static final String FILE_DIR_NAME = "serveIt";
    private static final FileStore fileStore = new FileStore();
    private static final String TAG = "FileStore";
    public static final String EVENT_ACTION = "com.file.event";
    public static final String PROGRESS_ACTION = "com.file.progress";
    public static final File ROOT_DIR = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
    public static final File TARGET_DIR = new File(ROOT_DIR.getAbsolutePath(),FILE_DIR_NAME);
    private static Context appContext;
    private AssetManager assetManager;
    private FileStoreStates fileStoreState = FileStoreStates.NEW;
    private Intent broadcastIntent = new Intent();
    private File portDir;

    private FileStore() {}

    public static FileStore getInstance(Context appContext) {
        FileStore.appContext = appContext;
        return fileStore;
    }
    @Override
    public FileStoreStates getFileStoreState() {
        return fileStoreState;
    }

    public void checkFileStore() {
        Log.d(TAG, ROOT_DIR.getAbsolutePath());
        if (!TARGET_DIR.exists() ||!TARGET_DIR.isDirectory()) {
            fileStoreState = FileStoreStates.INIT;
            Log.d(TAG, "Init root folder" + ROOT_DIR.getAbsolutePath());
            sendMyBroadcast(PROGRESS_ACTION, "create " + ROOT_DIR, 100);
        }
        PreLoading task = new PreLoading();
        task.execute();
    }

    private void sendMyBroadcast(String action, String event, int progress) {
        if (!action.equals(EVENT_ACTION)) {
            broadcastIntent.setAction(action);
            broadcastIntent.putExtra("event", event);
            broadcastIntent.putExtra("progress", progress);
        } else {
            broadcastIntent.setAction(action);
        }
        appContext.getApplicationContext().sendBroadcast(broadcastIntent);
    }

    private class PreLoading extends AsyncTask<String, Integer, Long> {
        @Override
        protected Long doInBackground(String... folderNames) {
            if (fileStoreState == FileStoreStates.INIT) {
                assetManager = appContext.getApplicationContext().getAssets();
                extractFolder(FILE_DIR_NAME);
            }
            return null;
        }

        private void extractFolder(String folderName) {
            try {
                String[] filesInside = assetManager.list(folderName);

                if (filesInside.length == 0) {
                    Log.d(TAG, "find file" + folderName);
                    extractFile(folderName);
                } else {
                    File fileDir = new File(ROOT_DIR, folderName);
                    if (!fileDir.exists() || !fileDir.isDirectory()) {
                        if (!fileDir.mkdir()) {
                            Log.e(TAG, "Error with make new folder " + folderName);
                            sendMyBroadcast(PROGRESS_ACTION, "Error with make new folder " + folderName, 100);
                            return;
                        }
                        Log.d(TAG, "Make new folder " + folderName);
                    } else {
                        Log.d(TAG, "Folder exists " + folderName);
                    }
                    for (String eachFile : filesInside) {
                        extractFolder(folderName + "/" + eachFile);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void extractFile(String fileName) {
            sendMyBroadcast(PROGRESS_ACTION, "Extracting:" + fileName, 0);
            Log.d(TAG, "Extracting " + fileName + "...");
            try {
                InputStream in = assetManager.open(fileName);
                int total = in.available();
                OutputStream out = new FileOutputStream(new File(ROOT_DIR, fileName));
                byte buffer[] = new byte[100000];
                int len = 0;
                while (true) {
                    int count = in.read(buffer);
                    if (count < 0)
                        break;
                    out.write(buffer, 0, count);
                    len += count;
                    int progress = (int) ((len * 1.0 / total) * 100);
                    Log.d(TAG, "extract:" + progress);
                    sendMyBroadcast(PROGRESS_ACTION, "Extracting: " + fileName, progress);
                }
                in.close();
                out.close();
                Log.d(TAG, "Extracted " + len + " bytes to " + fileName);
            } catch (IOException e) {
                sendMyBroadcast(PROGRESS_ACTION, "can not Extracting: " + fileName, 100);
                Log.e(TAG, "Error to copy " + fileName + ":" + e);
            }
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            if (TARGET_DIR.exists() && TARGET_DIR.isDirectory() && TARGET_DIR.canExecute()) {
                fileStoreState = FileStoreStates.READY;
                sendMyBroadcast(PROGRESS_ACTION, "Check all files", 100);
                Log.d(TAG, "FileStore is correct");
            } else {
                fileStoreState = FileStoreStates.FAILED;
                sendMyBroadcast(PROGRESS_ACTION, "Files are not correct", 100);
                Log.e(TAG, "FileStore is missing");
            }
            Log.d(TAG, "file service pre-loading is finish, status: " + fileStoreState);
            sendMyBroadcast(EVENT_ACTION, "", 0);
        }
    }

    @Override
    public List<Map<String, Object>> readFile() {
        List<Map<String, Object>> fileList = new ArrayList<>();
        for (String fileName : TARGET_DIR.list()) {
            File gameDir = new File(TARGET_DIR, fileName);
            if (gameDir.exists() && gameDir.isDirectory()) {
                Log.d(TAG, "read " + gameDir);
                Date lastModified = new Date(gameDir.lastModified());
                Map<String, Object> gameItem = new HashMap<>();
                gameItem.put("title", gameDir.getName());
                gameItem.put("date", lastModified.toString());
                fileList.add(gameItem);
            }
        }
        return fileList;
    }

    @Override
    public void selectFile(String fileName) {
        portDir = new File(TARGET_DIR.getAbsolutePath(), fileName);
    }

    public File getPortDir(){
        return portDir;
    }

    @Override
    public String getFileStoreType() {
        return "Real";
    }
}

