package com.example.blackbox.functionalityTests;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.blackbox.MainActivity;
import com.example.blackbox.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FilterDialogTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testFilterDialog(){
        Espresso.onView(ViewMatchers.withId(R.id.filter_button)).perform(ViewActions.click());
    }

}
