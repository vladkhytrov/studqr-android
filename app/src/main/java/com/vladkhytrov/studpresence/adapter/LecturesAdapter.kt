package com.vladkhytrov.studpresence.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vladkhytrov.studpresence.R
import com.vladkhytrov.studpresence.data.Lecture

class LecturesAdapter(
    private var items: List<Lecture>,
    private val clickListener: View.OnClickListener
) : RecyclerView.Adapter<LecturesAdapter.ViewHolder>() {

    class ViewHolder(view: View, clickListener: View.OnClickListener) :
        RecyclerView.ViewHolder(view) {

        val eventName: TextView = view.findViewById(R.id.lectureName)

        init {
            view.tag = this
            view.setOnClickListener(clickListener)
        }

    }

    fun refresh(items: List<Lecture>) {
        this.items = items;
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lecture, parent, false)
        return ViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.eventName.text = items[position].name
    }

    override fun getItemCount(): Int {
        return items.size
    }

}