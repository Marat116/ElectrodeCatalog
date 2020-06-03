package com.marat.electrodecatalog.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "electrode")
data class Electrode(
    @PrimaryKey val id: Int,
    val typeMark: String,
    val tuGost: String,
    val form: String,
    val function: String,
    val vremSoprRaz: String,
    val otnUdl: String,
    val udVyaz: String,
    val barcode: String,
    val rodTokaEletrodov: String,
    val polojenieSvarki: String
)