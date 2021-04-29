package com.udacity.project4

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//TODO: Create a fake data source to act as a double to the real data source
// Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeAndroidDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()): ReminderDataSource {

    private var setReturnError = false

    fun shouldReturnError(value: Boolean) {
        setReturnError = value
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }
    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (setReturnError || reminders == null) {
            Result.Error("Test exception")
        }else
            Result.Success(ArrayList(reminders))
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