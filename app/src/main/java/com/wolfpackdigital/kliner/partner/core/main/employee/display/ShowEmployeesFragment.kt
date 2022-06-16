package com.wolfpackdigital.kliner.partner.core.main.employee.display

import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.ShowEmployeesBinding
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShowEmployeesFragment :
    BaseFragment<ShowEmployeesBinding, ShowEmployeesViewModel>(R.layout.fr_show_employees) {

    // Properties

    override val viewModel by viewModel<ShowEmployeesViewModel>()

    private val employeesAdapter by lazy { EmployeesAdapter() }

    // Lifecycle

    override fun setupViews() {
        binding?.rvEmployees?.apply {
            adapter = employeesAdapter
        }
        setupObservers()
        viewModel.fetchCleaners()
    }

    private fun setupObservers() {
        viewModel.cleaners.observe(viewLifecycleOwner, employeesAdapter::submitList)
    }
}
