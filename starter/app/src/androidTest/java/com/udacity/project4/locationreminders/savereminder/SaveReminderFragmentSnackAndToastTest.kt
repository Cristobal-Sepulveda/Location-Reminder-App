package com.udacity.project4.locationreminders.savereminder

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.RemindersActivity
import com.udacity.project4.ui.saveReminderFragment.SaveReminderFragment
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class SaveReminderFragmentSnackAndToastTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val activityRule: ActivityTestRule<RemindersActivity> =
        ActivityTestRule(RemindersActivity::class.java)

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Test
    fun snackbars_areWorking() {
        // Given
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)

        // When
        onView(withId(R.id.saveReminder)).perform(click())

        // Then
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("Please enter title")))
        Thread.sleep(3000)
        onView(withId(R.id.reminderTitle)).perform(typeText("title"))
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("Please select location")))
    }

    @Test
    fun toast_isWorking()= mainCoroutineRule.runBlockingTest  {
        // Given
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
        val navController = mock(NavController::class.java)

        // When
        scenario.onFragment {
            it.view?.let { view -> Navigation.setViewNavController(view, navController) }
        }
        onView(withId(R.id.reminderTitle)).perform(typeText("title"))
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder)).perform(click())
        Thread.sleep(3000)

        // Then
        onView(withText(R.string.reminder_saved))
                .inRoot(withDecorView(not(activityRule.activity
                        .window.decorView
                ))).check(matches(isDisplayed()))
    }
}