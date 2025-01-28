package com.on.staccato.presentation.category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.databinding.ItemStaccatosBinding
import com.on.staccato.presentation.category.CategoryHandler
import com.on.staccato.presentation.category.model.CategoryStaccatoUiModel

class StaccatosAdapter(private val handler: CategoryHandler) : ListAdapter<CategoryStaccatoUiModel, StaccatoViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): StaccatoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStaccatosBinding.inflate(inflater, parent, false)
        return StaccatoViewHolder(binding, handler)
    }

    override fun onBindViewHolder(
        holder: StaccatoViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun updateStaccatos(staccatos: List<CategoryStaccatoUiModel>) {
        submitList(staccatos)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<CategoryStaccatoUiModel>() {
                override fun areItemsTheSame(
                    oldItem: CategoryStaccatoUiModel,
                    newItem: CategoryStaccatoUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: CategoryStaccatoUiModel,
                    newItem: CategoryStaccatoUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}
