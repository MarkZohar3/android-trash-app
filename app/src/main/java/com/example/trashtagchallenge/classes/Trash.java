package com.example.trashtagchallenge.classes;

import java.util.Date;


public class Trash {
    private String trashID;
    private double lat;
    private double lng;

    private String entryDescription;
    private String entryUsername;
    private String entryLink;
    private TrashDate entryDate;

    private String cleanedDescription;
    private String cleanedUsername;
    private String cleanedLink;
    private TrashDate cleanedDate;

    public void setEntryDate(TrashDate entryDate) {
        this.entryDate = entryDate;
    }

    public void setCleanedDate(TrashDate cleanedDate) {
        this.cleanedDate = cleanedDate;
    }

    public TrashDate getEntryDate() {
        return entryDate;
    }

    public TrashDate getCleanedDate() {
        return cleanedDate;
    }



    public Trash(){

    }


    public Trash(String trashID, String entryDescription, double lat, double lng, String entryUsername, String entryLink, TrashDate entryDate) {
        this.trashID = trashID;
        this.entryDescription = entryDescription;
        this.lat = lat;
        this.lng = lng;
        this.entryUsername = entryUsername;
        this.entryLink = entryLink;
        this.entryDate = entryDate;
        this.cleanedDescription = null;
        this.cleanedUsername= null;
        this.cleanedLink= null;
        this.cleanedDate = null;
    }

    public String getEntryDescription() {
        return entryDescription;
    }

    public void setEntryDescription(String entryDescription) {
        this.entryDescription = entryDescription;
    }

    public void setCleanedDescription(String cleanedDescription) {
        this.cleanedDescription = cleanedDescription;
    }

    public void setTrashID(String trashID) {
        this.trashID = trashID;
    }

    public String getTrashID() {
        return trashID;
    }



    public String getCleanedDescription() {
        return cleanedDescription;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }



    public void setCleanedUsername(String cleanedUsername) {
        this.cleanedUsername = cleanedUsername;
    }

    public String getEntryLink() {
        return entryLink;
    }

    public String getCleanedLink() {
        return cleanedLink;
    }

    public void setEntryLink(String entryLink) {
        this.entryLink = entryLink;
    }

    public void setCleanedLink(String cleanedLink) {
        this.cleanedLink = cleanedLink;
    }



    public String getEntryUsername() {
        return entryUsername;
    }

    public String getCleanedUsername() {
        return cleanedUsername;
    }

}
