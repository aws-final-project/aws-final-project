package com.galvanize.autos;

import com.galvanize.autos.exceptions.AutoNotFoundException;
import com.galvanize.autos.exceptions.InvalidAutoException;
import org.springframework.stereotype.Service;

@Service
public class AutosService {

    private AutosRepository autosRepository;

    public AutosService(AutosRepository autosRepository) {
        this.autosRepository = autosRepository;
    }

    public AutoList getAutos() {
        return new AutoList(autosRepository.findAll());
    }

    public AutoList getAutos(String color, String make) {
        return new AutoList(autosRepository.findAllByColorContainsIgnoreCaseAndMakeContainsIgnoreCase(color, make));
    }

    public Automobile addAuto(Automobile auto) {
        if(auto.getVin() == null || auto.getMake() == null || auto.getYear() == 0 || auto.getModel() == null) {
            throw new InvalidAutoException();
        } else {
            return autosRepository.save(auto);
        }
    }

    public Automobile getAuto(String vin) {
        return autosRepository.findByVinIgnoreCase(vin).orElse(new Automobile());
    }

    public Automobile updateAuto(String vin, String owner, String color) {
        Automobile auto = getAuto(vin);
        if (!auto.isEmpty()) {
            auto.setOwner(owner);
            auto.setColor(color);
            autosRepository.save(auto);
            return auto;
        } else {
            return new Automobile();
        }
    }

    public void deleteAuto(String vin) {
        Automobile dAuto = getAuto(vin);
        if(!dAuto.isEmpty()) {
            autosRepository.delete(dAuto);
        } else {
            throw new AutoNotFoundException();
        }
    }
}
