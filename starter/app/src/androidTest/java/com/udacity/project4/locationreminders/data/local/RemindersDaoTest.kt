package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

//    TODO: Add testing implementation to the RemindersDao.kt
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun settingDataBase(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java).build()
    }

    @After
    fun cleaningMemory(){
        database.close()
    }

    @Test
    fun allDaoMethods_areWorking() = mainCoroutineRule.runBlockingTest{
        //Given - One newReminder
        val reminder = ReminderDTO(
            "title",
        "description",
            "location",
            10.0,
            10.0,
        )
        //When - The we save a reminder
        database.reminderDao().saveReminder(reminder)

        //Then - the database Size will be one
        assertThat("${database.reminderDao().getReminders().size}", `is` ("1"))
        assertThat(database.reminderDao().getReminderById(reminder.id)!!.title, `is`(reminder.title))
        database.reminderDao().deleteAllReminders()
        assertThat("${database.reminderDao().getReminders().size}", `is` ("0"))
    }
}