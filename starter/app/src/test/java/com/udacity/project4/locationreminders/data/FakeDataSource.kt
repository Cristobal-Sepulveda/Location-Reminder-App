package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import java.lang.Exception

//TODO: Create a fake data source to act as a double to the real data source
// Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()):ReminderDataSource {

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }
    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        reminders?.let{
            return Result.Success(ArrayList(it)) }
        return Result.Error("Test_Problem")
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        var i = 0
        reminders?.let {
            while (i < it.size) {
                if (it[i].id == id) {
                    return Result.Success(it[i])
                }
                i++
            }
        }
        return Result.Error("No reminder(s) found")
    }
}