package com.state.greenmiles.com_state_greenmiles.presentation.navigation

    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.internal.composableLambda
    import androidx.compose.runtime.remember
    import androidx.navigation.compose.NavHost
    import androidx.navigation.compose.composable
    import androidx.navigation.compose.navigation
    import androidx.navigation.compose.rememberNavController
    import com.state.greenmiles.com_state_greenmiles.data.remote.api.ApiConstants.BASE_URL
    import com.state.greenmiles.com_state_greenmiles.data.remote.api.TripsApi
    import com.state.greenmiles.com_state_greenmiles.data.repository.TripsRepositoryImpl
    import com.state.greenmiles.com_state_greenmiles.presentation.common.AuthViewModel
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.aadhaar_kyc.AadhaarKycScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.HomeScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.otp_screen.OtpScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.phone_number_screen.PhoneNumberScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool_1_publish_Ride.SelectLocation
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride.RideDetailsScreen.TripDetailsScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride.SearchRide
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride.SearchRideViewModel
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.profileScreen.ProfileViewModel
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.signup_screen.SignUpScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.forgot_password.ForgotPasswordScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.forgot_password.ForgotPasswordViewModel
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel.ParcelTripsScreen

    import com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel.ParcelDetailsScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel.ParcelTripsViewModel
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel.public.CreatePublicParcelScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel.car.CreateCarParcelScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.choice.ServiceSelectionScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.choice.ParcelTypeSelectionScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.splash_screen.SplashScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.welcome_screen.WelcomeScreen
    import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage
    import io.ktor.client.HttpClient
    import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
    import io.ktor.serialization.kotlinx.json.json
    import kotlinx.serialization.json.Json
    import org.koin.compose.viewmodel.koinViewModel
    import com.russhwolf.settings.Settings
    import com.state.greenmiles.com_state_greenmiles.data.remote.api.AuthApi
    import com.state.greenmiles.com_state_greenmiles.data.repository.AuthRepositoryImpl
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.profileScreen.ProfileScreen
    import com.state.greenmiles.com_state_greenmiles.presentation.screens.tripScreen.MyTripsScreen

    @Composable
    fun RootNavigator() {

        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = AppScreen.SplashScreen.name
        ) {

            composable(AppScreen.SplashScreen.name) {
                SplashScreen(navController)
            }

            // ---------------- AUTH GRAPH ----------------
            navigation(
                startDestination = AppScreen.WelcomeScreen.name,
                route = "auth_graph"
            ) {
                composable(AppScreen.WelcomeScreen.name) {
                    val parentEntry = remember {
                        navController.getBackStackEntry("auth_graph")
                    }
                    val authViewModel: AuthViewModel = koinViewModel(
                        viewModelStoreOwner = parentEntry
                    )

                    WelcomeScreen(navController, authViewModel)
                }

                composable("${AppScreen.PhoneNumberScreen.name}/{authType}") { backStackEntry ->
                    val authType = backStackEntry.arguments?.getString("authType")
                    val parentEntry = remember {
                        navController.getBackStackEntry("auth_graph")
                    }
                    val authViewModel: AuthViewModel = koinViewModel(
                        viewModelStoreOwner = parentEntry
                    )
                
                // Set the authType from navigation
                LaunchedEffect(authType) {
                    authViewModel.authType = authType
                }

                PhoneNumberScreen(navController, authViewModel)
            }

            composable("${AppScreen.SignUp.name}/{verifiedToken}") { backStackEntry ->
                val verifiedToken = backStackEntry.arguments?.getString("verifiedToken") ?: ""
                val parentEntry = remember {
                    navController.getBackStackEntry("auth_graph")
                }
                val authViewModel: AuthViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )

                SignUpScreen(navController, authViewModel, verifiedToken)
            }

            composable("${AppScreen.ForgotPassword.name}/{phoneNumber}/{otpToken}") { backStackEntry ->
                val phoneNumber = backStackEntry.arguments?.getString("phoneNumber")
                val otpToken = backStackEntry.arguments?.getString("otpToken")
                
                val parentEntry = remember {
                    navController.getBackStackEntry("auth_graph")
                }
                val forgotPasswordViewModel: ForgotPasswordViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )

                // Initialize the ViewModel with the passed data
                LaunchedEffect(phoneNumber, otpToken) {
                    if (phoneNumber != null && otpToken != null) {
                        forgotPasswordViewModel.initializeWithVerifiedToken(phoneNumber, otpToken)
                    }
                }

                ForgotPasswordScreen(navController, forgotPasswordViewModel)
            }
        }

        // ---------------- HOME SCREEN ----------------
        composable(AppScreen.HomeScreen.name) {
            HomeScreen(
                onFindRide = {
                 navController.navigate(AppScreen.SearchRide.name)

                },
                onPublishRide = {
                    navController.navigate(AppScreen.PublishRide.name)
                },
                onProfileClick = {
                    navController.navigate(AppScreen.Profile.name)
                },
                onTripsClick = {
                    navController.navigate(AppScreen.MyTrips.name)
                },
                onSearchParcels = {
                    navController.navigate(AppScreen.ParcelTrips.name)
                },
                onPublishParcel = {
                    navController.navigate(AppScreen.ParcelTypeSelection.name)
                },
                onPublishPublicParcel = {
                    navController.navigate(AppScreen.CreatePublicParcel.name)
                },
                onPublishCarParcel = {
                    navController.navigate(AppScreen.CreateCarParcel.name)
                },
                onServiceSelection = {
                    navController.navigate(AppScreen.ServiceSelection.name)
                }
            )
        }
