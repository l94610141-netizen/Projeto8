package com.projeto8;

import android.content.Context;
import android.content.SharedPreferences;

public class GerenciadorJogador {
    private static final String PREF = "projeto8_jogador";
    private static final String KEY_NIVEL = "nivel";
    private static final String KEY_XP = "xp";
    private static final String KEY_PARTIDAS = "partidas";
    private static final String KEY_VITORIAS = "vitorias";
    
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    
    public GerenciadorJogador(Context context) {
        prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        editor = prefs.edit();
        
        if (prefs.getInt(KEY_NIVEL, -1) == -1) {
            editor.putInt(KEY_NIVEL, 1);
            editor.putInt(KEY_XP, 0);
            editor.putInt(KEY_PARTIDAS, 0);
            editor.putInt(KEY_VITORIAS, 0);
            editor.apply();
        }
    }
    
    public int getNivel() { return prefs.getInt(KEY_NIVEL, 1); }
    public int getXp() { return prefs.getInt(KEY_XP, 0); }
    public int getPartidas() { return prefs.getInt(KEY_PARTIDAS, 0); }
    public int getVitorias() { return prefs.getInt(KEY_VITORIAS, 0); }
    
    public int getXpProximoNivel() {
        int nivel = getNivel();
        return nivel * 100;
    }
    
    public int getProgresso() {
        int xp = getXp();
        int xpMax = getXpProximoNivel();
        return (int)((float)xp / xpMax * 100);
    }
    
    public void addXp(int quantidade) {
        int xpAtual = getXp();
        int nivel = getNivel();
        
        xpAtual += quantidade;
        int xpNecessario = getXpProximoNivel();
        
        while (xpAtual >= xpNecessario) {
            xpAtual -= xpNecessario;
            nivel++;
            xpNecessario = nivel * 100;
        }
        
        editor.putInt(KEY_NIVEL, nivel);
        editor.putInt(KEY_XP, xpAtual);
        editor.apply();
    }
    
    public void registrarPartida(boolean venceu) {
        int partidas = getPartidas() + 1;
        editor.putInt(KEY_PARTIDAS, partidas);
        
        if (venceu) {
            int vitorias = getVitorias() + 1;
            editor.putInt(KEY_VITORIAS, vitorias);
        }
        editor.apply();
    }
}
