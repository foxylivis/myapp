package com.example.myapp

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.Button
import android.widget.Toast
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query

class MainActivity : AppCompatActivity() {

    private lateinit var textInputLogin: TextInputLayout
    private lateinit var textInputPass: TextInputLayout
    private lateinit var loginButton: Button;
    private lateinit var regButton: Button;

    private var idUser: Long = 0;
    private var logUser: String = "";
    private var passUser: String = "";

    private lateinit var userBox: Box<User>
    private lateinit var userQuery: Query<User>

    private lateinit var prefUserId: SharedPreferences

    private var MESS_USER_EXISTS: String = "Такой пользователь уже существует."
    private var MESS_INVALID: String = "Не верный ввод"
    private var MESS_EMPTY: String = "Поле не может быть пустым"
    private var MESS_LOGIN_PASS_INVALID: String = "Не верный логин или пароль."
    private var REG_LOG: String = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    private var REG_PASS: String =  "^[а-яА-ЯёЁa-zA-Z0-9]+$"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpViews();

        userBox = ObjectBox.boxStore.boxFor()

        idUser = loadUser()
        if(idUser>=0){
            goList()
        }
    }

    private fun setUpViews() {
        textInputLogin = findViewById<TextInputLayout>(R.id.text_input_log).apply {  }
        textInputPass = findViewById<TextInputLayout>(R.id.text_input_pass).apply {  }
        loginButton = findViewById<Button>(R.id.buttonLogin).apply {  }
        regButton = findViewById<Button>(R.id.buttonReg).apply{}
    }

    private fun dataInfo(){
        logUser = textInputLogin.getEditText()!!.getText().toString().trim()
        passUser = textInputPass.getEditText()!!.getText().toString().trim()
    }

    private fun checkLogPass(log:String, pass:String):Boolean {
        userQuery = userBox.query().equal(User_.login, log).equal(User_.password, pass).build()
        val user = userQuery.findUnique()
        if(user!=null){
            idUser = userQuery.property(User_.id).findLong()
                    return true}
        else{
            return false
        }
    }

    private fun checkLog(log:String):Boolean{
        userQuery = userBox.query().equal(User_.login, log).build()
        val user = userQuery.findUnique()
        if(user!=null){
            return true
        } else {
            return false
        }
    }

    private fun checkLogEmpty(log:String):Boolean{
        if(log.isEmpty()){
            textInputLogin.setError(MESS_EMPTY)
            return false
        } else {
            textInputLogin.setError(null)
            return true
        }
    }

    private fun checkLogCorrect(log:String):Boolean{
        if(!log.matches(REG_LOG.toRegex()) ){
            textInputLogin.setError(MESS_INVALID)
            return false
        } else {
            textInputLogin.setError(null)
            return true
        }
    }

    private fun checkPassCorrect(pass:String):Boolean{
        if(!pass.matches(REG_PASS.toRegex())){
            textInputPass.setError(MESS_INVALID)
            return false
        } else {
            textInputPass.setError(null)
            return true
        }
    }

    private fun checkPassEmpty(pass:String):Boolean{
        if(pass.isEmpty()){
            textInputPass.setError(MESS_EMPTY)
            return false
        } else {
            textInputPass.setError(null)
            return true
        }
    }

    private fun allCheckforReg ():Boolean {
        if (!checkLog(logUser)) {
            if ((checkLogEmpty(logUser) and checkPassEmpty(passUser)) && (checkLogCorrect(logUser) and checkPassCorrect(passUser))) {
                return true
            } else {
                return false
            }
        } else {
            showMessage(MESS_USER_EXISTS)
            return false
        }
    }


    private fun goList(){
        val intent = Intent(this, ListActivity::class.java)

        //mew
        /*
        userQuery = userBox.query().build()
        val user = userQuery.find()
        Toast.makeText(this, idUser.toString() + " " + user.size.toString(), Toast.LENGTH_LONG).show()*/

        saveUser(idUser)
        finish()
        startActivity(intent)
    }

    private fun saveUser(id: Long){
        prefUserId = getSharedPreferences("MyPref",MODE_PRIVATE)
        var edId : SharedPreferences.Editor = prefUserId.edit()
        edId.putLong("idUser", id)
        edId.commit()
    }

    private fun loadUser():Long{
        var id: Long
        prefUserId = getSharedPreferences("MyPref",MODE_PRIVATE)
        id = prefUserId.getLong("idUser", -1)
        return id
    }

    private fun addUser() {
        val user = User(login = logUser, password = passUser)
        userBox.put(user)
    }

    fun onButtonClickLogin(view: View) {
        dataInfo()
        if (checkLogPass(logUser, passUser)){
            goList()
        }
        else
        {
            showMessage(MESS_LOGIN_PASS_INVALID)
        }
    }

    fun onButtonClickReg(view: View) {
        dataInfo()
        if(allCheckforReg()){addUser()}
    }

    private fun showMessage(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
