package com.example.harkkatyo;

public class JobSelfSufficiencyData {
    private int yearjs;
    private float selfsufficiency;

    public JobSelfSufficiencyData(int yjs, float jss) {
        yearjs = yjs;
        selfsufficiency = jss;
    }

    public int getYearjs() {

        return yearjs;
    }
    public void setYearjs(int yearjs) {

        this.yearjs = yearjs;
    }
    public float getSelfsufficiency() {

        return selfsufficiency;
    }
    public void setSelfsufficiency(float selfsufficiency) {

        this.selfsufficiency = selfsufficiency;
    }
}
