package com.woowacourse.staccato.presentation.moment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.databinding.ItemMyVisitLogBinding
import com.woowacourse.staccato.databinding.ItemVisitDefaultBinding
import com.woowacourse.staccato.presentation.moment.model.MomentDetailUiModel

class MomentAdapter(private val items: MutableList<MomentDetailUiModel> = mutableListOf()) :
    RecyclerView.Adapter<MomentViewHolder>() {
    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return if (position == MOMENT_DEFAULT_POSITION) {
            MomentViewHolderType.MOMENT_DEFAULT.value
        } else {
            MomentViewHolderType.MY_COMMENTS.value
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MomentViewHolder {
        return when (MomentViewHolderType.from(viewType)) {
            MomentViewHolderType.MOMENT_DEFAULT -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemVisitDefaultBinding.inflate(inflater, parent, false)
                MomentViewHolder.MomentDefaultViewHolder(binding)
            }

            MomentViewHolderType.MY_COMMENTS -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMyVisitLogBinding.inflate(inflater, parent, false)
                MomentViewHolder.MyCommentViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(
        holder: MomentViewHolder,
        position: Int,
    ) {
        if (holder is MomentViewHolder.MomentDefaultViewHolder) {
            holder.bind(items[position] as MomentDetailUiModel.MomentDefaultUiModel)
        }
        if (holder is MomentViewHolder.MyCommentViewHolder) {
            holder.bind(items[position] as MomentDetailUiModel.CommentsUiModel)
        }
    }

    fun updateMomentDefault(newMomentDefault: MomentDetailUiModel.MomentDefaultUiModel) {
        val result = mutableListOf<MomentDetailUiModel>(newMomentDefault)
        result.addAll(items.drop(MOMENT_DEFAULT_ITEM_SIZE))
        replaceAllItems(result)
        notifyItemChanged(MOMENT_DEFAULT_POSITION)
    }

    fun updateVisitLogs(newComments: List<MomentDetailUiModel.CommentsUiModel>) {
        val result = items.take(MOMENT_DEFAULT_ITEM_SIZE).toMutableList()
        result.addAll(newComments)
        replaceAllItems(result)
        notifyItemRangeInserted(MOMENT_DEFAULT_ITEM_SIZE, result.size)
    }

    private fun replaceAllItems(result: MutableList<MomentDetailUiModel>) {
        items.clear()
        items.addAll(result)
    }

    companion object {
        private const val MOMENT_DEFAULT_POSITION = 0
        private const val MOMENT_DEFAULT_ITEM_SIZE = 1
    }
}
