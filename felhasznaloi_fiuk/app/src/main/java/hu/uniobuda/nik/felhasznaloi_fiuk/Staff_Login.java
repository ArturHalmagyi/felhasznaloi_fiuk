package hu.uniobuda.nik.felhasznaloi_fiuk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Staff_Login extends ActionBarActivity {


    private EditText username=null; //felhasználói név mező
    private EditText password=null; //jelszó mező
    private Button btnLogin; //belépés gomb

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);

        username = (EditText)findViewById(R.id.edit_username);
        password = (EditText)findViewById(R.id.edit_password);

        username.setText("nem");//Ez a könnyebb értékelést szolgálja :)
        password.setText("nem");

        btnLogin = (Button) findViewById(R.id.button);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //belépés gomb megnyomására
                ProgressDialog pd = new ProgressDialog(Staff_Login.this); //megjelenik egy töltést jelző dialog
                pd.setMessage(getResources().getString(R.string.layout_activity_guest_main_menu_dolgozom));
                pd.show();

                Communication.Authentication(username.getText().toString(),password.getText().toString()); //a név, jelszó mező tartalmát átadjuk a communication osztálynak

                if (!Communication.testMode){ //ha nem teszt mód
                    try {

                        Communication.getServerCom().get(1000, TimeUnit.MILLISECONDS); //kommunikálunk a szerverrel, erre max 1 másodpercet várunk
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }
                pd.dismiss(); //megsemmisítjük a töltő dialogot

                username.setText("");
                password.setText("");

                if(Communication.staff) //ha sikeres volt az azonosítás
                { Toast.makeText(Staff_Login.this, getResources().getString(R.string.layout_activity_staff_login_sikerult),
                        Toast.LENGTH_LONG).show(); //kiírjuk,hogy sikeres
                    Intent intent = new Intent(Staff_Login.this, Staff_Main_menu.class);
                    startActivity(intent);//elindítjuk a dolgozók főmenüjét tartalmazó activity-t
                }
                else { // ha nem jó a név/jelszó, kiírjuk, hogy nem sikerült az azonosítás
                    Toast.makeText(Staff_Login.this, getResources().getString(R.string.layout_activity_staff_login_rossz),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_staff_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

}
