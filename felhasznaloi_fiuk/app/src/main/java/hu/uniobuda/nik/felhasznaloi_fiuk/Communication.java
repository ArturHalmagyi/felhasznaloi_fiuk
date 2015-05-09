package hu.uniobuda.nik.felhasznaloi_fiuk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Barnaby-Laptop on 2015.04.28..
 */

public class Communication {

    public static ArrayList<Product> products;  //Termékek listája
    public static ArrayList<Table> tables;      //Asztalok listája
    public static String ip = "http://192.168.1.5";  //A szerver cime
    public static String table_id = "";             //Aktuális asztal száma
    public static Table actualTable;                //Aktuális asztal

    public static ArrayList<Table> getTables() {
        return tables;
    }

    public static Random rand = new Random();  //A tesztadatok feltöltéséhez használt random szám generátor
    static Boolean testMode=false;              //Tesztmód beállitása
    static boolean staff=true;                  //Személyzet belépve
    private static ComTask ServerCom;

    public static ComTask getServerCom() {
        return ServerCom;
    }

    public Communication(Boolean test) {
        testMode = test;
    }


    //teszt mód beállítás
    public static void SetTestModeOn(){
        testMode = true;
    }  //Tesztmód bekapcsol
    public static void SetTestModeOff(){
        testMode = false;
    } //Tesztmód kikapcsol, ilyenkor a szerverrel történik a kommunikáció


    //kommunikáció a szerverrel
    public static void GetProducts(){
        if (testMode){
            LoadTestProducts();
        }
        else {
            GetProductsFromServer();
        }
    } //menü lekérdezése
    public static void GetTable(String tableNumber){
        Table temp = new Table();
        int i = 0;
        while (i < Communication.tables.size() && !Communication.tables.get(i).getName().equals(tableNumber)){
            i++;
        }
        if (i < Communication.tables.size()){
            temp = Communication.tables.get(i);
        }
        actualTable = temp;
    }   //egy asztal állapotának lekérdezése
    public static void GetTables(){
        if (testMode){
            LoadTestTables();
        }
        else{
            GetTablesFromServer();
        }
    }  //az összes asztal állapotának lekérése
    public static void SendOrder(Table table){
        if (testMode){
        }
        else{
            SendOrderToServer(table);
        }
    }   //megrendelés elküldése a szervernek
    public static void Authentication(String user, String pass){
        if (testMode){
            AuthenticationTest(user,pass);
        }
        else {
            AuthenticationOnServer(user,pass);
        }
    }  //Autentikáció
    public static void SendPayRequest(String tableNumber){
        if (testMode){
        }
        else{
            SendPayRequestToServer(tableNumber);
        }
    }       //Fizetési jelzés elküldése


