package com.galvanize.autos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.autos.exceptions.AutoNotFoundException;
import com.galvanize.autos.exceptions.InvalidAutoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutosController.class)
public class AutosControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AutosService autosService;

    ObjectMapper objectMapper = new ObjectMapper();

    // - GET: /api/autos returns list of all cars in database
    @Test
    public void getReturnsListOfAllCars() throws Exception {
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1990 + i, "Toyota", "Camry", "ASDF" + i));
        }
        when(autosService.getAutos()).thenReturn(new AutoList(automobiles));

        mockMvc.perform(get("/api/autos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.automobiles", hasSize(5)));
    }

    // - GET: /api/autos returns 204 when no cars are found
    @Test
    public void getNoParamsNoMatchShouldReturnNoContent() throws Exception {
        when(autosService.getAutos()).thenReturn(new AutoList());
        mockMvc.perform(get("/api/autos"))
                .andExpect(status().isNoContent());
    }

    // - GET: /api/autos?color=slate returns all slate coloured cars
    @Test
    public void getAllAutosByColorReturnsList() throws Exception {
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1990 + i, "Toyota", "Camry", "ASDF" + i));
        }
        when(autosService.getAutos(anyString(), anyString())).thenReturn(new AutoList(automobiles));

        mockMvc.perform(get("/api/autos?color=SLATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.automobiles", hasSize(5)))
                .andExpect(jsonPath("isEmpty").doesNotExist());
    }

    // - GET: /api/autos?make=Toyota returns all Toyotas
    @Test
    public void getAllAutosByMakeReturnsList() throws Exception {
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1990 + i, "Toyota", "Camry", "ASDF" + i));
        }
        when(autosService.getAutos(anyString(), anyString())).thenReturn(new AutoList(automobiles));

        mockMvc.perform(get("/api/autos?make=Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.automobiles", hasSize(5)));
    }

    // - GET: /api/autos?color=SLATE&make=Toyota returns all slate Toyotas
    @Test
    public void getAllAutosByMakeAndColorReturnsList() throws Exception {
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1990 + i, "Toyota", "Camry", "ASDF" + i));
        }
        when(autosService.getAutos(anyString(), anyString())).thenReturn(new AutoList(automobiles));

        mockMvc.perform(get("/api/autos?color=SLATE&make=Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.automobiles", hasSize(5)));
    }


    //- POST: /api/autos return the car added to the database when successful
    @Test
    public void addAutoToDataBaseValid() throws Exception{
        Automobile newCar = new Automobile(2020, "Kia", "Forte", "1234");

        when(autosService.addAuto(any(Automobile.class))).thenReturn(newCar);

        mockMvc.perform(post("/api/autos").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("make").value("Kia"))
                .andExpect(jsonPath("year").value(2020))
                .andExpect(jsonPath("model").value("Forte"))
                .andExpect(jsonPath("vin").value("1234"));
    }

    //- POST: /api/autos return 400 when the car cannot be added to the database
    @Test
    public void addAutoBadRequest() throws Exception {
        Automobile newCar = new Automobile(2020, "Kia", "Forte", "1234");

        when(autosService.addAuto(any(Automobile.class))).thenThrow(InvalidAutoException.class);

        mockMvc.perform(post("/api/autos").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isBadRequest());
    }

    //- GET: /api/autos/{vin} return the car when search by VIN
    @Test
    public void getAutoByVinReturnAuto() throws Exception {
        Automobile newCar = new Automobile(2020, "Kia", "Forte", "1234");

        when(autosService.getAuto(anyString())).thenReturn(newCar);

        mockMvc.perform(get("/api/autos/"+newCar.getVin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("vin").value("1234"));
    }

    //- GET: /api/autos/{vin} 204 vehicle not found when no match with provided VIN
    @Test
    public void getAutoByVinBadRequest() throws Exception {
        when(autosService.getAuto(anyString())).thenReturn(new Automobile());

        mockMvc.perform(get("/api/autos/1234"))
                .andExpect(status().isNoContent());
    }

    //- PATCH: /api/autos/{vin} return the car with updated color and owner
    @Test
    public void patchAutoByVinReturnsAuto() throws Exception {
        Automobile newCar = new Automobile(2020, "Kia", "Forte", "1234");
        newCar.setColor("blue");
        newCar.setOwner("Joe");
        String json = "{\"owner\":\"Joe\",\"color\":\"blue\"}";

        when(autosService.updateAuto(anyString(), anyString(), anyString())).thenReturn(newCar);

        mockMvc.perform(patch("/api/autos/" + newCar.getVin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("owner").value("Joe"))
                .andExpect(jsonPath("color").value("blue"));
    }

    //- PATCH: /api/autos/{vin} return car with updated color
    @Test
    public void patchAutoByVinColorOnlyReturnsAuto() throws Exception {
        Automobile newCar = new Automobile(2020, "Kia", "Forte", "1234");
        newCar.setColor("blue");
        String json = "{\"color\":\"blue\"}";

        when(autosService.updateAuto(anyString(), eq(null), anyString())).thenReturn(newCar);

        mockMvc.perform(patch("/api/autos/" + newCar.getVin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("owner").isEmpty())
                .andExpect(jsonPath("color").value("blue"));
    }

    //- PATCH: /api/autos/{vin} return car with updated owner
    @Test
    public void patchAutoByVinOwnerOnlyReturnsAuto() throws Exception {
        Automobile newCar = new Automobile(2020, "Kia", "Forte", "1234");
        newCar.setOwner("Casey");
        String json = "{\"owner\":\"Casey\"}";

        when(autosService.updateAuto(anyString(), anyString(), eq(null))).thenReturn(newCar);

        mockMvc.perform(patch("/api/autos/" + newCar.getVin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("color").isEmpty())
                .andExpect(jsonPath("owner").value("Casey"));
    }

    //- PATCH: /api/autos/{vin} return 204 vehicle not found if VIN doesn't have a match in DB
    @Test
    public void patchAutoByVinReturnsVehicleNotFound() throws Exception {
        when(autosService.updateAuto(anyString(), anyString(), anyString())).thenReturn(new Automobile());

        mockMvc.perform(patch("/api/autos/1234")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"owner\":\"Joe\",\"color\":\"blue\"}"))
                .andExpect(status().isNoContent());
    }

    //- PATCH: /api/autos/{vin} return 400 bad request if attribute other than color and/or owner is requested to be updated
    @Test
    public void patchAutoByVinWithInvalidRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/api/autos/1234")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"model\":\"Tucson\",\"color\"\":blue\"}"))
                .andExpect(status().isBadRequest());
    }

    //- DELETE: /api/autos/{vin} return 202 automobile delete request accepted
    @Test
    public void deleteAutoByVinReturnsAccepted() throws Exception {
        mockMvc.perform(delete("/api/autos/1234"))
                .andExpect(status().isAccepted());
        verify(autosService).deleteAuto(anyString());

    }

    //- DELETE: /api/autos/{vin} return 204 vehicle not found when VIN doesn't have a match in DB
    @Test
    public void deleteAutoByVinReturns204() throws Exception {
        doThrow(new AutoNotFoundException()).when(autosService).deleteAuto(anyString());
        mockMvc.perform(delete("/api/autos/1234"))
                .andExpect(status().isNoContent());
    }
}
