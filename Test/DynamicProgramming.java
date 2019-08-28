/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicpricing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author cdavalos
 */
public class DynamicProgramming {
    
    private Flight flight;
    private ArrayList<Stage> stageList;    
    
    public DynamicProgramming(int advancePurchaseDays, Flight flight, ArrayList<MinMaxPrices> minMaxData) {
        stageList = setStages(advancePurchaseDays, flight, minMaxData);
        this.flight = flight;
    }
    
    private ArrayList<Stage> setStages(int advancePurchaseDays, Flight flight, ArrayList<MinMaxPrices> minMaxData) {
        //Crea las entidades de clase Stage que representan el Advanced Purchase 
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        
        ArrayList<Stage> newStages = new ArrayList<>();
        double minPrice = 0;
        double maxPrice = 0;
        double betaAdvancePurchase = 0;
        double betaSalesDayWeek = 0;
        int seats = 0;
        
        double betaTotal = 0;
        double betaPrice = 0;
        
        for(int i = 0; i <= advancePurchaseDays; i++ ) {
            for(MinMaxPrices iterMinMax: minMaxData) {
                if(iterMinMax.getOriginDestination().equals(flight.getOriginDestination()) & iterMinMax.getDepartureTimeWindow().equals(flight.getDepartureTimeWindow()) & i == iterMinMax.getAdvancePurchase()) {
                   minPrice = iterMinMax.getMinPrice();
                   maxPrice = iterMinMax.getMaxPrice();
                   break;
                }
            }
            
            betaAdvancePurchase = flight.getBetaByAdvancePurchase(i);
            
            calendar.setTime(flight.getDepartureDate());
            calendar.add(Calendar.DATE, -i);
            String salesDayWeek = simpleDateformat.format(calendar.getTime());
            betaSalesDayWeek = flight.getBetaBySalesDayWeek(salesDayWeek);
            
            betaTotal = flight.getBetaIntercept() + flight.getBetaDepartureDayWeek() + flight.getBetaDepartureTimeWindow() + betaAdvancePurchase + betaSalesDayWeek;
            betaPrice = flight.getBetaPrice();
            
            seats = flight.getSeats();
            
           Stage newStage;
            if(i != 0) {
                 newStage = new Stage(i, minPrice, maxPrice, betaTotal, betaPrice, seats, newStages.get(i-1));
            } else {
                Stage previousStage = null;
                 newStage = new Stage(i, minPrice, maxPrice, betaTotal, betaPrice, seats, previousStage);
            }
            newStages.add(newStage);
        }
        return(newStages);
    } 

    public Flight getFlight() {
        return flight;
    }

    public ArrayList<Stage> getStageList() {
        return stageList;
    }
    
    
}