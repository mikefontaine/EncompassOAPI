package com.mikefontaine.program.encompassoapi;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetLoan extends AppCompatActivity {
    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_loan);
        // init interface input fields
        EditText userBox = findViewById(R.id.editUserName);
        EditText passBox= findViewById(R.id.editPassword);
        EditText instanceBox = findViewById(R.id.editInstanceID);
        EditText tokenBox = findViewById(R.id.editToken);
        //init input variables and check for stored values
        settings = this.getPreferences(MODE_PRIVATE);
        editor = settings.edit();
        String user = settings.getString("username", "test");
        String pass = settings.getString("password", "test");
        String instance = settings.getString("instance", "test");
        String token = "test";
        // set constants
        final String CLIENT_SECRET = getString(R.string.clientsecret);
        final String CLIENT_ID = getString(R.string.clientid);
        //create view

        // populate UI
        userBox.setText(user);

        passBox.setText(pass);
        instanceBox.setText(instance);
        tokenBox.setText(token);
        // create button listener
        final Button getTokenButton = findViewById(R.id.button_getToken);
        getTokenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText UserName = findViewById(R.id.editUserName);
                final String user_name  = UserName.getText().toString();
                editor.putString("username", user_name);
                EditText Password = findViewById(R.id.editPassword);
                final String pass = Password.getText().toString();
                editor.putString("password", pass);
                final EditText instance = findViewById(R.id.editInstanceID);
                editor.putString("instance", instance.getText().toString());
                final EditText Results = findViewById(R.id.editResults);
                editor.commit();
                Results.setText(user_name);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        // All your networking logic
                        // should be here
                        try {
                            URL TokenEndpoint = new URL("https://api.elliemae.com/oauth2/v1/token");
                            HttpsURLConnection myConnection = (HttpsURLConnection) TokenEndpoint.openConnection();
                            myConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            String body = "grant_type=password&username="+ user_name +"%40encompass%3A"+ instance.getText().toString() +"&password="+pass+"&client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET;
                            myConnection.setRequestMethod("POST");
                            myConnection.getOutputStream().write(body.getBytes());
                            if (myConnection.getResponseCode() == 200) {
                                InputStream responseBody = myConnection.getInputStream();
                                EditText et = findViewById(R.id.editResults);
                                et.setText("Worked" +" " + body);
                                BufferedReader reader = new BufferedReader( new InputStreamReader(responseBody, "UTF-8"), 8);
                                StringBuilder sb = new StringBuilder();
                                String line = null;
                                while((line = reader.readLine()) != null){
                                    sb.append(line + "\n");
                                }
                                String result = sb.toString();
                                JSONObject jObject = new JSONObject(result);
                                String token = jObject.getString("access_token");
                                EditText tok = findViewById(R.id.editToken);
                                tok.setText(token.toString());
                                String response = myConnection.getHeaderFields().toString();
                                et.setText("Worked "+response);



                            } else {
                                EditText et = findViewById(R.id.editResults);
                                et.setText("Failed");

                              //  Results.setText(myConnection.getResponseCode());
                            }

                            myConnection.disconnect();



                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            EditText et = findViewById(R.id.editResults);
                            et.setText(e.getMessage().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            EditText et = findViewById(R.id.editResults);
                            et.setText(e.getMessage().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //handle item selection
        switch (item.getItemId()){
            case R.id.menu_exit:
                System.exit(0);
                return true;
            case R.id.menu_clear:
                EditText userBox = findViewById(R.id.editUserName);
                EditText passBox= findViewById(R.id.editPassword);
                EditText instanceBox = findViewById(R.id.editInstanceID);
                EditText tokenBox = findViewById(R.id.editToken);
                userBox.setText("");
                passBox.setText("");
                instanceBox.setText("");
                tokenBox.setText("");
                editor.putString("password", "");
                editor.putString("username", "");
                editor.putString("instance", "");
                editor.commit();

                return true;
            default:
                return true;


        }
    }


}
