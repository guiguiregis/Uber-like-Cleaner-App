package com.wolfpackdigital.kliner.partner.shared.noNetwork

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.wolfpackdigital.kliner.partner.BR
import com.wolfpackdigital.kliner.partner.NoNetworkBinding
import com.wolfpackdigital.kliner.partner.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class NoNetworkDialog : DialogFragment() {
    private val viewModel by viewModel<NoNetworkViewModel>()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.NoNetworkDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<NoNetworkBinding>(
        layoutInflater,
        R.layout.dialog_no_network,
        null,
        false
    ).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                NoNetworkViewModel.Command.CloseDialog -> dismiss()
                else -> {
                }
            }
        }
    }

    companion object {

        fun getInstance() = NoNetworkDialog()
    }
}
