package dev.thinkalex.menlovending.services.stripe.client

import android.app.Application
import com.stripe.stripeterminal.TerminalApplicationDelegate
import dev.thinkalex.menlovending.services.manager.MenloVendingManager

class MenloVendingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MenloVendingManager.initialize(this)
        TerminalApplicationDelegate.onCreate(this)
    }
}