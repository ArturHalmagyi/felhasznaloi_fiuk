package hu.uniobuda.nik.felhasznaloi_fiuk;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Guest_Main_menu extends ActionBarActivity {

    Button btnAzonositas;
    Button btnMeal;
    String azonositottString;
    // szerverrel való kommunikációért felelős példány
    public static Communication communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main_menu);

        //szerverrel való kommunikációért felelős osztály példányosítása
        //communicator = new Communication(true);
        //communicator.LoadTestTables();
        //communicator.LoadTestProducts();
        // SERVER TESTs
        //Communication.SendOrderToServer(communicator.getTables().get(0));
        Communication.products=null;
        //Communication.GetProductsFromServer();





        btnAzonositas = (Button) findViewById(R.id.btn_tables);
        azonositottString = "";

        btnAzonositas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(Guest_Main_menu.this);
                integrator.initiateScan(integrator.QR_CODE_TYPES);
            }
        });

        btnMeal = (Button) findViewById(R.id.btn_meal);
        btnMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Communication.GetProductsFromServer();
                    Communication.getServerCom().get(1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                /*
                Communication.products = null;
                Communication.GetProductsFromServer();
                int i=0;
                while (Communication.products == null) {
                    i++;
                    Toast.makeText(Guest_Main_menu.this,"még null "+i,Toast.LENGTH_SHORT).show();
                }*/
                Intent intent = new Intent(Guest_Main_menu.this, Guest_Meal.class);

                startActivity(intent);
            }
        });

    }

    public void startLogin(View view)
    {
        Intent intent = new Intent(this, Staff_Login.class);
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            azonositottString = result.getContents();
            if (contents != null) {
                //showDialog(R.string.result_succeeded, result.toString());
                Toast.makeText(this, azonositottString, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"Result féjld",Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guest_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
