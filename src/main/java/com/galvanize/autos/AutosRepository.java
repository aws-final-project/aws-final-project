package com.galvanize.autos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutosRepository extends JpaRepository<Automobile, Long> {
    List<Automobile> findAllByColorContainsIgnoreCaseAndMakeContainsIgnoreCase(String color, String make);

    Optional<Automobile> findByVinIgnoreCase(String vin);
}
