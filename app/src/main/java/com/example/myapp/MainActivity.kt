package com.example.myapp

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.Button
import android.widget.Toast
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch


class MainActivity : AppCompatActivity() {

    private lateinit var textInputLogin: TextInputLayout
    private lateinit var textInputPass: TextInputLayout
    private lateinit var loginButton: Button
    private lateinit var regButton: Button

    private var idUser: Long = 0
    private var logUser: String = ""
    private var passUser: String = ""

    private lateinit var userBox: Box<User>
    private lateinit var userQuery: Query<User>

    private lateinit var prefUserId: SharedPreferences

    private var MESS_USER_EXISTS: String = "Такой пользователь уже существует."
    private var MESS_INVALID: String = "Используется недопустимый символ"
    private var MESS_EMPTY: String = "Поле не может быть пустым"
    private var MESS_LOGIN_PASS_INVALID: String = "Не верный логин или пароль."
    //private var MESS_NULL: String = "null"
    private var REG_LOG: String = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    private var REG_PASS: String =  "^[а-яА-ЯёЁa-zA-Z0-9]+$"
    private var log_err:  String = ""
    private var pass_err:  String = ""
    private var MESS_USER_ADD: String = " успешно добавлен!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpViews()

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

    private fun checkLog(log:String):Boolean {
        if (log.isEmpty()) {
            log_err = MESS_EMPTY
            return false
        } else {
            if (!log.matches(REG_LOG.toRegex())) {
                log_err = MESS_INVALID
                return false
            } else {
                userQuery = userBox.query().equal(User_.login, log).build()
                val user = userQuery.findUnique()
                if (user != null) {
                    log_err = MESS_USER_EXISTS
                    return false
                } else {
                    log_err = ""
                    return true
                }
            }
        }
    }

    private fun checkPass(pass:String):Boolean {
        if(pass.isEmpty()){
            pass_err = MESS_EMPTY
            return false
        } else {
            if(!pass.matches(REG_PASS.toRegex())){
               pass_err = MESS_INVALID
                return false
            } else {
                pass_err = ""
                return true
            }
        }
    }

    private fun allCheckforReg () {

        var checkLogBool = false
        var checkPassBool = false

        val logCheck = async(CommonPool) {
            checkLog(logUser)
        }
        val passCheck = async(CommonPool) {
            checkPass(passUser)
        }

        launch(UI) {
            checkLogBool = logCheck.await()
            checkPassBool = passCheck.await()
            if (log_err.isEmpty()) {
                textInputLogin.setError(null)
            } else {textInputLogin.setError(log_err)}
            if (pass_err.isEmpty()) {
                textInputPass.setError(null)
            } else {textInputPass.setError(pass_err)}
            if (checkLogBool and checkPassBool){
                textInputPass.setError(checkLogBool.toString())
                addUser()
            }
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
        val edId : SharedPreferences.Editor = prefUserId.edit()
        edId.putLong("idUser", id)
        edId.apply()
    }

    private fun loadUser():Long{
        val id: Long
        prefUserId = getSharedPreferences("MyPref",MODE_PRIVATE)
        id = prefUserId.getLong("idUser", -1)
        return id
    }

    private fun addUser() {
        val user = User(login = logUser, password = passUser)
        userBox.put(user)
        Toast.makeText(this, logUser + MESS_USER_ADD, Toast.LENGTH_LONG).show()
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
        allCheckforReg()
    }

    private fun showMessage(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
