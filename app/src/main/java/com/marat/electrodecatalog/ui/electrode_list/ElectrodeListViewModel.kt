package com.marat.electrodecatalog.ui.electrode_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxrelay2.BehaviorRelay
import com.marat.electrodecatalog.data.ElectrodeDao
import com.marat.electrodecatalog.entity.Electrode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ElectrodeListViewModel(private val electrodeDao: ElectrodeDao) : ViewModel() {

    private var disposable: Disposable? = null
    val electrodeList = BehaviorRelay.create<List<Electrode>>()
    private var lastQuery: String? = null

    init {
        disposable = electrodeDao.getAllElectrodes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { electrodeList.accept(it) },
                { Log.e(javaClass.canonicalName, "query electrodes error", it) }
            )
    }

    fun onSearchSubmit(query: String?) {
        lastQuery = query
        query?.let {
            disposable = electrodeDao.getElectrodeByTypeMarkPattern("%$it%")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> electrodeList.accept(result) },
                    { e -> Log.e(javaClass.canonicalName, "query electrode by pattern error", e) }
                )
        }
    }

    fun onSearchClose() {
        lastQuery = null
        disposable = electrodeDao.getAllElectrodes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { electrodeList.accept(it) },
                { Log.e(javaClass.canonicalName, "query electrodes error", it) }
            )
    }

    fun onSearchOpen() {
        if (lastQuery.isNullOrEmpty()) electrodeList.accept(emptyList())
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    class Factory(private val electrodeDao: ElectrodeDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ElectrodeListViewModel(electrodeDao) as T
        }
    }
}