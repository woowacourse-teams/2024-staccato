package com.woowacourse.staccato.presentation.moment.feeling

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.databinding.ItemMomentFeelingSelectionBinding
import com.woowacourse.staccato.domain.model.Feeling
import com.woowacourse.staccato.presentation.mapper.toFeelingUiModel

class FeelingSelectionAdapter(private val feelingHandler: FeelingHandler) :
    ListAdapter<FeelingUiModel, FeelingSelectionAdapter.FeelingSelectionViewHolder>(diffUtil) {
    private val feelings = listOf(
        Feeling.HAPPY.toFeelingUiModel(),
        Feeling.ANGRY.toFeelingUiModel(),
        Feeling.SAD.toFeelingUiModel(),
        Feeling.SCARED.toFeelingUiModel(),
        Feeling.EXCITED.toFeelingUiModel(),
    )

    init {
        submitList(feelings)
    }

    class FeelingSelectionViewHolder(private val binding: ItemMomentFeelingSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            feelingUiModel: FeelingUiModel,
            feelingHandler: FeelingHandler,
        ) {
            binding.feeling = feelingUiModel
            binding.feelingHandler = feelingHandler
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FeelingSelectionViewHolder {
        val binding =
            ItemMomentFeelingSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return FeelingSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FeelingSelectionViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position], feelingHandler)
    }

    fun updateFeelings(updatedFeelings: List<FeelingUiModel>) {
        submitList(updatedFeelings)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<FeelingUiModel>() {
                override fun areContentsTheSame(
                    oldItem: FeelingUiModel,
                    newItem: FeelingUiModel,
                ): Boolean = oldItem == newItem

                override fun areItemsTheSame(
                    oldItem: FeelingUiModel,
                    newItem: FeelingUiModel,
                ): Boolean = oldItem.feeling == newItem.feeling
            }
    }
}
