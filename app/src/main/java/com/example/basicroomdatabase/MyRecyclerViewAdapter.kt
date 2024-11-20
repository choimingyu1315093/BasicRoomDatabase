package com.example.basicroomdatabase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.basicroomdatabase.databinding.ListItemBinding
import com.example.basicroomdatabase.db.Subscriber

class MyRecyclerViewAdapter(private val onClicked: (subscriber: Subscriber) -> Unit): RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

    private val subscribersList = ArrayList<Subscriber>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subscribersList[position], onClicked)
    }

    fun setList(subscribers: List<Subscriber>){
        subscribersList.clear()
        subscribersList.addAll(subscribers)
    }

    override fun getItemCount(): Int {
        return subscribersList.size
    }

    inner class ViewHolder(private val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(subscriber: Subscriber, onClicked: (subscriber: Subscriber) -> Unit){
            binding.nameTextView.text = subscriber.name
            binding.emailTextView.text = subscriber.email
            binding.listItemLayout.setOnClickListener {
                onClicked(subscriber)
            }
        }
    }
}