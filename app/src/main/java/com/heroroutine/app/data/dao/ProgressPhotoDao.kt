package com.heroroutine.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heroroutine.app.data.model.ProgressPhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressPhotoDao {
    @Query("SELECT * FROM progress_photos WHERE taskId = :taskId ORDER BY timestamp DESC")
    fun getPhotosForTask(taskId: Long): Flow<List<ProgressPhotoEntity>>

    @Query("SELECT * FROM progress_photos ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<ProgressPhotoEntity>>

    @Query("SELECT * FROM progress_photos ORDER BY timestamp DESC")
    suspend fun getAllPhotosList(): List<ProgressPhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: ProgressPhotoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<ProgressPhotoEntity>)

    @Delete
    suspend fun deletePhoto(photo: ProgressPhotoEntity)

    @Query("DELETE FROM progress_photos")
    suspend fun clearAllPhotos()
}
