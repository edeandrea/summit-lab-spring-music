package com.redhat.summit2019.springmusic.domain;

import io.swagger.v3.oas.annotations.media.Schema;

public class ApplicationInfo {
    @Schema(description = "The application profiles")
    private String[] profiles;

    @Schema(description = "The application services")
    private String[] services;

    public ApplicationInfo(String[] profiles, String[] services) {
        this.profiles = profiles;
        this.services = services;
    }

    public ApplicationInfo() {

    }

    public String[] getProfiles() {
        return this.profiles;
    }

    public void setProfiles(String[] profiles) {
        this.profiles = profiles;
    }

    public String[] getServices() {
        return this.services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }
}
