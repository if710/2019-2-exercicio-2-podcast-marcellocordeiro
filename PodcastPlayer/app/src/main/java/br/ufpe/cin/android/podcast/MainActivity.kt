package br.ufpe.cin.android.podcast

import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_feed.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        app_bar.setExpanded(false, false)
        toolbar_layout.title = getString(R.string.app_name)
        toolbar_layout.setExpandedTitleColor(0)

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork

        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "feed-db"
        ).build()

        doAsync {
            try {
                val feedData = if (activeNetwork != null) {
                    val rss = URL("https://ffkhunion.libsyn.com/rss").readText()
                    Parser.parse(rss)
                } else {
                    Snackbar.make(
                        window.decorView.rootView,
                        getString(R.string.using_cached_db_warning),
                        Snackbar.LENGTH_LONG
                    ).show()
                    db.itemFeedDAO().getAll()
                }

                val img =
                    URL("https://ssl-static.libsyn.com/p/assets/2/0/e/5/20e599a280c70f15/ffu-khu.jpg").readBytes()
                val bmp = BitmapFactory.decodeByteArray(img, 0, img.size)


                // TODO: do this more efficiently
                db.itemFeedDAO().insertAll(*feedData.toTypedArray())

                uiThread {
                    progressBar.visibility = View.GONE

                    app_bar_image.setImageBitmap(bmp)
                    app_bar.setExpanded(false, false)
                    app_bar_image.visibility = View.VISIBLE
                    app_bar.setExpanded(true, true)


                    feed.apply {
                        //setHasFixedSize(true)
                        //ViewCompat.setNestedScrollingEnabled(feed, false)
                        layoutManager = LinearLayoutManager(it)
                        adapter = MyAdapter(feedData)

                        addItemDecoration(
                            DividerItemDecoration(
                                context,
                                (layoutManager as LinearLayoutManager).orientation
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                uiThread {
                    progressBar.visibility = View.GONE

                    Snackbar.make(
                        window.decorView.rootView,
                        e.message ?: e.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
