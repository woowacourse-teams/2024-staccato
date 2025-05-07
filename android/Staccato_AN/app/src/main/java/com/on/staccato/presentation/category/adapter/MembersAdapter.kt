package com.on.staccato.presentation.category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.databinding.ItemMemberInviteBinding
import com.on.staccato.databinding.ItemMemberProfileBinding
import com.on.staccato.domain.model.Member
import com.on.staccato.presentation.category.adapter.MembersViewHolder.MemberInviteViewHolder
import com.on.staccato.presentation.category.adapter.MembersViewHolder.MemberProfileViewHolder
import com.on.staccato.presentation.category.adapter.MembersViewType.MEMBER_INVITE
import com.on.staccato.presentation.category.adapter.MembersViewType.MEMBER_PROFILE

class MembersAdapter(private val memberInviteHandler: MemberInviteHandler) :
    ListAdapter<Member, MembersViewHolder>(diffUtil) {
    init {
        submitList(listOf(inviteButton))
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == INVITE_BUTTON_POSITION) {
            MEMBER_INVITE.viewType
        } else {
            MEMBER_PROFILE.viewType
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MembersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (MembersViewType.from(viewType)) {
            MEMBER_INVITE -> {
                val binding = ItemMemberInviteBinding.inflate(inflater, parent, false)
                MemberInviteViewHolder(binding, memberInviteHandler)
            }

            MEMBER_PROFILE -> {
                val binding = ItemMemberProfileBinding.inflate(inflater, parent, false)
                MemberProfileViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(
        holder: MembersViewHolder,
        position: Int,
    ) {
        when (holder) {
            is MemberInviteViewHolder -> holder.bind()
            is MemberProfileViewHolder -> holder.bind(getItem(position))
        }
    }

    fun updateMembers(members: List<Member>) {
        submitList(listOf(inviteButton) + members)
    }

    companion object {
        const val INVITE_BUTTON_POSITION = 0

        val diffUtil =
            object : DiffUtil.ItemCallback<Member>() {
                override fun areItemsTheSame(
                    oldItem: Member,
                    newItem: Member,
                ): Boolean = oldItem.memberId == newItem.memberId

                override fun areContentsTheSame(
                    oldItem: Member,
                    newItem: Member,
                ): Boolean = oldItem == newItem
            }

        val inviteButton by lazy {
            Member(
                0,
                "",
                null,
            )
        }
    }
}
