package com.example.harkkatyo;

public class JobRateData {
    private int yearjr;
    private float jobrate;

    public JobRateData(int yjr, float jr) {
        yearjr = yjr;
        jobrate = jr;
    }

    public int getYearjr() {

        return yearjr;
    }
    public void setYearjr(int yearjr) {

        this.yearjr = yearjr;
    }
    public float getJobRate() {

        return jobrate;
    }
    public void setJobrate(float jobrate) {

        this.jobrate = jobrate;
    }
}
