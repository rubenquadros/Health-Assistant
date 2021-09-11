package com.ruben.healthassistant.presentation.dialog

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ruben.healthassistant.R
import com.ruben.healthassistant.databinding.FragmentPermissionDialogBinding
import com.ruben.healthassistant.core.viewLifecycle

class PermissionDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPermissionDialogBinding by viewLifecycle()

    override fun getTheme(): Int = R.style.Theme_HealthAssistant_BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionDialogBinding.inflate(inflater, container, false)
        isCancelable = false
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.permissionPositiveBtn.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", requireContext().packageName, null)
            startActivity(intent)
            dismissAllowingStateLoss()
        }

        _binding.permissionNegativeBtn.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    override fun onStop() {
        dismissAllowingStateLoss()
        super.onStop()
    }

    companion object {
        fun newInstance(): PermissionDialogFragment = PermissionDialogFragment()
    }
}