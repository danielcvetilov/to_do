package com.dnl.to_do;

import com.dnl.to_do.ui.on_boarding.OnBoardingActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class OnBoardingTest {
    @Rule
    public final ActivityTestRule<OnBoardingActivity> onBoardingActivityActivityTestRule = new ActivityTestRule<>(OnBoardingActivity.class);

    @Test
    public void TestOnBoarding() {
        onView(withId(R.id.content_vp)).perform(swipeLeft());
        onView(withId(R.id.content_vp)).perform(swipeLeft());

        onView(withId(R.id.get_started_btn)).check(matches(isDisplayed()));

        onView(withId(R.id.get_started_btn)).perform(click());

        assertTrue(onBoardingActivityActivityTestRule.getActivity().isFinishing());
    }
}
