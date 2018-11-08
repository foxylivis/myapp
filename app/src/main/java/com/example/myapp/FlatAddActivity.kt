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

class FlatAddActivity: AppCompatActivity()  {

    private lateinit var addFlatButton: Button
    private lateinit var textInputStreet: TextInputLayout
    private lateinit var textInputPrice: TextInputLayout
    private lateinit var textInputArea: TextInputLayout
    private lateinit var textInputRoom: TextInputLayout
    private lateinit var textInputFloor: TextInputLayout

    private lateinit var flatBox: Box<Flat>

    private lateinit var userBox: Box<User>

    private var MESS_ERR: String = "Введите правильные значения"

    var idUser: Long=0

    private lateinit var prefUserId: SharedPreferences

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_flat)

        flatBox = ObjectBox.boxStore.boxFor()

        userBox = ObjectBox.boxStore.boxFor()

        loadUser()

        setUpViews()

    }

    private fun setUpViews() {

        addFlatButton = findViewById<Button>(R.id.buttonAdd).apply { }

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
    }

    private fun showMessage(mess:String){
        Toast.makeText(this, mess, Toast.LENGTH_LONG).show()
    }

    private fun loadUser(){
        prefUserId = getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
        idUser = prefUserId.getLong("idUser", 0)
    }

    fun onAddButtonClick(view: View) {
        addFlat()
    }

    private fun addFlat() {
        try{
        val streetText = textInputStreet.getEditText()!!.getText().toString().trim()
        val priceDouble = textInputPrice.getEditText()!!.getText().toString().trim().toDouble()
        val areaDouble = textInputArea.getEditText()!!.getText().toString().trim().toDouble()
        val floorInt = textInputFloor.getEditText()!!.getText().toString().trim().toInt()
        val roomInt = textInputRoom.getEditText()!!.getText().toString().trim().toInt()

        val flat = Flat(street = streetText, price = priceDouble, area = areaDouble, floor = floorInt, room = roomInt)

        val user = userBox.get(idUser)
        user.flat.add(flat)
        userBox.put(user)
            goBack()
        }
        catch(e: Exception) {
            showMessage(MESS_ERR)
        }
    }

    private fun goBack() {
        val intent = Intent(this, ListActivity::class.java)
        finish()
        startActivity(intent)
    }

}