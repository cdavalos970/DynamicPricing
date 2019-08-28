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
public class Stage {
    
    private int advancePurchase;
    double minPrice;
    double maxPrice;
    double betaTotal;
    double betaPrice;
    private Stage previousStage;
    private ArrayList<State> stateList;

    public Stage(int advancePurchase, double minPrice, double maxPrice, double betaTotal, double betaPrice, int seats, Stage previousStage) {        
        
        stateList = setStates(minPrice, maxPrice, betaTotal, betaPrice, seats, previousStage);
        this.advancePurchase = advancePurchase;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.betaTotal = betaTotal;
        this.betaPrice = betaPrice;
        this.previousStage = previousStage;
        
    }  
    
    private  ArrayList<State> setStates(double minPrice, double maxPrice, double betaTotal, double betaPrice, int seats, Stage previousStage) {
        //
        ArrayList<State> newStates = new ArrayList<>();
        for(int i = 0; i <= seats; i++ ) {
           State newState = new State(i, previousStage, betaTotal, betaPrice, minPrice, maxPrice);
           newStates.add(newState);
        }
        return(newStates);
    }

    public int getAdvancePurchase() {
        return advancePurchase;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public double getBetaTotal() {
        return betaTotal;
    }

    public double getBetaPrice() {
        return betaPrice;
    }

    public Stage getPreviousStage() {
        return previousStage;
    }

    public ArrayList<State> getStateList() {
        return stateList;
    }
    
    public State getState(int stateNumber) {
        return stateList.get(stateNumber);
    }

    public void setPreviousStage(Stage previousStage) {
        this.previousStage = previousStage;
    }
    
    
}
