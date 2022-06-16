package com.wolfpackdigital.kliner.partner.core.auth.registration.face

import com.wolfpackdigital.kliner.partner.FaceRecognitionBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FaceRecognitionFragment :
    BaseFragment<FaceRecognitionBinding, FaceRecognitionViewModel>(R.layout.fr_face_recognition) {

    // Properties

    override val viewModel by viewModel<FaceRecognitionViewModel>()

    // Lifecycle

    override fun setupViews() {
    }

    companion object {
        fun newInstance() = FaceRecognitionFragment()
    }
}
