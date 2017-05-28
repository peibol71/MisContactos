package com.appsguays.miscontactos;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.net.Uri;
import android.provider.ContactsContract;
import android.database.Cursor;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ExportActivity  extends AppCompatActivity {
    private static final String NOM_FICH_EXCEL = "FicheroConMisContactos.xls";
    private static final String NOM_FICH_XML = "FicheroConMisContactos.xml";
    private static final String PACKAGE_NAME = "com.appsguays.miscontactos";

    Button button;
    EditText destinationAddress;
    EditText sbj;
    CheckBox chkTxt;
    CheckBox chkExc;
    CheckBox chkXml;

    TextView txLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_layout);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        sbj = (EditText) findViewById(R.id.subject);
        button = (Button) findViewById(R.id.button);
        destinationAddress = (EditText) findViewById(R.id.destinationAddress);
        chkTxt = (CheckBox) findViewById(R.id.chkTexto);
        chkExc = (CheckBox) findViewById(R.id.chkExcel);
        chkXml = (CheckBox) findViewById(R.id.chkXml);
        txLog = (TextView) findViewById(R.id.txtLog);

        chkTxt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                button.setEnabled(BotonActivo());
            }
        });
        chkExc.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                button.setEnabled(BotonActivo());
            }
        });
        chkXml.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button.setEnabled(BotonActivo());
            }
        });

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                boolean bTx = chkTxt.isChecked();
                boolean bEx = chkExc.isChecked();
                boolean bXml = chkXml.isChecked();

                if (!bTx && !bEx && !bXml)
                    return;

                String subject = sbj.getText().toString();
                String to = destinationAddress.getText().toString();
                File tmpExcelFile = null;
                File tmpXmlFile = null;

                String outputDir = Environment.getExternalStorageDirectory()
                        + "/Android/data/" + PACKAGE_NAME + "/cache/";
                txLog.setText(outputDir);
                //String outputFile;
                if (bEx || bXml) {
                    File dirFile = new File(outputDir);
                    if (!dirFile.isDirectory()) {
                        if (!dirFile.mkdirs()) {
                            txLog.setText("Mierda " + dirFile);
                            return;
                        }
                    }
                    if (bEx)
                        tmpExcelFile = new File(dirFile, NOM_FICH_EXCEL);
                    if (bXml)
                        tmpXmlFile = new File(dirFile, NOM_FICH_XML);
                }

                String message = fetchContacts(tmpExcelFile, tmpXmlFile);
                txLog.setText("fetch ok");

                Intent emailIntent;
                if (bEx && bXml)
                    emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                else
                    emailIntent = new Intent(Intent.ACTION_SEND);

                //set up the recipient address
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});

                //set up the email subject
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

                //you can specify cc addresses as well
                // email.putExtra(Intent.EXTRA_CC, new String[]{ ...});// email.putExtra(Intent.EXTRA_BCC, new String[]{ ... });

                // ATTACHMENT ...
                if (bEx && bXml) {
                    ArrayList<Uri> uris = new ArrayList<Uri>();
                    Uri a = Uri.fromFile(tmpExcelFile);
                    uris.add(a);
                    a = Uri.fromFile(tmpXmlFile);
                    uris.add(a);
                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                }
                else {
                    if (bEx) {
                        Uri a = Uri.fromFile(tmpExcelFile);
                        emailIntent.putExtra(Intent.EXTRA_STREAM, a);
                    }
                    if (bXml) {
                        Uri a = Uri.fromFile(tmpXmlFile);
                        emailIntent.putExtra(Intent.EXTRA_STREAM, a);
                    }
                }

                //set up the message body
                if (bTx) {
                    emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                    emailIntent.setType("text/xml");

                } else {
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Contactos en fichero(s) adjunto(s).");
                    emailIntent.setType("text/xml"); //emailActivity.setType("message/rfc822");
                }

                startActivity(Intent.createChooser(emailIntent, "Selecciona tu proveedor de correo:"));
                //startActivity(Intent.createChooser(emailIntent, "Select your Email Provider :"));
            }
        });
    }

    public String fetchContacts(File excelFile, File xmlFile) {

        int nCol;
        int n;
        boolean bExcel = (excelFile != null);
        boolean bXml = (xmlFile != null);
        StringBuilder sb = new StringBuilder();
        StringBuilder sbX = new StringBuilder();

        if (bExcel) {
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
            sb.append("<?mso-application progid=\"Excel.Sheet\"?>");
            sb.append("\n<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">");
            sb.append("\n<DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">");
            sb.append("<Author>Linda App Android</Author>");
            sb.append("</DocumentProperties>");
            sb.append("\n<Styles>");
            sb.append(" <Style ss:ID=\"s21\"><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style>");
            sb.append("</Styles>");
            sb.append("\n<Worksheet ss:Name=\"Contactos\">");
            sb.append("\n<Table ss:LeftCell=\"2\" ss:TopCell=\"2\" ss:ExpandedColumnCount=\"10\" x:FullColumns=\"1\" x:FullRows=\"1\">");
            sb.append("<Column ss:Index=\"2\" ss:Width=\"110\"/>");
            sb.append("<Column ss:Index=\"3\" ss:Width=\"90\"/>");
            sb.append("<Column ss:Index=\"4\" ss:Width=\"120\"/>");
            sb.append("\n<Row ss:Index=\"1\">");
            sb.append("<Cell ss:Index=\"2\" ss:StyleID=\"s21\"><Data ss:Type=\"String\">Nombre</Data></Cell>");
            sb.append("<Cell ss:Index=\"3\" ss:StyleID=\"s21\"><Data ss:Type=\"String\">Nº Teléfono</Data></Cell>");
            sb.append("<Cell ss:Index=\"4\" ss:StyleID=\"s21\"><Data ss:Type=\"String\">e-mail</Data></Cell>");
            sb.append("</Row>");
            sb.append("\n");
        }

        if (bXml) {
            sbX.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
            sbX.append("<Contactos>\n");
        }

        int row = 1;

        String phoneNumber = null;
        String email = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        StringBuffer output = new StringBuffer();

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            row++;
            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {

                    row++;
                    nCol = 3;

                    output.append("\nNombre:  " + name);

                    if (bExcel) {
                        sb.append("\n<Row ss:Index=\"" + Integer.toString(row) + "\">");
                        sb.append("<Cell ss:Index=\"2\"><Data ss:Type=\"String\">" + name + "</Data></Cell>");
                    }
                    if (bXml)
                        sbX.append("<Contacto><Nombre>" + name + "</Nombre>");

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append("\nNº teléfono:  " + phoneNumber);
                        if (bExcel) {
                            sb.append("<Cell ss:Index=\"" + String.valueOf(nCol) +
                                    "\"><Data ss:Type=\"String\">" + phoneNumber + "</Data></Cell>");
                        }

                        if (bXml) {
                            sbX.append("<Telefono>" + phoneNumber + "</Telefono>");
                        }
                        nCol++;
                        break;
                    }
                    phoneCursor.close();

                    n = 1;
                    // Query and loop for every email of the contact
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (emailCursor.moveToNext() && n<6) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        output.append("\nEmail:" + email);

                        if (bExcel) {
                            sb.append("<Cell ss:Index=\""  + String.valueOf(nCol) +
                                    "\"><Data ss:Type=\"String\">" + email + "</Data></Cell>");
                        }

                        if (bXml) {
                            sbX.append("<email" + String.valueOf(n) + ">" + email + "</email" + String.valueOf(n) + ">");
                        }
                        nCol++;
                        n++;
                    }
                    emailCursor.close();
                    if (bExcel)
                        sb.append("</Row>");

                    if (bXml)
                        sbX.append("</Contacto>\n");
                    output.append("\n");
                }
            }

            if (bExcel) {
                sb.append("\n</Table>");
                sb.append("\n<WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">");
                sb.append("<Selected />");
                sb.append("</WorksheetOptions>");
                sb.append("\n</Worksheet>");
                sb.append("\n</Workbook>");
                try {
                    excelFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(excelFile);
                    //openFileOutput(NOMBRE_FICHERIN, Context.MODE_WORLD_READABLE); // Context.MODE_PRIVATE);
                    fos.write(sb.toString().getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    //  Log.e("Mi Aplicación",e.getMessage(),e);
                    e.printStackTrace();
                }
            }
            if (bXml) {
                sbX.append("</Contactos>");
                try {
                    xmlFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(xmlFile);
                    fos.write(sbX.toString().getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return output.toString();
        }
        return "";
    }

    public boolean BotonActivo() {
        boolean bEnable = true;
        if (!chkTxt.isChecked() && !chkExc.isChecked() && !chkXml.isChecked()) {
            bEnable = false;
        }
        return bEnable;
    }
}
