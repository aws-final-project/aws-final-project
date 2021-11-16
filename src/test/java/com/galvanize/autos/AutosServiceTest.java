package com.galvanize.autos;

import com.galvanize.autos.exceptions.AutoNotFoundException;
import com.galvanize.autos.exceptions.InvalidAutoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AutosServiceTest {

    private AutosService autosService;
    private Automobile automobile;

    @Mock
    private AutosRepository autosRepository;

    @BeforeEach
    public void setUp() {
        autosService = new AutosService(autosRepository);
        automobile = new Automobile(2000, "Toyota", "Venza", "AKS123");
    }

    @Test
    public void getAutosNoParamsReturnsAutoList() {
        when(autosRepository.findAll()).thenReturn(Arrays.asList(automobile));
        AutoList autoList = autosService.getAutos();
        assertThat(autoList).isNotNull();
        assertThat(autoList.isEmpty()).isFalse();
    }

    @Test
    public void getAutosWithColorAndMakeReturnsAutoList() {
        automobile.setColor("BLACK");
        when(autosRepository.findAllByColorContainsIgnoreCaseAndMakeContainsIgnoreCase(anyString(), anyString())).thenReturn(Arrays.asList(automobile));
        AutoList autoList = autosService.getAutos("BLACK", "Toyota");
        assertThat(autoList).isNotNull();
        assertThat(autoList.isEmpty()).isFalse();

    }

    @Test
    public void addAutoToDbReturnsAuto() {
        when(autosRepository.save(any(Automobile.class))).thenReturn(automobile);
        Automobile addAuto = autosService.addAuto(automobile);
        assertThat(addAuto).isNotNull();
        assertThat(addAuto.getMake()).isEqualTo("Toyota");
    }

    @Test
    public void addAutoInvalidAutoReturnsInvalidAutoEx() {
        assertThrows(InvalidAutoException.class, () ->{
            autosService.addAuto(new Automobile());
        });
    }

    @Test
    public void getAutoByVinValidReturnsAuto() {
        when(autosRepository.findByVinIgnoreCase(anyString())).thenReturn(java.util.Optional.ofNullable(automobile));
        Automobile auto = autosService.getAuto(automobile.getVin());
        assertThat(auto).isNotNull();
        assertThat(auto.getVin()).isEqualTo(automobile.getVin());
    }

    @Test
    public void getAutoByVinInvalidReturnsEmptyAuto() {
        when(autosRepository.findByVinIgnoreCase(anyString())).thenReturn(Optional.empty());
        Automobile auto = autosService.getAuto(automobile.getVin());
        assertThat(auto.isEmpty()).isTrue();
    }

    @Test
    public void updateAutoByVinValidReturnsAuto() {
        when(autosRepository.findByVinIgnoreCase(anyString())).thenReturn(Optional.ofNullable(automobile));
        when(autosRepository.save(any(Automobile.class))).thenReturn(automobile);
        Automobile auto = autosService.updateAuto(automobile.getVin(), "Christopher", "RED");
        assertThat(auto).isNotNull();
    }

    @Test
    public void updateAutoByVinInvalidReturnsEmptyAuto() {
        when(autosRepository.findByVinIgnoreCase(anyString())).thenReturn(Optional.empty());
        Automobile auto = autosService.updateAuto(automobile.getVin(), "Christopher", "RED");
        assertThat(auto.isEmpty()).isTrue();
    }

    @Test
    public void deleteAutoByVinValid() {
        when(autosRepository.findByVinIgnoreCase(anyString())).thenReturn(Optional.ofNullable(automobile));

        autosService.deleteAuto(automobile.getVin());
        verify(autosRepository).delete(any(Automobile.class));
    }

    @Test
    public void deleteAutoByVinInvalid() {
        when(autosRepository.findByVinIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThrows(AutoNotFoundException.class, ()->{
           autosService.deleteAuto("badvin");
        });

    }
}
