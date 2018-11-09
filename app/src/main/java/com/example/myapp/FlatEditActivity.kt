package com.example.myapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import io.objectbox.Box
import io.objectbox.kotlin.boxFor

class FlatEditActivity : AppCompatActivity() {

    private lateinit var addFlatButton: Button
    private lateinit var textInputStreet: TextInputLayout
    private lateinit var textInputPrice: TextInputLayout
    private lateinit var textInputArea: TextInputLayout
    private lateinit var textInputRoom: TextInputLayout
    private lateinit var textInputFloor: TextInputLayout

    private lateinit var flatBox: Box<Flat>

    private lateinit var userBox: Box<User>

    var idUser: Long = 0
    var idFlat: Long = 0
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

        addFlatButton = findViewById(R.id.buttonEdit)
        textInputStreet = findViewById(R.id.text_input_street)
        textInputPrice = findViewById(R.id.text_input_price)
        textInputArea = findViewById(R.id.text_input_area)
        textInputFloor = findViewById(R.id.text_input_floor)
        textInputRoom = findViewById(R.id.text_input_room)

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

            textInputStreet.editText!!.setText(streetFlat.toString())
            textInputPrice.editText!!.setText(priceFlat.toString())
            textInputArea.editText!!.setText(areaFlat.toString())
            textInputFloor.editText!!.setText(floorFlat.toString())
            textInputRoom.editText!!.setText(roomFlat.toString())
        }

    }

    fun onEditButtonClick(view: View) {
        editFlat()
    }

    private fun editFlat() {
        try {
            val streetText = textInputStreet.editText!!.text.toString().trim()
            val priceDouble = textInputPrice.editText!!.text.toString().trim().toDouble()
            val areaDouble = textInputArea.editText!!.text.toString().trim().toDouble()
            val floorInt = textInputFloor.editText!!.text.toString().trim().toInt()
            val roomInt = textInputRoom.editText!!.text.toString().trim().toInt()

            val flat = Flat(id = idFlat, street = streetText, price = priceDouble, area = areaDouble, floor = floorInt, room = roomInt)

            flatBox.put(flat)

            val user = userBox.get(idUser)

            user.flat.add(flat)

            userBox.put(user)

            goBack()
        } catch (e: Exception) {
            showMessage(getString(R.string.mess_err))
        }
    }

    private fun showMessage(mess: String) {
        Toast.makeText(this, mess, Toast.LENGTH_LONG).show()
    }

    private fun loadUser() {
        prefUserId = getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
        idUser = prefUserId.getLong("idUser", 0)
    }

    private fun goBack() {
        val intent = Intent(this, ListActivity::class.java)
        finish()
        startActivity(intent)
    }

}