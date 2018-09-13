package com.example.dervis.inventoryapp;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Utils {
    public static void showConfirmDialog(String message, DialogInterface.OnClickListener positiveButtonOnClickListener, DialogInterface.OnClickListener negativeButtonOnClickListener, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);

        builder.setPositiveButton("Yes", positiveButtonOnClickListener);

        builder.setNegativeButton("No", negativeButtonOnClickListener);

        builder.create().show();
    }
}
