package com.marat.electrodecatalog

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.room.Room
import com.marat.electrodecatalog.data.AppDatabase
import com.marat.electrodecatalog.data.ElectrodeDao
import com.marat.electrodecatalog.data.ElectrodeListCreator
import com.marat.electrodecatalog.entity.Electrode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class App : Application() {

    private lateinit var database: AppDatabase
    lateinit var electrodeDao: ElectrodeDao

    override fun onCreate() {
        super.onCreate()
        createDatabase()
        initDatabaseItems()
    }

    private fun createDatabase() {
        database = Room
            .databaseBuilder(this, AppDatabase::class.java, "app_database.db")
            .build()
        electrodeDao = database.electrodeDao()
    }

    @SuppressLint("CheckResult")
    private fun initDatabaseItems() {
        val electrodeList = ElectrodeListCreator.createElectrodeList()
        electrodeDao
            .insertElectrodes(electrodeList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Log.d(javaClass.canonicalName, "electrode table items successfully inserted")},
                { Log.e(javaClass.canonicalName, "electrode table items insert error", it)}
            )
    }
}