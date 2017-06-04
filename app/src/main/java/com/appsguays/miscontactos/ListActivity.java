package com.appsguays.miscontactos;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView lvCont;
    private AdapterContacto myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        lvCont = (ListView) findViewById(R.id.lvContactos);
        populateContactList();

        lvCont.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String txt;
                // get the clicked item ID
                Contacto c = (Contacto) parent.getItemAtPosition(position);
                txt = c.getId() + " - " + c.getPhone() + " - " + c.getName();
                PreguntaSiEliminaContacto(txt, c.getId());
            }
        } );
    }

    private void populateContactList() {
        String contact_id;
        String name;
        int hasPhoneNumber;
        String phoneNumber = null;
        String s;
        Cursor phoneCursor;

        ArrayList<Contacto> myList = new ArrayList<>();
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

    private void PreguntaSiEliminaContacto(String txt, final String contactId)  {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("¿Estás seguro que quieres eliminar a\n"
                + txt
                + "\nde tus contactos?");
        dlgAlert.setTitle("Atención");
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Elimina contacto
                        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                                .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(contactId)})
                                .build());
                        try {
                            ContentProviderResult[] res =
                                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                        } catch (RemoteException | OperationApplicationException e) {
                            e.printStackTrace();
                            //txLog.setText("kk2 - " + e.getMessage());
                        }
                    }
                });
        dlgAlert.create().show();
    }

}
