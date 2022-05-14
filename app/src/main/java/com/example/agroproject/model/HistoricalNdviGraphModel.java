package com.example.agroproject.model;

public class HistoricalNdviGraphModel {

   // DateTime
   private String dt;
   // Max value of vegetation
   private Double max;
   // Mean value of vegetation
   private Double mean;
   // min value of vegetation
   private Double min;


    /**
     * Instantiates a new HistoricalNDviGraphModel.
     *
     * @param dt date
     * @param max value of vegetation
     * @param mean value of vegetation
     * @param min value of vegetation
     */
   public HistoricalNdviGraphModel(String dt, Double max, Double mean, Double min){
       this.dt = dt;
       this.max = max;
       this.mean = mean;
       this.min = min;
   }

    public String getDt() {
        return dt;
    }

    public Double getMax() {
        return max;
    }

    public Double getMean() {
        return mean;
    }

    public Double getMin() {
        return min;
    }
}
