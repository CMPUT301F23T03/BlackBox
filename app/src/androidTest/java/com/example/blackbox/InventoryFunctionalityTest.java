package com.example.blackbox;

        import static androidx.test.espresso.Espresso.onData;
        import static androidx.test.espresso.Espresso.onView;
        import static androidx.test.espresso.action.ViewActions.click;
        import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
        import static androidx.test.espresso.assertion.ViewAssertions.matches;
        import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
        import static androidx.test.espresso.matcher.ViewMatchers.withId;
        import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
        import static androidx.test.espresso.matcher.ViewMatchers.withText;

        import static org.hamcrest.CoreMatchers.allOf;
        import static org.hamcrest.CoreMatchers.containsString;
        import static org.hamcrest.CoreMatchers.instanceOf;
        import static org.hamcrest.CoreMatchers.is;
        import static org.hamcrest.CoreMatchers.not;

        import android.util.Log;

        import androidx.test.espresso.Espresso;
        import androidx.test.espresso.action.ViewActions;
        import androidx.test.espresso.matcher.ViewMatchers;
        import androidx.test.ext.junit.rules.ActivityScenarioRule;
        import androidx.test.ext.junit.runners.AndroidJUnit4;

        import com.example.blackbox.inventory.Item;
        import com.example.blackbox.tag.Tag;
        import com.example.blackbox.utils.StringFormatter;

        import org.junit.Rule;
        import org.junit.Test;
        import org.junit.runner.RunWith;

        import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class InventoryFunctionalityTest {
    final private String name = "Item";
    final private String name2 = "Item2";
    final private ArrayList<Tag> tags = new ArrayList<>();
    final private double estimatedValue = 120;
    final private double estimatedValue2 = 1;
    final private String dateOfPurchase = "2023-11-08";
    final private String make = "12";
    final private String model = "12";
    final private String serialNumber = "789";
    final private String description = "Hi";
    final private String comment = "This test item is beautiful";
    final private Integer maxDelay = 1000;  // the number of milliseconds the app can wait for

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
     * Method used to set test items up and tags
     */
    public void setup() {
        clearDBs();

        // Mock data for testing
        // Switch to the tag fragment
        TagFunctionalityTest.navigateToTags();

        // Click on the "Add" button to add a new tag.
        onView(withId(R.id.add_tag_button)).perform(click());

        // Create and add the first tag
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Tag1"));
        onView(withId(R.id.desc_editText)).perform(ViewActions.replaceText("Tag1 Description"));

        // Select a color for the first tag (e.g., Red)
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Orange")).perform(click());

        // Click the "Add" button to add the first tag
        onView(withId(R.id.small_save_button)).perform(click());

        // Click on the "Add" button to add a new tag.
        onView(withId(R.id.add_tag_button)).perform(click());

        // Create and add the second tag
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Tag2"));
        onView(withId(R.id.desc_editText)).perform(ViewActions.replaceText("Tag2 Description"));

        // Select a color for the first tag (e.g., Red)
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Blue")).perform(click());

        // Click the "Add" button to add the first tag
        onView(withId(R.id.small_save_button)).perform(click());

        // Click on the "Add" button to add a new tag.
        onView(withId(R.id.add_tag_button)).perform(click());

        // Create and add the Third tag
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Tag3"));
        onView(withId(R.id.desc_editText)).perform(ViewActions.replaceText("Tag3 Description"));

        // Select a color for the first tag (e.g., Red)
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Red")).perform(click());

        // Click the "Add" button to add the first tag
        onView(withId(R.id.small_save_button)).perform(click());

        // Switch back to the item fragment screen
        TagFunctionalityTest.navigateFromTagsToItems();

        // Click on the "Add" button to add a new item
        onView(withId(R.id.add_button)).perform(click());

        // Fill in the item information.
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(name));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(estimatedValue)));

        // Click on the "Tags" dropdown to open it.
        onView(withId(R.id.tag_dropdown)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Select one or more tags from the dropdown.
        onView(withText("Tag1")).perform(click());
        onView(withText("Tag2")).perform(click());

        // Close the dropdown.
        onView(withText("OK")).perform(click());

        // Add the item.
        onView(withId(R.id.small_save_button)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Click on the "Add" button to add a new item
        onView(withId(R.id.add_button)).perform(click());

        // Fill in the item information.
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(name2));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(estimatedValue2)));

        // Add the item.
        onView(withId(R.id.small_save_button)).perform(click());

    }

    /**
     * This test checks if the InventoryFragment is accessible by clicking the bottom left icon
     */
    @Test
    public void testInventorySwitchActivity(){
        onView(withId(R.id.inventory)).perform(click());
        onView(withId(R.id.inventory_fragment)).check(matches(isDisplayed()));
    }

    /**
     * This test checks if an item can be added
     * For now, only checks for name and price against the list in the InventoryFragment
     * It's better to check an item against a ViewFragment of itself - will implement later
     */
    @Test
    public void testAddItemActivity(){
        // start with a fresh database
        InventoryDBTest.clearInventoryDB();
        // click on add item
        onView(withId(R.id.add_button)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(name));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(estimatedValue)));

        // This isn't working yet - I need to find a way to scroll down
        // onView(withId(R.id.comment_editText)).perform(ViewActions.scrollTo());
        // onView(withId(R.id.desc_editText)).perform(ViewActions.typeText(description));

        // add item
        onView(withId(R.id.small_save_button)).perform(click());
        // see if the newly added data is displayed

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        onView(withText(name)).check(matches(isDisplayed()));
        onView(withText(StringFormatter.getMonetaryString(estimatedValue))).check(matches(isDisplayed()));

        // This isn't working yet
        //onView(withText(description)).check(matches(isDisplayed()));
    }

    /**
     * This test checks if an item can be edited
     * For now, only checks for name and price against the list in the InventoryFragment
     * It's better to check an item against a ViewFragment of itself - will implement later
     */
    @Test
    public void testEditItemActivity(){
        // start with a fresh database
        InventoryDBTest.clearInventoryDB();
        // click on addItem
        onView(withId(R.id.add_button)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText("Random Test"));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(150)));
        // add item
        onView(withId(R.id.small_save_button)).perform(click());
        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }
        // open the newly added item
        onData(is(instanceOf(Item.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        // change the name
        onView(withId(R.id.name_editText)).perform(ViewActions.clearText());
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(name));
        // change the value
        onView(withId(R.id.value_editText)).perform(ViewActions.scrollTo());
        onView(withId(R.id.value_editText)).perform(ViewActions.clearText());
        onView(withId(R.id.value_editText)).perform(ViewActions.typeText(String.valueOf(estimatedValue)));
        // update item
        onView(withId(R.id.small_save_button)).perform(click());
        // check if the displayed values are correct
        onView(withText(name)).check(matches(isDisplayed()));
        onView(withText(StringFormatter.getMonetaryString(estimatedValue))).check(matches(isDisplayed()));
    }

    /**
     * This test checks if an item can be deleted
     */
    @Test
    public void testDeleteItemActivity() {
        // start with a fresh database
        InventoryDBTest.clearInventoryDB();
        // click on addItem
        onView(withId(R.id.add_button)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(name));
        onView(withId(R.id.value_editText)).perform(ViewActions.typeText(String.valueOf(estimatedValue)));
        // add item
        onView(withId(R.id.small_save_button)).perform(click());
        // open the newly added item
        onData(is(instanceOf(Item.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        // delete the item
        onView(withId((R.id.delete_item_button))).perform(click());
        onView(withText("CONFIRM")).perform(click());
        // check that item no longer exists
        onView(withText(name)).check(doesNotExist());
    }

    @Test
    public void testBackButton() {
        // click on addItem
        onView(withId(R.id.add_button)).perform(click());
        // click on backButton
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.inventory_fragment)).check(matches(isDisplayed()));
    }

    /**
     * This test checks if a user can create an item with multiple tags
     */
    @Test
    public void testAddItemWithTags() {
        // start with a fresh database
        setup();

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Verify that the newly added item is displayed with its name and estimated value.
        onView(withText(name)).check(matches(isDisplayed()));
        onView(withText(StringFormatter.getMonetaryString(estimatedValue))).check(matches(isDisplayed()));

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Verify that the selected tags are associated with the item.
        onView(withText(name)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        onView(allOf(
                withId(R.id.tag_dropdown),
                withText(containsString("Tag1")),
                withText(containsString("Tag2"))
        )).check(matches(isDisplayed()));
    }

    /**
     * This test checks if a user can edit an item with multiple tags
     */
    @Test
    public void testEditItemWithTags() {
        // start with a fresh database
        setup();

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Edit the item
        onView(withText(name)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        onView(withId(R.id.tag_dropdown)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Select one or more tags from the dropdown.
        onView(withText("Tag3")).perform(click());

        // Close the dropdown.
        onView(withText("OK")).perform(click());

        // Save the updated item.
        onView(withId(R.id.small_save_button)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Enter the Item View
        onView(withText(name)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        onView(allOf(
                withId(R.id.tag_dropdown),
                withText(containsString("Tag1")),
                withText(containsString("Tag2")),
                withText(containsString("Tag3"))
        )).check(matches(isDisplayed()));
    }

    /**
     * This tests if the total sum is displayed correctly
     */
    @Test
    public void testTotalEstimatedValue() {
        setup();

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        double testing_sum = estimatedValue + estimatedValue2;
        String expectedTotalSum = String.format("Total: "+StringFormatter.getMonetaryString(testing_sum));

        // Check if the total estimated value is displayed correctly
        onView(withId(R.id.total_sum)).check(matches(withText(expectedTotalSum)));

    }

    @Test
    public void testTagImageViewVisibility() {
        // start with a fresh database
        setup();

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Setup for 2 tags
        onView(withText(name2)).perform(click());

        onView(withId((R.id.delete_item_button))).perform(click());
        onView(withText("CONFIRM")).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Check if the first tag ImageView is visible
        onView(withId(R.id.tag_image)).check(matches(isDisplayed()));

        // Check if the second tag ImageView is visible
        onView(withId(R.id.tag_image2)).check(matches(isDisplayed()));

        // Setting up the next case 1 tag
        onView(withText(name)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        onView(withId(R.id.tag_dropdown)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Remove one of the tags
        onView(withText("Tag2")).perform(click());

        // Close the dropdown.
        onView(withText("OK")).perform(click());

        // Save the updated item.
        onView(withId(R.id.small_save_button)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Case for 1 tag
        onView(withId(R.id.tag_image)).check(matches(isDisplayed()));
        onView(withId(R.id.tag_image2)).check(matches(not(isDisplayed())));

        // Setting up the last case 0 tags
        onView(withText(name)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        onView(withId(R.id.tag_dropdown)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Remove one of the tags
        onView(withText("Tag1")).perform(click());

        // Close the dropdown.
        onView(withText("OK")).perform(click());

        // Save the updated item.
        onView(withId(R.id.small_save_button)).perform(click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Case for 0 tags
        onView(withId(R.id.tag_image)).check(matches(not(isDisplayed())));
        onView(withId(R.id.tag_image2)).check(matches(not(isDisplayed())));
    }

    /**
     * Creates items for testing, assuming in inventory fragment
     * @param itemName
     */
    public void createItemSetup(String itemName) {
        // Click on the "Add" button to add a new item
        onView(withId(R.id.add_button)).perform(click());

        // Fill in the item information.
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(itemName));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(estimatedValue2)));

        // Add the item.
        onView(withId(R.id.small_save_button)).perform(click());

        try {
            Thread.sleep(maxDelay/2);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }
    }

    /**
     * Test for deleting multiple items
     */
    @Test
    public void testDeleteSelectedItems() {
        setup();

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Testing for deletion of all items with multi-select
        // Perform a click on the long-clickable item
        Espresso.onView(withText(name)).perform(ViewActions.longClick());

        // Perform clicks on other items to simulate selection
        onView(withText(name2)).perform(click());

        // Click the delete button
        Espresso.onView(ViewMatchers.withId(R.id.inventory_delete_button)).perform(ViewActions.click());

        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        // Check if the items are deleted
        onView(withText(name)).check(doesNotExist());
        onView(withText(name2)).check(doesNotExist());

        // Testing for deletion of one item in the list with several other items
        createItemSetup("item1");
        createItemSetup("item2");
        createItemSetup("item3");
        createItemSetup("item4");

        // Clicking on long clickable item
        Espresso.onView(withText("item4")).perform(ViewActions.longClick());

        // Clicks delete button
        Espresso.onView(ViewMatchers.withId(R.id.inventory_delete_button)).perform(ViewActions.click());

        // Checks if item is deleted
        onView(withText("item4")).check(doesNotExist());
        onView(withText("item1")).check(matches(isDisplayed()));
        onView(withText("item2")).check(matches(isDisplayed()));
        onView(withText("item3")).check(matches(isDisplayed()));

        // Clicking on long clickable item
        Espresso.onView(withText("item3")).perform(ViewActions.longClick());

        // Perform clicks on other items to simulate selection
        onView(withText("item2")).perform(click());

        // Clicks delete button
        Espresso.onView(ViewMatchers.withId(R.id.inventory_delete_button)).perform(ViewActions.click());

        // Checks if item is deleted
        onView(withText("item3")).check(doesNotExist());
        onView(withText("item2")).check(doesNotExist());
        onView(withText("item1")).check(matches(isDisplayed()));
    }

//    /**
//     * Testing Selection of items and Canceling
//     */
//    @Test
//    public void testSelectItemsAndCancel() {
//        clearDBs();
//
//        // Setting up the items
//        createItemSetup("item1");
//        createItemSetup("item2");
//        createItemSetup("item3");
//
//        // Perform a click on the first item to enable multi-selection
//        Espresso.onView(withText("item1")).perform(ViewActions.longClick());
//
//        // Perform clicks on other items to simulate selection
//        onView(withText("item2")).perform(click());
//        onView(withText("item3")).perform(click());
//
//        // TODO: do an assertion to check all items are selected
//
//        // Deselect an itme
//        onView(withText("item3")).perform(click());
//
//        // TODO: do an assertion checking item3 is deslected while other items are selected
//
//        // Click the cancel button
//        Espresso.onView(ViewMatchers.withId(R.id.inventory_cancel_button)).perform(ViewActions.click());
//
//        // TODO:
//        // Check if selection is cleared after clicking cancel
//
//    }
}
