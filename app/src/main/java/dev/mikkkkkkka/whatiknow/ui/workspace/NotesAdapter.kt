package dev.mikkkkkkka.whatiknow.ui.workspace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.mikkkkkkka.whatiknow.R

class NotesAdapter(
    private val onClick: (noteId: String) -> Unit
) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private val noteIdNames = mutableListOf<Pair<String, String>>()

    fun submitList(list: List<Pair<String, String>>) {
        noteIdNames.clear()
        noteIdNames.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(noteIdNames[position], onClick)
    }

    override fun getItemCount(): Int = noteIdNames.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.noteTitle)

        fun bind(noteIdName: Pair<String, String>, onClick: (String) -> Unit) {
            title.text = noteIdName.second
            itemView.setOnClickListener { onClick(noteIdName.first) }
        }
    }
}
