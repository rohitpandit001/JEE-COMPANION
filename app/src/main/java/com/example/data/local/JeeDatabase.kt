package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserAccountEntity::class,
        SubjectEntity::class,
        ChapterEntity::class,
        StudySessionEntity::class,
        BacklogEntity::class,
        MockTestEntity::class,
        MistakeEntity::class,
        DailyStudyLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class JeeDatabase : RoomDatabase() {

    abstract fun jeeDao(): JeeDao

    companion object {
        @Volatile
        private var INSTANCE: JeeDatabase? = null

        fun getInstance(context: Context): JeeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JeeDatabase::class.java,
                    "jee_companion_v2_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    val dao = database.jeeDao()
                                    dao.insertChapters(SeedData.getInitialChapters())
                                }
                            }
                        }

                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            super.onDestructiveMigration(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    val dao = database.jeeDao()
                                    dao.insertChapters(SeedData.getInitialChapters())
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
