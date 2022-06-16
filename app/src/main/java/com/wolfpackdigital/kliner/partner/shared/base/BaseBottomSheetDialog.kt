package com.wolfpackdigital.kliner.partner.shared.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wolfpackdigital.kliner.partner.BR
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.utils.views.LoadingDialog

abstract class BaseBottomSheetDialog<BINDING : ViewDataBinding, VIEW_MODEL : BaseViewModel>(
    @LayoutRes private val layoutRes: Int
) : BottomSheetDialogFragment() {

    protected var dialogBinding: BINDING? = null
    protected abstract val viewModel: VIEW_MODEL
    protected val loadingDialog: LoadingDialog?
        get() = (activity as? BaseActivity<*, *>)?.loadingDialog

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = DataBindingUtil.inflate<BINDING>(
        layoutInflater,
        layoutRes,
        null,
        false
    ).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        dialogBinding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLoadingDialog()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        dialogBinding = null
    }

    private fun observeLoadingDialog() {
        viewModel.apiCallsCount.observe(viewLifecycleOwner) {
            if (it == 0)
                loadingDialog?.dismiss()
            else
                loadingDialog?.show()
        }
    }
}
