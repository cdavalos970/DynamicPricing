/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicpricing;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author cdavalos
 */
public class Flight {

    private final String originDestination;
    private final Date departureDate;
    private final String departureDayWeek;
    private final String departureTimeWindow;
    private final int seats;
    private double betaIntercept;
    private double betaPrice;
    private double betaDepartureDayWeek;
    private double betaDepartureTimeWindow;
    private double[] betaAdvancePurchase;
    private String[][] betaSalesDayWeek;
    
    public Flight(String originDestination, String departureDate, String departureTimeWindow, int seats) throws ParseException{
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat formatDayWeek = new SimpleDateFormat("EEEE"); 
        
        Date departureDateFormat = formatDate.parse(departureDate);
        this.originDestination = originDestination;
        this.departureDate = departureDateFormat;
        this.departureDayWeek = formatDayWeek.format(departureDateFormat);
        this.departureTimeWindow = departureTimeWindow;
        this.seats = seats;
        betaIntercept = 0;
        betaPrice = 0;
        betaDepartureDayWeek = 0;
        betaDepartureTimeWindow = 0;
        betaAdvancePurchase = null;
        betaSalesDayWeek = null;
    }

    public String getOriginDestination() {
        return originDestination;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public String getDepartureDayWeek() {
        return departureDayWeek;
    }

    public String getDepartureTimeWindow() {
        return departureTimeWindow;
    }

    public int getSeats() {
        return seats;
    }

    public double getBetaIntercept() {
        return betaIntercept;
    }

    public double getBetaPrice() {
        return betaPrice;
    }

    public double getBetaDepartureDayWeek() {
        return betaDepartureDayWeek;
    }

    public double getBetaDepartureTimeWindow() {
        return betaDepartureTimeWindow;
    }

    public double getBetaByAdvancePurchase(int advancePurchase) {
        return  betaAdvancePurchase[advancePurchase];
    }

    public double getBetaBySalesDayWeek(String SalesDayWeek) {
        double valueBetaSalesDayWeek = 0;
        
        for (String[] betaSalesDayWeek1 : betaSalesDayWeek) {
            if (betaSalesDayWeek1[0].equals(SalesDayWeek)) {
                valueBetaSalesDayWeek = Double.parseDouble(betaSalesDayWeek1[1]);
                break;
            }
        } 
        return valueBetaSalesDayWeek;
    }

    public void setBetaIntercept(double betaIntercept) {
        this.betaIntercept = betaIntercept;
    }

    public void setBetaPrice(double betaPrice) {
        this.betaPrice = betaPrice;
    }

    public void setBetaDepartureDayWeek(double betaDepartureDayWeek) {
        this.betaDepartureDayWeek = betaDepartureDayWeek;
    }

    public void setBetaDepartureTimeWindow(double betaDepartureTimeWindow) {
        this.betaDepartureTimeWindow = betaDepartureTimeWindow;
    }

    public void setBetaAdvancePurchase(double[] betaAdvancePurchase) {
        this.betaAdvancePurchase = betaAdvancePurchase;
    }

    public void setBetaSalesDayWeek(String[][] betaSalesDayWeek) {
        this.betaSalesDayWeek = betaSalesDayWeek;
    }

    
    
    
}
