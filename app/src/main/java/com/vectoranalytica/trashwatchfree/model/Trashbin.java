package com.vectoranalytica.trashwatchfree.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.orm.SugarRecord;
import com.vectoranalytica.trashwatchfree.ImageResizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Nobosny on 1/6/2015.
 */
public class Trashbin extends SugarRecord<Trashbin> {
    Date recorddate;

    Boolean mosquitoes;
    Boolean cockroaches;
    Boolean mice;
    Boolean biohazard;
    Boolean tick;
    String othervectors;

    Boolean houses;
    Boolean businesses;
    Boolean constructionsites;
    String othersources;

    String imagefile;

    String description;

    Double lat;
    Double lon;

    Boolean synced;

    public Trashbin() {
        mosquitoes = false;
        cockroaches = false;
        mice = false;
        biohazard = false;
        tick = false;

        houses = false;
        businesses = false;
        constructionsites = false;

        synced = false;
    }

    public static List<Trashbin> getUnsynced() {
        return Trashbin.find(Trashbin.class, "synced = ?", "0");
    }

// Getters and Setters
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

    public Boolean getTick() {
        return tick;
    }

    public void setTick(Boolean tick) {
        this.tick = tick;
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

    public String getImageFileBase64() {
        if (imagefile == null) {
            return "";
        }
        File ifile = new File(imagefile);
        if (ifile.exists()) {
            Bitmap bitmap = ImageResizer.decodeSampledBitmapFromFile(imagefile, 800, 600);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.URL_SAFE);
            //String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            return encodedImage;
            //return "IMAGE SIZE " + encodedImage.length();
        } else {
            return "";
        }
    }

    public byte[] getImageFileBase64Bytes() {
        Bitmap bitmap = BitmapFactory.decodeFile(imagefile);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
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

    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }

    @Override
    public String toString() {
        return "Trashbin{" +
                "recorddate=" + recorddate +
                ", mosquitoes=" + mosquitoes +
                ", cockroaches=" + cockroaches +
                ", mice=" + mice +
                ", biohazard=" + biohazard +
                ", tick=" + tick +
                ", othervectors='" + othervectors + '\'' +
                ", houses=" + houses +
                ", businesses=" + businesses +
                ", constructionsites=" + constructionsites +
                ", othersources='" + othersources + '\'' +
                ", imagefile='" + imagefile + '\'' +
                ", description='" + description + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", synced=" + synced +
                '}';
    }
}