    //Tesztadatok
    public static void LoadTestTables(){

        tables = null;

        //int randTestTables = rand.nextInt(30)+10;
        for (int i = 0; i <14; i++){
            Table temp = new Table();
            temp.setName(String.valueOf(i+1));

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
    }  //Asztalok feltöltése tesztadatokkal
    public static void LoadTestProducts(){
        products = null;

        if (products == null){
            products = new ArrayList<Product>();
        }
        int randTestProduct = rand.nextInt(30)+10;
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
    } //Termékek feltöltése tesztadatokkal
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
    } //Asztalok rendeléseinek feltöltése tesztadatokkal
    public static void AuthenticationTest(String user, String pass){
        if(user.equals("nem")&&
                pass.equals("nem"))
            staff=true;

    } //Tesztmód autentikáció

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
        ArrayList<Product> products=new ArrayList<Product>(ProStrs.length);
        Integer db = ProStrs.length;
        for (int i=0;i<db;i++){
            int elso = ProStrs[i].indexOf(";");
            int masodik = ProStrs[i].indexOf(";",elso+1);
            String newName=ProStrs[i].substring(0,elso);
            String newPrice=ProStrs[i].substring(elso+1,masodik);
            String newUnit=ProStrs[i].substring(masodik+1);
            products.add(new Product(newPrice,newName,newUnit,"0"));
        }
        return products;
    }
    private static ArrayList<Product> StrToProductTwo(String str){
        String[] ProStrs= str.split("`");
        ArrayList<Product> products=new ArrayList<Product>(ProStrs.length);
        Integer db = ProStrs.length;
        for (int i=0;i<db;i++){
            int elso = ProStrs[i].indexOf(";");
            int masodik = ProStrs[i].indexOf(";",elso+1);
            int harom = ProStrs[i].indexOf(";",masodik+1);
            String newName=ProStrs[i].substring(0,elso);
            String newPrice=ProStrs[i].substring(elso+1,masodik);
            String newUnit=ProStrs[i].substring(masodik+1,harom);
            String newDB =ProStrs[i].substring(harom+1);
            products.add(new Product(newPrice,newName,newUnit,newDB));
        }
        return products;
    }
    public static ArrayList<Table> StrToTable(String str) {
        String[] StrTables= str.split("!");
        ArrayList<Table> tables=new ArrayList<Table>(14);
        for(int i=0;i<14;i++){
            Table temp=new Table();
            temp.setName(String.valueOf(i+1));
            temp.setState("szabad");
            tables.add(temp);
        }
        for (int i=0 ; i < StrTables.length ; i++){
            int dollar=StrTables[i].indexOf("$");
            int szazalek=StrTables[i].indexOf("%");
            String tProductsStr=StrTables[i].substring(szazalek+1);
            String tName=StrTables[i].substring(0,dollar);
            String tStatus=StrTables[i].substring(dollar+1,szazalek);
            ArrayList<Product> tProducts=StrToProductTwo(tProductsStr);
            Table tempTable=new Table();
            tempTable.setName(tName);
            tempTable.setState(tStatus);
            tempTable.setProducts(tProducts);
            //tables.add(Integer.parseInt(tName)-1,tempTable);
            tables.set(Integer.parseInt(tName)-1,tempTable);
        }
        return tables;
    }
    //Szerveradatok
    public static void GetProductsFromServer(){
        //return products;
        ServerCom = new ComTask(ip);
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
        ServerCom = new ComTask(ip);
        ServerCom.setOnConnectionListener(new ComTask.onConnectionListener() {
            @Override
            public void onDownloadSuccess(String response) {
                Log.d("DOWNLOAD_SUCCESS:","getTablesResult:"+response);
                tables = StrToTable(response);
            }

            @Override
            public void onDownloadFail(String errorMessage) {
                Log.d("DOWNLOAD_FAIL:","getTablesFrom Server say:"+errorMessage);
            }
        });
        ServerCom.execute("1");
    }   //asztalok állapotának lekérdezése
    public static void SendOrderToServer(Table table){
        ServerCom = new ComTask(ip);
        ServerCom.setOnConnectionListener(new ComTask.onConnectionListener() {
            @Override
            public void onDownloadSuccess(String response) {
                Log.d("UPLOAD_SUCCESS:","sendOrderResult:"+response);
            }

            @Override
            public void onDownloadFail(String errorMessage) {
                Log.d("UPLOAD_FAIL:",errorMessage);
            }
        });
        ServerCom.execute("2",table.getName(),MyProductStrBuilder(table.getProducts()));
    } //rendelés elküldése a szervernek
    public static void SendPayRequestToServer(String tableNumber){
        ServerCom = new ComTask(ip);
        ServerCom.setOnConnectionListener(new ComTask.onConnectionListener() {
            @Override
            public void onDownloadSuccess(String response) {
                Log.d("UPLOAD_SUCCESS:","PayRequest:"+response);
            }

            @Override
            public void onDownloadFail(String errorMessage) {
                Log.d("UPLOAD_FAIL:",errorMessage);
            }
        });
        ServerCom.execute("3",tableNumber);
    } // Asztal fizetésre állítása
    public static void AuthenticationOnServer(String user, String pass){
        ServerCom = new ComTask(ip);
        ServerCom.setOnConnectionListener(new ComTask.onConnectionListener() {
            @Override
            public void onDownloadSuccess(String response) {
                Log.d("ACCESS_RESULT:",response);
                if(response.equalsIgnoreCase("access gained"))
                    staff=true;
                else
                    staff=false;
            }

            @Override
            public void onDownloadFail(String errorMessage) {
                Log.d("ACCESS_RESULT:",errorMessage);
            }
        });
        ServerCom.execute("4",user,pass);
    } // user&pass ellenőrzés
}
