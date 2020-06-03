package com.marat.electrodecatalog.ui.electrode_detailed

import com.marat.electrodecatalog.entity.Electrode

sealed class ViewState {
    data class ElectrodeData(val electrode: Electrode) : ViewState()
    object Error : ViewState()
}