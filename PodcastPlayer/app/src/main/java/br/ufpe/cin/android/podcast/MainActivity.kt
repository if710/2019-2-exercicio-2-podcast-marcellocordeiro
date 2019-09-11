package br.ufpe.cin.android.podcast

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

                // TODO: do this more efficiently
                db.itemFeedDAO().insertAll(*feedData.toTypedArray())

                uiThread {
                    feed.apply {
                        setHasFixedSize(true)
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
