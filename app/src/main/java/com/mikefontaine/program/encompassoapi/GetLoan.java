package com.mikefontaine.program.encompassoapi;

import android.content.SharedPreferences;
//import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.JsonReader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class GetLoan extends AppCompatActivity {
    static SharedPreferences settings;
    static SharedPreferences.Editor editor;
    private RecyclerView GuidListViewer;
    private RecyclerView.LayoutManager loanLayoutManager;
    private ArrayList<String> loanList;
    //private List<String> test;
    private LoanAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_loan);
        // init interface input fields
        EditText userBox = findViewById(R.id.editUserName);
        EditText passBox= findViewById(R.id.editPassword);
        EditText instanceBox = findViewById(R.id.editInstanceID);
        EditText tokenBox = findViewById(R.id.editToken);
        GuidListViewer = findViewById(R.id.guidList);
        loanLayoutManager = new LinearLayoutManager(this);
        //init input variables and check for stored values
        settings = this.getPreferences(MODE_PRIVATE);
        editor = settings.edit();
        String user = settings.getString("username", "test");
        String pass = settings.getString("password", "test");
        String instance = settings.getString("instance", "test");
        String token = "test";
        loanList = new ArrayList<>();
        adapter = new LoanAdapter(loanList);
        // set constants
        //create view
        // populate UI
        userBox.setText(user);
        passBox.setText(pass);
        instanceBox.setText(instance);
        tokenBox.setText(token);
        GuidListViewer.setLayoutManager(loanLayoutManager);
        GuidListViewer.setHasFixedSize(true);
        GuidListViewer.setAdapter(adapter);
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
                getToken();

            }
        });
        final Button getLoanButton = findViewById(R.id.getLoanButton);
        getLoanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loanList.clear();
                getLoans();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
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

    public void getToken(){

        TextView user = findViewById(R.id.editUserName);
        TextView password = findViewById((R.id.editPassword));
        final TextView instance = findViewById(R.id.editInstanceID);
        final String user_name = user.getText().toString();
        final String pass = password.getText().toString();
        final String CLIENT_SECRET = getString(R.string.clientsecret);
        final String CLIENT_ID = getString(R.string.clientid);
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

    public void getLoans(){
        final TextView token = findViewById(R.id.editToken);
        final TextView loanNumber = findViewById(R.id.editLoanNum);
        final String tok = token.getText().toString();
        final String loan = loanNumber.getText().toString();
        final TextView results = findViewById(R.id.editResults);
        // send post to retrieve loan to server
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String authString = "Bearer "+tok;
                    URL endpoint = new URL("https://api.elliemae.com/encompass/v1/loanPipeline");
                    HttpsURLConnection myConnection = (HttpsURLConnection) endpoint.openConnection();
                    myConnection.setRequestProperty("Content-Type", "application/json");
                    myConnection.setRequestProperty("Cache-Control", "no-cache");
                    myConnection.setRequestProperty("Authorization", authString);
                    String body = "{\"filter\": {\"terms\": [{\"canonicalName\": \"Fields.364\",\"value\": \""+loan
                            +"\",\"matchType\": \"exact\"}]},\"sortOrder\": [{\"canonicalName\": \"Loan.LoanNumber\",\"order\": \"asc\"},{\"canonicalName\": \"Fields.4000\",\"order\": \"desc\"}]}";
                    myConnection.setRequestMethod("POST");
                    String requestProperties  = myConnection.getRequestProperties().toString();
                    myConnection.getOutputStream().write(body.getBytes());
                    String body_and_properties = body+"                      "+ requestProperties;
                    results.setText(body_and_properties);
                    if (myConnection.getResponseCode() == 200) {
                        InputStream responseBody = myConnection.getInputStream();
                        BufferedReader reader = new BufferedReader( new InputStreamReader(responseBody, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while((line = reader.readLine()) != null){
                            String newline  = line + "\n";
                            sb.append(newline);
                        }
                        String result = sb.toString();
                        JSONArray jObject = null;
                        try {
                            jObject = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jObject.length()>0) {
                            for(int x = 0; x<jObject.length(); x++){
                                JSONObject GUIDObject = jObject.getJSONObject(x);
                                if (GUIDObject != null) {
                                    String GUID = GUIDObject.optString("loanGuid");
                                    if (GUID != null) {
                                        results.setText(GUID);
                                        loanList.add(GUID);
                                    } else {
                                        results.setText("Problem reading JSON");
                                    }
                                } else {
                                    results.setText("No Loan Found for " + loan);
                                }
                            }
                        }else {
                            results.setText("No Loan Found");
                        }
                    } else {
                        String current_results = results.getText().toString();
                        String new_results = current_results
                                +"           \\n"+myConnection.getResponseCode()
                                +"           \n" + myConnection.getHeaderFields().toString()
                                +"           \\\n"+myConnection.getResponseMessage();

                        results.setText(new_results);
                        //token.setText("failed " + myConnection.getResponseMessage().toString());
                    }
                    myConnection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
       // loanList.add("something");
    }


}
