package com.marat.electrodecatalog.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marat.electrodecatalog.entity.Electrode

@Database(
    entities = [Electrode::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun electrodeDao(): ElectrodeDao
}