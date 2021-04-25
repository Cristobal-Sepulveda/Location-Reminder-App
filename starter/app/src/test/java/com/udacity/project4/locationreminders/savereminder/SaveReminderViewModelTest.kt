package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.ui.reminderListFragment.ReminderDataItem
import com.udacity.project4.ui.reminderListFragment.RemindersListViewModel
import com.udacity.project4.ui.saveReminderFragment.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.stopKoin

/**
 * To test LiveData, i should consider 2 things, one, InstantTaskExecutorRule() and
 * make sure that the LiveData is observed. if i test liveData, i should use the INSTANTTASKEXECUTORRULE()
 *     //JUnit rules are classes that allow you to define some code that runs before and after each
//test runs. This rule here runs all architecture components related background jobs in the
//same thread. Remember to add the gradle dependency :
// testImplementation "androidx.arch.core:core-testing:$archTestingVersion"
 */

//TODO: provide testing to the SaveReminderView and its live data objects
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest
{

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     *Class that allow me to use a boilerplate_code when i need it, just calling the class.
     */

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    // Subject under test
    private lateinit var viewModel: SaveReminderViewModel
    // TestDouble
    private lateinit var fakeDataSource: FakeDataSource

    @Before
    fun setupViewModel(){
        fakeDataSource = FakeDataSource()
        viewModel =
            SaveReminderViewModel(
                ApplicationProvider.getApplicationContext(),
                fakeDataSource)
    }

    @Test
    fun onClear_isWorking(){
        //Given
        viewModel.reminderTitle.value = "asd"
        viewModel.reminderDescription.value = "asd"
        viewModel.reminderSelectedLocationStr.value = "asd"
        viewModel.selectedPOI.value = null
        viewModel.latitude.value = 12.1
        viewModel.longitude.value = 11.1
        //WHEN
        viewModel.onClear()
        //THEN
        assertThat(viewModel.reminderTitle.value ,`is` (nullValue()))
        assertThat(viewModel.reminderDescription.value,`is`(nullValue()))
        assertThat(viewModel.reminderSelectedLocationStr.value,`is`(nullValue()))
        assertThat(viewModel.selectedPOI.value,`is`(nullValue()))
        assertThat(viewModel.latitude.value,`is`(nullValue()))
        assertThat(viewModel.longitude.value,`is`(nullValue()))
        stopKoin()
    }

    @Test
    fun validateEnteredData_isWorking(){
        //WHEN
        val reminderDataItem1 = ReminderDataItem(
            "title",
            "description",
            "location",
            10.0,
            10.0
        )
        val reminderDataItem2 = ReminderDataItem(
            null,
            "",
            "location",
            10.0,
            10.0
        )
        val reminderDataItem3 = ReminderDataItem(
            "title",
            "",
            null,
            10.0,
            10.0
        )

        //THEN
        assertThat(viewModel.validateEnteredData(reminderDataItem1), `is` (true))
        assertThat(viewModel.validateEnteredData(reminderDataItem2), `is` (false))
        assertThat(viewModel.validateEnteredData(reminderDataItem3), `is` (false))
        stopKoin()
    }

    @Test
    fun saveReminder_isWorking() = mainCoroutineRule.runBlockingTest{
        //GIVEN - a reminder to save
        val reminder = ReminderDataItem("Title",
            "Description",
            "Loc",
            10.0,
            10.0)

        mainCoroutineRule.pauseDispatcher()

        //WHEN - saving reminder
        viewModel.saveReminder(reminder)

        //THEN - show loading snackbar
        assertThat(viewModel.showLoading.value, `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.value, `is`(false))
    }
}