package com.example.basicroomdatabase

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.basicroomdatabase.databinding.ActivityMainBinding
import com.example.basicroomdatabase.db.Subscriber
import com.example.basicroomdatabase.db.SubscriberDAO
import com.example.basicroomdatabase.db.SubscriberDatabase
import com.example.basicroomdatabase.db.SubscriberRepository

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel

    companion object {
        const val TAG = "MainActivity"
    }

    lateinit var myRecyclerViewAdapter: MyRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao = SubscriberDatabase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val factory = SubscriberViewModelFactor(repository)
        subscriberViewModel = ViewModelProvider(this, factory).get(SubscriberViewModel::class.java)
        binding.myViewModel = subscriberViewModel
        binding.lifecycleOwner = this

        displaySubscribersList()

        subscriberViewModel.message.observe(this){
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun displaySubscribersList() = with(binding){
        myRecyclerViewAdapter = MyRecyclerViewAdapter(onClicked = {
            listItemClicked(it)
        })
        subscriberRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = myRecyclerViewAdapter
        }

        subscriberViewModel.subscribers.observe(this@MainActivity){
            myRecyclerViewAdapter.setList(it)
            myRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    private fun listItemClicked(subscriber: Subscriber){
        subscriberViewModel.initUpdateAndDelete(subscriber)
    }
}