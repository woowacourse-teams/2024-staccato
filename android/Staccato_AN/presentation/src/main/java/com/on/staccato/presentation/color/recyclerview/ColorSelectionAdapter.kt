package com.on.staccato.presentation.color.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.on.staccato.presentation.color.CategoryColor
import com.on.staccato.presentation.databinding.ItemColorSelectionBinding

class ColorSelectionAdapter(
    private val items: List<CategoryColor>,
    private val handler: ColorSelectionHandler,
) : Adapter<ColorSelectionViewHolder>() {
    private var fromIndex: Int? = 0

    fun changeSelectedItem(newItem: CategoryColor) {
        if (fromIndex != newItem.index) {
            notifyItemChanged(fromIndex ?: 0)
            notifyItemChanged(newItem.index)
        }
        fromIndex = newItem.index
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ColorSelectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemColorSelectionBinding.inflate(inflater, parent, false)
        return ColorSelectionViewHolder(binding, handler)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: ColorSelectionViewHolder,
        position: Int,
    ) {
        holder.bind(items[position], position == fromIndex)
    }
}