// ---------------- RIDE GRAPH ----------------
        navigation(
            startDestination = AppScreen.SearchRide.name,
            route = "ride_graph"
        ) {

            composable(route = AppScreen.SearchRide.name) {

                val parentEntry = remember {
                    navController.getBackStackEntry("ride_graph")
                }

                val viewModel: SearchRideViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )

                SearchRide(
                    viewModel = viewModel,
                    onNavigateToTripDetails = {
                        navController.navigate(AppScreen.RideDetailsScreen.name)
                    }
                )
            }

            composable(route = AppScreen.RideDetailsScreen.name) {

                val parentEntry = remember {
                    navController.getBackStackEntry("ride_graph")
                }

                val viewModel: SearchRideViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )

                TripDetailsScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onBookingSuccess = {
                        navController.navigate(AppScreen.HomeScreen.name)
                    }
                )
            }
        }


        // ---------------- PUBLISH RIDE ----------------
        composable(route = AppScreen.PublishRide.name) {
            SelectLocation(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTripCreated = {
                    navController.navigate(AppScreen.HomeScreen.name)
                }
            )
        }

        composable(AppScreen.Profile.name) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                navController
            )
        }

        composable(AppScreen.MyTrips.name) {
            MyTripsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(route = AppScreen.AadhaarKycScreen.name) {
            AadhaarKycScreen()
        }

        // ---------------- PARCEL GRAPH ----------------
        navigation(
            startDestination = AppScreen.ParcelTrips.name,
            route = "parcel_graph"
        ) {
            composable(route = AppScreen.ParcelTrips.name) {
                val parentEntry = remember {
                    navController.getBackStackEntry("parcel_graph")
                }
                val viewModel: ParcelTripsViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                ParcelTripsScreen(
                    viewModel = viewModel,
                    onNavigateToTripDetails = {
                        navController.navigate(AppScreen.ParcelDetails.name)
                    }
                )
            }

            composable(route = AppScreen.ParcelDetails.name) {
                val parentEntry = remember {
                    navController.getBackStackEntry("parcel_graph")
                }
                val viewModel: ParcelTripsViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                ParcelDetailsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onBookingSuccess = {
                        navController.navigate(AppScreen.HomeScreen.name)
                    }
                )
            }
        }



        composable(route = AppScreen.CreatePublicParcel.name) {
            CreatePublicParcelScreen(
                onNavigateBack = { navController.popBackStack() },
                onTripCreated = { navController.navigate(AppScreen.HomeScreen.name) }
            )
        }

        composable(route = AppScreen.CreateCarParcel.name) {
            CreateCarParcelScreen(
                onNavigateBack = { navController.popBackStack() },
                onTripCreated = { navController.navigate(AppScreen.HomeScreen.name) }
            )
        }

        composable(route = AppScreen.ServiceSelection.name) {
            ServiceSelectionScreen(
                onRideSelected = { navController.navigate(AppScreen.PublishRide.name) },
                onParcelSelected = { navController.navigate(AppScreen.ParcelTypeSelection.name) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = AppScreen.ParcelTypeSelection.name) {
            ParcelTypeSelectionScreen(
                onPublicSelected = { navController.navigate(AppScreen.CreatePublicParcel.name) },
                onCarSelected = { navController.navigate(AppScreen.CreateCarParcel.name) },
                onBack = { navController.popBackStack() }
            )
        }
    }


}

