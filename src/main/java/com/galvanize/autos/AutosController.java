package com.galvanize.autos;

import com.galvanize.autos.exceptions.AutoNotFoundException;
import com.galvanize.autos.exceptions.InvalidAutoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
public class AutosController {

    AutosService autosService;

    public AutosController(AutosService autosService) {
        this.autosService = autosService;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void invalidAutoExceptionHandler(InvalidAutoException e) {}

    @GetMapping("api/autos")
    public ResponseEntity<AutoList> getAutos(@RequestParam(defaultValue = "%") String color,
                                             @RequestParam(defaultValue = "%") String make) {
        AutoList autoList;
        if(color.equals("%") && make.equals("%")) {
            autoList = autosService.getAutos();
        } else {
            autoList = autosService.getAutos(color, make);
        }
        return autoList.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(autoList);
    }

    @GetMapping("/api/autos/{vin}")
    public ResponseEntity<Automobile> getAutoByVin(@PathVariable String vin) {
        Automobile auto = autosService.getAuto(vin);
        return auto.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(auto);
    }

    @PostMapping("/api/autos")
    public Automobile addAuto(@RequestBody Automobile auto) {
        return autosService.addAuto(auto);
    }

    @PatchMapping("/api/autos/{vin}")
    public ResponseEntity<Automobile> updateAuto(@PathVariable String vin, @RequestBody UpdateAutoRequest request) {
        Automobile auto = autosService.updateAuto(vin, request.getOwner(), request.getColor());
        return auto.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(auto);
    }

    @DeleteMapping("/api/autos/{vin}")
    public ResponseEntity deleteAuto(@PathVariable String vin) {
        try{
            autosService.deleteAuto(vin);
        } catch (AutoNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.accepted().build();
    }
}
