package uk.ac.ic.kyoto.simulations;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ic.kyoto.util.sim.jsonobjects.simulations.CountryData;

import com.google.inject.AbstractModule;

public class LoadCountryDataTest {

	Map<String, CountryData> sampleData;

	@Before
	public void setUp() {
		sampleData = new HashMap<String, CountryData>();
		sampleData.put("GBR", new CountryData("AnnexOneReduce",
				"United Kingdom", "GBR", "241930", "56121", "1300000000000.0",
				"0.0385", "611278440", "554139000", "570219000"));
		sampleData.put("LVA", new CountryData("AnnexOneSustain", "Latvia",
				"LVA", "62249", "17926", "5901334632.0", "0.0496", "8543268",
				"7990000", "14052000"));
		sampleData.put("BHR", new CountryData("NonAnnexOne", "Bahrain", "BHR",
				"760", "19", "6892190977.0", "0.0701", "18405000", "18405000",
				"11885000"));
		sampleData.put("USA", new CountryData("USAgent", "United States",
				"USA", "9161966", "1650062", "8740000000000.0", "0.0492",
				"6304070667", "5449078000", "4879376000"));
	}

	private void compareCountryData(CountryData c1, CountryData c2) {
		assertEquals(c1.getClassName(), c2.getClassName());
		assertEquals(c1.getName(), c2.getName());
		assertEquals(c1.getISO(), c2.getISO());
		assertEquals(Integer.parseInt(c1.getLandArea()),
				Integer.parseInt(c2.getLandArea()));
		assertEquals(Integer.parseInt(c1.getArableLandArea()),
				Integer.parseInt(c2.getArableLandArea()));
		assertEquals(Double.parseDouble(c1.getGDP()),
				Double.parseDouble(c2.getGDP()), 1E-5);
		assertEquals(Double.parseDouble(c1.getGDPRate()),
				Double.parseDouble(c2.getGDPRate()), 1E-5);
		assertEquals(Long.parseLong(c1.getEnergyOutput()),
				Long.parseLong(c2.getEnergyOutput()));
		assertEquals(Long.parseLong(c1.getCarbonOutput()),
				Long.parseLong(c2.getCarbonOutput()));
		assertEquals(Long.parseLong(c1.getCarbonOutput1990()),
				Long.parseLong(c2.getCarbonOutput1990()));
	}

	@Test
	public void testLoadCountries() {
		Simulation sim = new Simulation(Collections.<AbstractModule> emptySet());
		sim.COUNTRIES = "LVA,GBR,BHR,USA";
		Map<String, CountryData> countries = sim.getCountriesFromCSV();

		assertEquals(4, countries.size());
		assertTrue(countries.containsKey("LVA"));
		compareCountryData(sampleData.get("LVA"), countries.get("LVA"));
		assertTrue(countries.containsKey("GBR"));
		compareCountryData(sampleData.get("GBR"), countries.get("GBR"));
		assertTrue(countries.containsKey("BHR"));
		compareCountryData(sampleData.get("BHR"), countries.get("BHR"));
		assertTrue(countries.containsKey("USA"));
		compareCountryData(sampleData.get("USA"), countries.get("USA"));
	}
	
	@Test
	public void testIgnoreUnknownCountries() {
		Simulation sim = new Simulation(Collections.<AbstractModule> emptySet());
		sim.COUNTRIES = "LVA,ZZZ";
		Map<String, CountryData> countries = sim.getCountriesFromCSV();

		assertEquals(1, countries.size());
		assertTrue(countries.containsKey("LVA"));
		compareCountryData(sampleData.get("LVA"), countries.get("LVA"));
	}
	
	@Test
	public void testIgnoreDuplicateCountries() {
		Simulation sim = new Simulation(Collections.<AbstractModule> emptySet());
		sim.COUNTRIES = "LVA,GBR,GBR,LVA";
		Map<String, CountryData> countries = sim.getCountriesFromCSV();

		assertEquals(2, countries.size());
		assertTrue(countries.containsKey("LVA"));
		compareCountryData(sampleData.get("LVA"), countries.get("LVA"));
		assertTrue(countries.containsKey("GBR"));
		compareCountryData(sampleData.get("GBR"), countries.get("GBR"));
	}
	
	@Test
	public void testZeroCountries() {
		Simulation sim = new Simulation(Collections.<AbstractModule> emptySet());
		sim.COUNTRIES = "";
		Map<String, CountryData> countries = sim.getCountriesFromCSV();
		
		assertTrue(countries.isEmpty());
	}
	
	@Test
	public void testBadDataSource() {
		Simulation sim = new Simulation(Collections.<AbstractModule> emptySet());
		sim.COUNTRIES = "LVA,GBR,BHR,USA";
		sim.countryDataSource = "nonexistantfile.csv";
		try {
			sim.getCountriesFromCSV();
			fail();
		} catch(NullPointerException e) {}
	}
}
