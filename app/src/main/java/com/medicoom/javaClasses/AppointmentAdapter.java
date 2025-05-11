package com.medicoom.javaClasses;

import static com.medicoom.utils.myUtils.dateFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    public AppointmentAdapter(Context context, ArrayList<Appointment> arr) {
        super(context, R.layout.treatment_item, arr);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Appointment curr_app = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.treatment_item, null);
        }
        TextView name = convertView.findViewById(R.id.appointment_name);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid() + "/medicines");
        mDatabase.child(curr_app.getMedicine_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Medicine med = dataSnapshot.getValue(Medicine.class);
                        String appointment_name = med.getName() + " " + med.getDosage();
                        name.setText(appointment_name);
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
                if (curr_app.isArchive()){
                    showMenu(v, R.menu.in_archive_treatment_menu, curr_app);
                } else if (curr_app.isOn_pause()){
                    showMenu(v, R.menu.on_pause_treatment_menu, curr_app);
                } else {
                    showMenu(v, R.menu.default_treatment_menu, curr_app);
                }
            }
        });

        return convertView;
    }

    void showMenu(View v, int menu, Appointment curr_app) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.getMenuInflater().inflate(menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_dont_remind){
                    curr_app.setNotifications(false);
                }
                if (item.getItemId() == R.id.action_pause){
                    curr_app.setOn_pause(true);
                }
                if (item.getItemId() == R.id.action_continue){
                    curr_app.setOn_pause(false);
                }
                if (item.getItemId() == R.id.action_to_archive){
                    curr_app.setArchive(true);
                }
                if (item.getItemId() == R.id.action_restart_get){
                    curr_app.setArchive(false);
                }
                if (item.getItemId() == R.id.action_stop_get){
                    curr_app.setDeleted(true);
                    /*DialogInterface.OnClickListener yesListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            curr_app.setDeleted(true);
                        }
                    };*/
                    /*new AlertDialog.Builder(getContext())
                            .setTitle(R.string.app_delete)
                            .setMessage(R.string.you_want_app)
                            .setPositiveButton(R.string.yes, yesListener)
                            .setNegativeButton(R.string.no, null)
                            .show();*/

                }
                Appointment res_app = new Appointment(curr_app.getAmount_at_once(),
                        curr_app.isArchive(), curr_app.getDays(), curr_app.days_of_week,
                        curr_app.isDeleted(), curr_app.every_x_days, curr_app.getHow_to_get(),
                        curr_app.getMedicine_id(), curr_app.isNotifications(), curr_app.isOn_pause(),
                        curr_app.getStart_date(), curr_app.getTimes());
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance
                                ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("users" + "/" + currentUser.getUid());
                mDatabase.child("appointments").child(curr_app.getPost_id())
                        .setValue(res_app).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {}});
                return false;
            }
        });
    }
}
