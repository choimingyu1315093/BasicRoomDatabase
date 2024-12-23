package com.example.basicroomdatabase

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basicroomdatabase.db.Subscriber
import com.example.basicroomdatabase.db.SubscriberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.E

class SubscriberViewModel(private val repository: SubscriberRepository): ViewModel() {
    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    val inputName = MutableLiveData<String>()
    val inputEmail = MutableLiveData<String>()

    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private var statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun saveOrUpdate(){
        if(inputName.value == null){
            statusMessage.value = Event("Enter subscriber's name")
        }else if(inputEmail.value == null){
            statusMessage.value = Event("Enter subscriber's email")
        }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()){
            statusMessage.value = Event("Enter a correct email address")
        }else {
            if(isUpdateOrDelete){
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            }else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(Subscriber(0, name, email))
                inputName.value = ""
                inputEmail.value = ""
            }
        }
    }

    fun clearAllOrDelete(){
        if(isUpdateOrDelete){
            delete(subscriberToUpdateOrDelete)
        }else {
            clearAll()
        }
    }

    private fun insert(subscriber: Subscriber){
        viewModelScope.launch(Dispatchers.IO) {
            val newRowId = repository.insert(subscriber)
            withContext(Dispatchers.Main){
                if(newRowId > -1){
                    statusMessage.value = Event("Subscriber inserted successfully")
                }else {
                    statusMessage.value = Event("Error Occurred")
                }
            }
        }
    }

    private fun update(subscriber: Subscriber){
        viewModelScope.launch(Dispatchers.IO) {
            val numberOfRows =repository.update(subscriber)
            withContext(Dispatchers.Main){
                if(numberOfRows > 0){
                    inputName.value = ""
                    inputEmail.value = ""
                    isUpdateOrDelete = false
                    saveOrUpdateButtonText.value = "Save"
                    clearAllOrDeleteButtonText.value = "Clear All"
                    statusMessage.value = Event("Subscriber updated successfully")
                }else {
                    statusMessage.value = Event("Error Occurred")
                }
            }
        }
    }

    private fun delete(subscriber: Subscriber){
        viewModelScope.launch(Dispatchers.IO) {
            val numberOfRowsDeleted = repository.delete(subscriber)
            withContext(Dispatchers.Main){
                if(numberOfRowsDeleted > 0){
                    inputName.value = ""
                    inputEmail.value = ""
                    isUpdateOrDelete = false
                    saveOrUpdateButtonText.value = "Save"
                    clearAllOrDeleteButtonText.value = "Clear All"
                    statusMessage.value = Event("Subscriber deleted successfully")
                }else{
                    statusMessage.value = Event("Error Occurred")
                }
            }
        }
    }

    private fun clearAll(){
        viewModelScope.launch(Dispatchers.IO) {
            val numberOfRowsDeleted = repository.deleteAll()
            withContext(Dispatchers.Main){
                if(numberOfRowsDeleted > 0){
                    statusMessage.value = Event("All subscribers cleared successfully")
                }else {
                    statusMessage.value = Event("Error Occurred")
                }
            }
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber){
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }
}