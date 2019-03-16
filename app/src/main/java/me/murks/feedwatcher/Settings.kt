package me.murks.feedwatcher

/**
 * @author zouroboros
 */
interface Settings {
    val showNotifcations: Boolean
    val backgroundScanning: Boolean
    val backgroundScanInterval: Int
}