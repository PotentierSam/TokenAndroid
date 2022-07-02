package fr.supavenir.lsts.token;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

public class TokenList extends AppCompatActivity {
    private ListView lvTokenList;
    private TokenAdapter adapter;
    private int actualPosition = 0;

    public void setActualPosition( int position ) {this.actualPosition = position;}

    private ActivityResultLauncher<Intent> lanceurActiviteChoixCouleur = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        float high = result.getData().getIntExtra("high", 0);
                        float low = result.getData().getIntExtra("low", 0);
                        float actual = result.getData().getIntExtra("actual", 0);
                        String name = result.getData().getStringExtra("name");

                        String baseName = result.getData().getStringExtra("baseName");

                        String requete = result.getData().getStringExtra("requete");
                        if ( requete.equals("AJOUT")) {
                            adapter.addToken(new Token(actual,0,0, name));
                        }
                        else if ( requete.equals("MODIF"))  {
                            adapter.updateToken( new Token(actual,0,0, name), baseName );
                            formatTokenList();
                        }
                    }
                    else if ( result.getResultCode() == RESULT_CANCELED )
                    {
                        Toast.makeText( TokenList.this , "Opération annulée" , Toast.LENGTH_SHORT).show();
                    }
                }
            } );

    public void launchTokenChoiceIntent( Intent intention )  {
        lanceurActiviteChoixCouleur.launch( intention );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.token_list_activity );

        boolean dbUpToDate = checkDbState();
        if(!dbUpToDate) {
            writeDbState();
            clearDB();
        }
        createAndPopulateDb();

        formatTokenList();
    }

    public void clearDB (){
        DBHelperToken dbHelperToken = new DBHelperToken(TokenList.this);
        SQLiteDatabase db = dbHelperToken.getWritableDatabase();
        db.execSQL("DELETE FROM Token;");
    }

    public void formatTokenList() {
        adapter = new TokenAdapter( this , getTokenFromDB() );
        lvTokenList = findViewById( R.id.lvTokens );
        lvTokenList.setAdapter( adapter );
    }

    private boolean checkDbState() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        return sharedPreferences.getBoolean("dbUpToDate", false);
    }

    private void writeDbState() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dbUpToDate", true);
        editor.commit();
    }

    private String getData(String tokenName) {
        ArrayList<Token> tokens = getTokenFromDB();
        for (int i = 0; i < tokens.size(); i++) {
            if (String.valueOf(tokens.get(i).getName()).equals(tokenName)) {
                getUpdateFromAPI(tokens.get(i));
                return null;
            }
        }
        getDataFromAPI(tokenName);
        return null;
    }

    private void getUpdateFromAPI(Token token) {
        RequestQueue request = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,"https://api.coinbase.com/v2/prices/"+token.getName()+"-EUR/buy" , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    DBHelperToken dbHelperToken = new DBHelperToken(TokenList.this);
                    JSONObject obj = response.getJSONObject("data");
                    float price = Float.parseFloat(obj.getString("amount"));
                    token.setActual(price);
                    if (token.getHigh()<price) {
                        token.setHigh(price);
                    }
                    if (token.getLow()>price) {
                        token.setLow(price);
                    }
                    String[] name = {obj.getString("base")};
                    dbHelperToken.updateTokenByName(token,name);

                } catch (Exception e) {
                    Log.e("Error From API",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error From API",error.getMessage());
            }
        });
        request.add(jsonObjectRequest);
    }

    private void getDataFromAPI(String tokenName) {

        RequestQueue request = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,"https://api.coinbase.com/v2/prices/"+tokenName+"-EUR/buy" , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    DBHelperToken dbHelperToken = new DBHelperToken(TokenList.this);
                    JSONObject obj = response.getJSONObject("data");
                    float price = Float.parseFloat(obj.getString("amount"));
                    Token token = new Token(price,price,price,obj.getString("base"));
                    dbHelperToken.addToken(token);

                } catch (Exception e) {
                    Log.e("Error From API",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error From API",error.getMessage());
            }
        });
        request.add(jsonObjectRequest);
    }

    private void createAndPopulateDb() {
        getData("BTC");
        getData("ETH");
        getData("SOL");
    }

    @SuppressLint("Range")
    private ArrayList<Token> getTokenFromDB() {

        String sqlQuery = "Select * from Token";
        DBHelperToken dbHelperToken = new DBHelperToken(TokenList.this);
        SQLiteDatabase db = dbHelperToken.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        ArrayList<Token> Tokens = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {

            String name = cursor.getString(cursor.getColumnIndex("name"));
            int  high = cursor.getInt(cursor.getColumnIndex("high"));
            int  low = cursor.getInt(cursor.getColumnIndex("low"));
            int  actual = cursor.getInt(cursor.getColumnIndex("actual"));

            Tokens.add( new Token(actual,low,high, name));
        }
        return Tokens;
    }
}
