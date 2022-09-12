package com.mycityhome.InquilinOs.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mycityhome.InquilinOs.R;


public class YourStay extends Fragment {



    public YourStay() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_your_stay, container, false);
        //Button btnBack = myView.findViewById(R.id.btnBack);
        TextView txtRules = myView.findViewById(R.id.txtRules);

        return myView;
    }
}