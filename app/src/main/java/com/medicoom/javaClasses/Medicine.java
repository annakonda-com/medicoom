package com.medicoom.javaClasses;

public class Medicine {
    String name;
    String dosage;
    int num_of_tablets;
    int good_until;
    int remind_when;
    String post_id;
    boolean deleted;

    public Medicine(String name, String dosage, int num_of_tablets, int good_until, int remind_when, boolean deleted) {
        this.name = name;
        this.dosage = dosage;
        this.num_of_tablets = num_of_tablets;
        this.good_until = good_until;
        this.remind_when = remind_when;
        this.deleted = deleted;
    }

    public Medicine() {
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getPostId() {
        return post_id;
    }

    public void setPostId(String post_id) {
        this.post_id = post_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getNum_of_tablets() {
        return num_of_tablets;
    }

    public void setNum_of_tablets(int num_of_tablets) {
        this.num_of_tablets = num_of_tablets;
    }

    public int getGood_until() {
        return good_until;
    }

    public void setGood_until(int good_until) {
        this.good_until = good_until;
    }

    public int getRemind_when() {
        return remind_when;
    }

    public void setRemind_when(int remind_when) {
        this.remind_when = remind_when;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "name='" + name + '\'' +
                ", dosage='" + dosage + '\'' +
                ", num_of_tablets=" + num_of_tablets +
                ", good_until=" + good_until +
                ", remind_when=" + remind_when +
                '}';
    }
}
