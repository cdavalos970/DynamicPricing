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
public class Option {
    
    private int sellingOption;
    private double price;
    private double revenueAcum;
    private double revenue;

    public Option(int sellingOption, double price, double revenueAcum, double revenue) {
        this.sellingOption = sellingOption;
        this.price = price;
        this.revenueAcum = revenueAcum;
        this.revenue = revenue;
    }

    public int getSellingOption() {
        return sellingOption;
    }

    public double getPrice() {
        return price;
    }

    public double getRevenueAcum() {
        return revenueAcum;
    }
    
    public double getRevenue() {
        return revenue;
    }

    public void setSellingOption(int sellingOption) {
        this.sellingOption = sellingOption;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRevenueAcum(double revenueAcum) {
        this.revenueAcum = revenueAcum;
    }   
    
    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }  
}
