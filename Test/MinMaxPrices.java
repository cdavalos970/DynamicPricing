/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicpricing;

/**
 *
 * @author cdavalos
 */
public class MinMaxPrices {
    private final String originDestination;
    private final String departureTimeWindow;
    private final int advancePurchase;
    private final double minPrice;
    private final double maxPrice;
    
    public MinMaxPrices(String varOriginDestination, String varDepartureTimeWindow, String varAdvancePurchase ,String varMinPrice, String varMaxPrice){
        originDestination = varOriginDestination;
        departureTimeWindow = varDepartureTimeWindow;
        advancePurchase = Integer.parseInt(varAdvancePurchase);
        minPrice = Double.parseDouble(varMinPrice);
        maxPrice = Double.parseDouble(varMaxPrice);
    }

    public double getMinPrice() {     
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public String getOriginDestination() {
        return originDestination;
    }

    public String getDepartureTimeWindow() {
        return departureTimeWindow;
    }

    public int getAdvancePurchase() {
        return advancePurchase;
    }

}
