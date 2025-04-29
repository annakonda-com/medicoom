package com.medicoom.javaClasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AppointmentAdapter extends ArrayAdapter<AppointementPost> {
    SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy", Locale.getDefault());

    public AppointmentAdapter(Context context, ArrayList<AppointementPost> arr) {
        super(context, R.layout.treatment_item, arr);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AppointementPost curr_app = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.treatment_item, null);
        }
        TextView name = convertView.findViewById(R.id.appointment_name);

        Log.d("MAYTAG", curr_app.getMedicine_id());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid() + "/medicines");
        mDatabase.child(curr_app.getMedicine_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Medicine med = dataSnapshot.getValue(Medicine.class);
                        name.setText(med.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                    }
                });

        Date startDate = new Date(curr_app.getStart_date() * 1000L);
        String description = "с " + dateFormat.format(startDate);
        ((TextView) convertView.findViewById(R.id.appointment_description)).setText(description);

        convertView.findViewById(R.id.menu_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v, R.menu.default_treatment_menu);
            }
        });

        return convertView;
    }

    void showMenu(View v, int menu) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.getMenuInflater().inflate(menu, popupMenu.getMenu());
        popupMenu.show();
    }

}
