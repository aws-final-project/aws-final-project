package com.galvanize.autos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AutosApiApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private AutosRepository autosRepository;
	private Random random = new Random();
	private List<Automobile> autos;

	@BeforeEach
	public void setUp() {
		autos = new ArrayList<>();
		String[] colors = new String[]{"RED", "BLUE", "BLACK", "GOLD", "SILVER"};
		String[] makes = new String[]{"Ford", "Toyota", "Honda", "Tesla"};
		Automobile auto;
		String model;

		for (int i = 0; i < 50; i++) {
			int r = random.nextInt(4);
			int year = random.nextInt(2021 - 1990) + 1990;
			if (r == 0) {
				model = "Fusion";
			} else if (r == 1) {
				model = "Camry";
			} else if (r == 2) {
				model = "Accord";
			} else {
				model = "X";
			}
			auto = new Automobile(year, makes[r], model, "AABBCC"+i);
			auto.setColor(colors[random.nextInt(5)]);
			autos.add(auto);
		}
		autosRepository.saveAll(autos);
	}


	@AfterEach
	public void tearDown() {
		autosRepository.deleteAll();
	}

	@Test
	public void contextLoads() {
	}

//GETs
	@Test
	public void getAutosExistsReturnsAutosList() {
		ResponseEntity<AutoList> response = restTemplate.getForEntity("/api/autos", AutoList.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().isEmpty()).isFalse();
	}

	@Test
	public void getAutosNoContentReturns204() {
		autosRepository.deleteAll();

		ResponseEntity<AutoList> response = restTemplate.getForEntity("/api/autos", AutoList.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void getAutoByMakeAndColorReturnsAutosList() {
		String color = autos.get(0).getColor();
		String make = autos.get(0).getMake();

		ResponseEntity<AutoList> response = restTemplate.getForEntity(String.format("/api/autos?color=%s&make=%s", color, make), AutoList.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().isEmpty()).isFalse();
		assertThat(response.getBody().getAutomobiles().get(0).getColor()).isEqualTo(color);
		assertThat(response.getBody().getAutomobiles().get(0).getMake()).isEqualTo(make);
	}

	@Test
	public void getAutoByMakeAndColorIgnoreCaseReturnsAutoList() {
		Automobile newAuto = new Automobile(2022, "RiVIAn", "TruckThingy", "12345");
		newAuto.setColor("FoREst GrEEn");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<Automobile> request = new HttpEntity<>(newAuto, httpHeaders);

		restTemplate.postForEntity("/api/autos", request, Automobile.class);

		ResponseEntity<AutoList> response = restTemplate.getForEntity(String.format("/api/autos?color=%s&make=%s", "forest green", "rivian"), AutoList.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getAutomobiles().get(0).getColor()).isEqualTo(newAuto.getColor());
		assertThat(response.getBody().getAutomobiles().get(0).getMake()).isEqualTo(newAuto.getMake());
	}

	@Test
	public void getAutoByVinIgnoreCaseReturnsAuto() {
		Automobile newAuto = new Automobile(2022, "RiVIAn", "TruckThingy", "1234ALT");
		newAuto.setColor("FoREst GrEEn");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<Automobile> request = new HttpEntity<>(newAuto, httpHeaders);

		restTemplate.postForEntity("/api/autos", request, Automobile.class);

		ResponseEntity<Automobile> response = restTemplate.getForEntity(String.format("/api/autos/1234alt"), Automobile.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getVin()).isEqualTo(newAuto.getVin());
	}

//POSTs
	@Test
	public void addAutoToDBReturnsAuto() {
		Automobile newAuto = new Automobile(2022, "Rivian", "TruckThingy", "12345");
		newAuto.setColor("Forest Green");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<Automobile> request = new HttpEntity<>(newAuto, httpHeaders);

		ResponseEntity<Automobile> response = restTemplate.postForEntity("/api/autos", request, Automobile.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getVin()).isEqualTo(newAuto.getVin());
		assertThat(response.getBody().getModel()).isEqualTo(newAuto.getModel());

		ResponseEntity<Automobile> getResponse = restTemplate.getForEntity("/api/autos/"+newAuto.getVin(), Automobile.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().getModel()).isEqualTo(newAuto.getModel());
	}

	@Test
	public void addAutoToDBInvalidAutoReturns400() {
		Automobile badAuto = new Automobile();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<Automobile> request = new HttpEntity<>(badAuto, httpHeaders);

		ResponseEntity<Automobile> response = restTemplate.postForEntity("/api/autos", request, Automobile.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

//PATCHEs

//DELETEs
	@Test
	public void canDeleteValidShouldReturn202() {
		HttpEntity<Void> httpEntity = new HttpEntity<>(new HttpHeaders());
		ResponseEntity<Void> response = restTemplate.exchange("/api/autos/AABBCC0", HttpMethod.DELETE, httpEntity, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

		ResponseEntity<AutoList> getResponse = restTemplate.getForEntity("/api/autos", AutoList.class);
		assertThat(getResponse.getBody().getAutomobiles().size()).isEqualTo(49);
	}

	@Test
	public void canDeleteInvalidReturn204() {
		HttpEntity<Void> httpEntity = new HttpEntity<>(new HttpHeaders());
		ResponseEntity<Void> response = restTemplate.exchange("/api/autos/AABBCC", HttpMethod.DELETE, httpEntity, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
}
