package skynight.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

@Suppress("OverridingDeprecatedMember")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.title = "主页"
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            webView.loadUrl("https://github.com/1552980358")
            progressBar.visibility = View.VISIBLE
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        // 加载url
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.allowFileAccess = true // 本地文件
        webSettings.setSupportZoom(true) //缩放
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return false
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = when (newProgress) {
                    100 -> {
                        progressBar.visibility = View.GONE
                        0
                    }
                    else -> {
                        progressBar.visibility = View.VISIBLE
                        newProgress
                    }
                }
            }
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                    toolbar.title = title!!
                    searchView.queryHint = title
            }
        }

        webView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                if (webView.canGoBack()) {
                    webView.goBack()
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }

        // 加载
        webView.loadUrl("https://github.com/1552980358")

        searchView.setIconifiedByDefault(true)
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "主页"
        searchView.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                //
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                Thread {
                    val url: String
                    if (p0 != null) {
                        url = if (!p0.startsWith("https://") || !p0.startsWith("http://")) {
                            "https://$p0"
                        } else {
                            p0
                        }
                        runOnUiThread { webView.loadUrl(url) }
                    }
                }.start()
                return false
            }
        })

        nav_view.setNavigationItemSelectedListener(this)
    }

    var exit = 0L

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (System.currentTimeMillis() - exit > 2000) {
                Toast.makeText(this, "再次点击退回键退出", Toast.LENGTH_SHORT).show()
                exit = System.currentTimeMillis()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.stopLoading()
        webView.webViewClient = null
        webView.webChromeClient = null
        webView.removeAllViews()
        webView.destroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                webView.loadUrl("https://github.com/1552980358")
            }
            R.id.nav_exit -> System.exit(0)
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}