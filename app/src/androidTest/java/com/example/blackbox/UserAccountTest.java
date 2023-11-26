package com.example.blackbox;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.blackbox.inventory.InventoryDB;
import com.example.blackbox.inventory.Item;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.tag.TagDB;
import com.google.firebase.firestore.CollectionReference;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;


/**
 * This tests whether changes to the database of a user is reflected
 * in the app
 * It also tests whether changes to the database of other users affects
 * what appears in the app (it should not)
 */

@RunWith(AndroidJUnit4.class)
public class UserAccountTest {
    final private Integer maxDelay = 1000;
    private final Tag basicTag = new Tag("Name", 1, "Color", "Description");
    private final Item basicItem = new Item("Name", Double.valueOf(120), "Description");
    private final String testUID = "testID";
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Method used to clear all DBs for testing
     */
    private void clearDBs(){
        // clear inventory first to avoid errors
        InventoryDBTest.clearInventoryDB();
        TagDBTest.clearTagDB();
    }

    /**
     * Method used to clear all DBs for testing
     */
    private void clearDBs(String userID){
        // clear inventory first to avoid errors
        InventoryDBTest.clearInventoryDB(userID);
        TagDBTest.clearTagDB(userID);
    }



    private String getUserID(){
        return new GoogleAuthDB().getUid();
    }

    /**
     * Tests if an item will be displayed if it is added to
     * that account currently logged in
     * (it should be)
     */
    @Test
    public void testAddItemToAccount(){
        // clear the database
        clearDBs();

        // create an item to add
        basicItem.setUserID(getUserID());
        basicItem.setTags(new ArrayList<>());
        // add the item to the database
        InventoryDB inventoryDB = new InventoryDB();
        inventoryDB.addItemToDB(basicItem);

        // wait for the item to be added and update to process
        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        onView(withText(basicItem.getName())).check(matches(isDisplayed()));

    }

    /**
     * Tests if a tag will be displayed if it is added to
     * that account currently logged in
     * (it should be)
     */
    @Test
    public void testAddTagToAccount(){
        // clear the database
        clearDBs();

        // navigate to tags page
        onView(withId(R.id.settings)).perform(click());

        // create an tag to add
        basicTag.setUserID(getUserID());

        // add the tag to the database
        TagDB tagDB = new TagDB();
        tagDB.addTagToDB(basicTag);

        // wait for the item to be added and update to process
        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        onView(withText(basicTag.getName())).check(matches(isDisplayed()));

    }

    /**
     * Tests if an item will be displayed if it is added to
     * a different account than the one currently logged in
     * (it should not be)
     */
    @Test
    public void testAddItemToDiffAccount(){
        // clear the current DB
        String userID = new GoogleAuthDB().getUid();
        // check that test userID is not the same as userID
        Assert.assertNotEquals(userID, testUID);
        // clear both databases
        clearDBs(userID);
        clearDBs(testUID);
        // create an item to add
        basicItem.setUserID(testUID);
        basicItem.setTags(new ArrayList<>());
        // add the item to the other database
        InventoryDB inventoryDB = new InventoryDB();
        inventoryDB.addItemToDB(basicItem);
        // check that the newly added item is not displayed
        onView(withText(basicItem.getName())).check(doesNotExist());
    }

    /**
     * Tests if a tag will be displayed if it is added to
     * a different account than the one currently logged in
     * (it should not be)
     */
    @Test
    public void testAddTagToDiffAccount(){
        // clear the current DB
        String userID = new GoogleAuthDB().getUid();
        // check that test userID is not the same as userID
        Assert.assertNotEquals(userID, testUID);
        // clear both databases
        clearDBs(userID);
        clearDBs(testUID);

        // navigate to tags page
        onView(withId(R.id.settings)).perform(click());
        // create an tag to add
        basicTag.setUserID(testUID);
        // add the tag to the other database
        TagDB tagDB = new TagDB();
        tagDB.addTagToDB(basicTag);
        // check that the newly added tag is not displayed
        onView(withText(basicItem.getName())).check(doesNotExist());
    }
}
