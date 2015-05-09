package hu.uniobuda.nik.felhasznaloi_fiuk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Guest_Main_menu extends ActionBarActivity { //Ez a fő activity, ami launcherként is szolgál, innen lehet minden funkciót elérni

    Button btnID; //Az azonosító gomb
    Button btnMeal; //Menüt meghívó gomb
    Button btnPay; //Fizetést jelző gomb
    Button btnMyOrders; //Megrendeléseket lekérdező gomb

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main_menu);

        Communication.SetTestModeOn(); //A tesztmóddal lehet a szerverrel való kapcsolat nélkül is tesztelni a funkciókat
        Communication.products=null;

        btnID = (Button) findViewById(R.id.btn_tables);
        btnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(Guest_Main_menu.this);
                integrator.initiateScan(integrator.QR_CODE_TYPES); //A QR-kór olvasó elindítása
            }
        });

        btnMeal = (Button) findViewById(R.id.btn_meal);
        btnMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    ProgressDialog pd = new ProgressDialog(Guest_Main_menu.this);
                    pd.setMessage(getResources().getString(R.string.layout_activity_guest_main_menu_dolgozom));
                    pd.show();
                    //Elindítja a kommunikáció osztály asztal lekérdezését, mely után elérhetjük az ott tárolt adatokat
                    Communication.GetProducts();
                    if (!Communication.testMode) {
                        Communication.getServerCom().get(1000, TimeUnit.MILLISECONDS);
                    }
                    pd.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

                //A menü activity elindítása
                Intent intent = new Intent(Guest_Main_menu.this, Guest_Meal.class);
                startActivity(intent);
            }
        });

        btnPay = (Button) findViewById(R.id.btn_pay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //A fizetési igény elküldése a szervernek
                if (Communication.testMode){
                    Toast.makeText(Guest_Main_menu.this,getResources().getString(R.string.layout_activity_guest_main_menu_fizetek), Toast.LENGTH_LONG).show();
                }
                else{
                    Communication.SendPayRequestToServer(Communication.table_id);
                }
            }
        });

        btnMyOrders = (Button) findViewById(R.id.btn_order);
        btnMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Az azonosított asztal rendeléseinek lekérdezése
                if (Communication.table_id != "") {
                    ProgressDialog pd = new ProgressDialog(Guest_Main_menu.this);
                    pd.setMessage("Processing...");
                    pd.show();
                    Communication.GetTables();

                    if (!Communication.testMode) {
                        try {
                            Communication.getServerCom().get(1000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                    pd.dismiss();

                    Communication.GetTable(Communication.table_id);

                    if (Communication.actualTable.products.size() > 0) { //Ha van rendelt termék, kijelzi azokat, ha nincs, szól
                        Toast.makeText(Guest_Main_menu.this, Communication.actualTable.getName() + String.valueOf(Communication.actualTable.products.size()), Toast.LENGTH_LONG).show();
                        AlertDialog.Builder dlgAlert= new AlertDialog.Builder(Guest_Main_menu.this); //Felugró ablak létrehozása

                        String temp = "";
                        int len = Communication.actualTable.products.size();


                        //Az ablak tartalmának összefűzése 'Termék: x db y Ft' formátumban, a végösszeggel együtt
                        List<Product> tempProductList = Communication.actualTable.products;
                        int cost = 0;
                        for (int i = 0; i < len; i++){
                            temp += tempProductList.get(i).getName();
                            temp += ": ";
                            temp += tempProductList.get(i).getQuantity();
                            temp += "db ";
                            int q =Integer.parseInt(tempProductList.get(i).getQuantity());
                            int p =Integer.parseInt(tempProductList.get(i).getPrice());
                            temp += String.valueOf(q*p);
                            temp += "Ft ";
                            cost += q*p;
                            temp += "\n";
                        }
                        temp += "\n";
                        temp += "Összesen " + String.valueOf(cost) + "Ft";


                        dlgAlert.setMessage(temp);
                        dlgAlert.setTitle("Megrendelések");
                        dlgAlert.setPositiveButton("OK",null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show(); //Az ablak megjelenítése
                    }
                    else{
                        Toast.makeText(Guest_Main_menu.this,getResources().getString(R.string.layout_activity_guest_main_menu_nincs_rendelés), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(Guest_Main_menu.this,getResources().getString(R.string.layout_activity_guest_main_menu_nincs_azon), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void startLogin(View view) //A személyzet gombra kattintva elindul a login activity
    {
        Intent intent = new Intent(this, Staff_Login.class);
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) { // A QR azonosításnál a fragment értékét itt dolgozzuk fel
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            Communication.table_id = result.getContents();
            if (contents != null) { //Ha baj volt az azonosítással, azt kiírjuk
                Toast.makeText(this, Communication.table_id, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,getResources().getString(R.string.layout_activity_guest_main_menu_qr_nemjo),Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_guest_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
