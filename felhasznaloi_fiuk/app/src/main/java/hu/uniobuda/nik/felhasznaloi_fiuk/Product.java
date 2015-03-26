package hu.uniobuda.nik.felhasznaloi_fiuk;

/**
 * Created by Barnaby-Laptop on 2015.03.26..
 */
public class Product {

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    int price;
    String name;

    public Product(int price, String name) {
        this.price = price;
        this.name = name;
    }
}
