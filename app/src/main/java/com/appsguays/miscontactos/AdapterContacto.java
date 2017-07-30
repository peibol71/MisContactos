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

     @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView number;
        protected CheckBox check;
        //protected ImageView image;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final ViewHolder holder;

        if (v == null) {
            holder = new ViewHolder();
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_contacto, null);
            holder.name = (TextView) v.findViewById(R.id.name);
            holder.number = (TextView) v.findViewById(R.id.phone);
            holder.check = (CheckBox) v.findViewById(R.id.contactcheck);
            //holder.image = (ImageView) view.findViewById(R.id.contactimage);
            v.setTag(holder);
            v.setTag(R.id.name, holder.name);
            v.setTag(R.id.phone, holder.number);
            v.setTag(R.id.contactcheck, holder.check);
            holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton vw, boolean isChecked) {
                    Contacto cont = items.get((Integer) vw.getTag());
                    cont.setSelected(vw.isChecked());
                }
            });
        } else {
            holder = (ViewHolder) v.getTag();
        }

        Contacto cont = items.get(position);
        holder.name.setText(cont.getName());
        holder.number.setText(cont.getPhone());
        holder.check.setTag(position);
        holder.check.setChecked(cont.isSelected());

        return v;
    }
}
