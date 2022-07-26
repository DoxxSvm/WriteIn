package com.doxx.writein.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doxx.writein.databinding.NoteItemBinding
import com.doxx.writein.models.NoteResponse
import com.doxx.writein.utils.AES
import okhttp3.internal.notify

class NoteAdapter(private val onNoteClicked: (NoteResponse) -> Unit) :
    ListAdapter<NoteResponse, NoteAdapter.NoteViewHolder>(ComparatorDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        note?.let {
            holder.bind(it)
        }
    }

    inner class NoteViewHolder(private val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: NoteResponse) {
            var title = note.title
            var desc = note.description
            title= getSecretKey()?.let { AES.decrypt(title, it) }.toString()
            desc= getSecretKey()?.let { AES.decrypt(desc, it) }.toString()

            binding.title.text = title
            binding.desc.text = desc
            binding.root.setOnClickListener {
                onNoteClicked(note)
            }
        }
        private fun getSecretKey(): String? {
            val sharedPreference =  itemView.context.getSharedPreferences("DOXX", Context.MODE_PRIVATE)
            return sharedPreference.getString("SECRET_KEY",null)
        }


    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<NoteResponse>() {
        override fun areItemsTheSame(oldItem: NoteResponse, newItem: NoteResponse): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: NoteResponse, newItem: NoteResponse): Boolean {
            return oldItem == newItem
        }
    }
}