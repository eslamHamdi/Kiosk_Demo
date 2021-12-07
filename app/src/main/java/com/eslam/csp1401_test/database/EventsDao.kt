package com.eslam.csp1401_test.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(list:List<EventEntity>)


    @Query("Delete From EventEntities")
    suspend fun wipeEvents()


    @Query("SELECT * FROM EventEntities")
     fun getAllEvents():Flow<List<EventEntity>>

}