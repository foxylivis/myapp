package com.example.myapp

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.widget.TextView
import android.view.LayoutInflater


class FlatViewAdapter : BaseAdapter() {

    var flatArr = ArrayList<FlatItem>()
    var context: Context? = null

    fun FlatViewAdapter(context: Context, arr: ArrayList<FlatItem>?) {
        if (arr != null) {
            flatArr = arr
        }
        this.context = context
    }

    override fun getView(position: Int, _convertView: View?, parent: ViewGroup?): View {

        val inflater = LayoutInflater.from(context)

        lateinit var convertView: View
        convertView = inflater.inflate(R.layout.flat, parent, false)

        val headerFlat = convertView.findViewById(R.id.textViewTitle) as TextView
        val valueFlat = convertView.findViewById(R.id.textViewValue) as TextView

        headerFlat.text = flatArr[position].headerFlat
        valueFlat.text = flatArr[position].valueFlat

        return convertView
    }

    override fun getItem(position: Int): Any {
        return flatArr[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int = flatArr.size

}