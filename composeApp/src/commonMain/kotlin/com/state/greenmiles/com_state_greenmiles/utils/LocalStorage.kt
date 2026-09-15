package com.state.greenmiles.com_state_greenmiles.utils

import com.russhwolf.settings.Settings
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.CarDetailsDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalStorage(
    private val settings: Settings
) {

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_SAVED_CARS_LIST = "saved_cars_list"
    }

   fun saveToken(token: String) {
        settings.putString(KEY_TOKEN, token)
    }

    fun getToken(): String? {
        return if (settings.hasKey(KEY_TOKEN)) {
            settings.getString(KEY_TOKEN, "")
        } else null
    }
    fun saveUserId(userId: String) {
        settings.putString(KEY_USER_ID, userId)
    }
    fun getUserId(): String? {
        return settings.getStringOrNull(KEY_USER_ID)
    }



    fun deleteToken() {
        settings.remove(KEY_TOKEN)
    }

    fun getSavedCars(): List<CarDetailsDto> {
        val jsonString = settings.getString(KEY_SAVED_CARS_LIST, "")
        if (jsonString.isBlank()) return emptyList()
        return try {
            Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            println("❌ LocalStorage | Error decoding saved cars: ${e.message}")
            emptyList()
        }
    }

    fun saveCarToList(car: CarDetailsDto) {
        val currentList = getSavedCars().toMutableList()
        val index = currentList.indexOfFirst { it.licensePlate == car.licensePlate }
        if (index != -1) {
            currentList[index] = car
        } else {
            currentList.add(car)
        }
        val newJsonString = Json.encodeToString(currentList)
        println("💾 LocalStorage | Saving cars list: $newJsonString")
        settings.putString(KEY_SAVED_CARS_LIST, newJsonString)
    }

    fun clearAll() {
        settings.clear()
    }
}
