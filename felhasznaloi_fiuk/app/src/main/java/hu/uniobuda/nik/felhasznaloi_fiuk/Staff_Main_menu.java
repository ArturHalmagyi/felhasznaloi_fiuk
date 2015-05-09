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

    String table_id;
    Button btn_tables;  //Asztalok állapotának lekérdezése gomb
    Button btn_meal;    //Menü megjelenitése gomb
    Button btn_pay;     //Kiválasztott asztal fizetésigényének elküldése gomb
    NumberPicker np;    //Asztal kiválasztása (Amihez az alkalmazott rendel, vagy aminek elküldheti a fizetési igényét)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main_menu);

        table_id = "";

        btn_tables = (Button) findViewById(R.id.btn_tables);
        btn_tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    ProgressDialog pd = new ProgressDialog(Staff_Main_menu.this);
                    pd.setMessage(getResources().getString(R.string.layout_activity_guest_main_menu_dolgozom));
                    pd.show();

                    Communication.GetTables();
                    if (!Communication.testMode) {
                        Communication.getServerCom().get(1000, TimeUnit.MILLISECONDS);
                    }

                    Intent intent = new Intent(Staff_Main_menu.this, Tables.class);
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

        np = (NumberPicker) findViewById(R.id.asztalSzamValaszto);
        np.setMinValue(1);
        np.setMaxValue(14);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Communication.table_id=Integer.toString(newVal);
            }
        });


        btn_meal = (Button) findViewById(R.id.btn_meal);
        btn_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ProgressDialog pd = new ProgressDialog(Staff_Main_menu.this);
                    pd.setMessage("Processing...");
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

        btn_pay = (Button) findViewById(R.id.btn_pay);
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


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            table_id = result.getContents();
            if (contents != null) {
                Toast.makeText(this, table_id, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,getResources().getString(R.string.layout_activity_guest_main_menu_qr_nemjo),Toast.LENGTH_LONG);
            }
        }
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
