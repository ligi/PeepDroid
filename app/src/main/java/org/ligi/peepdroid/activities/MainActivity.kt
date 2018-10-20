package org.ligi.peepdroid.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.android.inject
import org.ligi.kaxt.recreateWhenPossible
import org.ligi.kaxt.startActivityFromClass
import org.ligi.kaxtui.alert
import org.ligi.peepdroid.R
import org.ligi.peepdroid.api.PeepAPI
import org.ligi.peepdroid.api.parsePeeper
import org.ligi.peepdroid.api.parsePeeps
import org.ligi.peepdroid.model.SessionStore
import org.ligi.peepdroid.model.Settings
import org.ligi.peepdroid.ui.PeepAdapter
import org.ligi.peepdroid.ui.asPeepethImageURL

class MainActivity : AppCompatActivity() {

    private val peepAPI: PeepAPI by inject()
    private val okHttpClient: OkHttpClient by inject()
    private val settings: Settings by inject()

    private var currentSecret: String? = null
    private var lastNightMode: Int? = null

    private val actionBarDrawerToggle by lazy { ActionBarDrawerToggle(this, drawer_layout, R.string.drawer_open, R.string.drawer_close) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        peep_recycler.layoutManager = LinearLayoutManager(this)

        refresh()

        fab.setOnClickListener {
            startActivityFromClass(PeepActivity::class.java)
        }

        swipe_refresh_layout.setOnRefreshListener {
            refresh()
        }

        nav_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_sign_in -> true.also {
                    signIn()
                }
                R.id.menu_change_user -> true.also {
                    SessionStore.address = null
                    signIn()
                }
                R.id.menu_info -> true.also {
                    startActivityFromClass(InfoActivity::class.java)
                }
                R.id.menu_preferences -> true.also {
                    startActivityFromClass(PreferenceActivity::class.java)
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (lastNightMode != null && lastNightMode != settings.getNightMode()) {
            recreateWhenPossible()
            return
        }

        lastNightMode = settings.getNightMode()

        refresh()
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
    }

    private fun refresh() {
        launch {

            peepAPI.getPeeps()?.let {
                async(UI) {
                    peep_recycler.adapter = PeepAdapter(parsePeeps(it), settings, peepAPI)
                    swipe_refresh_layout.isRefreshing = false
                }
            }
        }

        nav_view.menu.findItem(R.id.menu_change_user).isVisible = SessionStore.address != null

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    private fun signIn() {
        launch {
            val init = peepAPI.init()
            val tokenLine = init?.lines()?.first { it.contains("csrf-token") }?.split("content=")?.last()?.split("\"")?.get(1)

            peepAPI.setIsUser(tokenLine!!)
            currentSecret = peepAPI.getNewSecret()
            val addressPart = SessionStore.address ?: ""
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("ethereum:esm-$addressPart/$currentSecret"))

            async(UI) {
                try {
                    startActivityForResult(intent, 123)
                } catch (e: ActivityNotFoundException) {
                    alert("Wallet not found!")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        launch {
            val signature = data?.getStringExtra("SIGNATURE")

            val address = data?.getStringExtra("ADDRESS")?.toLowerCase()

            val url = "https://peepeth.com/verify_signed_secret.js?signed_token=0x" + signature + "&original_token=" + currentSecret?.replace(" ", "+") + "&address=0x$address&provider=metamask"

            val result = okHttpClient.newCall(Request.Builder()
                    .header("X-Requested-With", "XMLHttpRequest")
                    .url(url).build()).execute()

            peepAPI.getPeeper(address.toString(), true, true)?.let {
                SessionStore.currentPeeper = parsePeeper(it)
            }

            async(UI) {
                if (result.code() != 200) {
                    AlertDialog.Builder(this@MainActivity).setMessage(result.body()?.string()).show()
                } else {
                    val currentPeeper = SessionStore.currentPeeper
                    user_name_label.text = currentPeeper?.slug
                    val bgURL = currentPeeper?.backgroundUrl?.asPeepethImageURL("backgrounds","medium")
                    UrlImageViewHelper.setUrlDrawable(user_bg_img, bgURL)
                    SessionStore.address = address
                }

                refresh()

            }
        }
    }
}
