package com.galvanize.autos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table (name = "automobiles")
public class Automobile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int year;
    private String make;
    private String model;
    private String color;
    private String owner;
    private String vin;

    public Automobile() {}

    public Automobile(int year, String make, String model, String vin) {
        this.year = year;
        this.make = make;
        this.model = model;
        this.vin = vin;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getYear() {
        return year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getVin() {
        return vin;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return this.vin == null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Automobile that = (Automobile) o;
        return vin.equals(that.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin);
    }

    @Override
    public String toString() {
        return "Automobile{" +
                "year=" + year +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", color='" + color + '\'' +
                ", owner='" + owner + '\'' +
                ", vin='" + vin + '\'' +
                '}';
    }
}
