package com.appsguays.miscontactos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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
        Intent i;
        i = new Intent(this, com.appsguays.miscontactos.ListActivity.class);
        startActivity(i);
    }
}
