package com.example.myapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import io.objectbox.Box
import io.objectbox.kotlin.boxFor

class FlatViewActivity: AppCompatActivity()  {

    private lateinit var listView: ListView

    private lateinit var flatBox: Box<Flat>

    private lateinit var userBox: Box<User>

    private lateinit var flatViewAdapter: FlatViewAdapter

    var flatArr = ArrayList<FlatItem>()

    var idUser: Long=0
    var idFlat: Long=0

    private lateinit var prefUserId: SharedPreferences

    private var STRING_RUB: String = " руб."

    private var STRING_M2: String = " кв.м."

    private var STRING_ADDRESS: String =  "Адрес"
    private var STRING_PRICE: String = "Цена"
    private var STRING_AREA: String =  "Площадь"
    private var STRING_FLOOR: String = "Этаж"
    private var STRING_ROOM: String =  "Количество комнат"
    private var STRING_COST: String =  "Цена за кв. м."

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_flat)

        flatBox = ObjectBox.boxStore.boxFor()

        userBox = ObjectBox.boxStore.boxFor()

        loadUser()

        addArrList()

        setUpViews()

    }


    private fun addArrList() {

        val extras = intent.extras
        if (extras != null) {
            idFlat = extras.getLong("idFlat")

            val streetFlat = flatBox.query().equal(Flat_.id, idFlat).build()
                    .property(Flat_.street)
                    .findString()
            val priceFlat = flatBox.query().equal(Flat_.id, idFlat).build()
                    .property(Flat_.price)
                    .findDouble()
            val areaFlat = flatBox.query().equal(Flat_.id, idFlat).build()
                    .property(Flat_.area)
                    .findDouble()
            val floorFlat = flatBox.query().equal(Flat_.id, idFlat).build()
                    .property(Flat_.floor)
                    .findInt()
            val roomFlat = flatBox.query().equal(Flat_.id, idFlat).build()
                    .property(Flat_.room)
                    .findInt()

            flatArr.add(FlatItem(STRING_ADDRESS, streetFlat.toString()))
            flatArr.add(FlatItem(STRING_PRICE, priceFlat.toString() + getString(R.string.rub)))
            flatArr.add(FlatItem(STRING_AREA, areaFlat.toString() + STRING_M2))
            flatArr.add(FlatItem(STRING_FLOOR, floorFlat.toString()))
            flatArr.add(FlatItem(STRING_ROOM, roomFlat.toString()))

            val area = areaFlat
            val price = priceFlat
            val costFlat = price / area
            flatArr.add(FlatItem(STRING_COST, String.format("%.2f", costFlat)+ STRING_RUB))
        }
    }

    private fun setUpViews() {

        flatViewAdapter = FlatViewAdapter()
        flatViewAdapter.FlatViewAdapter(this, flatArr)

        listView = findViewById<ListView>(R.id.listViewFlat).apply {
            adapter = flatViewAdapter
              }
    }

    private fun loadUser(){
        prefUserId = getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
        idUser = prefUserId.getLong("idUser", 0)
    }

    private fun goEdit(){
        val intent = Intent(this, FlatEditActivity::class.java)
        intent.putExtra("idFlat", idFlat)
        finish()
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        when (id) {
            R.id.edit -> {
                goEdit()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }


}