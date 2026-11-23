import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        IosAds.shared.install(bridge: AdMobBridge())
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
