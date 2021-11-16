package com.galvanize.autos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Objects;

public class AutoList {

    private List<Automobile> automobiles;

    public AutoList() { }

    public AutoList(List<Automobile> automobiles) {
        this.automobiles = automobiles;
    }

    public List<Automobile> getAutomobiles() {
        return automobiles;
    }

    public void setAutomobiles(List<Automobile> automobiles) {
        this.automobiles = automobiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoList autoList1 = (AutoList) o;
        return automobiles.equals(autoList1.automobiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(automobiles);
    }

    @Override
    public String toString() {
        return "AutoList{" +
                "autoList=" + automobiles +
                '}';
    }

    @JsonIgnore
    public boolean isEmpty() {
        return this.automobiles == null || this.automobiles.isEmpty();
    }
}
