package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.udacity.project4.locationreminders.data.dto.Result

//    TODO: Add testing implementation to the RemindersLocalRepository.kt
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    /**
     * subject on testing plus an argument of the subject
     */
    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    /**
     *  here im creating a database in memory and a RemindersLocalRepository for testing porpoise
     */
    @Before
    fun settingRemindersRepository(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java).build()
        remindersLocalRepository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    /**
     * Here im cleaning the Memory that i use building the database
     */
    @After
    fun clearingRemindersRepository(){
        database.close()
    }

    @Test
    fun allRepositoryMethods_areWorking()= mainCoroutineRule.runBlockingTest {
        // Given - One new Reminder
        val reminder = ReminderDTO(
                "title",
                "description",
                "location",
                1.0,
                1.0,
        )
        val reminder2 = ReminderDTO(
                "title2",
                "description2",
                "location2",
                2.0,
                2.0,
        )
        //When - The we save a reminder
        remindersLocalRepository.saveReminder(reminder)
        remindersLocalRepository.saveReminder(reminder2)
        //Then - The repository will return all the reminders & a specific Reminder
        assertThat(
                "${
                    (remindersLocalRepository.getReminders() as
                            Result.Success<List<ReminderDTO>>).data.size
                }", `is`("2"))
        assertThat(
                (remindersLocalRepository.getReminder(reminder2.id) as
                        Result.Success<ReminderDTO>).data.id, `is`(reminder2.id))
        remindersLocalRepository.deleteAllReminders()
        assertThat(
                "${
                    (remindersLocalRepository.getReminders() as
                            Result.Success<List<ReminderDTO>>).data.size
                }", `is`("0"))

        assertThat("${(remindersLocalRepository.getReminder(reminder2.id) as 
                Result.Error).message}",
                `is` ("Reminder not found!") )

    }
}