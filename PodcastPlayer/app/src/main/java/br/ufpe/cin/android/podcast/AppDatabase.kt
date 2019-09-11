package br.ufpe.cin.android.podcast

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ItemFeed::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemFeedDAO(): ItemFeedDAO
}
