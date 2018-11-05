package com.example.myapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import io.objectbox.Box
import io.objectbox.kotlin.boxFor

class FlatViewActivity: AppCompatActivity()  {

    private lateinit var textViewStreet: TextView
    private lateinit var textViewPrice: TextView
    private lateinit var textViewArea: TextView
    private lateinit var textViewCost: TextView
    private lateinit var textViewRoom: TextView
    private lateinit var textViewFloor: TextView

    private lateinit var flatBox: Box<Flat>

    private lateinit var userBox: Box<User>

    var idUser: Long=0
    var idFlat: Long=0
    private lateinit var prefUserId: SharedPreferences

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_flat)

        flatBox = ObjectBox.boxStore.boxFor()

        userBox = ObjectBox.boxStore.boxFor()

        loadUser()

        setUpViews()

    }

    private fun setUpViews() {

        textViewStreet = findViewById<TextView>(R.id.textViewStreet).apply {
        }
        textViewPrice = findViewById<TextView>(R.id.textViewPrice).apply {
        }
        textViewArea = findViewById<TextView>(R.id.textViewArea).apply {
        }
        textViewFloor = findViewById<TextView>(R.id.textViewFloor).apply {
        }
        textViewRoom = findViewById<TextView>(R.id.textViewRoom).apply {
        }
        textViewCost = findViewById<TextView>(R.id.textViewCost).apply {
        }
        val extras = intent.extras
        if (extras != null) {
            idFlat = extras.getLong("idFlat")

            val streetFlat = flatBox.query().equal(Flat_.id, idFlat).build()
                    .property(Flat_.street)
                    .findString();
            val priceFlat = flatBox.query().equal(Flat_.id, idFlat).build()
                    .property(Flat_.price)
                    .findDouble();
            val areaFlat = flatBox.query().equal(Flat_.id, idFlat).build()
                    .property(Flat_.area)
                    .findDouble();
            val floorFlat = flatBox.query().equal(Flat_.id, idFlat).build()
                    .property(Flat_.floor)
                    .findInt();
            val roomFlat = flatBox.query().equal(Flat_.id, idFlat).build()
                    .property(Flat_.room)
                    .findInt();

            textViewStreet.setText(streetFlat.toString())
            textViewPrice.setText(priceFlat.toString() + " руб.")
            textViewArea.setText(areaFlat.toString() + " кв.м.")
            textViewFloor.setText(floorFlat.toString())
            textViewRoom.setText(roomFlat.toString())

            val area = areaFlat
            val price = priceFlat
            val costFlat = price / area

            textViewCost.setText(String.format("%.2f", costFlat)+ " руб.")
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