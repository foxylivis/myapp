package com.example.myapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import io.objectbox.Box
import io.objectbox.kotlin.boxFor

class FlatEditActivity: AppCompatActivity()  {

    private lateinit var addFlatButton: Button
    private lateinit var textInputStreet: TextInputLayout
    private lateinit var textInputPrice: TextInputLayout
    private lateinit var textInputArea: TextInputLayout
    private lateinit var textInputRoom: TextInputLayout
    private lateinit var textInputFloor: TextInputLayout

    private lateinit var flatBox: Box<Flat>

    private lateinit var userBox: Box<User>

    var idUser: Long=0
    var idFlat: Long=0
    private lateinit var prefUserId: SharedPreferences

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_flat)

        flatBox = ObjectBox.boxStore.boxFor()

        userBox = ObjectBox.boxStore.boxFor()

        loadUser()

        setUpViews()

    }

    private fun setUpViews() {

        addFlatButton = findViewById<Button>(R.id.buttonEdit).apply { }

        textInputStreet = findViewById<TextInputLayout>(R.id.text_input_street).apply {
        }
        textInputPrice = findViewById<TextInputLayout>(R.id.text_input_price).apply {
        }
        textInputArea = findViewById<TextInputLayout>(R.id.text_input_area).apply {
        }
        textInputFloor = findViewById<TextInputLayout>(R.id.text_input_floor).apply {
        }
        textInputRoom = findViewById<TextInputLayout>(R.id.text_input_room).apply {
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

            textInputStreet.getEditText()!!.setText(streetFlat.toString())
            textInputPrice.getEditText()!!.setText(priceFlat.toString())
            textInputArea.getEditText()!!.setText(areaFlat.toString())
            textInputFloor.getEditText()!!.setText(floorFlat.toString())
            textInputRoom.getEditText()!!.setText(roomFlat.toString())
        }

    }

    fun onEditButtonClick(view: View) {
        editFlat()
        goBack()
    }

    private fun editFlat() {
        val streetText = textInputStreet.getEditText()!!.getText().toString().trim()
        val priceDouble = textInputPrice.getEditText()!!.getText().toString().trim().toDouble()
        val areaDouble = textInputArea.getEditText()!!.getText().toString().trim().toDouble()
        val floorInt = textInputFloor.getEditText()!!.getText().toString().trim().toInt()
        val roomInt = textInputRoom.getEditText()!!.getText().toString().trim().toInt()

        val flat = Flat(id = idFlat, street = streetText, price = priceDouble, area = areaDouble, floor = floorInt, room = roomInt)

        flatBox.put(flat)

        val user = userBox.get(idUser)

        user.flat.add(flat)

        userBox.put(user)
    }

    private fun loadUser(){
        prefUserId = getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
        idUser = prefUserId.getLong("idUser", 0)
    }

    private fun goBack() {
        val intent = Intent(this, ListActivity::class.java)
        finish()
        startActivity(intent)
    }

}