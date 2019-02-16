package com.bateman.rich.exercisetrack;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class EmailSender {

    /**
     * Send an e-mail.
     * https://developer.android.com/guide/components/intents-common#ComposeEmail
     */
    static void launchEmailIntentWithAttachment(Context context, String subject, Uri attachment) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain"); // mime type for plain text.
            // I think the "type" here controls what kind of apps popup for sending.
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your exercise data is attached.");
        emailIntent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(emailIntent);
        }
    }
}
