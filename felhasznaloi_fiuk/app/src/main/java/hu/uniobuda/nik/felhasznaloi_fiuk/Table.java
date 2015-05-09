package hu.uniobuda.nik.felhasznaloi_fiuk;

import java.util.ArrayList;

/**
 * Created by Artur on 2015.04.28..
 */
public class Table { //Ez egy asztalt reprezentáló osztály
    ///Adattagok
    String name; //Az asztal neve
    String state; //Az asztal állapota (szabad, foglalt, fizetne)
    ArrayList<Product> products; //Az asztalnál rendelt termékek

    ///Elérési metódusok az adattagokhoz
    public Table() {products = new ArrayList<Product>();}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getState() {return state;}
    public void setState(String state) {this.state = state;}
    public ArrayList<Product> getProducts() {return products;}
    public void setProducts(ArrayList<Product> products) {this.products = products;}

    //Termék hozzáadása a rendeltekhez
    public void addProduct(Product product){products.add(product);}
    //Termékek nullázása
    public void resetProducts(){products = new ArrayList<Product>();}
}
