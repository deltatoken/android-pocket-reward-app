package com.droidoxy.easymoneyrewards;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.Menu;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.droidoxy.easymoneyrewards.app.App;
import com.droidoxy.easymoneyrewards.constants.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReferActivity extends BaseActivity {
    Button btnRefer,SubmitReferal;
    EditText referText;
    TextView text1;
    private PrefManager prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        getSupportActionBar().setIcon(R.drawable.ic_back_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.refer);

        btnRefer = (Button) findViewById(R.id.btnrefer);
        SubmitReferal = (Button) findViewById(R.id.SubmitReferal);
        referText = (EditText) findViewById(R.id.EnterReferal);
        text1 = (TextView) findViewById(R.id.text_appname);
		validate();

        prf = new PrefManager(this);
        if(prf.isCheckin()){
            set_referal_id();
        }
        if (!prf.isRefered()) {
            SubmitReferal.setVisibility(View.INVISIBLE);
            referText.setVisibility(View.INVISIBLE);
        }

        text1.setText(prf.getString("referal_id"));

        btnRefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referURL();
            }
        });


        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", prf.getString("referal_id"));
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ReferActivity.this,R.string.refer_code_copied,Toast.LENGTH_SHORT).show();
            }
        });


        SubmitReferal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String RefId = referText.getText().toString();
                if(RefId != null && !RefId.isEmpty()){

                    if(RefId.intern() == prf.getString("referal_id").intern()){

                        Toast.makeText(ReferActivity.this,R.string.self_refer,Toast.LENGTH_SHORT).show();

                    }else {
                        refer(Config.referal_reward, getString(R.string.referal_income), RefId);
                    }

                }else{

                    Toast.makeText(ReferActivity.this,R.string.enter_valid_code,Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

            case R.id.share:
                shareURL();
                return true;

            case R.id.buy:
                getcode();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void getcode() {
        Uri uri = Uri.parse(Constants.Code_License);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    // sharing
    public void shareURL() {
        try
        { Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String sAux = getString(R.string.share_text)+"\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName()+"\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, getString(R.string.choose_one)));
        }
        catch(Exception e)
        { //e.toString();
        }
    }

    // Refer
    public void referURL() {
        try
        { Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String sAux = getString(R.string.refer_text1)+prf.getString("referal_id")+"\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName()+"\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, getString(R.string.choose_one)));
        }
        catch(Exception e)
        { //e.toString();
        }
    }


    void validate(){
        if (Config.Base_Url.intern() == Constants.API_License && !BuildConfig.DEBUG && Constants.release) {

            App.getInstance().logout();
            Intent i = new Intent(getApplicationContext(), AppActivity.class);
            startActivity(i);
            finish();
            ActivityCompat.finishAffinity(ReferActivity.this);
        }
    }

    // Referral
    void refer(int points, final String type, final String refer_Code){

        String awr = Config.Base_Url+"get/jamesbond.php";
        final String awrds = Integer.toString(points);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
        final String Current_Date = dd.format(c.getTime());

        final String v1 ="1";
        final String v0 ="0";
        final String v2 ="2";
        final String v3 ="3";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, awr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.intern() == v1.intern()) {
                            prf.setRefered(false);
                            AlertDialog alert = new AlertDialog.Builder(ReferActivity.this).create();
                            alert.setTitle(getString(R.string.great));
                            alert.setMessage(Config.referal_reward + " "+getString(R.string.referal_reward));
                            alert.setCanceledOnTouchOutside(false);
                            alert.setIcon(R.drawable.custom_img);

                            alert.setButton(getString(R.string.ok2), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    // do nothin
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            });
                            alert.show();

                        }
                        if(response.intern() == v0.intern()){

                            prf.setRefered(false);
                            AlertDialog alert = new AlertDialog.Builder(ReferActivity.this).create();

                            alert.setTitle(getString(R.string.reward_taken));
                            alert.setMessage(getString(R.string.allowed));
                            alert.setCanceledOnTouchOutside(false);

                            alert.setButton(getString(R.string.ok2), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }

                            });
                            alert.show();

                        }if(response.intern() == "self"){

                            prf.setRefered(false);
                            Toast.makeText(ReferActivity.this,R.string.self_refer,Toast.LENGTH_SHORT).show();

                        }
                        if(response.intern() == v2.intern()){

                            Toast.makeText(ReferActivity.this,R.string.server_problem,Toast.LENGTH_SHORT).show();
                        }
                        if(response.intern() == v3.intern()){

                            Toast.makeText(ReferActivity.this,R.string.enter_valid_code,Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",App.getInstance().getUsername());
                params.put("fullname",App.getInstance().getFullname());
                params.put("referer",refer_Code);
                params.put("points",awrds);
                params.put("type",type);
                params.put("date",Current_Date);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void set_referal_id(){
        String refer_server = Config.Base_Url+"get/setrefer.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, refer_server,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.intern() == "2".intern()){
                            // do nothin
                        }else {

                            //Toast.makeText(ReferActivity.this, response, Toast.LENGTH_SHORT).show();
                            prf.setString("referal_id",response);
                            prf.setCheckin(false);
                            chkref();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",App.getInstance().getUsername());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void chkref(){
        String refer_server = Config.Base_Url+"get/chkref.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, refer_server,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.intern() == "1".intern()){
                            prf.setRefered(false);

                            // reload and remove enterting refer code and show refer code
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }else {
                            // do nothin
                            // reload and show refer code
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",App.getInstance().getUsername());
                params.put("type",getString(R.string.referal_income));
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}