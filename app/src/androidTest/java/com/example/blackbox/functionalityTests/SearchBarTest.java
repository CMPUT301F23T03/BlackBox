package com.example.blackbox.functionalityTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.SearchView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.blackbox.DBTests.InventoryDBTest;
import com.example.blackbox.DBTests.TagDBTest;
import com.example.blackbox.MainActivity;
import com.example.blackbox.R;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SearchBarTest {
    private void clearDBs(){
        // clear inventory first to avoid errors
        InventoryDBTest.clearInventoryDB();
        TagDBTest.clearTagDB();
    }

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * A method which sets up items and tags to be sorted
     */
    @Before
    public void setup() {
        clearDBs();
        addTestTags();
        addTestItem1();
        addTestItem2();
        addTestItem3();
    }

    public void addTestTags() {
        // Switch to the tag fragment
        TagFunctionalityTest.navigateToTags();
        // Click on the "Add" button to add a new tag.
        onView(ViewMatchers.withId(R.id.add_tag_button)).perform(click());

        // Create and add the first tag
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Tag1"));
        onView(withId(R.id.desc_editText)).perform(ViewActions.replaceText("Tag1 Description"));

        // Select a color for the first tag (e.g., Orange)
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Orange")).perform(click());

        // Click the "Add" button to add the first tag
        onView(withId(R.id.small_save_button)).perform(click());

        // Click on the "Add" button to add a new tag.
        onView(withId(R.id.add_tag_button)).perform(click());

        // Create and add the second tag
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Tag2"));
        onView(withId(R.id.desc_editText)).perform(ViewActions.replaceText("Tag2 Description"));

        // Select a color for the second tag (e.g., Blue)
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Blue")).perform(click());

        // Click the "Add" button to add the second tag
        onView(withId(R.id.small_save_button)).perform(click());

        // Click on the "Add" button to add a new tag.
        onView(withId(R.id.add_tag_button)).perform(click());

        // Create and add the third tag
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Tag3"));
        onView(withId(R.id.desc_editText)).perform(ViewActions.replaceText("Tag3 Description"));

        // Select a color for the third tag (e.g., Red)
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Red")).perform(click());

        // Click the "Add" button to add the third tag
        onView(withId(R.id.small_save_button)).perform(click());

        // Click on the "Add" button to add a new tag.
        onView(withId(R.id.add_tag_button)).perform(click());

        // Create and add the forth tag
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Antique"));
        onView(withId(R.id.desc_editText)).perform(ViewActions.replaceText("Items from olden times"));

        // Select a color for the forth tag (e.g., Purple)
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Purple")).perform(click());

        // Click the "Add" button to add the forth tag
        onView(withId(R.id.small_save_button)).perform(click());

        // Switch back to the item fragment screen
        TagFunctionalityTest.navigateFromTagsToItems();
    }

    /**
     * Adds item 1
     */
    public void addTestItem1() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Item 1"));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(150)));
        onView(withId(R.id.make_editText)).perform(ViewActions.replaceText("Toyota"));
        onView(withId(R.id.desc_editText)).perform(scrollTo());
        onView(withId(R.id.desc_editText)).perform(replaceText("Nice Car"));
        onView(withId(R.id.tag_dropdown)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Tag1")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.small_save_button)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds item 2
     */
    public void addTestItem2() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Item 2"));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(100)));
        onView(withId(R.id.make_editText)).perform(ViewActions.replaceText("Audi"));
        onView(withId(R.id.desc_editText)).perform(scrollTo());
        onView(withId(R.id.desc_editText)).perform(replaceText("Nicer Car"));
        onView(withId(R.id.tag_dropdown)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Tag2")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.small_save_button)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds item 3
     */
    public void addTestItem3() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Item 3"));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(200)));
        onView(withId(R.id.make_editText)).perform(ViewActions.replaceText("Mercedes"));
        onView(withId(R.id.desc_editText)).perform(scrollTo());
        onView(withId(R.id.desc_editText)).perform(replaceText("Nicest Car"));
        onView(withId(R.id.tag_dropdown)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Antique")).perform(click());
        onView(withText("Tag3")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.small_save_button)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public ViewAction typeSearchView(String text){
        return new ViewAction() {
            @Override
            public String getDescription() {
                return "Change search query";
            }

            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(SearchView.class);
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((SearchView) view).setQuery(text,true);
            }
        };
    }
    @Test
    public void testSearchBar(){
        String query = "nicest, car";
        onView(withId(R.id.searchView)).perform(typeSearchView(query));

        onView(withText("Item 3")).check(matches(isDisplayed()));
        onView(withText("Item 1")).check(doesNotExist());
        onView(withText("Item 1")).check(doesNotExist());

        //onView(withId(R.id.searchView)).perform(SearchVie)
    }
}
