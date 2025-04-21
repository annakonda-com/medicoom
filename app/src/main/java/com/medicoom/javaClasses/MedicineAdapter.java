package com.medicoom.javaClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.GnssAntennaInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medicoom.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MedicineAdapter extends ArrayAdapter<MedicinePost> {
    public MedicineAdapter(Context context, ArrayList<MedicinePost> arr) {
        super(context, R.layout.medicine_item, arr);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Medicine med = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.medicine_item, null);
        }
        Log.d("TAG", med.toString());
        Log.d("TAG",Integer.toString(med.good_until));
        ((TextView) convertView.findViewById(R.id.medicine_dosage)).setVisibility(View.GONE);
        ((TextView) convertView.findViewById(R.id.medicine_num_of_tablets)).setVisibility(View.GONE);
        ((TextView) convertView.findViewById(R.id.medicine_good_until)).setVisibility(View.GONE);

        ((TextView) convertView.findViewById(R.id.medicine_name)).setText(med.name);
        if (!med.dosage.isEmpty()) {
            ((TextView) convertView.findViewById(R.id.medicine_dosage)).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.medicine_dosage)).setText(med.dosage);
        }
        if (med.num_of_tablets != -1) {
            String str = med.num_of_tablets + "шт";
            ((TextView) convertView.findViewById(R.id.medicine_num_of_tablets)).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.medicine_num_of_tablets)).setText(str);
            if (med.remind_when != -1 && med.num_of_tablets <= med.remind_when) {
                ((TextView) convertView.findViewById(R.id.medicine_num_of_tablets))
                        .setTextColor(ContextCompat.getColor(getContext(), R.color.md_theme_error));
            }
        }
        if (med.good_until != -1) {
            SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yy", Locale.UK);
            long dat = med.good_until * 1000L;
            Date date = new Date(dat);
            ((TextView) convertView.findViewById(R.id.medicine_good_until)).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.medicine_good_until)).setText(formater.format(date));
            Date now = new Date();
            dat = dat - (24 * 7 * 60 * 60 * 1000);
            Date warning_time = new Date(dat);
            if (now.after(warning_time)) {
                ((TextView) convertView.findViewById(R.id.medicine_good_until))
                        .setTextColor(ContextCompat.getColor(getContext(), R.color.md_theme_error));
            }
        }
        DialogInterface.OnClickListener yesListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance
                                ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("users/" + currentUser.getUid());
                mDatabase.child("medicines").child(med.post_id).removeValue();
            }
        };
        convertView.findViewById(R.id.delete_med).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.med_delete)
                        .setMessage(R.string.you_want_med)
                        .setPositiveButton(R.string.yes, yesListener)
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });
        return convertView;
    }
}
