package uk.ac.ic.kyoto.simulations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.ic.kyoto.CarbonData1990;
import uk.ac.ic.kyoto.actions.AddRemoveFromMonitorHandler;
import uk.ac.ic.kyoto.actions.AddToCarbonTargetHandler;
import uk.ac.ic.kyoto.actions.ApplyMonitorTaxHandler;
import uk.ac.ic.kyoto.actions.QueryEmissionsTargetHandler;
import uk.ac.ic.kyoto.actions.RejoinKyotoHandler;
import uk.ac.ic.kyoto.actions.SubmitCarbonEmissionReportHandler;
import uk.ac.ic.kyoto.annex1reduce.AnnexOneReduce;
import uk.ac.ic.kyoto.annex1sustain.AnnexOneSustain;
import uk.ac.ic.kyoto.countries.AbstractCountry;
import uk.ac.ic.kyoto.countries.CarbonTarget;
import uk.ac.ic.kyoto.countries.GameConst;
import uk.ac.ic.kyoto.countries.Monitor;
import uk.ac.ic.kyoto.exceptions.NoCountryDataException;
import uk.ac.ic.kyoto.nonannexone.NonAnnexOne;
import uk.ac.ic.kyoto.roguestates.USAgent;
import uk.ac.ic.kyoto.services.CarbonReportingService;
import uk.ac.ic.kyoto.services.Decoder;
import uk.ac.ic.kyoto.services.Economy;
import uk.ac.ic.kyoto.services.GlobalTimeService;
import uk.ac.ic.kyoto.services.ParticipantCarbonReportingService;
import uk.ac.ic.kyoto.services.ParticipantTimeService;
import uk.ac.ic.kyoto.services.TradeHistoryService;
import uk.ac.ic.kyoto.singletonfactory.SingletonProvider;
import uk.ac.ic.kyoto.util.sim.jsonobjects.simulations.CountryData;
import uk.ac.imperial.presage2.core.simulator.InjectedSimulation;
import uk.ac.imperial.presage2.core.simulator.Parameter;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.rules.RuleModule;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironmentModule;
import uk.ac.imperial.presage2.util.network.NetworkModule;

import com.google.inject.AbstractModule;

public class Simulation extends InjectedSimulation {
	
	final private Logger logger = Logger.getLogger(Simulation.class);

	@Parameter(name="GROWTH_MARKET_STATE")
	public double GROWTH_MARKET_STATE;
	@Parameter(name="STABLE_MARKET_STATE")
	public double STABLE_MARKET_STATE;
	@Parameter(name="RECESSION_MARKET_STATE")
	public double RECESSION_MARKET_STATE;
	@Parameter(name="GROWTH_MARKET_CHANCE")
	public double GROWTH_MARKET_CHANCE;
	@Parameter(name="STABLE_MARKET_CHANCE")
	public double STABLE_MARKET_CHANCE;
	@Parameter(name="RECESSION_MARKET_CHANCE")
	public double RECESSION_MARKET_CHANCE;
	@Parameter(name="CARBON_INVESTMENT_PRICE")
	public double CARBON_INVESTMENT_PRICE;
	@Parameter(name="GROWTH_SCALER")
	public double GROWTH_SCALER;
	@Parameter(name="PERCENTAGE_OF_GDP")
	public double PERCENTAGE_OF_GDP;
	@Parameter(name="CARBON_REDUCTION_PRICE_MIN")
	public double CARBON_REDUCTION_PRICE_MIN;
	@Parameter(name="CARBON_REDUCTION_PRICE_MAX")
	public double CARBON_REDUCTION_PRICE_MAX;
	@Parameter(name="CARBON_ABSORPTION_PRICE_MIN")
	public double CARBON_ABSORPTION_PRICE_MIN;
	@Parameter(name="CARBON_ABSORPTION_PRICE_MAX")
	public double CARBON_ABSORPTION_PRICE_MAX;
	@Parameter(name="FOREST_CARBON_ABSORPTION")
	public double FOREST_CARBON_ABSORPTION;
	@Parameter(name="MAX_GDP_GROWTH")
	public double MAX_GDP_GROWTH;
	@Parameter(name="MONITOR_COST_PERCENTAGE")
	public double MONITOR_COST_PERCENTAGE;
	@Parameter(name="SANCTION_RATE")
	public double SANCTION_RATE;
	@Parameter(name="MONITORING_PRICE")
	public double MONITORING_PRICE;
	@Parameter(name="YEARS_IN_SESSION")
	public int YEARS_IN_SESSION;
	@Parameter(name="TARGET_REDUCTION")
	public double TARGET_REDUCTION;
	@Parameter(name="MINIMUM_KYOTO_REJOIN_TIME")
	public int MINIMUM_KYOTO_REJOIN_TIME;
	@Parameter(name="MINIMUM_KYOTO_MEMBERSHIP_DURATION")
	public int MINIMUM_KYOTO_MEMBERSHIP_DURATION;
	@Parameter(name="TICK_YEAR")
	public int TICK_YEAR;
	@Parameter(name="COUNTRIES")
	public String COUNTRIES = "";

