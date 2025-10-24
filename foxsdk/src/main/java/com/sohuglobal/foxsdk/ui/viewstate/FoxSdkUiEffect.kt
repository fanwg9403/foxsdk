package com.sohuglobal.foxsdk.ui.viewstate

import android.os.Bundle

/**
 *
 * 主要功能: 基础UI效果
 * @Description: 用于一次性事件，如Toast、导航等
 * @author: 范为广
 * @date: 2025年10月12日 9:41
 */
sealed class FoxSdkUiEffect {
    data class ShowToast(val message: String) : FoxSdkUiEffect()
    data class NavigateTo(val destination: String, val args: Bundle? = null) : FoxSdkUiEffect()
    object NavigateBack : FoxSdkUiEffect()
}