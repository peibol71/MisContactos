package com.appsguays.miscontactos;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static int REQUEST_FILE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Exportar(View view) {
        Intent i = new Intent(this, com.appsguays.miscontactos.ExportActivity.class);
        startActivity(i);
    }

    public void Lista(View view) {
        Intent i = new Intent(this, ListActivity.class);
        startActivity(i);
    }

    public void Importar(View view) {
        // Preguntamos fichero:
        Intent i = new Intent(this, com.appsguays.miscontactos.SelectFileDlg.class);
        i.putExtra(SelectFileDlg.FORMAT_FILTER, new String[]{"xml"}); //set file filter
        String myDir = Environment.getExternalStorageDirectory().toString(); // + "/Android/data/" + PACKAGE_NAME + "/";
        i.putExtra(SelectFileDlg.START_PATH, myDir);
        startActivityForResult(i, REQUEST_FILE);
//        Intent intent = new Intent()
//                    .setType("*/*")
//                    .setAction(Intent.ACTION_OPEN_DOCUMENT);
//                            //Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_FILE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE && resultCode == SelectFileDlg.RESULT_OK) {
            String filePath = data.getStringExtra(SelectFileDlg.RESULT_PATH);
            //System.out.println(filePath);
            Intent i = new Intent(this, com.appsguays.miscontactos.ImportList.class);
            i.putExtra(ImportList.FILE_PATH, filePath);
            startActivity(i);
        }
    }

}