	String countryDataSource = "countrydata.csv";
		
	@Override
	protected Set<AbstractModule> getModules() {
		
		new GameConst(
				GROWTH_MARKET_STATE, 
				STABLE_MARKET_STATE, 
				RECESSION_MARKET_STATE, 
				GROWTH_MARKET_CHANCE, 
				STABLE_MARKET_CHANCE, 
				RECESSION_MARKET_CHANCE, 
				MAX_GDP_GROWTH,
				MONITOR_COST_PERCENTAGE, 
				SANCTION_RATE, 
				MONITORING_PRICE,
				YEARS_IN_SESSION, 
				TARGET_REDUCTION, 
				MINIMUM_KYOTO_REJOIN_TIME,
				MINIMUM_KYOTO_MEMBERSHIP_DURATION,
				TICK_YEAR,
				CARBON_INVESTMENT_PRICE,
				GROWTH_SCALER,
				PERCENTAGE_OF_GDP,
				CARBON_REDUCTION_PRICE_MIN,
				CARBON_REDUCTION_PRICE_MAX,
				CARBON_ABSORPTION_PRICE_MIN,
				CARBON_ABSORPTION_PRICE_MAX,
				FOREST_CARBON_ABSORPTION);

		Set<AbstractModule> modules = new HashSet<AbstractModule>();
		
		modules.add(new AbstractEnvironmentModule()
			.addActionHandler(SubmitCarbonEmissionReportHandler.class)
			.addActionHandler(AddToCarbonTargetHandler.class)
			.addActionHandler(QueryEmissionsTargetHandler.class)
			.addActionHandler(AddRemoveFromMonitorHandler.class)
			.addActionHandler(ApplyMonitorTaxHandler.class)
			.addActionHandler(RejoinKyotoHandler.class)
			.addGlobalEnvironmentService(CarbonReportingService.class)
			.addGlobalEnvironmentService(Monitor.class)
			.addParticipantEnvironmentService(ParticipantCarbonReportingService.class)
			.addGlobalEnvironmentService(GlobalTimeService.class)
			.addParticipantEnvironmentService(ParticipantTimeService.class)
			.addParticipantEnvironmentService(Economy.class)
			.addGlobalEnvironmentService(CarbonTarget.class)
			.addGlobalEnvironmentService(TradeHistoryService.class)
			);
	
		modules.add(new RuleModule());
			//.addClasspathDrlFile("foo.drl")
		
		modules.add(NetworkModule.fullyConnectedNetworkModule().withNodeDiscovery());
		
		return modules;
	}

