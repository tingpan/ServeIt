package com.example.tingpan.nodeService.interfaces;

import android.os.Binder;

import com.example.tingpan.nodeService.NodeService;

import java.io.File;

/**
 * Created by TingPan on 3/24/15.
 */
public abstract class MyBinder extends Binder {
    abstract public String startNode(File hostFolder);
    abstract public void stopNode();
    abstract public NodeService.NodeState getNodeState();
    abstract public String getIpAddress();
}
