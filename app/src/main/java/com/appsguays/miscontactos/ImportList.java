package com.appsguays.miscontactos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ImportList extends AppCompatActivity {

    public static final String FILE_PATH = "FILE_PATH";

    private ListView lvFileCont;
    private AdapterContacto myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imp_list_layout);

        lvFileCont = (ListView) findViewById(R.id.lvFileContacts);
        lvFileCont.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        String filePath = getIntent().getStringExtra(FILE_PATH);
        RellenaListaDesdeFicheroXml(filePath);

        //SparseBooleanArray checked = lvFileCont.getCheckedItemPositions();

//        lvFileCont.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String txt;
//                // get the clicked item ID
//                Contacto c = (Contacto) parent.getItemAtPosition(position);
//                txt = c.getId() + " - " + c.getPhone() + " - " + c.getName();
//                //PreguntaSiEliminaContacto(txt, c.getId());
//            }
//        } );
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == SelectFileDlg.RESULT_OK) {
//            String filePath = data.getStringExtra(SelectFileDlg.RESULT_PATH);
//            //System.out.println(filePath);
//            RellenaListaDesdeFicheroXml(filePath);
//        }
//    }

    private void RellenaListaDesdeFicheroXml(String filePath) {
        String nombre;
        String telefono;
        int temp;
        ArrayList<Contacto> myList = new ArrayList<>();
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
            for (temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                //txLog.setText("Current Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    //System.out.println("id : " + eElement.getAttribute("id"));
                    //System.out.println("Nombre : " + eElement.getElementsByTagName("Nombre").item(0).getTextContent());
                    nombre = eElement.getElementsByTagName("Nombre").item(0).getTextContent();
                    telefono = eElement.getElementsByTagName("Telefono").item(0).getTextContent();
                    //System.out.println("Nombre : " + nombre);
                    //System.out.println("Teléfono : " + telefono);
                    //InsertContact(nombre,telefono);
                    cnt = new Contacto(String.valueOf(temp), telefono, nombre);
                    myList.add(cnt);
                }
            }
            //txLog.setText(Integer.toString(temp) + " contactos importados");
        } catch (ParserConfigurationException | IOException | org.xml.sax.SAXException ex) {
            System.out.println(ex.getMessage());
            //txLog.setText("kk1 - " + ex.getMessage());
        }
        myAdapter = new AdapterContacto(this, myList);
        lvFileCont.setAdapter(myAdapter);
    }

}