	public Simulation(Set<AbstractModule> modules) {
		super(modules);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void addToScenario(Scenario s) {
		// TODO Auto-generated method stub
		
		try{
			Map<String, CountryData> countries = getCountriesFromCSV();
			
			if(countries.isEmpty()){
				//TODO uncomment for final code
				throw new NoCountryDataException(); //Commented out for now.
			} else {
				
				for(String countryKey : countries.keySet()){
					logger.info(countries.get(countryKey));
					String className = countries.get(countryKey).getClassName();
					CountryData countryData = countries.get(countryKey);
					AbstractCountry abstractCountry = null;
					if(className.equals("NonAnnexOne")){
						abstractCountry = new NonAnnexOne(
										Random.randomUUID(), 
										countryData.getName(),
										countryData.getISO(), 
										Double.parseDouble(countryData.getLandArea()), 
										Double.parseDouble(countryData.getArableLandArea()), 
										Double.parseDouble(countryData.getGDP()),
										Double.parseDouble(countryData.getGDPRate()), 
										Double.parseDouble(countryData.getEnergyOutput()), 
										Double.parseDouble(countryData.getCarbonOutput()));
					} else if(className.equals("AnnexOneReduce")){
						abstractCountry = new AnnexOneReduce(
										Random.randomUUID(), 
										countryData.getName(),
										countryData.getISO(), 
										Double.parseDouble(countryData.getLandArea()), 
										Double.parseDouble(countryData.getArableLandArea()), 
										Double.parseDouble(countryData.getGDP()),
										Double.parseDouble(countryData.getGDPRate()), 
										Double.parseDouble(countryData.getEnergyOutput()), 
										Double.parseDouble(countryData.getCarbonOutput()));
					} else if(className.equals("CanadaAgent")){
//						abstractCountry = new CanadaAgent(
//										Random.randomUUID(), 
//										countryData.getName(),
//										countryData.getISO(), 
//										Double.parseDouble(countryData.getLandArea()), 
//										Double.parseDouble(countryData.getArableLandArea()), 
//										Double.parseDouble(countryData.getGDP()),
//										Double.parseDouble(countryData.getGDPRate()),
//										0.00,//Double.parseDouble(countryData.getEmissionsTarget()), //EmissionsTarget not specified yet
//										Double.parseDouble(countryData.getEnergyOutput()), 
//										Double.parseDouble(countryData.getCarbonOutput()));					
					} else if(className.equals("AnnexOneSustain")){
						abstractCountry = new AnnexOneSustain(
										Random.randomUUID(), 
										countryData.getName(),
										countryData.getISO(), 
										Double.parseDouble(countryData.getLandArea()), 
										Double.parseDouble(countryData.getArableLandArea()), 
										Double.parseDouble(countryData.getGDP()),
										Double.parseDouble(countryData.getGDPRate()),
										Double.parseDouble(countryData.getEnergyOutput()), 
										Double.parseDouble(countryData.getCarbonOutput()));		
					} else if(className.equals("USAgent")){
						abstractCountry = new USAgent(
											Random.randomUUID(), 
											countryData.getName(),
											countryData.getISO(), 
											Double.parseDouble(countryData.getLandArea()), 
											Double.parseDouble(countryData.getArableLandArea()), 
											Double.parseDouble(countryData.getGDP()),
											Double.parseDouble(countryData.getGDPRate()),
											Double.parseDouble(countryData.getEnergyOutput()), 
											Double.parseDouble(countryData.getCarbonOutput()));	
					}
					
					CarbonData1990.addCountry(countries.get(countryKey).getISO(), Double.parseDouble(countries.get(countryKey).getCarbonOutput1990()));
					
					if(abstractCountry != null){
						//TODO uncomment for final code
						Decoder.addCountry(abstractCountry.getID(), abstractCountry.getName(), abstractCountry.getISO());
						s.addParticipant(abstractCountry);
					}
				}
			}		
		
		} catch(NoCountryDataException e){
			logger.warn(e);
		}
		
		SingletonProvider.getTradeHistory().setSimID(this.simPersist.getID());
	

	}

	Map<String, CountryData> getCountriesFromCSV()
			throws NoCountryDataException {
		Map<String, CountryData> countriesData = new HashMap<String, CountryData>();
		// get set of ISO codes for countries to include in this simulation.
		Set<String> included = new HashSet<String>();
		included.addAll(Arrays.asList(COUNTRIES.split(",")));

		InputStream is = null;
		InputStreamReader isReader = null;
		BufferedReader countryCsv = null;
		try {
			// attempt to load csv county data
			is = this.getClass().getClassLoader()
					.getResourceAsStream(this.countryDataSource);
			isReader = new InputStreamReader(is);
			countryCsv = new BufferedReader(isReader);

			// discard first line, just headings
			countryCsv.readLine();

			String line = null;

			do {
				line = countryCsv.readLine();
				if (line != null) {
					String[] values = line.split(",");
					if (values.length != 10) {
						logger.warn("Missing/malformed line in countrydata.csv: '"
								+ line + "'");
						continue;
					}
					// check ISO code to see if we should load this CountryData
					if (!included.contains(values[2]))
						continue;
					CountryData c = new CountryData();
					c.setClassName(values[0]);
					c.setName(values[1]);
					c.setAgentName(values[1]);
					c.setISO(values[2]);
					c.setLandArea(values[3]);
					c.setArableLandArea(values[4]);
					c.setGDP(values[5]);
					c.setGDPRate(values[6]);
					c.setEnergyOutput(values[7]);
					c.setCarbonOutput(values[8]);
					c.setCarbonOutput1990(values[9]);
					countriesData.put(c.getISO(), c);
				}
			} while (line != null);

		} catch (IOException e) {
			throw new NoCountryDataException();
		} finally {
			if (countryCsv != null)
				try {
					countryCsv.close();
				} catch (IOException e) {
				}
			if (isReader != null)
				try {
					isReader.close();
				} catch (IOException e) {
				}
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
				}
		}
		return countriesData;
	}
}
