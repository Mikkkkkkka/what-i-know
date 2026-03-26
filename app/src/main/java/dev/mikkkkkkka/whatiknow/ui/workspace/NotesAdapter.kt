package dev.mikkkkkkka.whatiknow.ui.workspace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.mikkkkkkka.whatiknow.R
import dev.mikkkkkkka.whatiknow.domain.model.NoteSummary

class NotesAdapter(
    private val onClick: (noteId: String) -> Unit
) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private val noteSummaries = mutableListOf<NoteSummary>()

    fun submitList(list: List<NoteSummary>) {
        noteSummaries.clear()
        noteSummaries.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(noteSummaries[position], onClick)
    }

    override fun getItemCount(): Int = noteSummaries.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.noteTitle)

        fun bind(noteSummary: NoteSummary, onClick: (String) -> Unit) {
            title.text = noteSummary.name
            itemView.setOnClickListener { onClick(noteSummary.id) }
        }
    }
}
