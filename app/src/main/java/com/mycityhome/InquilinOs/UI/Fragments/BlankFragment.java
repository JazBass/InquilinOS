package com.mycityhome.InquilinOs.UI.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mycityhome.InquilinOs.UI.MainActivity;
import com.mycityhome.InquilinOs.R;

public class BlankFragment extends Fragment implements View.OnClickListener {

    String mLang;

    public BlankFragment() {

    }

    public BlankFragment(String mLang) {
        this.mLang = mLang;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_blank, container, false);
        Button btnAccept = (Button) myView.findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(this);
        RadioButton rbFrench = myView.findViewById(R.id.rbFrench);
        RadioButton rbSpanish = myView.findViewById(R.id.rbSpanish);
        RadioButton rbEnglish = myView.findViewById(R.id.rbEnglish);
        switch (mLang) {
            case "en":
                rbEnglish.setChecked(true);
                break;
            case "fr":
                rbFrench.setChecked(true);
                break;
            case "es":
                rbSpanish.setChecked(true);
                break;
        }
        setHasOptionsMenu(true);
        return myView;
    }

    public void onClick(View view) {
        RadioGroup myRadioGroup = requireActivity().findViewById(R.id.radioGroup);
        RadioButton myRadioButton = requireActivity().findViewById(myRadioGroup.getCheckedRadioButtonId());
        String lang = (String) myRadioButton.getTag();
        ((MainActivity) requireActivity()).setLanguage(lang);
        setItemsVisibly();
        requireActivity().onBackPressed();
    }

    @Override
    public void onStop() {
        setItemsVisibly();
        super.onStop();
    }

    private void setItemsVisibly(){
        requireActivity().findViewById(R.id.txtWelcome).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.btnAbout).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.btnContact).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.btnInfo).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.btnServices).setVisibility(View.VISIBLE);
    }
}