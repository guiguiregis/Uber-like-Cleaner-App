package com.wolfpackdigital.kliner.partner.core.auth.registration.company

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wolfpackdigital.kliner.partner.core.auth.registration.details.CompanyDetailsFragment
import com.wolfpackdigital.kliner.partner.core.auth.registration.idcard.IdentityCardFragment
import com.wolfpackdigital.kliner.partner.core.auth.registration.services.CompanyServicesFragment
import com.wolfpackdigital.kliner.partner.core.auth.registration.user.CleanerProfileFragment
import com.wolfpackdigital.kliner.partner.shared.useCases.Kind

class CompanyRegistrationAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount() = COUNT

    @Suppress("MagicNumber")
    override fun createFragment(position: Int) = when (position) {
        0 -> CleanerProfileFragment.newInstance(Kind.EMPLOYER)
        1 -> CompanyDetailsFragment.newInstance()
        2 -> IdentityCardFragment.newInstance()
        3 -> CompanyServicesFragment.newInstance()
        else -> Fragment()
    }

    companion object {
        const val COUNT = 4
    }
}
