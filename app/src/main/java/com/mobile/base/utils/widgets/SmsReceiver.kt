package com.mobile.base.utils.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsReceiver(
    private val onOtpReceived: (String) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            if (status.statusCode == CommonStatusCodes.SUCCESS) {
                val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

                val otp = Regex("\\d{6}").find(message)?.value
                otp?.let { onOtpReceived(it) }
            }
        }
    }
}
