package com.medicoom.javaClasses;

public class MedicinePost extends Medicine{
    public MedicinePost(String name, String dosage, int num_of_tablets, int good_until, int remind_when, String post_id) {
        super(name, dosage, num_of_tablets, good_until, remind_when);
        this.post_id = post_id;
    }
    public String getPostId(){
        return this.post_id;
    }
}
