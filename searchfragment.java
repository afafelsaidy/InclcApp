package com.example.afaf.inclcapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import com.example.afaf.inclcapp.helper_database.productSelector_helper;
import com.example.afaf.inclcapp.helper_database.productSelector_model;

import org.json.JSONException;

import java.util.List;

/**
 * Created by enterprise on 22/05/17.
 */

public class searchfragment extends ListFragment {

    String mType="";
    String mKey=null;

    public static  String Name;

    List<productSelector_model> list;
    productSelector_helper db;



    public searchfragment ( ){


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

        list = db.getAllProductSelector();
        productSearch_adapter adapter1 = new productSearch_adapter(getListView().getContext(), list, db);
        setListAdapter(adapter1);
        productSearch.searchflag=false;

        if(mKey!=null){
            if(!mKey.equals(""))
                try {
                    productSearch.searchflag=true;
                    list = db.getproductSelectorByKey(mKey);
                    productSearch_adapter adapter = new productSearch_adapter(getListView().getContext(), list, db);
                    setListAdapter(adapter);



                }catch(Exception e){

                }
        }



    }

    @SuppressLint("ValidFragment")
    public searchfragment (String type,String key){

        mType=type;
        mKey=key;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        db = new productSelector_helper(getActivity());
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (productSearch.searchflag == true) {
            int idint = (int) (id);
            list.get(idint);
         int x=0;
            //  int id1= model.getId();
            Name = list.get(idint).getProdname();

            productSelector_helper pdb = new productSelector_helper(getContext());
            List<productSelector_model> psel= pdb.getAllProductSelector();
            productSelector_model model = null;
            for (int i=0; i<=psel.size();i++){
                if(psel.get(i).getProdname() .equals(list.get(idint).getProdname())){
                   x=  psel.get(i).getId();
                    break;
                }
            }

            Intent i = new Intent(getActivity(), insert_products.class);
            i.putExtra("id", x + "");
            i.putExtra("servname", serviceSearch_fragment.serName);
            i.putExtra("Name", Name);

            startActivity(i);
            getActivity().finish();

            productSearch.searchflag=false;

        } else {
            int idint = (int) (id + 1);
            productSelector_model model = null;
            try {
                model = db.readproductSelector(idint);
            } catch (JSONException e) {

            }
            //  int id1= model.getId();
            Name = model.getProdname();


            Intent i = new Intent(getActivity(), insert_products.class);
            i.putExtra("id", idint + "");
            i.putExtra("servname", serviceSearch_fragment.serName);
            i.putExtra("Name", Name);

            startActivity(i);
            getActivity().finish();
        }
    }


}
