package com.marat.electrodecatalog.ui.electrode_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marat.electrodecatalog.R
import com.marat.electrodecatalog.entity.Electrode
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_electrode.*

class ElectrodeListAdapter(private val onItemClickListener: (electrode: Electrode) -> Unit) :
    ListAdapter<Electrode, ElectrodeListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<Electrode>() {
            override fun areItemsTheSame(oldItem: Electrode, newItem: Electrode): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Electrode, newItem: Electrode): Boolean =
                oldItem.id == newItem.id
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_electrode, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(override val containerView: View) :
        LayoutContainer, RecyclerView.ViewHolder(containerView) {

        lateinit var electrode: Electrode

        init {
            containerView.setOnClickListener { onItemClickListener(electrode) }
        }

        fun bind(electrode: Electrode) {
            this.electrode = electrode
            typeMarkTextView.text = electrode.typeMark
            tuGostTextView.text = electrode.tuGost
        }
    }
}