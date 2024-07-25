package com.woowacourse.staccato

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.woowacourse.staccato.databinding.FragmentDeleteDialogBinding

class DeleteDialogFragment(private val dialogHandler: DialogHandler) : DialogFragment() {
    private var _binding: FragmentDeleteDialogBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return
        val context = context ?: return

        val width = context.resources.displayMetrics.widthPixels
        window.setLayout((width * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDeleteDialogBinding.inflate(inflater, container, false)
        initButtonClickListener()
        return binding.root
    }

    private fun initButtonClickListener() {
        binding.btnDeleteCancel.setOnClickListener {
            dismiss()
        }
        binding.btnDeleteConfirm.setOnClickListener {
            dialogHandler.onConfirmClicked()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DeleteDialogFragment"
    }
}

fun interface DialogHandler {
    fun onConfirmClicked()
}
