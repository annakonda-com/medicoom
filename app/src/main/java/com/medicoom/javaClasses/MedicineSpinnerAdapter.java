package com.medicoom.javaClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medicoom.R;

import java.util.ArrayList;

public class MedicineSpinnerAdapter extends ArrayAdapter<Medicine> {
    public MedicineSpinnerAdapter(Context context, int textViewResourceId, ArrayList<Medicine> arr) {
        super(context, textViewResourceId, arr);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Medicine med = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, null);
        }


        ((TextView) convertView).setText(med.getName());

        return convertView;
    }

    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Medicine med = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        ((TextView) convertView).setText(med.getName());

        return convertView;
    }
}
