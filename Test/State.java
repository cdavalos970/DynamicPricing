/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicpricing;

import java.util.ArrayList;

/**
 *
 * @author cdavalos
 */
public class State {
    
    private int availableSeats;
    private double price;
    private double sellingOption;
    private double revenueAcum;
    private double revenue;
    private double lastMinPrice;

    public State(int availableSeats, Stage previousStage, double betaTotal, double betaPrice, double minPrice, double maxPrice) {
        Option optimalOption;
        if(previousStage == null) {
            optimalOption = generateOption(availableSeats, betaTotal, betaPrice, minPrice, maxPrice, previousStage);
        } else {
            optimalOption =  calculateOption(availableSeats, betaTotal, betaPrice, minPrice, maxPrice, previousStage);
        }
        
        this.availableSeats = availableSeats;
        this.price = optimalOption.getPrice();
        this.sellingOption = optimalOption.getSellingOption();
        this.revenueAcum = optimalOption.getRevenueAcum();
        this.revenue = optimalOption.getRevenue();
        
        if(previousStage == null) {
            this.lastMinPrice = optimalOption.getPrice();
        } else {
            if(optimalOption.getPrice() == Integer.MAX_VALUE) {
                this.lastMinPrice = previousStage.getState(availableSeats - optimalOption.getSellingOption()).getLastMinPrice();
            } else {
               this.lastMinPrice = optimalOption.getPrice(); 
            }
        }
    }

    private Option generateOption(int availableSeats, double betaTotal, double betaPrice, double minPrice, double maxPrice, Stage previousStage) {
        Option option = null;
        
        double optimalPrice = -(1/betaPrice);
        
        double PriceMin = (Math.log(availableSeats + 0.499)-(betaTotal))/(betaPrice);
        double PriceMax = (Math.log(availableSeats - 0.500)-(betaTotal))/(betaPrice);
        
        if(PriceMin < minPrice) {
            PriceMin = minPrice;
        } else if (PriceMin > maxPrice) {
            PriceMin = maxPrice;
        }

        if(PriceMax < minPrice) {
            PriceMax = minPrice;
        } else if (PriceMax > maxPrice) {
            PriceMax = minPrice;
        }
        
        if (optimalPrice > PriceMax) {
            optimalPrice =  PriceMax;
        }
        
        if(optimalPrice < PriceMin) {
            optimalPrice =  PriceMin;
        }

        int demand = (int) Math.round(Math.exp(betaTotal + betaPrice*optimalPrice));
        double totalRevenue = Math.exp(betaTotal + betaPrice*optimalPrice)*optimalPrice;
        
        if(availableSeats != 0) {
            option = new Option(demand , optimalPrice, totalRevenue, totalRevenue);
        } else {
            option = new Option(availableSeats , Integer.MAX_VALUE, 0, 0);
        }
  
        return(option);
    }
    
    private Option calculateOption(int availableSeats, double betaTotal, double betaPrice, double minPrice, double maxPrice, Stage previousStage) {
        ArrayList<Option> list = new ArrayList<>();
        double optimalPrice = -(1/betaPrice);
        Option option = null;
            
        for(int i = 0; i <= availableSeats; i++) {

            double PriceMin = (Math.log(i + 0.499)-(betaTotal))/(betaPrice);
            double PriceMax = (Math.log(i-0.5)-(betaTotal))/(betaPrice);
                      
            if(PriceMax > maxPrice & PriceMin > maxPrice) {
                optimalPrice = Integer.MAX_VALUE;
            } else if (PriceMax < minPrice & PriceMin < minPrice) {
                optimalPrice = minPrice;
            } else if(PriceMax <= maxPrice & PriceMin < minPrice) {
                PriceMin = minPrice; 
                if(Math.exp(betaTotal + betaPrice*PriceMin)*PriceMin > Math.exp(betaTotal + betaPrice*PriceMax)*PriceMax){
                    optimalPrice = PriceMin;
                } else {
                    optimalPrice = PriceMax;
                }  
            } else if(PriceMax > maxPrice & PriceMin >= minPrice) {
               PriceMax = maxPrice;
               if(Math.exp(betaTotal + betaPrice*PriceMin)*PriceMin > Math.exp(betaTotal + betaPrice*PriceMax)*PriceMax){
                    optimalPrice = PriceMin;
                } else {
                    optimalPrice = PriceMax;
                }  
            } else {
                if(Math.exp(betaTotal + betaPrice*PriceMin)*PriceMin > Math.exp(betaTotal + betaPrice*PriceMax)*PriceMax){
                    optimalPrice = PriceMin;
                } else {
                    optimalPrice = PriceMax;
                }  
            }
            
            int demand = (int) Math.round(Math.exp(betaTotal + betaPrice*optimalPrice));
            int remainingSeats = availableSeats - demand;
            double revenue = Math.exp(betaTotal + betaPrice*optimalPrice)*optimalPrice;
            double totalRevenue = revenue + previousStage.getState(remainingSeats).getRevenueAcum();
            
            if(availableSeats == 0) {
                option = new Option(i , Integer.MAX_VALUE, 0, 0);
            } else if(i == 0 ) {
                option = new Option(i , Integer.MAX_VALUE, previousStage.getState(availableSeats).getRevenueAcum(), 0);
            } else if(demand > i) {
                option = new Option(i , Integer.MAX_VALUE, previousStage.getState(i).getRevenueAcum(), 0);
            } else {
                double previousPrice =  previousStage.getState(remainingSeats).getPrice();
                if(optimalPrice <= previousPrice & optimalPrice <=  previousStage.getState(remainingSeats).getLastMinPrice()) {
                   option = new Option(demand , optimalPrice, totalRevenue, revenue); 
                } else {
                   option = new Option(i , Integer.MAX_VALUE, previousStage.getState(i).getRevenueAcum(), 0); 
                }
            }
            list.add(option);
        }
        
        double maxRevenue = 0;
        int maxOption = 0;
        for(Option iterOption: list) {
            if(iterOption.getRevenueAcum() > maxRevenue) {
                maxRevenue = iterOption.getRevenueAcum();
                maxOption = iterOption.getSellingOption();
            }
        }
        return(list.get(maxOption));
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public double getPrice() {
        return price;
    }

    public double getSellingOption() {
        return sellingOption;
    }

    public double getRevenueAcum() {
        return revenueAcum;
    }
    
    public double getRevenue() {
        return revenue;
    }
    
    public double getLastMinPrice() {
        return lastMinPrice;
    }
    
    
}
