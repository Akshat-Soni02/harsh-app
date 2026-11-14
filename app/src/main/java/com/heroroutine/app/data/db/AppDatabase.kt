package com.heroroutine.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.heroroutine.app.data.dao.ProgressPhotoDao
import com.heroroutine.app.data.dao.SessionDao
import com.heroroutine.app.data.dao.TaskDao
import com.heroroutine.app.data.dao.TaskLogDao
import com.heroroutine.app.data.model.ProgressPhotoEntity
import com.heroroutine.app.data.model.SessionEntity
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.data.model.TaskLogEntity
import com.heroroutine.app.data.model.TaskType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromTaskType(value: TaskType): String = value.name

    @TypeConverter
    fun toTaskType(value: String): TaskType = try {
        TaskType.valueOf(value)
    } catch (e: Exception) {
        TaskType.EXERCISE
    }
}

@Database(
    entities = [
        SessionEntity::class,
        TaskEntity::class,
        TaskLogEntity::class,
        ProgressPhotoEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao
    abstract fun taskDao(): TaskDao
    abstract fun taskLogDao(): TaskLogDao
    abstract fun progressPhotoDao(): ProgressPhotoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sessions ADD COLUMN alarmEnabled INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "heroroutine_database"
                )
                .addMigrations(MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
