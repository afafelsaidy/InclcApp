package com.example.afaf.inclcapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.afaf.inclcapp.helper_database.productSelector_helper;
import com.example.afaf.inclcapp.helper_database.productSelector_model;

import java.util.List;

/**
 * Created by enterprise on 22/05/17.
 */

public class productSearch_adapter extends ArrayAdapter<productSelector_model> {
    protected Context mContext;
    protected List<productSelector_model> mProducts;


    public productSearch_adapter(Context context, List<productSelector_model> products, productSelector_helper db) {
        super(context, R.layout.product_row, products);
        mProducts = products;
        mContext = context;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.productsearch_row, null);
            holder = new ViewHolder();
            holder.prodname = (TextView) convertView.findViewById(R.id.NameP);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final productSelector_model pModel = mProducts.get(position);

        holder.prodname.setText(pModel.getProdname());

        holder.action = (LinearLayout) convertView.findViewById(R.id.linearAction);

        return convertView;

    }

    private static class ViewHolder {
        TextView prodname;
        LinearLayout action;
    }

}
