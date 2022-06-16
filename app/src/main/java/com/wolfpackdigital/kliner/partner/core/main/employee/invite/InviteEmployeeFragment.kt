package com.wolfpackdigital.kliner.partner.core.main.employee.invite

import androidx.navigation.fragment.navArgs
import com.wolfpackdigital.kliner.partner.InviteEmployeeBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class InviteEmployeeFragment :
    BaseFragment<InviteEmployeeBinding, InviteEmployeeViewModel>(R.layout.fr_invite_employee) {

    // Properties
    private val args by navArgs<InviteEmployeeFragmentArgs>()
    override val viewModel by viewModel<InviteEmployeeViewModel>() {
        parametersOf(args.isOnboarding)
    }

    // Lifecycle

    override fun setupViews() {
    }
}
