package com.example.myapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.example.myapp.ObjectBox.boxStore
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query


class ListActivity : AppCompatActivity() {

    var idUser: Long = 0
    var logUser: String? = null

    private lateinit var flatsBox: Box<Flat>
    private lateinit var flatsAdapter: FlatAdapter

    private lateinit var prefUserId: SharedPreferences

    private lateinit var userBox: Box<User>
    private lateinit var userQuery: Query<User>

    private lateinit var listView: ListView

    private val CTX_MENU_DEL = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        flatsBox = ObjectBox.boxStore.boxFor()
        userBox = ObjectBox.boxStore.boxFor()

        setUpView()

        updateFlats()
    }

    private fun updateFlats() {
        val user = boxStore.boxFor(User::class.java).get(idUser)
        flatsAdapter.setFlats(user.flat)
    }

    private fun loadUser() {
        prefUserId = getSharedPreferences("MyPref", MODE_PRIVATE)
        idUser = prefUserId.getLong("idUser", 0)
        userQuery = userBox.query().equal(User_.id, idUser).build()
        val user = userQuery.findUnique()
        logUser = user!!.login
    }

    private fun removeUser() {
        prefUserId = getSharedPreferences("MyPref", MODE_PRIVATE)
        val edId: SharedPreferences.Editor = prefUserId.edit()
        edId.putLong("idUser", -1)
        edId.apply()
    }

    private fun setUpView() {
        flatsAdapter = FlatAdapter()

        listView = findViewById<ListView>(R.id.listView).apply {
            adapter = flatsAdapter
            onItemClickListener = flatClickListener
        }
        registerForContextMenu(listView)

        findViewById<FloatingActionButton>(R.id.fab).apply { setOnClickListener(onFabClickListener) }

        loadUser()

        this.title = logUser

    }

    private var flatClickListener: AdapterView.OnItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val intent = Intent(this, FlatViewActivity::class.java)
        intent.putExtra("idFlat", id)
        startActivity(intent)
    }

    private var onFabClickListener: View.OnClickListener = View.OnClickListener {
        val intent = Intent(this, FlatAddActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        goBack()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(Menu.NONE, CTX_MENU_DEL, Menu.NONE, getString(R.string.ctx_title_del))
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == CTX_MENU_DEL) {
            val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
            val flat = flatsAdapter.getItem(info.position) as Flat
            flatsBox.remove(flat)
            updateFlats()
        }
        return super.onContextItemSelected(item)
    }

    private fun goBack() {
        val intent = Intent(this, MainActivity::class.java)
        removeUser()
        finish()
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}


