package br.ufpe.cin.android.podcast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_episode_detail.*

class EpisodeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        val itemDetails = intent.getParcelableExtra("item_details") as ItemFeed?

        if (itemDetails != null) {
            episode_title.text = itemDetails.title
            episode_link.text = itemDetails.link
        }
    }
}
