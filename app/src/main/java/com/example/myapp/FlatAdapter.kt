package com.example.myapp

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

 class FlatAdapter: BaseAdapter() {


    private var STRING_RUB: String = " руб."

    private var STRING_M2: String = " кв.м."

    private val dataset: MutableList<Flat> = mutableListOf()

  // var latinit res:Resources

    private class FlatViewHolder(itemView: View){
        val street: TextView = itemView.findViewById(R.id.textViewStreet)
        val area: TextView = itemView.findViewById(R.id.textViewArea)
        val price: TextView = itemView.findViewById(R.id.textViewPrice)
    }

    fun setFlats(flats: List<Flat>){
        dataset.apply {
            clear()
            addAll(flats)
        }
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?:LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false).also {
            it.tag = FlatViewHolder(it)
        }
        val holder = view.tag as FlatViewHolder

        val flat = getItem(position)
        if (flat != null) {
            holder.street.text = flat.street
            holder.area.text = flat.area.toString() //+ resources.getString(R.string.rub)
            holder.price.text = flat.price.toString() + STRING_RUB
        } else {
            holder.street.text = ""
            holder.area.text = ""
            holder.price.text = ""
        }

        return  view
    }

    override fun getItem(position: Int): Flat? {
        return if (position >= 0 && position < dataset.size) {
            dataset[position]
        } else {
            null
        }
    }

    override fun getItemId(position: Int): Long = dataset[position].id

    override fun getCount(): Int  = dataset.size
}