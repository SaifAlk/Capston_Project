package com.saif.gogopharmacy.configuration;

import android.content.Context;
import android.widget.Toast;

public class ToastMessage {
    public ToastMessage() {
    }

    public void ShowShortMessage(String Message, Context context) {
        // Get the message from outside, then Crate the toast
        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
    }

    public void ShowLongMessage(String Message, Context context) {
        // Get the message from outside, then Crate the toast
        Toast.makeText(context, Message, Toast.LENGTH_LONG).show();
    }
}
