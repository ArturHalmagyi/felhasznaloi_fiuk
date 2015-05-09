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


    private EditText  username=null;
    private EditText  password=null;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);
        username = (EditText)findViewById(R.id.edit_username);
        password = (EditText)findViewById(R.id.edit_password);
        btnLogin = (Button) findViewById(R.id.button);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd = new ProgressDialog(Staff_Login.this);
                pd.setMessage(getResources().getString(R.string.layout_activity_guest_main_menu_dolgozom));
                pd.show();

                Communication.Authentication(username.getText().toString(),password.getText().toString());

                if (!Communication.testMode){
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

                if(Communication.staff)
                { Toast.makeText(Staff_Login.this, getResources().getString(R.string.layout_activity_staff_login_sikerult),
                        Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Staff_Login.this, Staff_Main_menu.class);
                    startActivity(intent);
                }
                else {
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


    public void login(View view){


        ProgressDialog pd = new ProgressDialog(Staff_Login.this);
        pd.setMessage(getResources().getString(R.string.layout_activity_guest_main_menu_dolgozom));
        pd.show();

        Communication.Authentication(username.getText().toString(),password.getText().toString());

        if (!Communication.testMode){
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

        if(Communication.staff)
        { Toast.makeText(this, getResources().getString(R.string.layout_activity_staff_login_sikerult),
                    Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, Staff_Main_menu.class);
        startActivity(intent);
        }
        else {
        Toast.makeText(this, getResources().getString(R.string.layout_activity_staff_login_rossz),
                Toast.LENGTH_LONG).show();
        }
    }
}
