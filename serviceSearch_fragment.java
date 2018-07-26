package com.example.afaf.inclcapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.example.afaf.inclcapp.helper_database.productSelector_helper;
import com.example.afaf.inclcapp.helper_database.productSelector_model;
import com.example.afaf.inclcapp.helper_database.service_selecor_model;
import com.example.afaf.inclcapp.helper_database.service_selector_helper;

import org.json.JSONException;

import java.util.List;

/**
 * Created by enterprise on 22/05/17.
 */

public class serviceSearch_fragment extends ListFragment {

    String mType="";
    String mKey=null;

    List<service_selecor_model> list;
    service_selector_helper db;

    public static String serName;


    public serviceSearch_fragment ( ){


    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.searchfragment, container, false);

        return v;
    }

    // ---------------------------------------------------------------------------------------
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = db.getAllServiceSel();
        servicesearch_adapter adapter1 = new servicesearch_adapter(getListView().getContext(), list, db);
        setListAdapter(adapter1);
serviceSearch.servicesearchflag=false;

        if(mKey!=null){
            if(!mKey.equals(""))
                try {
                    serviceSearch.servicesearchflag=true;
                    list = db.getservice_selecorByKey(mKey);
                    servicesearch_adapter adapter = new servicesearch_adapter(getListView().getContext(), list, db);
                    setListAdapter(adapter);
                }catch(Exception e){

                }
        }



    }

    @SuppressLint("ValidFragment")
    public serviceSearch_fragment (String type,String key){

        mType=type;
        mKey=key;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        db = new service_selector_helper(getActivity());
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        int idint= (int) (id+1);
        service_selecor_model model = null;
        try {
            model = db.readServiceSel(idint);
        } catch (JSONException e) {

        }
        //  int id1= model.getId();
         serName = model.getServicename();



        if (insert_service.serviceflag==true) {

            if (productSearch.searchflag == true) {
               // int idint = (int) (id);
                list.get(idint);
                int x = 0;
                //  int id1= model.getId();
                serName = list.get(idint).getServicename();

                service_selector_helper sdb = new service_selector_helper(getContext());
                List<service_selecor_model>ssel = sdb.getAllServiceSel();
                for (int i = 0; i <= ssel.size(); i++) {
                    if (ssel.get(i).getServicename().equals(list.get(idint).getServicename())) {
                        x = ssel.get(i).getId();
                        break;
                    }
                }

                Intent i = new Intent(getActivity(), insert_service.class);
                i.putExtra("id", x + "");
                //  i.putExtra("id",id1);
                i.putExtra("Name", searchfragment.Name);
                i.putExtra("servname", serName);

                startActivity(i);
                getActivity().finish();
            }else{

                Intent i = new Intent(getActivity(), insert_service.class);
                i.putExtra("id", idint + "");
                //  i.putExtra("id",id1);
                i.putExtra("Name", searchfragment.Name);
                i.putExtra("servname", serName);

                startActivity(i);
                getActivity().finish();
            }
        }else {


            if (productSearch.searchflag == true) {
                // int idint = (int) (id);
                list.get(idint);
                int x = 0;
                //  int id1= model.getId();
                serName = list.get(idint).getServicename();

                service_selector_helper sdb = new service_selector_helper(getContext());
                List<service_selecor_model> ssel = sdb.getAllServiceSel();
                for (int i = 0; i <= ssel.size(); i++) {
                    if (ssel.get(i).getServicename().equals(list.get(idint).getServicename())) {
                        x = ssel.get(i).getId();
                        break;
                    }
                }
                Intent i = new Intent(getActivity(), insert_products.class);
                i.putExtra("id", x + "");
                //  i.putExtra("id",id1);
                i.putExtra("Name", searchfragment.Name);
                i.putExtra("servname", serName);

                startActivity(i);
                getActivity().finish();
            }
            else{
                Intent i = new Intent(getActivity(), insert_products.class);
                i.putExtra("id", idint + "");
                //  i.putExtra("id",id1);
                i.putExtra("Name", searchfragment.Name);
                i.putExtra("servname", serName);

                startActivity(i);
                getActivity().finish();
            }
        }
    }


}

