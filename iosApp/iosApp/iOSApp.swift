import SwiftUI
import GoogleMaps
import GooglePlaces


@main
struct iOSApp: App {
    
    init() {
        let apiKey = "AIzaSyDaLrzuDut0Elh4XTQS7_Tku0az7UKK3rM"
        GMSServices.provideAPIKey(apiKey)
                GMSPlacesClient.provideAPIKey(apiKey)
        }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
