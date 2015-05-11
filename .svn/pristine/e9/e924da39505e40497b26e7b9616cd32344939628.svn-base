package com.example.tingpan.application;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.example.tingpan.displayUnit.MyMaterialDialog;
import com.example.tingpan.fileStore.IFileStore;
import com.example.tingpan.fileStore.MockFileStore;


/**
 * Created by TingPan on 3/24/15.
 */
public class ChooseGameTest extends ActivityUnitTestCase<ChooseGame> {

    private ChooseGame chooseActivity;
    private Intent testIntent;
    private MockFileStore fileStore;

    public ChooseGameTest() {
        super(ChooseGame.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        testIntent = new Intent(Intent.ACTION_MAIN);
        testIntent.putExtra("RUN_ON_TEST_MODE",true);
        fileStore = MockFileStore.getInstance();
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testPreconditions() throws Throwable {
        startActivity(testIntent,null,null);
        chooseActivity = getActivity();
        assertNotNull("Activity is null",chooseActivity);
        IFileStore iFileStore = chooseActivity.getFileStore();
        assertEquals("Not calling the correct mock file store","Mock",iFileStore.getFileStoreType());
        ListView gameList = (ListView) chooseActivity.findViewById(R.id.gameList);
        assertNotNull("List view is empty", gameList);
    }

    public void testFileStoreCorrect() throws Throwable {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.READY);
        startActivity(testIntent, null, null);
        chooseActivity = getActivity();
        assertNull("Dialog displays when everything is correct", chooseActivity.getLastDialog());
    }

    public void testFileStoreNotCorrect() throws Throwable {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.FAILED);
        startActivity(testIntent, null, null);
        chooseActivity = getActivity();
        MyMaterialDialog lastDialog = chooseActivity.getLastDialog();
        assertNotNull("Dialog did not display when file is not correct", lastDialog);
        assertEquals("Dialog text is not correct",lastDialog.getMessage()
                ,chooseActivity.getString(R.string.fileError));
    }

    public void testListViewCorrect() {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.READY);
        String[] list = new String[10];
        for (int i = 0; i < list.length; i++){
            list[i]  = String.valueOf((int) Math.floor(Math.random()*1000));
        }
        fileStore.setFileList(list);
        startActivity(testIntent, null, null);
        chooseActivity = getActivity();
        ListView gameList = (ListView) chooseActivity.findViewById(R.id.gameList);
        assertEquals("Item amounts is not correct",10,gameList.getCount());
        for (int i = 0; i < gameList.getCount(); i++){
            View itemView = gameList.getAdapter().getView(i,null,null);
            assertNotNull("List content is null",itemView);
            TextView gameTitle = (TextView) itemView.findViewById(R.id.gameTile);
            TextView gameDate = (TextView) itemView.findViewById(R.id.gameDate);
            assertNotNull(gameTitle);
            assertNotNull(gameDate);
            assertEquals("List Content is incorrect",gameTitle.getText(),fileStore.getFileList()[i]);
            assertEquals("List Content is incorrect",gameDate.getText(),"date");
        }
    }

    public void testEmptyListView() {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.READY);
        String[] list = new String[0];
        fileStore.setFileList(list);
        startActivity(testIntent, null, null);
        chooseActivity = getActivity();
        ListView gameList = (ListView) chooseActivity.findViewById(R.id.gameList);
        assertEquals("List Content is not empty",0,gameList.getCount());
    }

    public void testItemClick() {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.READY);
        String[] list = new String[10];
        for (int i = 0; i < list.length; i++){
            list[i]  = String.valueOf((int) Math.floor(Math.random()*1000));
        }
        fileStore.setFileList(list);
        startActivity(testIntent, null, null);
        chooseActivity = getActivity();
        ListView gameList = (ListView) chooseActivity.findViewById(R.id.gameList);
        int chooseItem = (int) Math.floor(Math.random()*10);
        gameList.performItemClick(
                gameList.getAdapter().getView(chooseItem, null, null),
                chooseItem,
                gameList.getItemIdAtPosition(chooseItem));
        Intent startIntent = getStartedActivityIntent();
        assertNotNull("New activity did not start",startIntent);
        assertEquals("Activity extra is not correct",
                fileStore.getFileList()[chooseItem]
                ,startIntent.getStringExtra("title"));
    }

    public void testLifeCycle () throws  Throwable {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.READY);
        startActivity(testIntent, null, null);
        chooseActivity = getActivity();
        getInstrumentation().callActivityOnStart(chooseActivity);
        getInstrumentation().callActivityOnPause(chooseActivity);
        getInstrumentation().callActivityOnResume(chooseActivity);
        getInstrumentation().callActivityOnDestroy(chooseActivity);
    }
}
