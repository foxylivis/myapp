package com.example.myapp

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.Button
import android.widget.TextView
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
    private lateinit var textViewReg: TextView

    private var idUser: Long = 0
    private var logUser: String = ""
    private var passUser: String = ""

    private lateinit var userBox: Box<User>
    private lateinit var userQuery: Query<User>

    private lateinit var prefUserId: SharedPreferences

    var logOrRegBool = true

    private var REG_LOG: String = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    private var REG_PASS: String = "^[а-яА-ЯёЁa-zA-Z0-9]+$"

    private var log_err: String = ""
    private var pass_err: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpViews()

        userBox = ObjectBox.boxStore.boxFor()

        idUser = loadUser()
        if (idUser >= 0) {
            goList()
        }
    }

    private fun setUpViews() {
        textInputLogin = findViewById(R.id.text_input_log)
        textInputPass = findViewById(R.id.text_input_pass)
        loginButton = findViewById(R.id.buttonLogin)
        regButton = findViewById<Button>(R.id.buttonReg).apply { visibility = View.GONE }
        textViewReg = findViewById(R.id.textViewReg)
    }

    private fun dataInfo() {
        logUser = textInputLogin.editText!!.text.toString().trim()
        passUser = textInputPass.editText!!.text.toString().trim()
    }

    private fun checkLogPass(log: String, pass: String): Boolean {
        userQuery = userBox.query().equal(User_.login, log).equal(User_.password, pass).build()
        val user = userQuery.findUnique()
        return if (user != null) {
            idUser = userQuery.property(User_.id).findLong()
            true
        } else {
            false
        }
    }

    private fun checkLog(log: String): Boolean {
        if (log.isEmpty()) {
            log_err = getString(R.string.mess_empty)
            return false
        } else {
            return if (!log.matches(REG_LOG.toRegex())) {
                log_err = getString(R.string.mess_invalid)
                false
            } else {
                userQuery = userBox.query().equal(User_.login, log).build()
                val user = userQuery.findUnique()
                if (user != null) {
                    log_err = getString(R.string.mess_user_exists)
                    false
                } else {
                    log_err = ""
                    true
                }
            }
        }
    }

    private fun checkPass(pass: String): Boolean {
        return if (pass.isEmpty()) {
            pass_err = getString(R.string.mess_empty)
            false
        } else {
            if (!pass.matches(REG_PASS.toRegex())) {
                pass_err = getString(R.string.mess_invalid)
                false
            } else {
                pass_err = ""
                true
            }
        }
    }

    private fun allCheckforReg() {

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
                textInputLogin.error = null
            } else {
                textInputLogin.error = log_err
            }
            if (pass_err.isEmpty()) {
                textInputPass.error = null
            } else {
                textInputPass.error = pass_err
            }
            if (checkLogBool and checkPassBool) {
                addUser()
            }
        }
    }

    private fun viewButtons() {
        if (logOrRegBool) {
            logOrRegBool = false
            textViewReg.text = getString(R.string.button_log)
            loginButton.visibility = View.GONE
            regButton.visibility = View.VISIBLE
        } else {
            logOrRegBool = true
            textViewReg.text = getString(R.string.button_reg)
            loginButton.visibility = View.VISIBLE
            regButton.visibility = View.GONE
        }
    }


    private fun goList() {
        val intent = Intent(this, ListActivity::class.java)

        saveUser(idUser)
        finish()
        startActivity(intent)
    }

    private fun saveUser(id: Long) {
        prefUserId = getSharedPreferences("MyPref", MODE_PRIVATE)
        val edId: SharedPreferences.Editor = prefUserId.edit()
        edId.putLong("idUser", id)
        edId.apply()
    }

    private fun loadUser(): Long {
        prefUserId = getSharedPreferences("MyPref", MODE_PRIVATE)
        return prefUserId.getLong("idUser", -1)
    }

    private fun addUser() {
        val user = User(login = logUser, password = passUser)
        userBox.put(user)
        showMessage(logUser + getString(R.string.mess_user_add))
    }

    fun onButtonClickLogin(view: View) {
        dataInfo()
        val logPassCheck = async(CommonPool) {
            checkLogPass(logUser, passUser)
        }
        launch(UI) {
            if (logPassCheck.await()) {
                goList()
            } else {
                showMessage(getString(R.string.mess_login_and_pass_invalid))
            }
        }
    }

    fun onButtonClickReg(view: View) {
        dataInfo()
        allCheckforReg()
    }

    fun onTextViewClick(view: View) {
        viewButtons()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
