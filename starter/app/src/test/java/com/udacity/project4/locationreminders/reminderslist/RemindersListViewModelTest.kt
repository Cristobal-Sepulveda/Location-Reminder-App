package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.ui.reminderListFragment.RemindersListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Before
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

    /**
     *Class that allow me to use a boilerplate_code when i need it, just calling the class.
     */
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    // Subject under test
    private lateinit var viewModel: RemindersListViewModel
    // TestDouble
    private lateinit var fakeDataSource: FakeDataSource

    @Before
    fun setupViewModel(){
        fakeDataSource = FakeDataSource()
        viewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(),
                fakeDataSource)
    }

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    @Test
    fun loadReminders_isWorking() = mainCoroutineRule.runBlockingTest {
        //GIVEN a reminder in the list
        val reminder1 = ReminderDTO(
            "Title1",
            "Description1",
            "location1",
            1.0,
            1.0,
            "1")
        fakeDataSource.saveReminder(reminder1)

        //WHEN
        viewModel.loadReminders()

        //THEN
        when(viewModel.remindersList.value){
            null -> assertThat(viewModel.remindersList.value!!.size
                    == 1 , `is` (false))
            else -> assertThat(viewModel.remindersList.value!!.size, `is` (1))
        }
        assertThat(viewModel.showNoData.value, `is` (false))
    }

    fun invalidateShowNoData_isWorking(){
        //WHEN
        viewModel.showNoData.value = viewModel.remindersList.value == null ||
                viewModel.remindersList.value?.isEmpty() == true
        //THEN
        assertThat(viewModel.showNoData.value, `is` (true))
    }

    fun deleteAllReminder_isWorking() = mainCoroutineRule.runBlockingTest{
        //GIVEN
        val reminder1 = ReminderDTO(
            "Title1",
            "Description1",
            "location1",
            1.0,
            1.0,
            "1")
        fakeDataSource.saveReminder(reminder1)
        //WHEN
        viewModel.deleteAllReminder()
        //THEN
        assertThat(viewModel.remindersList.value!!.isEmpty(), `is`(true))

    }
}