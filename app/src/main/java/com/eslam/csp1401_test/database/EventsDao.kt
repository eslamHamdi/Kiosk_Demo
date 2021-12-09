package com.eslam.csp1401_test.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(event:List<EventEntity>)


    @Query("Delete From EventEntities")
    suspend fun wipeEvents()

    @Query("Delete From EventEntities where id = :id")
    suspend fun deleteEvent(id:String)


    @Query("SELECT * FROM EventEntities Order by startDateTime Asc")
     fun getAllEvents():Flow<List<EventEntity>>

}