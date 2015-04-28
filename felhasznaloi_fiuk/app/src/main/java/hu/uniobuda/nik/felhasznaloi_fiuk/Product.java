package hu.uniobuda.nik.felhasznaloi_fiuk;

/**
 * Created by Barnaby-Laptop on 2015.03.26..
 */
public class Product {
    ///Adattagok
    String price; //A termék ára
    String name; //A termék neve
    String db; //A termék mértékegysége
    String quantity; //A termékből rendelt mennyiség

    ///Elérési metódusok az adattagokhoz
    public String getPrice() {return price;}
    public void setPrice(String price) {this.price = price;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getDb() {return db;}
    public void setDb(String db) {this.db = db;}
    public String getQuantity() {return quantity;}
    public void setQuantity(String quantity) {this.quantity = quantity;}

    ///Konstruktor
    public Product() {
    }
}
