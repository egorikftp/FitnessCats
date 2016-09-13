package com.egoriku.catsrunning.models.Tracks;

import com.egoriku.catsrunning.models.Track;

import java.util.ArrayList;

public class TracksAfterRequestModel {
    private String status;
    private String code;
    private ArrayList<Track> tracks;

    public TracksAfterRequestModel() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }
}
