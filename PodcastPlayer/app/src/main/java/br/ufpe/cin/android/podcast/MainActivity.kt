package br.ufpe.cin.android.podcast

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_feed.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val rssLink = "https://ffkhunion.libsyn.com/rss"
    private val bannerLink = "https://ssl-static.libsyn.com/p/assets/2/0/e/5/20e599a280c70f15/ffu-khu.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        app_bar.setExpanded(false, false)
        toolbar_layout.title = getString(R.string.app_name)
        toolbar_layout.setExpandedTitleColor(0)

        setPodcastFeed()
    }

    private fun setPodcastFeed() {
        val db = AppDatabase.getInstance(this)

        doAsync {
            try {
                val (feedData, bmp) = if (isConnected()) {
                    val rss = URL(rssLink).readText()
                    val img = URL(bannerLink).readBytes()

                    Pair(Parser.parse(rss), BitmapFactory.decodeByteArray(img, 0, img.size))
                } else {
                    showSnackbar(
                        window.decorView.rootView,
                        getString(R.string.using_cached_db_warning)
                    )

                    Pair(db.itemFeedDAO().getAll(), null)
                }

                // TODO: do this more efficiently
                db.itemFeedDAO().insertAll(*feedData.toTypedArray())

                uiThread {
                    refreshFeed(it, feedData, bmp)
                }
            } catch (e: Exception) {
                uiThread {
                    progressBar.visibility = View.GONE

                    showSnackbar(
                        window.decorView.rootView,
                        e.message ?: e.toString()
                    )
                }
            }
        }
    }

    private fun refreshFeed(context: Context, feedData: List<ItemFeed>, bmp: Bitmap?) {
        progressBar.visibility = View.GONE

        feedView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = MyAdapter(feedData)

            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
        }

        if (bmp != null) {
            app_bar_image.setImageBitmap(bmp)
            app_bar.setExpanded(false, false)
            app_bar_image.visibility = View.VISIBLE
            app_bar.setExpanded(true, true)
        } else {
            // TODO: fix this
            //app_bar_image.visibility = View.GONE
        }
    }

    private fun isConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork

        return activeNetwork != null
    }

    private fun showSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(view, message, duration).show()
    }
}
