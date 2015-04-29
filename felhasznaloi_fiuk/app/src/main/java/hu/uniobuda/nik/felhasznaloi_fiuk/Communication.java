package hu.uniobuda.nik.felhasznaloi_fiuk;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Barnaby-Laptop on 2015.04.28..
 */

public class Communication {

    public static ArrayList<Product> products;
    public static ArrayList<Table> tables;
    Random rand = new Random();


    public void LoadTestTables(){

        int randTestTables = rand.nextInt(30);
        for (int i = 0; i <randTestTables; i++){
            Table temp = new Table();
            temp.setName(String.valueOf(i));

            if (i % 10 == 0) {
                temp.setState("fizet");
                LoadTestProductsForTables(temp);
            }
            else if (i % 4 == 0){
                temp.setState("foglalt");
                LoadTestProductsForTables(temp);
            }
            else{
                temp.setState("szabad");
            }

            AddTable(temp);
        }
    }

    public void LoadTestProducts(){
        if (products == null){
            products = new ArrayList<Product>();
        }
        int randTestProduct = rand.nextInt(30);
        for (int i = 0; i < randTestProduct; i++){
            Product temp = new Product();
            temp.setName(String.valueOf(i));
            temp.setPrice(String.valueOf(i / 3 * 100));
            temp.setDb(String.valueOf(i / 5));
            int r = rand.nextInt(30);

            if ( r % 2 == 0){
                temp.setQuantity("1");
            }
            else if (r % 4 == 0){
                temp.setQuantity("3");
            }
            else {
                temp.setQuantity("2");
            }
            products.add(temp);
        }
    }
    public void LoadTestProductsForTables(Table table){

        int randTestProductForTables = rand.nextInt(30);
        for (int i = 0; i < randTestProductForTables; i++){
            Product temp = new Product();
            temp.setName(String.valueOf(i));
            temp.setPrice(String.valueOf(i / 3 * 100));
            temp.setDb(String.valueOf(i / 5));
            int r = rand.nextInt(30);
            if ( r % 2 == 0){
                temp.setQuantity("1");
            }
            else if (r % 4 == 0){
                temp.setQuantity("3");
            }
            else {
                temp.setQuantity("2");
            }


            table.addProduct(temp);

        }
    }

    public void LoadMenu(ArrayList<Product> products){
        this.products = products;
    }
    public static ArrayList<Product> GetProducts(){
        return products;
    }
    public void AddTable(Table table){
        if (tables == null){
            tables = new ArrayList<Table>();
        }
        tables.add(table);
    }
    public static ArrayList<Table> GetTables(){
        return tables;
    }
}
