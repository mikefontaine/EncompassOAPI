package com.mikefontaine.program.encompassoapi;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

public class GetLoan extends AppCompatActivity {
    static SharedPreferences settings;
    static SharedPreferences.Editor editor;
    private RecyclerView GuidListViewer;
    private RecyclerView.LayoutManager loanLayoutManager;
    private ArrayList<String> loanList;
    private LoanAdapter adapter;
    private EditText userBox;
    private EditText passBox;
    private EditText instanceBox;
    private EditText tokenBox;
    private EditText loanNumBox;
    private String token;
    private String username;
    private String password;
    private String instanceID;
    private String loanNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_loan);
        settings = this.getPreferences(MODE_PRIVATE);
        username = settings.getString("username", "");
        password = settings.getString("password", "");
        instanceID = settings.getString("instance", "");


        // init interface input fields

        userBox = findViewById(R.id.editUserName);
        passBox = findViewById(R.id.editPassword);
        instanceBox = findViewById(R.id.editInstanceID);
        tokenBox = findViewById(R.id.editToken);
        loanNumBox = findViewById(R.id.editLoanNum);
        GuidListViewer = findViewById(R.id.guidList);
        loanLayoutManager = new LinearLayoutManager(this);

        //init input variables and check for stored values

        editor = settings.edit();
        token = "";
        loanList = new ArrayList<>();
        adapter = new LoanAdapter(loanList);
        // set constants
        //create view
        // populate UI
        userBox.setText(username);
        passBox.setText(password);
        instanceBox.setText(instanceID);
        //tokenBox.setText(token);
        GuidListViewer.setLayoutManager(loanLayoutManager);
        GuidListViewer.setHasFixedSize(true);
        GuidListViewer.setAdapter(adapter);
        // create button listener
        final Button getTokenButton = findViewById(R.id.button_getToken);
        getTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText UserName = findViewById(R.id.editUserName);
                username = userBox.getText().toString();
                editor.putString("username", username);
                //EditText Password = findViewById(R.id.editPassword);
                password = passBox.getText().toString();
                editor.putString("password", password);
                // final EditText instance = findViewById(R.id.editInstanceID);
                instanceID = instanceBox.getText().toString();
                editor.putString("instance", instanceID);
                //final EditText Results = findViewById(R.id.editResults);
                editor.commit();
                //Results.setText(user_name);
                getToken();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tokenBox.setText(token);


            }
        });
        final Button getLoanButton = findViewById(R.id.getLoanButton);
        getLoanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle item selection
        switch (item.getItemId()) {
            case R.id.menu_exit:
                System.exit(0);
                return true;
            case R.id.menu_clear:
                //EditText userBox = findViewById(R.id.editUserName);
                //EditText passBox = findViewById(R.id.editPassword);
                //EditText instanceBox = findViewById(R.id.editInstanceID);
                //EditText tokenBox = findViewById(R.id.editToken);
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

    public void getToken() {

        //TextView user = findViewById(R.id.editUserName);
        //TextView password = findViewById((R.id.editPassword));
        //final TextView instance = findViewById(R.id.editInstanceID);
        username = userBox.getText().toString();
        password = passBox.getText().toString();
        instanceID = instanceBox.getText().toString();
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
                    String body = "grant_type=password&username=" + username + "%40encompass%3A" + instanceID + "&password=" + password + "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET;
                    myConnection.setRequestMethod("POST");
                    myConnection.getOutputStream().write(body.getBytes());
                    if (myConnection.getResponseCode() == 200) {
                        InputStream responseBody = myConnection.getInputStream();
                        //EditText et = findViewById(R.id.editResults);
                        //et.setText("Worked" + " " + body);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        String result = sb.toString();
                        JSONObject jObject = new JSONObject(result);
                        token = jObject.getString("access_token");
                        //EditText tok = findViewById(R.id.editToken);
                        //tok.setText(token.toString());
                        String response = myConnection.getHeaderFields().toString();
                        //et.setText("Worked " + response);


                    } else {
                        EditText et = findViewById(R.id.editResults);
                        et.setText("Failed");

                        //  Results.setText(myConnection.getResponseCode());
                    }

                    myConnection.disconnect();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    //EditText et = findViewById(R.id.editResults);
                    //et.setText(e.getMessage().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    //EditText et = findViewById(R.id.editResults);
                    //et.setText(e.getMessage().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void getLoans() {
        //final TextView token = findViewById(R.id.editToken);
        //final TextView loanNumber = findViewById(R.id.editLoanNum);
        //token = token.getText().toString();
        getToken();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //tokenBox.setText(token);
        loanNumber= loanNumBox.getText().toString();
        //final TextView results = findViewById(R.id.editResults);
        // send post to retrieve loan to server
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String authString = "Bearer " + token;
                    URL endpoint = new URL("https://api.elliemae.com/encompass/v1/loanPipeline");
                    HttpsURLConnection myConnection = (HttpsURLConnection) endpoint.openConnection();
                    myConnection.setRequestProperty("Content-Type", "application/json");
                    myConnection.setRequestProperty("Cache-Control", "no-cache");
                    myConnection.setRequestProperty("Authorization", authString);
                    String body = "{\"filter\": {\"terms\": [{\"canonicalName\": \"Fields.364\",\"value\": \"" + loanNumber
                            + "\",\"matchType\": \"exact\"}]},\"sortOrder\": [{\"canonicalName\": \"Loan.LoanNumber\",\"order\": \"asc\"},{\"canonicalName\": \"Fields.4000\",\"order\": \"desc\"}]}";
                    myConnection.setRequestMethod("POST");
                    String requestProperties = myConnection.getRequestProperties().toString();
                    myConnection.getOutputStream().write(body.getBytes());
                    //String body_and_properties = body + "                      " + requestProperties;
                    //results.setText(body_and_properties);
                    if (myConnection.getResponseCode() == 200) {
                        InputStream responseBody = myConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            String newline = line + "\n";
                            sb.append(newline);
                        }
                        String result = sb.toString();
                        JSONArray jObject = null;
                        try {
                            jObject = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jObject.length() > 0) {
                            for (int x = 0; x < jObject.length(); x++) {
                                JSONObject GUIDObject = jObject.getJSONObject(x);
                                if (GUIDObject != null) {
                                    String GUID = GUIDObject.optString("loanGuid");
                                    if (GUID != null) {
                                        //results.setText(GUID);
                                        loanList.add(GUID);
                                    }// else {
                                        //results.setText("Problem reading JSON");
                                   // }
                                }// else {
                                    //results.setText("No Loan Found for " + loan);
                               // }
                            }
                        } //else {
                            //results.setText("No Loan Found");
                        //}
                    } //else {
                        //String current_results = results.getText().toString();
                        //String new_results = current_results
                          //      + "           \\n" + myConnection.getResponseCode()
                            //    + "           \n" + myConnection.getHeaderFields().toString()
                              //  + "           \\\n" + myConnection.getResponseMessage();

                        //results.setText(new_results);
                        //token.setText("failed " + myConnection.getResponseMessage().toString());
                   // }
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
