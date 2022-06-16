package com.wolfpackdigital.kliner.partner.core.auth.confirmation

import android.view.KeyEvent
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.wolfpackdigital.kliner.partner.CodeConfirmationBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.auth.dialogs.WorkWithKlinerBottomSheetDialog
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CodeConfirmationFragment :
    BaseFragment<CodeConfirmationBinding, CodeConfirmationViewModel>(R.layout.fr_code_confirmation) {

    // Properties

    override val viewModel by viewModel<CodeConfirmationViewModel> {
        parametersOf(args.phoneNumber, args.isEditMode)
    }
    private val args by navArgs<CodeConfirmationFragmentArgs>()

    private val dialog by lazy { WorkWithKlinerBottomSheetDialog() }

    // Lifecycle

    override fun setupViews() {
        binding?.pinView1?.post {
            binding?.pinView1?.focus()
        }
        binding?.tvRenewCode?.paint?.isUnderlineText = true
        setupObservers()
    }

    private fun setupObservers() {
        setupPinObservers()

        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                CodeConfirmationViewModel.Command.ShowWorkWithKlinerDialog -> {
                    if (!dialog.isVisible && !dialog.isAdded)
                        dialog.showNow(
                            childFragmentManager,
                            WorkWithKlinerBottomSheetDialog::class.simpleName
                        )
                }
                CodeConfirmationViewModel.Command.HideDialog ->
                    dialog.dismiss()
                CodeConfirmationViewModel.Command.FocusFirstLetter ->
                    binding?.pinView1?.focus()
            }
        }
    }

    private fun setupPinObservers() {
        binding?.pinView2?.binding?.etContent?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL &&
                event.action == KeyEvent.ACTION_DOWN &&
                (v as TextInputEditText).text.isNullOrEmpty()
            ) {
                binding?.pinView1?.focus()
                return@setOnKeyListener true
            }
            false
        }

        binding?.pinView3?.binding?.etContent?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL &&
                event.action == KeyEvent.ACTION_DOWN &&
                (v as TextInputEditText).text.isNullOrEmpty()
            ) {
                binding?.pinView2?.focus()
                return@setOnKeyListener true
            }
            false
        }

        binding?.pinView4?.binding?.etContent?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL &&
                event.action == KeyEvent.ACTION_DOWN &&
                (v as TextInputEditText).text.isNullOrEmpty()
            ) {
                binding?.pinView3?.focus()
                return@setOnKeyListener true
            }
            false
        }

        viewModel.letter1.observe(viewLifecycleOwner) {
            if (it.length == 1)
                binding?.pinView2?.focus()
        }

        viewModel.letter2.observe(viewLifecycleOwner) {
            if (it.length == 1)
                binding?.pinView3?.focus()
        }

        viewModel.letter3.observe(viewLifecycleOwner) {
            if (it.length == 1)
                binding?.pinView4?.focus()
        }

        viewModel.finalCode.observe(viewLifecycleOwner) {
            viewModel.verifyCode(it)
        }
    }
}
