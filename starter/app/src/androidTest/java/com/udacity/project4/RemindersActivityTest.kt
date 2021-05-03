package com.udacity.project4

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.ui.reminderListFragment.RemindersListViewModel
import com.udacity.project4.ui.saveReminderFragment.SaveReminderFragment
import com.udacity.project4.ui.saveReminderFragment.SaveReminderViewModel
import com.udacity.project4.util.*
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
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

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest : AutoCloseKoinTest() {
// Extended Koin Test - embed autoclose @after method to close Koin after every test

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() = runBlocking {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
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

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun snackbars_areWorking(): Unit = runBlocking {
        // Given: One single fragment
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)

        // When: i try to save a reminder without data
        onView(withId(R.id.saveReminder)).perform(click())

        // Then: I check the snackbar's error text's
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.err_enter_title)))
        Thread.sleep(3000)
        onView(withId(R.id.reminderTitle)).perform(typeText("title"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.err_select_location)))
    }

    @Test
    fun toastAndSaveReminder_areWorking() = runBlocking {
        //Given: a New Activity launch
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //When: The app navigates and do some work
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(typeText("title"))
        onView(withId(R.id.reminderDescription)).perform(typeText("description"))
        onView(withId(R.id.selectLocation)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.map)).perform(ViewActions.longClick())
        /**
         * Method that clicks on a view, in the given coordinates, the coordinates start at
         * the top-left corner in 0,0
         */
        Thread.sleep(2000)
        onView(withId(R.id.map)).perform(clickOnCenter())
        Thread.sleep(2000)
        onView(withId(R.id.savePOILatLgn_button)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())
        Thread.sleep(2000)

        //Then: a toast message & a new item is displayed in the screen
        onView(withText(R.string.reminder_saved)).inRoot(isToast()).check(matches(isDisplayed()))
        onView(withId(R.id.reminderssRecyclerView))
            .check(matches(hasDescendant(withText("title"))))
        onView(withId(R.id.reminderssRecyclerView))
            .check(matches(hasDescendant(withText("description"))))
        activityScenario.close()
    }
}
