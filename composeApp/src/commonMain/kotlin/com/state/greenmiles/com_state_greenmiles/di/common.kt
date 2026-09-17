package com.state.greenmiles.com_state_greenmiles.di

import androidx.lifecycle.SavedStateHandle
import com.russhwolf.settings.Settings
import com.state.greenmiles.com_state_greenmiles.data.remote.api.AadhaarApi
import com.state.greenmiles.com_state_greenmiles.data.remote.api.AuthApi
import com.state.greenmiles.com_state_greenmiles.data.remote.api.ParcelApi
import com.state.greenmiles.com_state_greenmiles.data.remote.api.TripsApi
import com.state.greenmiles.com_state_greenmiles.data.repository.AadhaarRepositoryImpl
import com.state.greenmiles.com_state_greenmiles.data.repository.AuthRepositoryImpl
import com.state.greenmiles.com_state_greenmiles.data.repository.ParcelRepositoryImpl
import com.state.greenmiles.com_state_greenmiles.data.repository.TripsRepositoryImpl
import com.state.greenmiles.com_state_greenmiles.domain.model.AuthUseCases
import com.state.greenmiles.com_state_greenmiles.domain.repository.AadhaarRepository
import com.state.greenmiles.com_state_greenmiles.domain.repository.AuthRepository
import com.state.greenmiles.com_state_greenmiles.domain.repository.ParcelRepository
import com.state.greenmiles.com_state_greenmiles.domain.repository.TripsRepository
import com.state.greenmiles.com_state_greenmiles.domain.usecases.CompleteRegistrationUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.LoginUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.ResetPasswordUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.SendForgotPasswordOtpUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.SendRegisterOtpUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.UploadAadhaarXmlUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.VerifyAadhaarUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.VerifyForgotPasswordOtpUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.VerifyRegisterOtpUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.ConfirmProfileUpdateUseCase
import com.state.greenmiles.com_state_greenmiles.presentation.screens.forgot_password.ForgotPasswordViewModel
import com.state.greenmiles.com_state_greenmiles.presentation.common.AuthViewModel
import com.state.greenmiles.com_state_greenmiles.presentation.screens.aadhaar_kyc.AadhaarViewModel
import com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool_1_publish_Ride.CreateTripViewModel
import com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride.SearchRideViewModel
import com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel.CreateParcelTripViewModel
import com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel.ParcelTripsViewModel
import com.state.greenmiles.com_state_greenmiles.presentation.screens.profileScreen.ProfileViewModel
import com.state.greenmiles.com_state_greenmiles.presentation.screens.splash_screen.SplashViewmodel
import com.state.greenmiles.com_state_greenmiles.presentation.screens.tripScreen.MyTripsViewModel
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation

import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val common = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    explicitNulls = false
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
        }
    }
}
val authModule = module {
    single { AuthApi(get()) }

    single { TripsApi(get(),get()) }
    single { ParcelApi(get(),get()) }

    single<AuthRepository> {
        AuthRepositoryImpl(get(),get())
    }

    single<TripsRepository> {
        TripsRepositoryImpl(

            get(),get()
        )
    }

    single<ParcelRepository> {
        ParcelRepositoryImpl(
            get()
        )
    }

    factory { SendRegisterOtpUseCase(get()) }
    factory { VerifyRegisterOtpUseCase(get()) }
    factory { CompleteRegistrationUseCase(get(), get()) }
    factory { LoginUseCase(get(), get()) }
    factory { SendForgotPasswordOtpUseCase(get()) }
    factory { VerifyForgotPasswordOtpUseCase(get()) }
    factory { ResetPasswordUseCase(get()) }

    factory {
        AuthUseCases(
            sendRegisterOtp = get<SendRegisterOtpUseCase>(),
            verifyRegisterOtp = get<VerifyRegisterOtpUseCase>(),
            completeRegistration = get<CompleteRegistrationUseCase>(),
            login = get<LoginUseCase>(),
            sendForgotPasswordOtp = get<SendForgotPasswordOtpUseCase>(),
            verifyForgotPasswordOtp = get<VerifyForgotPasswordOtpUseCase>(),
            resetPassword = get<ResetPasswordUseCase>()
        )
    }

    viewModel {
        AuthViewModel(
            authUseCases = get()
        )
    }

    viewModel {
        ForgotPasswordViewModel(
            authUseCases = get()
        )
    }

    viewModel {
        SplashViewmodel(
            get()
        )
    }

    viewModel {
        CreateTripViewModel(
            get(),
            get()
        )
    }
    viewModel {
        ProfileViewModel(
            repository = get(),
            localStorage = get()
        )
    }
    viewModel {
        MyTripsViewModel(
            repository = get()
        )
    }
    viewModel { (handle: SavedStateHandle) ->
        SearchRideViewModel(
            tripsRepository = get(),
            savedStateHandle = handle
        )
    }

    viewModel {
        ParcelTripsViewModel(
            repository = get()
        )
    }

    viewModel {
        CreateParcelTripViewModel(
            parcelRepository = get(),
            tripsRepository = get(),
            localStorage = get()
        )
    }


    single<Settings> { Settings() }


    single {
        LocalStorage(
            get()
        )
    }





        // EXISTING CODE ...

        // 🪪 Aadhaar API
        single { AadhaarApi(get()) }

        // 🪪 Repository
        single<AadhaarRepository> {
            AadhaarRepositoryImpl(
                api = get(),
                localStorage = get()
            )
        }

        // 🪪 UseCases
        factory { UploadAadhaarXmlUseCase(get()) }
        factory { VerifyAadhaarUseCase(get()) }
        factory { ConfirmProfileUpdateUseCase(get()) }

        // 🪪 ViewModel
        viewModel {
            AadhaarViewModel(
                uploadUseCase = get(),
                verifyUseCase = get(),
                confirmUpdateUseCase = get()
            )

    }


}
