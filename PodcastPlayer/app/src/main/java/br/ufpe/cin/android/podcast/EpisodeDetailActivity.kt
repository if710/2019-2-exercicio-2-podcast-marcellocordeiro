package br.ufpe.cin.android.podcast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.activity_episode_detail.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class EpisodeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        val itemDetails = intent.getParcelableExtra("item_details") as ItemFeed?

        if (itemDetails != null) {
            episode_title.text = itemDetails.title
            episode_description.text =
                HtmlCompat.fromHtml(itemDetails.description, HtmlCompat.FROM_HTML_MODE_LEGACY).trim()

            episode_link.apply {
                onClick {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(itemDetails.link)
                    startActivity(i)
                }
            }
        }
    }
}
