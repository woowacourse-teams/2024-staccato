package com.on.staccato.presentation.category.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.on.staccato.domain.model.Participant
import com.on.staccato.presentation.databinding.ItemMemberInviteBinding
import com.on.staccato.presentation.databinding.ItemMemberProfileBinding

sealed class MembersViewHolder(binding: ViewDataBinding) : ViewHolder(binding.root) {
    class MemberInviteViewHolder(
        private val binding: ItemMemberInviteBinding,
        private val memberInviteHandler: MemberInviteHandler,
    ) : MembersViewHolder(binding) {
        fun bind() {
            binding.handler = memberInviteHandler
        }
    }

    class MemberProfileViewHolder(
        private val binding: ItemMemberProfileBinding,
    ) : MembersViewHolder(binding) {
        fun bind(member: Participant) {
            binding.member = member
        }
    }
}
