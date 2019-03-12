package com.dnl.to_do;

import com.dnl.to_do.ui.task_groups.TaskGroupsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.dnl.to_do.RecyclerViewMatchers.hasItem;
import static org.hamcrest.Matchers.endsWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TaskGroupCreateRecordTests {
    @Rule
    public final ActivityTestRule<TaskGroupsActivity> activityActivityTestRule = new ActivityTestRule<>(TaskGroupsActivity.class);

    @Test
    public void TaskGroupNewRecordCreation() {
        onView(withId(R.id.create_record_fab)).perform(click());

        onView(withText(R.string.text_input_dialog_enter_name)).check(matches(isDisplayed()));

        onView(withClassName(endsWith("EditText"))).perform(typeTextIntoFocusedView("New Group"));
        onView(withClassName(endsWith("EditText"))).perform(pressImeActionButton());

        onView(withText(R.string.text_input_dialog_ok)).perform(click());

        onView(withId(R.id.recycler_view)).check(matches(hasItem(hasDescendant(withText("New Group")))));
    }
}