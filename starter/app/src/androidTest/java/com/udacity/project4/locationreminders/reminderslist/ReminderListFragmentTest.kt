package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.material.R.*
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.FakeAndroidDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.ui.reminderListFragment.ReminderListFragment
import com.udacity.project4.ui.reminderListFragment.ReminderListFragmentDirections
import com.udacity.project4.ui.reminderListFragment.RemindersListViewModel
import com.udacity.project4.ui.saveReminderFragment.SaveReminderFragment
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest: AutoCloseKoinTest() {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val repository: FakeAndroidDataSource by inject()
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private val viewModel: RemindersListViewModel by inject()

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = ApplicationProvider.getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as FakeAndroidDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
            single { FakeAndroidDataSource() }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }

        runBlocking {
            repository.deleteAllReminders()
        }
    }

//    TODO: test the displayed data on the UI.
    @Test
    fun reminder_isDisplayedInUi()= mainCoroutineRule.runBlockingTest{
        // GIVEN - Creating a reminder to add it to the DB
        val reminder = ReminderDTO(
            title = "Title",
            description = "Description",
            location = "Loc",
            latitude = 0.0,
            longitude = 0.0,
        )
        //WHEN - added to the Repository
        runBlocking {
            repository.apply {
                saveReminder(reminder)
            }
        }
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)

        //THEN - correct title and description are displayed on reminders list
        onView(withText("Title")).check(matches(isDisplayed()))
        onView(withText("Description")).check(matches(isDisplayed()))
        onView(withText("Loc")).check(matches(isDisplayed()))
    }

    //    TODO: test the navigation of the fragment.
    @Test
    fun navigation_isWorking() {
        //GIVEN
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
        val navController = mock(NavController::class.java)

        //WHEN
        scenario.onFragment {
            it.view?.let { view -> Navigation.setViewNavController(view, navController) }
        }
        onView(withId(R.id.addReminderFAB)).perform(click())

        //THEN - check if it navigates to next SaveReminderFragment
        verify(navController).navigate(
                ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun noDataView_isWorking() = mainCoroutineRule.runBlockingTest{
        //When
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
        //Then
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun snackBarMessage_isWorking(){
        // Given
        repository.setReturnError(true)

        // When
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
        // Then
        onView(withId(id.snackbar_text))
                .check(matches(withText("Test exception")))
    }
}