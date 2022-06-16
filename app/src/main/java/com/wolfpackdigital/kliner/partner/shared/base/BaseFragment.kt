package com.wolfpackdigital.kliner.partner.shared.base

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import androidx.transition.Transition
import com.wolfpackdigital.kliner.partner.BR
import com.wolfpackdigital.kliner.partner.shared.noNetwork.NoNetworkDialog
import com.wolfpackdigital.kliner.partner.shared.utils.Variables
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.navController
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.snackBar
import com.wolfpackdigital.kliner.partner.shared.utils.views.LoadingDialog

abstract class BaseFragment<BINDING : ViewDataBinding, VIEW_MODEL : BaseViewModel>(
    @LayoutRes private val layoutResource: Int,
    private val enterTrans: Transition = Fade(),
    private val exitTrans: Transition = Fade()
) : Fragment() {

    // In the case of fragments, simply having the binding as a
    // lateinit can lead to memory leaks because it holds context
    protected var binding: BINDING? = null
        private set
    protected abstract val viewModel: VIEW_MODEL
    private val loadingDialog: LoadingDialog?
        get() = (activity as? BaseActivity<*, *>)?.loadingDialog
    private val networkDialog: NoNetworkDialog?
        get() = (activity as? BaseActivity<*, *>)?.networkDialog

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = enterTrans
        exitTransition = exitTrans
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DataBindingUtil.inflate<BINDING>(
        layoutInflater,
        layoutResource,
        null,
        false
    ).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        binding = it
    }.root

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (binding?.root as? ViewGroup)?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)
        setupViews()
        observeLoadingDialog()
        observeNetworkDialog()
        observeBaseCommands()
    }

    private fun observeBaseCommands() {
        viewModel.baseCmd.observe(viewLifecycleOwner) {
            when (it) {
                is BaseCommand.ShowToastById ->
                    Toast.makeText(context ?: return@observe, it.stringId, Toast.LENGTH_SHORT)
                        .show()
                is BaseCommand.ShowToast ->
                    Toast.makeText(context ?: return@observe, it.message, Toast.LENGTH_SHORT)
                        .show()
                is BaseCommand.ShowSnackbarById -> snackBar(getString(it.stringId))
                is BaseCommand.ShowSnackbar -> snackBar(it.message)
                is BaseCommand.PerformNavAction -> navigateToAction(it)
                is BaseCommand.PerformNavById -> navController?.navigate(
                    it.destinationId,
                    it.bundle,
                    it.options,
                    it.extras
                )
                is BaseCommand.GoBack -> navController?.popBackStack()
            }
        }
    }

    private fun navigateToAction(navAction: BaseCommand.PerformNavAction) {
        navAction.extras?.let { extras ->
            navController?.navigate(navAction.direction, extras)
        } ?: run {
            navController?.navigate(navAction.direction)
        }
    }

    private fun observeLoadingDialog() {
        viewModel.apiCallsCount.observe(viewLifecycleOwner) {
            if (it == 0)
                loadingDialog?.dismiss()
            else
                loadingDialog?.show()
        }
    }

    private fun observeNetworkDialog() {
        Variables.isNetworkConnectedObservable.observe(viewLifecycleOwner) { isNetworkConnected ->
            if (isNetworkConnected == null) return@observe
            if (isNetworkConnected) {
                if (networkDialog?.isVisible == true && networkDialog?.isAdded == true) {
                    networkDialog?.dismiss()
                }
            } else if (networkDialog?.isVisible == false && networkDialog?.isAdded == false)
                networkDialog?.show(
                    childFragmentManager,
                    NoNetworkDialog::class.simpleName
                )
        }
    }

    abstract fun setupViews()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /* Since the binding is nullable, in some cases,
    like after receiving a response from an api call,
    the fragment might be destroyed so we need to check it's
    nullability, in cases where we are sure it is not null (onCreateView)
    we can use this helper function to avoid null checks */
    protected fun requireBinding(): BINDING = binding
        ?: throw NullPointerException("View was destroyed and the binding is null")
}

private const val TAG = "BaseFragment"

abstract class BaseAndroidFragment<BINDING : ViewDataBinding, VIEW_MODEL : BaseAndroidViewModel>(
    @LayoutRes private val layoutResource: Int,
    private val enterTrans: android.transition.Transition = android.transition.Fade(),
    private val exitTrans: android.transition.Transition = android.transition.Fade()
) : Fragment() {

    // In the case of fragments, simply having the binding as a
    // lateinit can lead to memory leaks because it holds context
    protected var binding: BINDING? = null
        private set
    protected abstract val viewModel: VIEW_MODEL
    protected val loadingDialog: LoadingDialog?
        get() = (activity as? BaseActivity<*, *>)?.loadingDialog

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = enterTrans
        exitTransition = exitTrans
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        DataBindingUtil.inflate<BINDING>(
            layoutInflater,
            layoutResource,
            null,
            false
        ).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.setVariable(BR.viewModel, viewModel)
            binding = it
        }.root

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeLoadingDialog()
    }

    private fun observeLoadingDialog() {
        viewModel.apiCallsCount.observe(viewLifecycleOwner) {
            if (it == 0)
                loadingDialog?.dismiss()
            else
                loadingDialog?.show()
        }
    }

    abstract fun setupViews()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /* Since the binding is nullable, in some cases,
    like after receiving a response from an api call,
    the fragment might be destroyed so we need to check it's
    nullability, in cases where we are sure it is not null (onCreateView)
    we can use this helper function to avoid null checks */
    protected fun requireBinding(): BINDING = binding
        ?: throw NullPointerException("View was destroyed and the binding is null")
}
