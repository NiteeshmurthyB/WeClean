package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Complaint implements Parcelable {
    private String userID, driverID , adminStatus, citizenStatus, address,lattitude,longitude,
            dateofComplaint,citizenImageFilename,driverImageFilename;

    public Complaint() {
    }

    public Complaint(String userID, String driverID, String adminStatus, String citizenStatus, String address, String lattitude, String longitude, String dateofComplaint, String citizenImageFilename, String driverImageFilename) {
        this.userID = userID;
        this.driverID = driverID;
        this.adminStatus = adminStatus;
        this.citizenStatus = citizenStatus;
        this.address = address;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.dateofComplaint = dateofComplaint;
        this.citizenImageFilename = citizenImageFilename;
        this.driverImageFilename = driverImageFilename;
    }

    protected Complaint(Parcel in) {
        userID = in.readString();
        driverID = in.readString();
        adminStatus = in.readString();
        citizenStatus = in.readString();
        address = in.readString();
        lattitude = in.readString();
        longitude = in.readString();
        dateofComplaint = in.readString();
        citizenImageFilename = in.readString();
        driverImageFilename = in.readString();
    }

    public static final Creator<Complaint> CREATOR = new Creator<Complaint>() {
        @Override
        public Complaint createFromParcel(Parcel in) {
            return new Complaint(in);
        }

        @Override
        public Complaint[] newArray(int size) {
            return new Complaint[size];
        }
    };

    public String getDriverID() {
        return driverID;
    }

    public String getDateofComplaint() {
        return dateofComplaint;
    }

    public void setDateofComplaint(String dateofComplaint) {
        this.dateofComplaint = dateofComplaint;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(String adminStatus) {
        this.adminStatus = adminStatus;
    }

    public String getCitizenStatus() {
        return citizenStatus;
    }

    public void setCitizenStatus(String citizenStatus) {
        this.citizenStatus = citizenStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCitizenImageFilename() {
        return citizenImageFilename;
    }

    public void setCitizenImageFilename(String citizenImageFilename) {
        this.citizenImageFilename = citizenImageFilename;
    }

    public String getDriverImageFilename() {
        return driverImageFilename;
    }

    public void setDriverImageFilename(String driverImageFilename) {
        this.driverImageFilename = driverImageFilename;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userID);
        parcel.writeString(driverID);
        parcel.writeString(adminStatus);
        parcel.writeString(citizenStatus);
        parcel.writeString(address);
        parcel.writeString(lattitude);
        parcel.writeString(longitude);
        parcel.writeString(dateofComplaint);
        parcel.writeString(citizenImageFilename);
        parcel.writeString(driverImageFilename);
    }
}
