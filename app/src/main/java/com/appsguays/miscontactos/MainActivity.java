package com.appsguays.miscontactos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static int REQUEST_FILE = 1234;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1235;

    private static final int ACT_ID_EXPORT = 1;
    private static final int ACT_ID_LIST = 2;
    private static final int ACT_ID_IMPORT = 3;
    private int boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Exportar(View view) {
        boton = ACT_ID_EXPORT;
        if (NoHayPermisos())
            return;
        AExportar();
        boton = 0;
    }

    public void Lista(View view) {
        boton = ACT_ID_LIST;
        if (NoHayPermisos())
            return;
        AListar();
        boton = 0;
    }

    public void Importar(View view) {
        boton = ACT_ID_IMPORT;
        if (NoHayPermisos())
            return;
        AImportar();
        boton = 0;
    }

    public void Ajustes(View view) {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        i.setData(uri);
        startActivity(i);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE && resultCode == SelectFileDlg.RESULT_OK) {
            String filePath = data.getStringExtra(SelectFileDlg.RESULT_PATH);
            //System.out.println(filePath);
            Intent i = new Intent(this, ImportActivity.class);
            i.putExtra(ImportActivity.FILE_PATH, filePath);
            startActivity(i);
        }
    }

    private boolean NoHayPermisos() {
        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int contacts = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (contacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return true; // permissionOk;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (boton == ACT_ID_EXPORT) {
                        AExportar();
                    } else if (boton == ACT_ID_LIST) {
                        AListar();
                    } else if (boton == ACT_ID_IMPORT) {
                        AImportar();
                    }
                    boton = 0;
                } // else  // permission denied
            }
        }
    }

    private void AExportar() {
        Intent i = new Intent(this, com.appsguays.miscontactos.ExportActivity.class);
        startActivity(i);
    }

    private void AListar() {
        Intent i = new Intent(this, ListActivity.class);
        startActivity(i);
    }

    private void AImportar() {
        // Preguntamos fichero:
        Intent i = new Intent(this, com.appsguays.miscontactos.SelectFileDlg.class);
        i.putExtra(SelectFileDlg.FORMAT_FILTER, new String[]{"xml"}); //set file filter
        String myDir = Environment.getExternalStorageDirectory().toString();
        i.putExtra(SelectFileDlg.START_PATH, myDir);
        startActivityForResult(i, REQUEST_FILE);
    }
}
