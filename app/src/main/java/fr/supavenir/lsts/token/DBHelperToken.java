package fr.supavenir.lsts.token;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelperToken extends SQLiteOpenHelper {

    private final static int dbVersion = 1;
    private final static String dbName="TokenDB";

    public DBHelperToken(Context context) {
        super(context, dbName, null, dbVersion);
    }


    public void deleteTokenByName( String name ) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        String[] args = { name };
        db.delete( "Token" ,"name=?", args );
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void updateTokenByName(Token token, String[] name) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", token.getName() );
        contentValues.put("high", token.getHigh() );
        contentValues.put("low", token.getLow() );
        contentValues.put("actual", token.getActual() );

        db.update("Token", contentValues, "name=?", name);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void addToken(Token token)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", token.getName() );
        contentValues.put("high", token.getHigh() );
        contentValues.put("low", token.getLow() );
        contentValues.put("actual", token.getActual() );

        db.insert("Token", null, contentValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Token (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, high FLOAT," +
                " low FLOAT, actual FLOAT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int indexVersion = oldVersion; indexVersion < newVersion;
             indexVersion++) {
            int nextVersion = indexVersion + 1;
            switch (nextVersion) {
                case 2:
                    // upgrapdeToVersion2(db);
                    break;
                case 3:
                    // mise à jour future pour la version 3
                    break;
            }

        }
    }
}
