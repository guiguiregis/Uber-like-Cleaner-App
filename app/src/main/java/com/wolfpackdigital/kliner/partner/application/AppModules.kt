package com.wolfpackdigital.kliner.partner.application

import com.wolfpackdigital.kliner.partner.core.auth.AuthActivityViewModel
import com.wolfpackdigital.kliner.partner.core.auth.confirmation.CodeConfirmationViewModel
import com.wolfpackdigital.kliner.partner.core.auth.dialogs.contract.SignContractViewModel
import com.wolfpackdigital.kliner.partner.core.auth.dialogs.country.SelectCountryViewModel
import com.wolfpackdigital.kliner.partner.core.auth.onboarding.OnboardingViewModel
import com.wolfpackdigital.kliner.partner.core.auth.onboarding.employee.OnboardingEmployeeViewModel
import com.wolfpackdigital.kliner.partner.core.auth.onboarding.placeholder.OnboardingPlaceholderFragment
import com.wolfpackdigital.kliner.partner.core.auth.onboarding.placeholder.OnboardingPlaceholderViewModel
import com.wolfpackdigital.kliner.partner.core.auth.phone.PhoneConfirmationViewModel
import com.wolfpackdigital.kliner.partner.core.auth.registration.RegistrationViewModel
import com.wolfpackdigital.kliner.partner.core.auth.registration.details.CompanyDetailsViewModel
import com.wolfpackdigital.kliner.partner.core.auth.registration.face.FaceRecognitionViewModel
import com.wolfpackdigital.kliner.partner.core.auth.registration.idcard.IdentityCardViewModel
import com.wolfpackdigital.kliner.partner.core.auth.registration.services.CompanyServicesViewModel
import com.wolfpackdigital.kliner.partner.core.auth.registration.user.CleanerProfileViewModel
import com.wolfpackdigital.kliner.partner.core.auth.registration.verification.UserVerificationViewModel
import com.wolfpackdigital.kliner.partner.core.auth.splash.SplashScreenViewModel
import com.wolfpackdigital.kliner.partner.core.auth.welcome.WelcomeViewModel
import com.wolfpackdigital.kliner.partner.core.main.MainActivityViewModel
import com.wolfpackdigital.kliner.partner.core.main.confirmations.geolocation.EnableGeolocationViewModel
import com.wolfpackdigital.kliner.partner.core.main.confirmations.notifications.EnableNotificationsViewModel
import com.wolfpackdigital.kliner.partner.core.main.dashboard.DashboardViewModel
import com.wolfpackdigital.kliner.partner.core.main.dialogs.EmployeeDetailsViewModel
import com.wolfpackdigital.kliner.partner.core.main.employee.add.AddEmployeeViewModel
import com.wolfpackdigital.kliner.partner.core.main.employee.display.ShowEmployeesViewModel
import com.wolfpackdigital.kliner.partner.core.main.employee.invite.InviteEmployeeViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.MoreViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.activity.ActivityViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.profile.ProfileDetailsViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.profile.account.AddBankAccountViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.profile.activityarea.ActivityAreaViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.profile.company.CompanyProfileViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.profile.company.address.CompanyAddressViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.profile.company.name.CompanyNameViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.profile.dialogs.delete.DeleteAccountViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.profile.dialogs.logout.LogoutViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.profile.gdpr.GdprViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.unassignedmissions.UnassignedMissionsViewModel
import com.wolfpackdigital.kliner.partner.core.main.offers.OffersViewModel
import com.wolfpackdigital.kliner.partner.core.main.offers.assign.AssignEmployeeViewModel
import com.wolfpackdigital.kliner.partner.core.main.planning.PlanningViewModel
import com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.MissionDetailsViewModel
import com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.clientinstructions.ClientInstructionsViewModel
import com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.dialogs.ChangeMissionRecurrenceViewModel
import com.wolfpackdigital.kliner.partner.data.api.ApiProvider
import com.wolfpackdigital.kliner.partner.data.api.RefreshTokenAuthenticator
import com.wolfpackdigital.kliner.partner.data.repo.AuthRepo
import com.wolfpackdigital.kliner.partner.data.repo.AuthRepoI
import com.wolfpackdigital.kliner.partner.data.repo.MainRepo
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.noNetwork.NoNetworkViewModel
import com.wolfpackdigital.kliner.partner.shared.useCases.AcceptOfferUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.AssignEmployeeUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.BankAccount
import com.wolfpackdigital.kliner.partner.shared.useCases.CallCustomerUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.CancelMissionUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.ConfirmMissionUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.ConfirmPhoneNumberUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateBankAccountUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateCompanyUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateEmployeeProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.DeleteAccountUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.DeleteEmployeeUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.EnableNotificationsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.FinishMissionUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetActivityRecordsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetBankAccountUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCleaningCompanyUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCompanyActivityUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCompanyCleanersUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCountriesUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetDashboardTipsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetEmployeeProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetMissionByIdUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetMissionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetMoreSectionItemsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetOffersUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetOptionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetTextUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetUnassignedMissionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetUnconfirmedMissionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.Kind
import com.wolfpackdigital.kliner.partner.shared.useCases.LogoutUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.NotificationsSettingsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.RefreshTokenUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.RefuseOfferUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.ResendCodeUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.SearchAddressUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.SendFCMTokenUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.SignContractUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.SignInUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.StartMissionUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateBankAccountUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateCleaningCompanyUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateEmployeeProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdatePhoneNumberUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.UploadUserPhotoUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.VerifyPhoneUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModules {
    private val viewModels = module {
        viewModel { MainActivityViewModel() }
        viewModel { SplashScreenViewModel(get()) }
        viewModel { AuthActivityViewModel() }
        viewModel { OnboardingViewModel() }
        viewModel { (state: OnboardingPlaceholderFragment.State) ->
            OnboardingPlaceholderViewModel(state)
        }
        viewModel { WelcomeViewModel() }
        viewModel { (prefix: String, phoneNumber: String?) ->
            PhoneConfirmationViewModel(
                prefix,
                phoneNumber,
                get(), get()
            )
        }
        viewModel { (phoneNumber: String?, isEditMode: Boolean) ->
            CodeConfirmationViewModel(
                phoneNumber,
                isEditMode,
                get(), get(), get(), get()
            )
        }
        viewModel { (count: Int) -> RegistrationViewModel(count) }
        viewModel { (kind: Kind) -> CleanerProfileViewModel(kind, get(), get(), get(), get()) }
        viewModel { FaceRecognitionViewModel() }
        viewModel { CompanyDetailsViewModel(get(), get(), get()) }
        viewModel { IdentityCardViewModel(get(), get()) }
        viewModel { SelectCountryViewModel(get()) }
        viewModel { CompanyServicesViewModel(get(), get(), get()) }
        viewModel { UserVerificationViewModel(get()) }
        viewModel { SignContractViewModel(get()) }
        viewModel { (id: Int) -> OnboardingEmployeeViewModel(id, get(), get(), get()) }
        viewModel { PlanningViewModel(get()) }
        viewModel { DashboardViewModel(get(), get(), get(), get(), get(), get(), get()) }
        viewModel { OffersViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { MoreViewModel(get()) }
        viewModel { (isOnboarding: Boolean) ->
            AddEmployeeViewModel(isOnboarding, get(), get(), get())
        }
        viewModel { (isOnboarding: Boolean) ->
            InviteEmployeeViewModel(isOnboarding)
        }
        viewModel { CompanyAddressViewModel(get(), get()) }
        viewModel { CompanyNameViewModel(get()) }
        viewModel { ProfileDetailsViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { CompanyProfileViewModel() }
        viewModel { (bankAccount: BankAccount) ->
            AddBankAccountViewModel(
                bankAccount,
                get(),
                get()
            )
        }
        viewModel { LogoutViewModel(get()) }
        viewModel { DeleteAccountViewModel(get()) }
        viewModel { ShowEmployeesViewModel(get()) }
        viewModel { ActivityViewModel(get(), get()) }
        viewModel { (missionId: Int, isRecurrentMission: Boolean, isRecurrenceChange: Boolean) ->
            AssignEmployeeViewModel(
                missionId,
                isRecurrentMission,
                isRecurrenceChange,
                get(),
                get()
            )
        }
        viewModel { UnassignedMissionsViewModel(get()) }
        viewModel { (id: Int) ->
            MissionDetailsViewModel(
                id,
                get(), get(), get(), get(), get()
            )
        }
        viewModel { EnableNotificationsViewModel(get()) }
        viewModel { EnableGeolocationViewModel() }
        viewModel { ClientInstructionsViewModel() }
        viewModel { (id: Int) -> EmployeeDetailsViewModel(id, get(), get(), get(), get(), get()) }
        viewModel { ActivityAreaViewModel(get(), get()) }
        viewModel { NoNetworkViewModel() }
        viewModel { (missionId: Int, employeeId: Int) ->
            ChangeMissionRecurrenceViewModel(
                missionId,
                employeeId,
                get()
            )
        }
    }

    private val apiModule = module {
        single { RefreshTokenAuthenticator(get()) }
        single { ApiProvider.provideOkHttpClient(get()) }
        single { ApiProvider.provideRetrofit(get()) }
        single { ApiProvider.provideAuthAPI(get()) }
        single { ApiProvider.provideMainAPI(get()) }
    }

    private val repoModule = module {
        single<AuthRepoI> { AuthRepo() }
        single<MainRepoI> { MainRepo() }
    }

    private val useCases = module {
        single { RefreshTokenUseCase(get()) }
        single { SignInUseCase(get()) }
        single { VerifyPhoneUseCase(get()) }
        single { ResendCodeUseCase(get()) }
        single { GetCountriesUseCase(get()) }
        single { SearchAddressUseCase(androidContext()) }
        single { CreateCleanerProfileUseCase(get()) }
        single { UploadUserPhotoUseCase(androidContext(), get()) }
        single { CreateCompanyUseCase(get()) }
        single { UpdateCleanerProfileUseCase(get()) }
        single { GetCleanerProfileUseCase(get()) }
        single { GetOptionsUseCase(get()) }
        single { GetTextUseCase(get()) }
        single { GetCleaningCompanyUseCase(get()) }
        single { GetDashboardTipsUseCase() }
        single { CreateEmployeeProfileUseCase(get()) }
        single { GetOffersUseCase(get()) }
        single { AcceptOfferUseCase(get()) }
        single { RefuseOfferUseCase(get()) }
        single { UpdateEmployeeProfileUseCase(get()) }
        single { GetCompanyCleanersUseCase(get()) }
        single { SignContractUseCase(get()) }
        single { GetMoreSectionItemsUseCase() }
        single { GetMissionsUseCase(get()) }
        single { GetUnconfirmedMissionsUseCase(get()) }
        single { GetUnassignedMissionsUseCase(get()) }
        single { CreateBankAccountUseCase(get()) }
        single { LogoutUseCase(get()) }
        single { DeleteAccountUseCase(get()) }
        single { UpdateBankAccountUseCase(get()) }
        single { GetBankAccountUseCase(get()) }
        single { GetCompanyActivityUseCase(get()) }
        single { UpdateCleaningCompanyUseCase(get()) }
        single { SendFCMTokenUseCase(get()) }
        single { GetMissionByIdUseCase(get()) }
        single { NotificationsSettingsUseCase(get()) }
        single { EnableNotificationsUseCase(get()) }
        single { ConfirmMissionUseCase(get()) }
        single { StartMissionUseCase(get()) }
        single { FinishMissionUseCase(get()) }
        single { CancelMissionUseCase(get()) }
        single { CallCustomerUseCase(get()) }
        single { DeleteEmployeeUseCase(get()) }
        single { GetEmployeeProfileUseCase(get()) }
        single { AssignEmployeeUseCase(get()) }
        single { UpdatePhoneNumberUseCase(get()) }
        single { ConfirmPhoneNumberUseCase(get()) }
        single { GetActivityRecordsUseCase(get()) }
        single { GdprViewModel() }
    }

    val modules = listOf(viewModels, apiModule, repoModule, useCases)
}
