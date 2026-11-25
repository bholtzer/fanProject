import Foundation
import UIKit
import GoogleMobileAds
import shared

/// Retained by IosAds for the lifetime of the shared application.
@MainActor
final class AdMobBridge: NSObject, @preconcurrency IosAdsBridge, FullScreenContentDelegate {
    private var initialized = false
    private var ready = false
    private var loadingInterstitial = false
    private var loadingRewarded = false
    private var interstitial: InterstitialAd?
    private var rewarded: RewardedAd?
    private var interstitialLoadedAt = Date.distantPast
    private var rewardedLoadedAt = Date.distantPast
    private var activeAd: AnyObject?
    private var closed: (() -> KotlinUnit)?

    private func unit(_ key: String) -> String? {
        guard let value = Bundle.main.object(forInfoDictionaryKey: key) as? String,
              !value.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return nil }
        return value
    }

    func initialize() {
        precondition(Thread.isMainThread)
        guard !initialized else { return }
        initialized = true
        MobileAds.shared.start { [weak self] _ in
            DispatchQueue.main.async {
                self?.ready = true
                self?.loadInterstitial()
                self?.loadRewarded()
            }
        }
    }

    func loadInterstitial() {
        precondition(Thread.isMainThread)
        guard ready, !loadingInterstitial,
              let id = unit("AdMobInterstitialID") else { return }
        if interstitial != nil && Date().timeIntervalSince(interstitialLoadedAt) < 3600 { return }
        interstitial = nil
        loadingInterstitial = true
        Task { @MainActor in
            defer { loadingInterstitial = false }
            do {
                interstitial = try await InterstitialAd.load(with: id, request: Request())
                interstitial?.fullScreenContentDelegate = self
                interstitialLoadedAt = Date()
            } catch {
                NSLog("Interstitial load failed: %@", error.localizedDescription)
            }
        }
    }

    func loadRewarded() {
        precondition(Thread.isMainThread)
        guard ready, !loadingRewarded,
              let id = unit("AdMobRewardedID") else { return }
        if rewarded != nil && Date().timeIntervalSince(rewardedLoadedAt) < 3600 { return }
        rewarded = nil
        loadingRewarded = true
        Task { @MainActor in
            defer { loadingRewarded = false }
            do {
                rewarded = try await RewardedAd.load(with: id, request: Request())
                rewarded?.fullScreenContentDelegate = self
                rewardedLoadedAt = Date()
            } catch {
                NSLog("Rewarded load failed: %@", error.localizedDescription)
            }
        }
    }

    func showInterstitial(onClosed: @escaping () -> KotlinUnit) {
        precondition(Thread.isMainThread)
        guard activeAd == nil, let ad = interstitial,
              Date().timeIntervalSince(interstitialLoadedAt) < 3600,
              let presenter = presenter() else {
            loadInterstitial()
            _ = onClosed()
            return
        }
        interstitial = nil
        activeAd = ad
        closed = onClosed
        ad.present(from: presenter)
    }

    func showRewarded(onReward: @escaping () -> KotlinUnit, onClosed: @escaping () -> KotlinUnit) {
        precondition(Thread.isMainThread)
        guard activeAd == nil, let ad = rewarded,
              Date().timeIntervalSince(rewardedLoadedAt) < 3600,
              let presenter = presenter() else {
            loadRewarded()
            _ = onClosed()
            return
        }
        rewarded = nil
        activeAd = ad
        closed = onClosed
        var granted = false
        ad.present(from: presenter) {
            guard !granted else { return }
            granted = true
            _ = onReward()
        }
    }

    private func presenter() -> UIViewController? {
        let scenes = UIApplication.shared.connectedScenes.compactMap { $0 as? UIWindowScene }
        guard let root = scenes.first(where: { $0.activationState == .foregroundActive })?
            .windows.first(where: { $0.isKeyWindow })?.rootViewController else { return nil }
        var current = root
        while true {
            if let presented = current.presentedViewController {
                current = presented
            } else if let navigation = current as? UINavigationController,
                      let visible = navigation.visibleViewController {
                current = visible
            } else if let tabs = current as? UITabBarController,
                      let selected = tabs.selectedViewController {
                current = selected
            } else { break }
        }
        return current.viewIfLoaded?.window == nil ? nil : current
    }

    func adDidDismissFullScreenContent(_ ad: FullScreenPresentingAd) {
        finish()
    }

    func ad(_ ad: FullScreenPresentingAd, didFailToPresentFullScreenContentWithError error: Error) {
        NSLog("Ad presentation failed: %@", error.localizedDescription)
        finish()
    }

    private func finish() {
        let callback = closed
        closed = nil
        activeAd = nil
        loadInterstitial()
        loadRewarded()
        _ = callback?()
    }
}
