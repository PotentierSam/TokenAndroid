package fr.supavenir.lsts.token;

import java.util.ArrayList;

public class TokenListModel {
    private ArrayList<Token> tokens;

    public TokenListModel() {
        tokens = new ArrayList<Token>();
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void setTokens( ArrayList<Token> Tokens ) {
        this.tokens = Tokens;
    }

    public void addToken( Token token ) {
        tokens.add( token );
    }

    public void deleteToken( int position )
    { tokens.remove( position ); }

    public void updateToken( int position , Token token )
    { tokens.set( position , token ); }
}
