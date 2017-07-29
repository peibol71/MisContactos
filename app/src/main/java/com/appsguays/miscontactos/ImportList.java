package com.appsguays.miscontactos;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ImportList extends AppCompatActivity {

    public static final String FILE_PATH = "FILE_PATH";

    Context context = null;
    private ListView lvFileCont;
    private AdapterContacto myAdapter;
    ArrayList<Contacto> myList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.imp_list_layout);

        lvFileCont = (ListView) findViewById(R.id.lvFileContacts);
        lvFileCont.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        String filePath = getIntent().getStringExtra(FILE_PATH);
        RellenaListaDesdeFicheroXml(filePath);

        Button btnImport = (Button) findViewById(R.id.btnImport);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ImportSelectedContacts();
            }
        });
    }

    private void RellenaListaDesdeFicheroXml(String filePath) {
        String nombre;
        String telefono;
        int temp;
        myList = new ArrayList<>();
        Contacto cnt;
        ArrayAdapter<String> adapter;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File fXmlFile = new File(filePath);
            Document doc = builder.parse(fXmlFile);
            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Contacto");
            NodeList nlTel;
            int len = nList.getLength();
            int i = 0;
            for (temp = 0; temp < len; temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    nombre = eElement.getElementsByTagName("Nombre").item(0).getTextContent();
                    nlTel = eElement.getElementsByTagName("Telefono");
                    if (nlTel != null) {
                        if (nlTel.getLength() > 0) {
                            telefono = nlTel.item(0).getTextContent();
                            cnt = new Contacto(String.valueOf(i), telefono, nombre);
                            myList.add(cnt);
                            i++;
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | IOException | org.xml.sax.SAXException ex) {
            System.out.println(ex.getMessage());
        }
        myAdapter = new AdapterContacto(this, myList);
        lvFileCont.setAdapter(myAdapter);
    }

    private void ImportSelectedContacts() {
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
            dlgAlert.setMessage("¿Estás seguro que quieres importar "
                    + Integer.toString(nSelected)
                    + " contacto(s)?");
            dlgAlert.setTitle("Atención");
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Contacto cnt;
                    ListIterator it = myList.listIterator();
                    while (it.hasNext()) {
                        cnt = (Contacto) it.next();
                        if (cnt.isSelected()) {
                            InsertContact(cnt.getName(),cnt.getPhone());
                        }
                    }
                    Toast.makeText(context, "Contactos importados",
                            Toast.LENGTH_SHORT).show();
                }
            });
            dlgAlert.create().show();
        }
    }

    private void InsertContact(String nom, String telef) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        //Phone Number
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,  rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, telef)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());

        //Display name/Contact name
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nom)
                .build());
        //Email details
//        ops.add(ContentProviderOperation
//                .newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
//                        rawContactInsertIndex)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Email.DATA, "abc@aho.com")
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, "2").build());


        try {
            ContentProviderResult[] res = getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
            System.out.println("kk2 - " + e.getMessage());
        }
    }
}
