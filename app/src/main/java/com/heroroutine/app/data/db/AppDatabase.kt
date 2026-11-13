package com.heroroutine.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
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
    version = 3,
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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "heroroutine_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
