package com.marat.electrodecatalog.ui.electrode_list

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.marat.electrodecatalog.App
import com.marat.electrodecatalog.R
import com.marat.electrodecatalog.data.ElectrodeDao
import com.marat.electrodecatalog.ui.electrode_detailed.ElectrodeDetailedFragment
import com.marat.electrodecatalog.ui.scan_fragment.ScanFragment
import com.marat.electrodecatalog.utils.addSystemBottomPadding
import com.marat.electrodecatalog.utils.addSystemTopPadding
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_electrode_list.*


class ElectrodeListFragment : Fragment(R.layout.fragment_electrode_list) {

    private val electrodeDao: ElectrodeDao by lazy {
        (requireContext().applicationContext as App).electrodeDao
    }

    private val viewModel: ElectrodeListViewModel by viewModels {
        ElectrodeListViewModel.Factory(electrodeDao)
    }

    private val listAdapter: ElectrodeListAdapter by lazy {
        ElectrodeListAdapter { electrode ->
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(
                    R.id.fragmentContainer,
                    ElectrodeDetailedFragment::class.java,
                    Bundle().apply { putInt(ElectrodeDetailedFragment.ARG_ID, electrode.id) }
                )
                .addToBackStack(ElectrodeDetailedFragment::class.java.canonicalName)
                .commit()
        }
    }

    private val renderDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.addSystemTopPadding()
        toolbar.apply {
            inflateMenu(R.menu.search)
            val searchItem: MenuItem = menu.findItem(R.id.action_search)
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d(javaClass.canonicalName, query ?: "null")
                    viewModel.onSearchSubmit(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    viewModel.onSearchOpen()
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    viewModel.onSearchClose()
                    return true
                }
            })

            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_scan) {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(
                            R.id.fragmentContainer,
                            ScanFragment::class.java,
                            null
                        )
                        .addToBackStack(javaClass.canonicalName)
                        .commit()
                    return@setOnMenuItemClickListener true
                }
                false
            }
        }
        electrodeRecyclerView.apply {
            addSystemBottomPadding()
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        renderDisposable.add(
            viewModel.electrodeList
                .subscribe { listAdapter.submitList(it) }
        )
    }

    override fun onPause() {
        renderDisposable.clear()
        super.onPause()
    }
}