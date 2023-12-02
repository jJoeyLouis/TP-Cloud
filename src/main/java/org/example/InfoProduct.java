package org.example;

/* This class allows to give a struct about my data for products
* 1 product, there is :
* - the name : which is the key in products <HashMap>
* - and (store in InfoProduct (class)):
*   - quantity
*   - price
*   - profit
*/
public class InfoProduct {
    private int quantity ;
    private double price ;
    private double profitTot;
    public InfoProduct(int quantity, double priceTot, double UnitProfit){
        this.quantity = quantity ;
        this.price = Math.round(priceTot * 100.0) / 100.0 ;
        this.profitTot = Math.round(UnitProfit*this.quantity  * 100.0) / 100.0 ;
    }

    public void add(int quantity, double priceTot, double UnitProfit){
        this.quantity += quantity ;
        this.price += Math.round(priceTot * 100.0) / 100.0 ;
        this.profitTot = Math.round(UnitProfit*this.quantity  * 100.0) / 100.0 ;
    }

    public int getQuantity() {return quantity;}
    public double getPrice() {return price;}
    public double getProfitTot() {return profitTot;}

}
