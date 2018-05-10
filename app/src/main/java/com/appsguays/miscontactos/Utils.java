package com.appsguays.miscontactos;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public class Utils {
    public static void MensajeError(Context context, String msg) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(msg);
        dlgAlert.setTitle("Atención :-O");
        dlgAlert.create().show();
    }
}
