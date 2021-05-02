package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.ui.reminderListFragment.RemindersListViewModel
import com.udacity.project4.ui.saveReminderFragment.SaveReminderFragment
import com.udacity.project4.ui.saveReminderFragment.SaveReminderViewModel
import com.udacity.project4.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class SaveReminderFragmentSnackAndToastTest: AutoCloseKoinTest() {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val activityRule: ActivityTestRule<RemindersActivity> =
        ActivityTestRule(RemindersActivity::class.java)

    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test
     * our code. at this step we will initialize Koin related code to be able to use it in out
     * testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = ApplicationProvider.getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Test
    fun snackbars_areWorking()=mainCoroutineRule.runBlockingTest {
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
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)

        // When
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(typeText("title"))
        closeSoftKeyboard()
        onView(withId(R.id.reminderDescription)).perform(typeText("description"))
        closeSoftKeyboard()
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.map)).perform(longClick())
        Thread.sleep(2000)
        /**
         * Method that clicks on a view, in the given coordinates, the coordinates start at
         * the top-left corner in 0,0
         */
        onView(withId(R.id.map)).perform(clickOnCenter())
        Thread.sleep(2000)
        onView(withId(R.id.savePOILatLgn_button)).perform(click())
        onView(withId(R.id.saveReminder))
        Thread.sleep(2000)

        // Then
        onView(withText(R.string.reminder_saved))
                .inRoot(withDecorView(not(activityRule.activity
                        .window.decorView
                ))).check(matches(isDisplayed()))
        scenario.close()
    }
}