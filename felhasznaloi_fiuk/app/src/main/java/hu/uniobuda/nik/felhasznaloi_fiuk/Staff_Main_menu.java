package hu.uniobuda.nik.felhasznaloi_fiuk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Staff_Main_menu extends ActionBarActivity {

    Button btn_tables;  //Asztalok állapotának lekérdezése gomb
    Button btn_meal;    //Menü megjelenitése gomb
    Button btn_pay;     //Kiválasztott asztal fizetésigényének elküldése gomb
    NumberPicker np;    //Asztal kiválasztását szolgáló gomb

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main_menu);

        btn_tables = (Button) findViewById(R.id.btn_tables);
        btn_tables.setOnClickListener(new View.OnClickListener() { //Asztalok állapotának lekérdezése
            @Override
            public void onClick(View v) {
                try {

                    ProgressDialog pd = new ProgressDialog(Staff_Main_menu.this);
                    pd.setMessage(getResources().getString(R.string.layout_activity_guest_main_menu_dolgozom));
                    pd.show();

                    Communication.GetTables(); //A szerver a kapcsolati módtól függően lekérdezi az asztalokat
                    if (!Communication.testMode) {
                        Communication.getServerCom().get(1000, TimeUnit.MILLISECONDS);
                    }

                    Intent intent = new Intent(Staff_Main_menu.this, Tables.class); //Az asztalok activity-ének elindítása
                    startActivity(intent);

                    pd.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });


        //Number picker beállítása
        np = (NumberPicker) findViewById(R.id.asztalSzamValaszto); //A személyzet kézzel tudja beállítani az asztal számát
        np.setMinValue(1);
        np.setMaxValue(14);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Communication.table_id=Integer.toString(newVal); //A picker állásának változására íródik át az asztal száma
            }
        });


        btn_meal = (Button) findViewById(R.id.btn_meal); //Innen is el lehet érni a menüt
        btn_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ProgressDialog pd = new ProgressDialog(Staff_Main_menu.this);
                    pd.setMessage(getResources().getString(R.string.layout_activity_guest_main_menu_dolgozom));
                    pd.show();
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

                Intent intent = new Intent(Staff_Main_menu.this, Guest_Meal.class);
                startActivity(intent);
            }
        });

        btn_pay = (Button) findViewById(R.id.btn_pay); //A személyzet adott asztaltól fizetési kérést is tud küldeni
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Communication.testMode){
                    Toast.makeText(Staff_Main_menu.this,getResources().getString(R.string.layout_activity_guest_main_menu_fizetek), Toast.LENGTH_LONG).show();
                }
                else{
                    Communication.SendPayRequestToServer(Communication.table_id);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_staff_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
