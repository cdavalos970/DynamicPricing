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
public class Beta {
    private final String originDestination;
    private final double coefficient;
    private final String betaName;
    
    public Beta(String varLocalOriginDestination, String varCoefficient, String varBetaName ){
        originDestination = varLocalOriginDestination;
        coefficient = Double.parseDouble(varCoefficient);
        betaName = varBetaName;
    }

    public String getOriginDestination() {
        return originDestination;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public String getBetaName() {
        return betaName;
    }
}
