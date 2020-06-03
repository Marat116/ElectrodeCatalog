package com.marat.electrodecatalog.ui.electrode_detailed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxrelay2.BehaviorRelay
import com.marat.electrodecatalog.data.ElectrodeDao
import com.marat.electrodecatalog.entity.Electrode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ElectrodeDetailedViewModel(
    electrodeDao: ElectrodeDao,
    id: Int?,
    barcode: String?
) : ViewModel() {

    private var disposable: Disposable? = null
    val viewState = BehaviorRelay.create<ViewState>()

    init {
        disposable =
            (if (id != null) {
                electrodeDao.getElectrodeById(id)
            } else {
                electrodeDao.getElectrodeByBarcode(barcode ?: "")
            })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { viewState.accept(ViewState.ElectrodeData(it)) },
                    {
                        Log.e(javaClass.canonicalName, "query electrode by id error", it)
                        viewState.accept(ViewState.Error)
                    }
                )
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    class Factory(
        private val electrodeDao: ElectrodeDao,
        private val id: Int?,
        private val barcode: String?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ElectrodeDetailedViewModel(electrodeDao, id, barcode) as T
        }
    }
}