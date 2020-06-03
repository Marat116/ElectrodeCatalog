package com.marat.electrodecatalog.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.marat.electrodecatalog.entity.Electrode
import io.reactivex.Single

@Dao
interface ElectrodeDao {

    @Query("SELECT * FROM electrode")
    fun getAllElectrodes(): Single<List<Electrode>>

    @Query("SELECT * FROM electrode WHERE id = :id")
    fun getElectrodeById(id: Int): Single<Electrode>

    @Query("SELECT * FROM electrode WHERE barcode = :barcode")
    fun getElectrodeByBarcode(barcode: String): Single<Electrode>

    @Query("SELECT * FROM electrode WHERE typeMark LIKE :typeMarkPattern")
    fun getElectrodeByTypeMarkPattern(typeMarkPattern: String): Single<List<Electrode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElectrodes(electrodeList: List<Electrode>): Single<List<Long>>
}