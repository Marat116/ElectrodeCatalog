package com.marat.electrodecatalog.ui.electrode_detailed

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.marat.electrodecatalog.App
import com.marat.electrodecatalog.R
import com.marat.electrodecatalog.data.ElectrodeDao
import com.marat.electrodecatalog.entity.Electrode
import com.marat.electrodecatalog.utils.addSystemBottomPadding
import com.marat.electrodecatalog.utils.addSystemTopPadding
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_electrode_detailed.*

class ElectrodeDetailedFragment : Fragment(R.layout.fragment_electrode_detailed) {

    companion object {
        const val ARG_ID = "arg id"
        const val ARG_BARCODE = "arg barcode"
    }

    private val electrodeId: Int? by lazy {
        val id = requireArguments().getInt(ARG_ID, -1)
        if (id == -1) null else id
    }

    private val electrodeBarcode: String? by lazy {
        requireArguments().getString(ARG_BARCODE)
    }

    private val electrodeDao: ElectrodeDao by lazy {
        (requireContext().applicationContext as App).electrodeDao
    }

    private val viewModel: ElectrodeDetailedViewModel by viewModels {
        ElectrodeDetailedViewModel.Factory(electrodeDao, electrodeId, electrodeBarcode)
    }

    private val renderDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.addSystemTopPadding()
        scrollView.addSystemBottomPadding()
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        renderDisposable.add(
            viewModel.viewState.subscribe { viewState ->
                when (viewState) {
                    is ViewState.ElectrodeData -> {
                        errorView.visibility = View.GONE
                        scrollView.visibility = View.VISIBLE
                        render(viewState.electrode)
                    }
                    is ViewState.Error -> {
                        errorView.visibility = View.VISIBLE
                        scrollView.visibility = View.GONE
                    }
                }
            }
        )
    }

    private fun render(electrode: Electrode) {
        typeMarkTextView.text = electrode.typeMark
        tuGostTextView.text = electrode.tuGost
        formTextView.text = electrode.form
        functionTextView.text = electrode.function
        vremSopRazTextView.text = electrode.vremSoprRaz
        otnUdlTextView.text = electrode.otnUdl
        udVyazTextView.text = electrode.udVyaz
        rodTokaTextView.text = electrode.rodTokaEletrodov
        polojenieSvarkiTextView.text = electrode.polojenieSvarki
    }

    override fun onPause() {
        renderDisposable.clear()
        super.onPause()
    }
}