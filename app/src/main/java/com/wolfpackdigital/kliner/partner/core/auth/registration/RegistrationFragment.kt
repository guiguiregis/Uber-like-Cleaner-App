package com.wolfpackdigital.kliner.partner.core.auth.registration

import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.RegistrationBinding
import com.wolfpackdigital.kliner.partner.core.auth.registration.company.CompanyRegistrationAdapter
import com.wolfpackdigital.kliner.partner.core.auth.registration.selfemployed.SelfEmployedRegistrationAdapter
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.useCases.Kind
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RegistrationFragment :
    BaseFragment<RegistrationBinding, RegistrationViewModel>(R.layout.fr_registration) {

    // Properties

    override val viewModel by viewModel<RegistrationViewModel> {
        parametersOf(adapter?.itemCount)
    }

    private val args by navArgs<RegistrationFragmentArgs>()
    private val adapter by lazy {
        when (args.userFlowData.kind) {
            Kind.EMPLOYER -> CompanyRegistrationAdapter(this)
            Kind.INDEPENDENT -> SelfEmployedRegistrationAdapter(this)
            else -> null
        }
    }

    // Lifecycle

    override fun setupViews() {
        binding?.vpRegistration?.apply {
            adapter = this@RegistrationFragment.adapter
            isUserInputEnabled = false
            registerOnPageChangeCallback(
                object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        viewModel.onPageSelected(position)
                    }
                }
            )
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.currentPage.observe(viewLifecycleOwner) {
            binding?.vpRegistration?.apply {
                this@RegistrationFragment.adapter?.itemCount?.let { itemCount ->
                    if (currentItem == it || currentItem >= itemCount) return@apply
                } ?: return@apply
                setCurrentItem(it, true)
            }
        }
    }
}
