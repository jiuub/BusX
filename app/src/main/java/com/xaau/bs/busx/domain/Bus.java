package com.xaau.bs.busx.domain;


public class Bus {
    private int busID;
    private String busName;
    private String busCity;
    private int busPrice;
    private String busStart;
    private String busEnd;

    public Bus() {
    }

    public int getBusID() {
        return busID;
    }

    public void setBusID(int busID) {
        this.busID = busID;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusCity() {
        return busCity;
    }

    public void setBusCity(String busCity) {
        this.busCity = busCity;
    }

    public int getBusPrice() {
        return busPrice;
    }

    public void setBusPrice(int busPrice) {
        this.busPrice = busPrice;
    }

    public String getBusStart() {
        return busStart;
    }

    public void setBusStart(String busStart) {
        this.busStart = busStart;
    }

    public String getBusEnd() {
        return busEnd;
    }

    public void setBusEnd(String busEnd) {
        this.busEnd = busEnd;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "busID=" + busID +
                ", busName='" + busName + '\'' +
                ", busCity='" + busCity + '\'' +
                ", busPrice=" + busPrice +
                ", busStart=" + busStart +
                ", busEnd=" + busEnd +
                '}';
    }
}
