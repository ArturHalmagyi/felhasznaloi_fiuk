package hu.uniobuda.nik.felhasznaloi_fiuk;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Barnaby-Laptop on 2015.04.28..
 */

public class Communication {

    ArrayList<Product> products;
    ArrayList<Table> tables;


    public void LoadTestTables(){
        for (int i = 0; i <10; i++){
            Table temp = new Table();
            temp.setName(String.valueOf(i));
            LoadTestProductsForTables(temp);
            AddTable(temp);
        }
    }

    public void LoadTestProducts(Table table){
        for (int i = 0; i < 20; i++){
            Product temp = new Product();
            temp.setName(String.valueOf(i));
            temp.setPrice(String.valueOf(i/3*100));
            temp.setDb(String.valueOf(i/5));
            products.add(temp);
        }
    }
    public void LoadTestProductsForTables(Table table){
        for (int i = 0; i < 20; i++){
            Product temp = new Product();
            temp.setName(String.valueOf(i));
            temp.setPrice(String.valueOf(i/3*100));
            temp.setDb(String.valueOf(i/5));
            table.addProduct(temp);

        }
    }

    public void LoadMenu(ArrayList<Product> products){
        this.products = products;
    }
    public ArrayList<Product> GetProducts(){
        return products;
    }
    public void AddTable(Table table){
        if (tables == null){
            tables = new ArrayList<Table>();
        }
        tables.add(table);
    }
    public ArrayList<Table> GetTables(){
        return tables;
    }
}
