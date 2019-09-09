package br.ufpe.cin.android.podcast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doAsync {
            try {
                val rss = URL("https://ffkhunion.libsyn.com/rss").readText()

                uiThread {
                    val data = Parser.parse(rss)

                    feed.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(it)
                        adapter = MyAdapter(data)

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
