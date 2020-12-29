package com.example.android.project3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.content_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        initViewModel()
//
        animationButton.setOnClickListener {
            animationButton.buttonState = ButtonState.Clicked
            loadingCircle.buttonState = ButtonState.Clicked
        }
//
//        observeButtonState()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.buttonState.value = animationButton.buttonState
    }

    private fun observeButtonState() {
        viewModel.buttonState.observe(this, { buttonState ->
            when (buttonState) {
                ButtonState.Clicked -> Toast.makeText(this, "HIIIIIIII", Toast.LENGTH_LONG).show()
                ButtonState.Loading -> Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show()
                ButtonState.Completed -> Toast.makeText(this, "completed", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}