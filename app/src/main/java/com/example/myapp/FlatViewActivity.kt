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

class FlatViewActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    private lateinit var flatBox: Box<Flat>

    private lateinit var userBox: Box<User>

    private lateinit var flatViewAdapter: FlatViewAdapter

    var flatArr = ArrayList<FlatItem>()

    var idUser: Long = 0
    var idFlat: Long = 0

    private lateinit var prefUserId: SharedPreferences

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

            flatArr.add(FlatItem(getString(R.string.string_address), streetFlat.toString()))
            flatArr.add(FlatItem(getString(R.string.string_price), getString(R.string.rub, priceFlat)))
            flatArr.add(FlatItem(getString(R.string.string_area), getString(R.string.m2, areaFlat)))
            flatArr.add(FlatItem(getString(R.string.string_floor), floorFlat.toString()))
            flatArr.add(FlatItem(getString(R.string.string_room), roomFlat.toString()))

            val costFlat = priceFlat / areaFlat
            flatArr.add(FlatItem(getString(R.string.string_cost),   getString(R.string.rub, costFlat)))

        }
    }

    private fun setUpViews() {

        flatViewAdapter = FlatViewAdapter()
        flatViewAdapter.FlatViewAdapter(this, flatArr)

        listView = findViewById<ListView>(R.id.listViewFlat).apply {
            adapter = flatViewAdapter
        }

        this.title = getString(R.string.title_view_flat)
    }

    private fun loadUser() {
        prefUserId = getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
        idUser = prefUserId.getLong("idUser", 0)
    }

    private fun goEdit() {
        val intent = Intent(this, FlatEditActivity::class.java)
        intent.putExtra("idFlat", idFlat)
        finish()
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        return when (id) {
            R.id.edit -> {
                goEdit()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


}