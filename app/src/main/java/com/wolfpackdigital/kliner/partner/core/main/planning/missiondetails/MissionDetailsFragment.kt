package com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails

import android.view.Gravity
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.wolfpackdigital.kliner.partner.MissionDetailsBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.dialogs.CancelMissionBottomSheetDialog
import com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.dialogs.EndMissionBottomSheetDialog
import com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.dialogs.StartMissionBottomSheetDialog
import com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.dialogs.TwillioInfoBottomSheetDialog
import com.wolfpackdigital.kliner.partner.shared.base.BaseActivity
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.downloadFile
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.navController
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.snackBar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MissionDetailsFragment : BaseFragment<MissionDetailsBinding, MissionDetailsViewModel>(
    R.layout.fr_mission_details,
    Slide(Gravity.BOTTOM),
    Slide(Gravity.BOTTOM)
) {
    private val args by navArgs<MissionDetailsFragmentArgs>()

    override val viewModel by viewModel<MissionDetailsViewModel>(parameters = {
        parametersOf(args.missionID)
    })

    private val tooEarlyMissionDialog by lazy {
        StartMissionBottomSheetDialog.getInstance(StartMissionBottomSheetDialog.DialogType.TOO_EARLY)
    }

    private val startMissionDialog by lazy {
        StartMissionBottomSheetDialog.getInstance(StartMissionBottomSheetDialog.DialogType.START)
    }

    private val endMissionDialog by lazy {
        EndMissionBottomSheetDialog.getInstance()
    }

    private val cancelMissionDialog by lazy {
        CancelMissionBottomSheetDialog.getInstance()
    }

    private val twillioInfoDialog by lazy {
        TwillioInfoBottomSheetDialog.getInstance()
    }

    override fun setupViews() {
        setupObservers()
    }

    @Suppress("ComplexMethod", "LongMethod")
    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is MissionDetailsViewModel.Command.LaunchIntent -> startActivity(it.intent)
                is MissionDetailsViewModel.Command.ShowStartMissionDialog ->
                    startMissionDialog.show(
                        childFragmentManager,
                        StartMissionBottomSheetDialog::class.simpleName +
                                startMissionDialog.type
                    )
                is MissionDetailsViewModel.Command.ShowTooEarlyDialog -> {
                    tooEarlyMissionDialog.show(
                        childFragmentManager,
                        StartMissionBottomSheetDialog::class.simpleName +
                                tooEarlyMissionDialog.type
                    )
                }
                is MissionDetailsViewModel.Command.ShowEndMissionDialog -> {
                    endMissionDialog.show(
                        childFragmentManager,
                        EndMissionBottomSheetDialog::class.simpleName
                    )
                }
                is MissionDetailsViewModel.Command.ShowCancelMissionDialog -> {
                    cancelMissionDialog.show(
                        childFragmentManager,
                        CancelMissionBottomSheetDialog::class.simpleName
                    )
                }
                is MissionDetailsViewModel.Command.ShowTwillioInfoDialog -> {
                    twillioInfoDialog.show(
                        childFragmentManager,
                        TwillioInfoBottomSheetDialog::class.simpleName
                    )
                }
                is MissionDetailsViewModel.Command.DownloadFile ->
                    requireContext().downloadFile(it.uri)
            }
        }

        viewModel.mission.observe(viewLifecycleOwner) { mission ->
            (activity as? BaseActivity<*, *>)?.loadColoredStatusBar(
                when {
                    mission.isFinished -> R.color.colorGreen
                    mission.isCancelled -> R.color.redGradientColor
                    else -> R.color.colorAccent
                }
            )
        }

        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>(Constants.REFRESH_MISSION)
            ?.observe(viewLifecycleOwner) {
                if (it)
                    viewModel.fetchMissionDetails()
            }

        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>(Constants.SHOW_MESSAGE)
            ?.observe(viewLifecycleOwner) { message ->
                snackBar(message)
            }
    }
}
