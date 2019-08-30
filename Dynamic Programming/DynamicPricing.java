/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicpricing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author cdavalos
 */

public class DynamicPricing {
    // 
    private ArrayList<MinMaxPrices> minMaxDataFrame;
    private ArrayList<Flight> flightList;
    
    public static final int LASTADVANCEPURCHASE = 28;
    private static final char DEFAULT_SEPARATOR = ',';
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    
    
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        // Se inicializa la clase principal Dynamic Pricing
            DynamicPricing dynamicPricing = new DynamicPricing();
            dynamicPricing.run(); 

        
    }
    
    public void run () throws Exception {
        // Para cada una de las entidades de tipo Flight, se crea una lista con los betas especificos para el vuelo
        // Para cada una de las entidades de tipo Flight, se crea una lista con los precios Minimos y Maximos de los Advanced Purchase 
        // Se crea una entidad Dynamic Programming con el vuelo a optimzar, dados unos coeficientes y los precios minimos y maximos por Advanced Purchase
        // Despues de obtener los resultados del Dynamic Programming se escriben los resultados
        flightList = readFlights();
        for(Flight test:flightList) {
            Beta[] betaDataFrame = readBetaFile(test);
            minMaxDataFrame = readMinMaxPriceFile(test);
            
            test.setBetaIntercept(getBetaIntercept(betaDataFrame, test));
            test.setBetaPrice(getBetaPrice(betaDataFrame, test));
            test.setBetaDepartureDayWeek(getBetaDepartureDayWeek(betaDataFrame, test));
            test.setBetaAdvancePurchase(getBetaAdvancePurchase(betaDataFrame, test));
            test.setBetaDepartureTimeWindow(getBetaDepartureTimeWindow(betaDataFrame, test));
            test.setBetaSalesDayWeek(getBetaSalesDayWeek(betaDataFrame, test));
        
            DynamicProgramming testDP = new DynamicProgramming(LASTADVANCEPURCHASE, test, minMaxDataFrame);
            writeResults(testDP, test);  
        }
        
    }
    private ArrayList<Flight> readFlights() throws ParseException, IOException {
        // Lee el archivo que tiene los vuelos a los que se les va a realizar la optimizacion,
        // Archivo separado por punto y coma
        // Header: OriginDestination, DepartureDate, TimeDeparture, Authorized Seats
        // Se crea una entidad de tipo Flight por cada vuelo en el arhivo, con los siguientes campos
            //OriginDestination, DepartureDate, TimeDeparture, Authorized Seats
        ArrayList<Flight> flights;
        String cvsSplitBy = ";";
        String line;
        String csvBetaFile = "G:/Dynamic Pricing/Test2/Output/Unique.csv";
    
        BufferedReader inputFileFlight = new BufferedReader(new FileReader(csvBetaFile));
        flights = new ArrayList<>();
        int j = 0;
        line = inputFileFlight.readLine();
        while((line = inputFileFlight.readLine()) != null) {
                String[] register = line.split(cvsSplitBy);
                
                Format formatt = new SimpleDateFormat("dd/MM/yyyy");
                
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateInString = register[3];
                Date date = formatter.parse(dateInString);
                String fechaVuelo = formatt.format(date);
                
                Flight addFlight = new Flight(register[0], fechaVuelo, register[1], Integer.parseInt(register[4]));
                flights.add(addFlight);
                j++;
        }
        return(flights);
    }
                
    private Beta[] readBetaFile(Flight vuelo) throws FileNotFoundException, IOException {
        //Existe un beta de acuerdo a cada OriginDestination 
        //Existe un beta para cada AdvancedPurchase, FlightDayWeek, FlightMonth y TimeDeparture
        //Se crea una entidad de clase Beta con el OriginDestination, Coeficiente y Nombre del Beta
        String varOriginDestination = vuelo.getOriginDestination();
        
        String cvsSplitBy = ";";
        String line;
        String csvBetaFile = "G:/Dynamic Pricing/Test2/Output/Coef";
        
        Beta[] betaArray;
        try (BufferedReader inputFileBeta = new BufferedReader(new FileReader(csvBetaFile + varOriginDestination + ".csv"))) {
            betaArray = new Beta[70];
            int j = 0;
            line = inputFileBeta.readLine();
            while((line = inputFileBeta.readLine()) != null) {
                String[] betaLeg = line.split(cvsSplitBy);
                betaArray[j] = new Beta(betaLeg[0], betaLeg[1], betaLeg[2]);
                j++;
            }
        }
        return(betaArray);
    } 
    
    private ArrayList<MinMaxPrices> readMinMaxPriceFile(Flight vuelo) throws FileNotFoundException, IOException {
        //Para cada entidad de Flight, de acuerdo a su OriginDestination, se obtienen los valores Minimos y Maximos
        String varOriginDestination = vuelo.getOriginDestination();
        
        String cvsSplitBy = ";";
        String line;
        String csvBetaFile = "G:/Dynamic Pricing/Test2/Output/MinMax_";
        
        ArrayList<MinMaxPrices> minMaxArray;
        try (BufferedReader inputFileBeta = new BufferedReader(new FileReader(csvBetaFile + varOriginDestination + ".csv"))) {
            minMaxArray = new ArrayList<>();
            int j = 0;
            line = inputFileBeta.readLine();
            while((line = inputFileBeta.readLine()) != null) {
                String[] register = line.split(cvsSplitBy);
                MinMaxPrices addMinMax = new MinMaxPrices(register[0], register[1], register[2], register[3], register[4]);
                minMaxArray.add(addMinMax);
                j++;
            }
        }
        return(minMaxArray);
    }
    
    private double getBetaIntercept (Beta[] dataFrame, Flight vuelo) {
        String varOriginDestination = vuelo.getOriginDestination();
        int iterBeta = 0;
        for(int i = 0; i < dataFrame.length; i++ ) {
            Beta betaRegister = dataFrame[i];
            if(betaRegister.getOriginDestination().equals(varOriginDestination) & betaRegister.getBetaName().equals("(Intercept)") ) {
                iterBeta = i;
                break;
            }
        }
        double valIntercept = dataFrame[iterBeta].getCoefficient();
        return(valIntercept);
    }
    
    private double getBetaPrice (Beta[] dataFrame, Flight vuelo) {
        String varOriginDestination = vuelo.getOriginDestination();
        int iterBeta = 0;
        for(int i = 0; i < dataFrame.length; i++ ) {
            Beta betaRegister = dataFrame[i];
            if(betaRegister.getOriginDestination().equals(varOriginDestination) & betaRegister.getBetaName().equals("AveragePrice") ) {
                iterBeta = i;
                break;
            }
        }
        double valIntercept = dataFrame[iterBeta].getCoefficient();
        return(valIntercept);
    }
    
    private double getBetaDepartureTimeWindow (Beta[] dataFrame, Flight vuelo) {
        String varOriginDestination = vuelo.getOriginDestination();
        String varDepartureTimeWindow = vuelo.getDepartureTimeWindow();
        int iterBeta;
        double value = 0;
      
        for(int i = 0; i < dataFrame.length; i++ ) {
            Beta betaRegister = dataFrame[i];
            if(betaRegister == null)
            {
                break;
            }
            else if(betaRegister.getOriginDestination().equals(varOriginDestination) & betaRegister.getBetaName().equals("DepartureTimeWindow" + varDepartureTimeWindow) ) {
                iterBeta = i;
                value = dataFrame[iterBeta].getCoefficient();
                break;
            }
        }
        return(value);
    }
    
    private double getBetaDepartureDayWeek (Beta[] dataFrame, Flight vuelo) {
        String varOriginDestination = vuelo.getOriginDestination();
        String varDepartureDayWeek = vuelo.getDepartureDayWeek();
        int iterBeta = 0;
        double valDepartureDayWeek = 0;
        for(int i = 60; i < dataFrame.length; i++ ) {
            Beta betaRegister = dataFrame[i];
            if(betaRegister == null)
            {
                break;
            }
            else if(betaRegister.getOriginDestination().equals(varOriginDestination) & betaRegister.getBetaName().equals("FlightDayOfWeek" + varDepartureDayWeek)) {
                iterBeta = i;
                valDepartureDayWeek = dataFrame[iterBeta].getCoefficient();
                break;
            }
        }
        
        return(valDepartureDayWeek);
    }
    
    private double[] getBetaAdvancePurchase (Beta[] dataFrame, Flight vuelo) {
        String varOriginDestination = vuelo.getOriginDestination();
        double[] iterBeta = new double[30];
        int advancePurchase = 1;
        for (Beta betaRegister : dataFrame) {
            if (betaRegister.getOriginDestination().equals(varOriginDestination) & betaRegister.getBetaName().equals("AdvancePurchase" + advancePurchase)) {
                iterBeta[advancePurchase] = betaRegister.getCoefficient();
                advancePurchase++;
                if(advancePurchase == LASTADVANCEPURCHASE)
                {
                    break;
                }
            }
        }
       return(iterBeta); 
    }
    
    private String[][] getBetaSalesDayWeek (Beta[] dataFrame, Flight vuelo) {
        String varOriginDestination = vuelo.getOriginDestination();
        String[][] iterBeta = new String[6][2];
        int advancePurchase = 0;
        for (Beta betaRegister : dataFrame) {
            if(betaRegister == null)
            {
                break;
            }
            else {
               String betaName = betaRegister.getBetaName();
                if (betaRegister.getOriginDestination().equals(varOriginDestination) & betaName.startsWith("Sales")) {
                    String dayWeek = betaName.substring(14);
                    iterBeta[advancePurchase][0] = dayWeek;
                    iterBeta[advancePurchase][1] =  Double.toString(betaRegister.getCoefficient());
                    advancePurchase++;
                } 
            }
        }
       return(iterBeta); 
    }
    
    private void writeResults(DynamicProgramming result, Flight test) throws FileNotFoundException {
        String departureTime = test.getDepartureTimeWindow();
        departureTime = departureTime.replace(":", "");
        String csvBetaFile = test.getOriginDestination() + "_" + departureTime + "_" + test.getDepartureDayWeek() + ".csv";
        try (PrintWriter print = new PrintWriter(new File(csvBetaFile))) {
            ArrayList<Stage> resultList = result.getStageList();
            
            for(Stage iter: resultList) {
                int advancePurchase = iter.getAdvancePurchase();
                ArrayList<State> listStage =  iter.getStateList();
                for(State iterState: listStage) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(advancePurchase);
                    sb.append(";");
                    sb.append(iterState.getAvailableSeats());
                    sb.append(";");
                    sb.append(iterState.getSellingOption());
                    sb.append(";");
                    sb.append(Math.round(iterState.getPrice()));
                    sb.append(";");
                    sb.append(Math.round(iterState.getRevenueAcum()));
                    sb.append(";");
                    sb.append(Math.round(iterState.getRevenue()));
                    sb.append(";");
                    sb.append(test.getOriginDestination());
                    sb.append(";");
                    sb.append(departureTime);
                    sb.append(";");
                    sb.append(test.getDepartureDayWeek());
                    sb.append('\n');
                    print.write(sb.toString());
                }
            }   }
}
    
}
