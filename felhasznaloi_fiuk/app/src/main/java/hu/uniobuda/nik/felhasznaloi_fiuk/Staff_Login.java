package hu.uniobuda.nik.felhasznaloi_fiuk;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Staff_Login extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_staff_login, menu);
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
    EditText username = (EditText)findViewById(R.id.editText);
    EditText password = (EditText)findViewById(R.id.editText2);
    public void login(View view){
        if(username.getText().toString().equals("admin") &&
                password.getText().toString().equals("admin")){
            Toast.makeText(getApplicationContext(), "sikeres azonositas",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "nem nyert",
                    Toast.LENGTH_SHORT).show();
        }}
}
