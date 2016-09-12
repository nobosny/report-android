package com.vectoranalytica.trashwatchfree.model;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Nobosny on 1/10/2015.
 */
public class Serverbin extends SugarRecord<Serverbin> {
    Date recorddate;

    Boolean mosquitoes;
    Boolean cockroaches;
    Boolean mice;
    Boolean biohazard;
    String othervectors;

    Boolean houses;
    Boolean businesses;
    Boolean constructionsites;
    String othersources;

    String imagefile;

    String description;

    Double lat;
    Double lon;

    public Serverbin() {
        mosquitoes = false;
        cockroaches = false;
        mice = false;
        biohazard = false;

        houses = false;
        businesses = false;
        constructionsites = false;
    }

    public Date getRecorddate() {
        return recorddate;
    }

    public void setRecorddate(Date recorddate) {
        this.recorddate = recorddate;
    }

    public Boolean getMosquitoes() {
        return mosquitoes;
    }

    public void setMosquitoes(Boolean mosquitoes) {
        this.mosquitoes = mosquitoes;
    }

    public Boolean getCockroaches() {
        return cockroaches;
    }

    public void setCockroaches(Boolean cockroaches) {
        this.cockroaches = cockroaches;
    }

    public Boolean getMice() {
        return mice;
    }

    public void setMice(Boolean mice) {
        this.mice = mice;
    }

    public Boolean getBiohazard() {
        return biohazard;
    }

    public void setBiohazard(Boolean biohazard) {
        this.biohazard = biohazard;
    }

    public String getOthervectors() {
        return othervectors;
    }

    public void setOthervectors(String othervectors) {
        this.othervectors = othervectors;
    }

    public Boolean getHouses() {
        return houses;
    }

    public void setHouses(Boolean houses) {
        this.houses = houses;
    }

    public Boolean getBusinesses() {
        return businesses;
    }

    public void setBusinesses(Boolean businesses) {
        this.businesses = businesses;
    }

    public Boolean getConstructionsites() {
        return constructionsites;
    }

    public void setConstructionsites(Boolean constructionsites) {
        this.constructionsites = constructionsites;
    }

    public String getOthersources() {
        return othersources;
    }

    public void setOthersources(String othersources) {
        this.othersources = othersources;
    }

    public String getImagefile() {
        return imagefile;
    }

    public void setImagefile(String imagefile) {
        this.imagefile = imagefile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
