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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Guest_Main_menu extends ActionBarActivity {

    Button btnID;
    Button btnMeal;
    Button btnPay;
    Button btnMyOrders;
    //String table_id;
    // szerverrel való kommunikációért felelős példány
    //public static Communication communicator;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main_menu);


        Communication.SetTestModeOn();
        //communicator = new Communication(true, this);
        //Communication.SetTestModeOn();
        //szerverrel való kommunikációért felelős osztály példányosítása
        //communicator = new Communication(true);
        //communicator.LoadTestTables();
        //communicator.LoadTestProducts();
        // SERVER TESTs
        //Communication.SendOrderToServer(communicator.getTables().get(0));
        Communication.products=null;
        //Communication.GetProductsFromServer();

        btnID = (Button) findViewById(R.id.btn_tables);
        btnID.setOnClickListener(new View.OnClickListener() {
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
                    //Communication.getServerCom().setContext(Guest_Main_menu.this);
                    //Communication.products = null;
                    ProgressDialog pd = new ProgressDialog(Guest_Main_menu.this);
                    pd.setMessage(getResources().getString(R.string.layout_activity_guest_main_menu_dolgozom));
                    pd.show();
                    //if (Communication.products == null) {
                        //Communication.GetProductsFromServer();
                    Communication.GetProducts();
                    if (!Communication.testMode) {
                        Communication.getServerCom().get(1000, TimeUnit.MILLISECONDS);
                    }
                    //}
                    pd.dismiss();
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
                //intent.putExtra("table_id", table_id);
                startActivity(intent);
            }
        });

        btnPay = (Button) findViewById(R.id.btn_pay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    if (Communication.actualTable.products.size() > 0) {
                        Toast.makeText(Guest_Main_menu.this, Communication.actualTable.getName() + String.valueOf(Communication.actualTable.products.size()), Toast.LENGTH_LONG).show();
                        AlertDialog.Builder dlgAlert= new AlertDialog.Builder(Guest_Main_menu.this);
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Guest_Main_menu.this,R.layout.activity_guest_main_menu);
                        arrayAdapter.add(Communication.actualTable.products.get(0).toString());
                        //dlgAlert.setMessage("valami");
                        dlgAlert.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        dlgAlert.setTitle("megrendelések");
                        dlgAlert.setPositiveButton("OK",null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    }
                    else{
                        Toast.makeText(Guest_Main_menu.this,"Nincs rendelése", Toast.LENGTH_LONG).show();
                    }
                    //todo megjeleniteni a tartalmat
                }
                else{
                    Toast.makeText(Guest_Main_menu.this,"Azonositsa az asztalt", Toast.LENGTH_LONG).show();
                }
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
            Communication.table_id = result.getContents();
            //table_id = result.getContents();
            if (contents != null) {
                //showDialog(R.string.result_succeeded, result.toString());
                Toast.makeText(this, Communication.table_id, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,getResources().getString(R.string.layout_activity_guest_main_menu_qr_nemjo),Toast.LENGTH_LONG);
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


        return super.onOptionsItemSelected(item);
    }
}
