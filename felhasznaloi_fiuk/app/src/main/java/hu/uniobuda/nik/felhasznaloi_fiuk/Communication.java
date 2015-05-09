package hu.uniobuda.nik.felhasznaloi_fiuk;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Barnaby-Laptop on 2015.04.28..
 */

public class Communication {

    public static Context context;

    public static ArrayList<Product> products;
    public static ArrayList<Table> tables;
    public static String ip = "http://192.168.43.183";

    public static ArrayList<Table> getTables() {
        return tables;
    }

    public static Random rand = new Random();
    static Boolean testMode;
    private static ComTask ServerCom;

    public static ComTask getServerCom() {
        return ServerCom;
    }

    public Communication(Boolean test, Context context) {
        testMode = test;
        this.context = context;

/*
        if (testMode == true){

        }
        else{

        }
        */
    }


    //teszt mód beállítás
    public static void SetTestModeOn(){
        testMode = true;
    }
    public static void SetTestModeOff(){
        testMode = false;
    }


    //kommunikáció a szerverrel
    public static void GetProducts(){
        if (testMode){
            LoadTestProducts();
        }
        else {
            GetProductsFromServer();
        }
    } //menü lekérdezése
    public static ArrayList<Product> GetTable(String tableNumber){
        ArrayList<Product> temp = new ArrayList<Product>();
        GetTables(); //ezután ki kell választani azt amelyik kell nekünk
        /////
        return temp;
    }   //egy asztal állapotának lekérdezése
    public static void GetTables(){
        if (testMode){
            LoadTestTables();
        }
        else{
            GetTablesFromServer();
        }
    }
    public static void SendOrder(Table table){
        if (testMode){
            Toast.makeText(context,"Rendelés elküldve", Toast.LENGTH_LONG); //TODO string ami meg a szervernek
        }
        else{
            SendOrderToServer(table);
        }
    }
    public static void Authentication(String user, String pass){
        if (testMode){

        }
        else {
            AuthenticationOnServer(user,pass);
        }
    }
    public static void SendPayRequest(String tableNumber){
        if (testMode){
            Toast.makeText(context,"Fizetés történik", Toast.LENGTH_LONG);
        }
        else{
            SendPayRequestToServer(tableNumber);
        }
    }




    //Tesztadatok
    public static void LoadTestTables(){

        int randTestTables = rand.nextInt(30)+10;
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
    public static void LoadTestProducts(){
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
    public static void LoadTestProductsForTables(Table table){

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

    //Teszt segédmetódusok
    public void LoadMenu(ArrayList<Product> products){
        this.products = products;
    }
    public static void AddTable(Table table){
        if (tables == null){
            tables = new ArrayList<Table>();
        }
        tables.add(table);
    }



    //Szerver segédmetódusok
    /// output: name1;quantity1|name2;quantity2...
    private static String MyProductStrBuilder(ArrayList<Product> products){
        StringBuilder sb = new StringBuilder();
        Integer db = products.size();
        for (int i=0;i<db;i++){
            sb.append(products.get(i).getName());
            sb.append(";");
            sb.append(products.get(i).getQuantity());
            sb.append("|");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
    private static ArrayList<Product> StrToProduct(String str){
        String[] ProStrs= str.split("`");
        ArrayList<Product> products=new ArrayList<>(ProStrs.length);
        Integer db = ProStrs.length;
        for (int i=0;i<db;i++){
            Integer elso = ProStrs[i].indexOf(";");
            Integer masodik = ProStrs[i].indexOf(";",elso+1);
            String newName=ProStrs[i].substring(0,elso);
            String newPrice=ProStrs[i].substring(elso+1,masodik);
            String newUnit=ProStrs[i].substring(masodik+1);
            products.add(new Product(newPrice,newName,newUnit));
        }
        return products;
    }
    //Szerveradatok
    public static void GetProductsFromServer(){
        //return products;
        ServerCom = new ComTask(ip, context);
        ServerCom.setOnConnectionListener(new ComTask.onConnectionListener() {
            @Override
            public void onDownloadSuccess(String response) {
                Log.d("DOWNLOAD_SUCCESS:","getMenu:"+response);
                products = StrToProduct(response);
            }

            @Override
            public void onDownloadFail(String errorMessage) {
                Log.d("DOWNLOAD_FAIL:",errorMessage);
            }
        });
        ServerCom.execute("0");

    } //menü lekérdezése
    public static void GetTablesFromServer(){
        ServerCom.setOnConnectionListener(new ComTask.onConnectionListener() {
            @Override
            public void onDownloadSuccess(String response) {
                Log.d("DOWNLOAD_SUCCESS:","getTablesResult:"+response);
                // TODO CREATE TABLE ARRAY FROM response
                //TODO kristók communication.tables = valami, uganug mint a meal-nél
            }

            @Override
            public void onDownloadFail(String errorMessage) {
                Log.d("DOWNLOAD_FAIL:",errorMessage);
            }
        });
        ServerCom.execute("1");
    }   //asztalok állapotának lekérdezése
    public static void SendOrderToServer(Table table){
        ServerCom.setOnConnectionListener(new ComTask.onConnectionListener() {
            @Override
            public void onDownloadSuccess(String response) {
                Log.d("UPLOAD_SUCCESS:","setOrderResult:"+response);
            }

            @Override
            public void onDownloadFail(String errorMessage) {
                Log.d("UPLOAD_FAIL:",errorMessage);
            }
        });

        ServerCom.execute("2",table.getName(),MyProductStrBuilder(table.getProducts()));  //rendelés elküldése a szervernek
    } //TODO krizantin
    public static void SendPayRequestToServer(String tableNumber){
        //todo krizantin
    }
    public static void AuthenticationOnServer(String user, String pass){
        //todo krizantin
    };
}
