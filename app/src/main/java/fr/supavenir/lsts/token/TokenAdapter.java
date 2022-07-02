package fr.supavenir.lsts.token;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TokenAdapter extends BaseAdapter {
    private TokenList context;
    private TokenListModel modele = new TokenListModel();
    private int actualPosition = 0;

    public int getActualPosition() {
        return this.actualPosition;
    }

    public TokenAdapter(Context context , ArrayList<Token> tokens ) {
        this.context = (TokenList) context;
        modele.setTokens( tokens );
    }

    public void addToken( Token token) {

        DBHelperToken dbHelperToken = new DBHelperToken(context);
        dbHelperToken.addToken(token);
        this.notifyDataSetChanged();
    }

    public void deleteToken( String name ) {

        DBHelperToken dbHelperToken = new DBHelperToken(context);
        dbHelperToken.deleteTokenByName(name);
        this.notifyDataSetChanged();
    }

    public void updateToken( Token token, String name ) {

        String[] tokenName = new String[] {name};

        DBHelperToken dbHelperToken = new DBHelperToken(context);
        dbHelperToken.updateTokenByName(token, tokenName);
        this.notifyDataSetChanged();
    }

    /** On adapte les methodes pour visualiser le modèle en mémoire. */

    @Override
    public int getCount() {
        return modele.getTokens().size();
    }

    @Override
    public Object getItem(int position) {
        return modele.getTokens().get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = LayoutInflater.from( context ).inflate(R.layout.token_list_item,
                parent , false );
        TextView tokenActualPrice = itemView.findViewById(R.id.tokenActualPrice);
        TextView tvTokenName = itemView.findViewById( R.id.tvTokenName );
        TextView low = itemView.findViewById( R.id.low );
        TextView high = itemView.findViewById( R.id.high );

        Token token = modele.getTokens().get( position );
        tvTokenName.setText( token.getName() );
        tokenActualPrice.setText(String.valueOf(token.getActual()));
        low.setText(String.valueOf(token.getLow()));
        high.setText(String.valueOf(token.getHigh()));
        return itemView;
    }
}
