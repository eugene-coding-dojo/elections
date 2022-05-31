package org.elections;

public class Elector {
    private String district;
    private String name;

    public static Elector withDistrict_Name_(String district, String name) {
        return new Elector().setDistrict_Name_(district, name);
    }

    private Elector setDistrict_Name_(String district, String name) {
        this.district = district;
        this.name = name;
        return this;
    }
}
