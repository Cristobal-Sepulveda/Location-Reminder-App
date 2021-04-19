package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.ui.reminderListFragment.RemindersListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    /**
     * To test LiveData, i should consider 2 things, one, InstantTaskExecutorRule() and
     * make sure that the LiveData is observed
     */

    //JUnit rules are classes that allow you to define some code that runs before and after each
    //test runs. This rule here runs all architecture components related background jobs in the
    //same thread. Remember to add the gradle dependency :
    // testImplementation "androidx.arch.core:core-testing:$archTestingVersion"
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()




    //TODO: provide testing to the RemindersListViewModel and its live data objects
    @Test
    fun loadReminders_isWorking(){
        val remindersListViewModel = RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),

                )
    }
}