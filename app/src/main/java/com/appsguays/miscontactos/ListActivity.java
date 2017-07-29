package com.appsguays.miscontactos;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ListIterator;

public class ListActivity extends AppCompatActivity {

    Context context = null;
    private ListView lvCont;
    private AdapterContacto myAdapter;
    Button btnDelete = null;
    ArrayList<Contacto> myList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.list_layout);

        lvCont = (ListView) findViewById(R.id.lvContactos);
        populateContactList();

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getSelectedContacts();
            }
        });
    }

    private void populateContactList() {
        String contact_id;
        String name;
        int hasPhoneNumber;
        String phoneNumber = null;
        String s;
        Cursor phoneCursor;

        myList = new ArrayList<>();
        Contacto cnt;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        if (cursor != null) {
            try {
                // Loop for every contact in the phone
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                        name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                        hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                        if (hasPhoneNumber > 0) {
                            // Query and loop for every phone number of the contact
                            phoneCursor = contentResolver.query(
                                    PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                            if (phoneCursor != null) {
                                while (phoneCursor.moveToNext()) {
                                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                                    //s += phoneNumber;
                                    break;
                                }
                                phoneCursor.close();
                            }
                        } else
                            phoneNumber = "Sin teléfono";
                        cnt = new Contacto(contact_id, phoneNumber, name);
                        myList.add(cnt);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        myAdapter = new AdapterContacto(this, myList);
        lvCont.setAdapter(myAdapter);
    }

    private void getSelectedContacts() {
        if (myList == null)
            return;
        Contacto cnt;
        int nSelected = 0;
        ListIterator it = myList.listIterator();
        while(it.hasNext()) {
            cnt = (Contacto)it.next();
            if (cnt.isSelected())
                nSelected++;
        }
        if (nSelected==0) {
            Toast.makeText(context, "Selecciona al menos un contacto",
                    Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("¿Estás seguro que quieres eliminar "
                    + Integer.toString(nSelected)
                    + " contacto(s)?");
            dlgAlert.setTitle("Atención");
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Elimina contactos
                    Contacto cnt;
                    ListIterator it = myList.listIterator();
                    while (it.hasNext()) {
                        cnt = (Contacto) it.next();
                        if (cnt.isSelected()) {

                            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,cnt.getId());
                            int deleted = context.getContentResolver().delete(uri,null,null);
                            if (deleted>0)
                                System.out.println("OK");
                        }
                    }
                    populateContactList();
                }
            });
            dlgAlert.create().show();
        }
    }
}
