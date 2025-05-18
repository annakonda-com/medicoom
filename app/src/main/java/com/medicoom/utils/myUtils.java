package com.medicoom.utils;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.javaClasses.Appointment;
import com.medicoom.javaClasses.AppointmentOnDate;
import com.medicoom.javaClasses.Medicine;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class myUtils {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy", Locale.getDefault());
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm", Locale.getDefault());
    public static final String dbPath = "https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/";


    public static boolean isSpace(String str) {
        String[] myStr = str.split("");
        boolean res = true;
        for (String a : myStr) {
            if (!(a.equals(" ") || a.equals("\n"))) {
                res = false;
                break;
            }
        }
        return res;
    }

    public static String secToTime(int time) {
        String hour;
        String minute;
        if (time / 3600 % 60 < 10) {
            hour = "0" + time / 3600 % 60;
        } else {
            hour = String.valueOf(time / 3600 % 60);
        }
        if (time % 3600 / 60 < 10) {
            minute = "0" + time % 3600 / 60;
        } else {
            minute = String.valueOf(time % 3600 / 60);
        }
        return hour + ":" + minute;
    }

    public static void change_num_of_tablets(boolean is_got, FirebaseUser curr_usr, AppointmentOnDate appointment, Context context) {
        DatabaseReference medUnit = FirebaseDatabase.getInstance(myUtils.dbPath)
                .getReference("users").child(curr_usr.getUid())
                .child("medicines").child(appointment.getMed_id());
        if (is_got) {
            medUnit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Medicine med = snapshot.getValue(Medicine.class);
                    if (!med.isDeleted() && med.getNum_of_tablets() != -1) {
                        DatabaseReference firstApp = FirebaseDatabase.getInstance(myUtils.dbPath)
                                .getReference("users").child(curr_usr.getUid()).child("appointments")
                                .child(appointment.getAppointment_id());
                        firstApp.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                Appointment thisApp = snapshot1.getValue(Appointment.class);
                                int new_num_of_tablets = (med.getNum_of_tablets()) - (thisApp.getAmount_at_once());
                                if (new_num_of_tablets >= 0) {
                                    DatabaseReference num_of_tabletss = snapshot.child("num_of_tablets")
                                            .getRef();
                                    Log.d("MAYTAG", String.valueOf(new_num_of_tablets));
                                    num_of_tabletss.setValue(new_num_of_tablets).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("MAYTAG", "succesful");
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "Слишком мало таблеток!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "loadMedicine:onCancelled", error.toException());
                            }

                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "loadMedicine:onCancelled", error.toException());
                }
            });
        } else {
            Log.d("MAYTAG", "Not got, ok");
            medUnit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Medicine med = snapshot.getValue(Medicine.class);
                    if (!med.isDeleted()  && med.getNum_of_tablets() != -1) {
                        DatabaseReference firstApp = FirebaseDatabase.getInstance(myUtils.dbPath)
                                .getReference("users").child(curr_usr.getUid()).child("appointments")
                                .child(appointment.getAppointment_id());
                        firstApp.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                Appointment thisApp = snapshot1.getValue(Appointment.class);
                                int new_num_of_tablets = med.getNum_of_tablets() + thisApp.getAmount_at_once();
                                DatabaseReference num_of_tablets = snapshot.child("num_of_tablets")
                                        .getRef();
                                num_of_tablets.setValue(new_num_of_tablets);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "loadMedicine:onCancelled", error.toException());
                            }

                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "loadMedicine:onCancelled", error.toException());
                }
            });
        }
    }
}
