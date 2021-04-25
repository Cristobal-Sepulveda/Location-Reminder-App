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

    /**
     * Subject on testing
     */
    private lateinit var database: RemindersDatabase

    /**
     *  here im creating a database in memory for testing porpoise
     */
    @Before
    fun settingDataBase(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java).build()
    }

    /**
     * Here im cleaning the Memory that i use building the database
     */
    @After
    fun cleaningMemory(){
        database.close()
    }

    @Test
    fun allDaoMethods_areWorking() = mainCoroutineRule.runBlockingTest{
        //Given - One new Reminder
        val reminder = ReminderDTO(
            "title",
        "description",
            "location",
            10.0,
            10.0,
        )
        //When - The we save a reminder
        database.reminderDao().saveReminder(reminder)

        //Then - the database will return lists, a specific Reminder & delete all his Reminders
        assertThat("${database.reminderDao().getReminders().size}", `is` ("1"))
        assertThat(database.reminderDao().getReminderById(reminder.id)!!.title, `is`(reminder.title))
        database.reminderDao().deleteAllReminders()
        assertThat("${database.reminderDao().getReminders().size}", `is` ("0"))
    }
}