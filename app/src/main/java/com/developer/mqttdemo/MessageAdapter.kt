package com.developer.mqttdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developer.mqttdemo.data.Message
import com.developer.mqttdemo.databinding.MsgItemLayoutBinding

class MessageAdapter : ListAdapter<Message, MessageAdapter.MsgViewHolder>(diffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgViewHolder {
        return MsgViewHolder(binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.msg_item_layout,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MsgViewHolder(
        private val binding: MsgItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.msg = item
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallBack = object: DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.ID == newItem.ID
            }

        }
    }
}