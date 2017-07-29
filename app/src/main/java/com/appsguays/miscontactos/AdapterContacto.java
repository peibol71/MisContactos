package com.appsguays.miscontactos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;


public class AdapterContacto extends BaseAdapter {
    protected Activity activity;
    protected ArrayList<Contacto> items;

    public AdapterContacto (Activity activity, ArrayList<Contacto> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }
    public void addAll(ArrayList<Contacto> category) {
        for (int i = 0; i < category.size(); i++) {
            items.add(category.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_contacto, null);
        }

        Contacto cont = items.get(position);

        TextView name = (TextView) v.findViewById(R.id.name);
        name.setText(cont.getName());

        TextView description = (TextView) v.findViewById(R.id.phone);
        description.setText(cont.getPhone());

        CheckBox check = (CheckBox) v.findViewById(R.id.contactcheck);
        check.setTag(position);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton vw, boolean isChecked) {
                Contacto cont = items.get((Integer) vw.getTag());
                cont.setSelected(vw.isChecked());
            }
        });

        return v;
    }
}
