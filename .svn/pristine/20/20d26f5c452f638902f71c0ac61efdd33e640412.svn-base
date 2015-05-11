package com.example.tingpan.application;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import com.example.tingpan.nodeService.NodeService;
import com.example.tingpan.nodeService.interfaces.MyBinder;


/**
 * Created by TingPan on 3/25/15.
 */
public class NodeServiceTest extends ServiceTestCase<NodeService> {
    /**
     * Constructor
     */
    public NodeServiceTest() {
        super(NodeService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * To Test if the test is set up correctly;
     */
    public void testPreconditions() {
    }

    /**
     * Test the start of the service
     */
    @SmallTest
    public void testStartable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), NodeService.class);
        startService(startIntent);
    }

    /**
     * Test binding to service
     */
    @MediumTest
    public void testBindable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), NodeService.class);
        MyBinder mybinder =(MyBinder) bindService(startIntent);
    }
}

