package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeAndroidDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.ui.reminderListFragment.ReminderListFragment
import com.udacity.project4.ui.reminderListFragment.RemindersListViewModel
import com.udacity.project4.ui.saveReminderFragment.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
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
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest: AutoCloseKoinTest() {

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as FakeAndroidDataSource
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

//    TODO: test the navigation of the fragments.
    fun navigation_isWorkingFine(){
        // Given -
    val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
    val navController = Mockito.mock(NavController::class.java)
    scenario.onFragment {
        Navigation.setViewNavController(it.view!!,navController)}
    Thread.sleep(2000)
        // When -

        // Then -


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
        repository.saveReminder(reminder)
    }

    val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
    val navController = Mockito.mock(NavController::class.java)
    scenario.onFragment {
        Navigation.setViewNavController(it.view!!,navController)}
    Thread.sleep(2000)
    //THEN - correct title and description are displayed on reminders list

    onView(withText("Title")).check(matches(isDisplayed()))
    onView(withText("Description")).check(matches(isDisplayed()))
    onView(withText("Loc")).check(matches(isDisplayed()))
    }

//    TODO: add testing for the error messages.

}