package br.ufpe.cin.android.podcast

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemFeed(
    @PrimaryKey val title: String,
    @ColumnInfo val link: String,
    @ColumnInfo val pubDate: String,
    @ColumnInfo val description: String,
    @ColumnInfo val downloadLink: String) {

    override fun toString(): String {
        return title
    }
}
