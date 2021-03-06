package com.example.myapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class FlatAdapter : BaseAdapter() {

    private val dataset: MutableList<Flat> = mutableListOf()

    private class FlatViewHolder(itemView: View) {
        val street: TextView = itemView.findViewById(R.id.textViewStreet)
        val area: TextView = itemView.findViewById(R.id.textViewArea)
        val price: TextView = itemView.findViewById(R.id.textViewPrice)
    }

    fun setFlats(flats: List<Flat>) {
        dataset.apply {
            clear()
            addAll(flats)
        }
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
                ?: LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false).also {
                    it.tag = FlatViewHolder(it)
                }
        val holder = view.tag as FlatViewHolder

        val flat = getItem(position)
        if (flat != null) {

            val context: View? = view
            holder.street.text = flat.street
            holder.area.text = context?.resources?.getString(R.string.m2, flat.area)
            holder.price.text = context?.resources?.getString(R.string.rub, flat.price)
        } else {
            holder.street.text = ""
            holder.area.text = ""
            holder.price.text = ""
        }

        return view
    }

    override fun getItem(position: Int): Flat? {
        return if (position >= 0 && position < dataset.size) {
            dataset[position]
        } else {
            null
        }
    }

    override fun getItemId(position: Int): Long = dataset[position].id

    override fun getCount(): Int = dataset.size
}