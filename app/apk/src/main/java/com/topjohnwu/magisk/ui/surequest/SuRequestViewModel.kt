package com.topjohnwu.magisk.ui.surequest

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.core.content.edit
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.topjohnwu.magisk.core.Config
import com.topjohnwu.magisk.core.data.magiskdb.PolicyDao
import com.topjohnwu.magisk.core.di.ServiceLocator
import com.topjohnwu.magisk.core.model.su.SuPolicy
import com.topjohnwu.magisk.core.su.SuRequestHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import java.util.concurrent.TimeUnit.SECONDS

class SuRequestViewModel(app: Application) : AndroidViewModel(app), DIAware {

    override val di by closestDI()

    private val db: PolicyDao by instance()

    private val timeoutPrefs: SharedPreferences by lazy { ServiceLocator.timeoutPrefs }

    val handler by lazy { SuRequestHandler(application.packageManager, db) }

    val seconds = MutableStateFlow(Config.suDefaultTimeout)

    val selected = MutableStateFlow(0)

    val timer by lazy { SuTimer(SECONDS.toMillis(seconds.value.toLong()), 1000) }

    private val _isGrantEnabled = MutableStateFlow(false)
    val isGrantEnabled = _isGrantEnabled.asStateFlow()

    val finishCallback = MutableStateFlow<(() -> Unit)?>(null)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            selected.first { it != 0 }
            seconds.value = 0
        }
        viewModelScope.launch(Dispatchers.Default) {
            seconds.first { it == 0 }
            Dispatchers.Main { timer.cancel() }
        }
        viewModelScope.launch(Dispatchers.Default) {
            val initial = seconds.value
            seconds.first { it != initial }
            _isGrantEnabled.value = true
        }
    }

    fun respond(action: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            seconds.value = 0
            timeoutPrefs.edit { putInt(handler.pkgInfo.packageName, selected.value) }
            handler.respond(action, Config.Value.TIMEOUT_LIST[selected.value])
            // Kill activity after response
            finishCallback.value?.invoke()
        }
    }

    inner class SuTimer(
        millis: Long,
        interval: Long
    ) : CountDownTimer(millis, interval) {

        override fun onTick(remains: Long) {
            seconds.value = (remains / 1000).toInt() + 1
        }

        override fun onFinish() {
            seconds.value = 0
            respond(SuPolicy.DENY)
        }

    }

    // Invisible for accessibility services
    object EmptyAccessibilityDelegate : AccessibilityDelegateCompat() {
        override fun dispatchPopulateAccessibilityEvent(host: View, event: AccessibilityEvent) = true
        override fun getAccessibilityNodeProvider(host: View) = null
        override fun onInitializeAccessibilityEvent(host: View, event: AccessibilityEvent) = Unit
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) = Unit
        override fun onPopulateAccessibilityEvent(host: View, event: AccessibilityEvent) = Unit
        override fun onRequestSendAccessibilityEvent(host: ViewGroup, child: View, event: AccessibilityEvent) = false
        override fun performAccessibilityAction(host: View, action: Int, args: Bundle?) = true
        override fun sendAccessibilityEvent(host: View, eventType: Int) = Unit
        override fun sendAccessibilityEventUnchecked(host: View, event: AccessibilityEvent) = Unit
    }

}
