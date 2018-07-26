package com.example.afaf.inclcapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.afaf.inclcapp.helper_database.service_selecor_model;
import com.example.afaf.inclcapp.helper_database.service_selector_helper;

import java.util.List;

/**
 * Created by enterprise on 22/05/17.
 */

public class servicesearch_adapter extends ArrayAdapter<service_selecor_model> {
    protected Context mContext;
    protected List<service_selecor_model> mservices;


    public servicesearch_adapter(Context context, List<service_selecor_model> service, service_selector_helper db) {
        super(context, R.layout.product_row, service);
        mservices = service;
        mContext = context;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.productsearch_row, null);
            holder = new ViewHolder();
            holder.sername = (TextView) convertView.findViewById(R.id.NameP);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final service_selecor_model pModel = mservices.get(position);

        holder.sername.setText(pModel.getServicename());

        holder.action = (LinearLayout) convertView.findViewById(R.id.linearAction);

        return convertView;

    }

    private static class ViewHolder {
        TextView sername;
        LinearLayout action;
    }

}

