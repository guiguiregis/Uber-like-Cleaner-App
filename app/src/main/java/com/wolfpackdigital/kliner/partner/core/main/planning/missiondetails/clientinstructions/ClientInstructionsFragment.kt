package com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.clientinstructions

import androidx.navigation.fragment.navArgs
import com.wolfpackdigital.kliner.partner.ClientInstructionsBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ClientInstructionsFragment :
    BaseFragment<ClientInstructionsBinding, ClientInstructionsViewModel>(R.layout.fr_client_instructions) {

    private val args by navArgs<ClientInstructionsFragmentArgs>()
    override val viewModel by viewModel<ClientInstructionsViewModel>()
    private val clientInstructionsAdapter by lazy { InstructionsAdapter() }

    override fun setupViews() {
        binding?.rvClientInstructions?.adapter = clientInstructionsAdapter
        clientInstructionsAdapter.submitList(args.instructions.toList())
    }
}
