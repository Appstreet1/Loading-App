package com.example.android.project3

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {


     val buttonState = MutableLiveData<ButtonState>()


}