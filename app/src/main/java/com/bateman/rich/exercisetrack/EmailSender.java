package com.bateman.rich.exercisetrack;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class EmailSender {

    /**
     * Send an e-mail.
     * https://developer.android.com/guide/components/intents-common#ComposeEmail
     * @param subject
     * @param attachment
     */
    public static void foo(Context context, String subject, Uri attachment) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("*/*");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        //emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message text");
        emailIntent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(emailIntent);
        }
    }
}
