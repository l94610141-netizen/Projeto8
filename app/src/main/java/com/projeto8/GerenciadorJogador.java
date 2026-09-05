package com.projeto8;

import android.content.Context;
import android.content.SharedPreferences;

public class GerenciadorJogador {
    private static final String PREF = "projeto8_jogador";
    private static final String KEY_NIVEL = "nivel";
    private static final String KEY_XP = "xp";
    private static final String KEY_PARTIDAS = "partidas";
    private static final String KEY_VITORIAS = "vitorias";
    private static final String KEY_XP_TOTAL = "xp_total";
    
    // 💵 Dólares e 💎 Diamantes
    private static final String KEY_DOLARES = "dolares";
    private static final String KEY_DIAMANTES = "diamantes";
    
    // 🎱 Avatar
    private static final String KEY_AVATAR = "avatar";
    
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    
    // Emojis para avatares
    private String[] avatares = {"🎱", "🏆", "⭐", "🎯", "🔥", "💪", "👑", "🌟", "🚀", "🎮"};
    
    public GerenciadorJogador(Context context) {
        prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        editor = prefs.edit();
        
        if (prefs.getInt(KEY_NIVEL, -1) == -1) {
            editor.putInt(KEY_NIVEL, 1);
            editor.putInt(KEY_XP, 0);
            editor.putInt(KEY_XP_TOTAL, 0);
            editor.putInt(KEY_PARTIDAS, 0);
            editor.putInt(KEY_VITORIAS, 0);
            editor.putInt(KEY_DOLARES, 1000);
            editor.putInt(KEY_DIAMANTES, 10);
            editor.putInt(KEY_AVATAR, 0);
            editor.apply();
        }
    }
    
    // ============ GETTERS ============
    public int getNivel() { return prefs.getInt(KEY_NIVEL, 1); }
    public int getXp() { return prefs.getInt(KEY_XP, 0); }
    public int getXpTotal() { return prefs.getInt(KEY_XP_TOTAL, 0); }
    public int getPartidas() { return prefs.getInt(KEY_PARTIDAS, 0); }
    public int getVitorias() { return prefs.getInt(KEY_VITORIAS, 0); }
    
    public int getDolares() { return prefs.getInt(KEY_DOLARES, 1000); }
    public int getDiamantes() { return prefs.getInt(KEY_DIAMANTES, 10); }
    public int getAvatar() { return prefs.getInt(KEY_AVATAR, 0); }
    public String getAvatarEmoji() { return avatares[getAvatar()]; }
    
    public int getXpProximoNivel() {
        int nivel = getNivel();
        return nivel * 100;
    }
    
    public float getProgresso() {
        int xp = getXp();
        int xpMax = getXpProximoNivel();
        return (float)xp / xpMax;
    }
    
    // ============ DÓLARES 💵 ============
    public void addDolares(int quantidade) {
        int atual = getDolares();
        editor.putInt(KEY_DOLARES, atual + quantidade);
        editor.apply();
    }
    
    public boolean gastarDolares(int quantidade) {
        if (getDolares() >= quantidade) {
            editor.putInt(KEY_DOLARES, getDolares() - quantidade);
            editor.apply();
            return true;
        }
        return false;
    }
    
    // ============ DIAMANTES 💎 ============
    public void addDiamantes(int quantidade) {
        int atual = getDiamantes();
        editor.putInt(KEY_DIAMANTES, atual + quantidade);
        editor.apply();
    }
    
    public boolean gastarDiamantes(int quantidade) {
        if (getDiamantes() >= quantidade) {
            editor.putInt(KEY_DIAMANTES, getDiamantes() - quantidade);
            editor.apply();
            return true;
        }
        return false;
    }
    
    // ============ AVATAR ============
    public void setAvatar(int index) {
        if (index >= 0 && index < avatares.length) {
            editor.putInt(KEY_AVATAR, index);
            editor.apply();
        }
    }
    
    public void proximoAvatar() {
        int atual = getAvatar();
        setAvatar((atual + 1) % avatares.length);
    }
    
    // ============ XP E NÍVEL ============
    public void addXp(int quantidade) {
        int xpAtual = getXp();
        int nivel = getNivel();
        int xpTotal = getXpTotal() + quantidade;
        
        xpAtual += quantidade;
        int xpNecessario = getXpProximoNivel();
        
        while (xpAtual >= xpNecessario) {
            xpAtual -= xpNecessario;
            nivel++;
            xpNecessario = nivel * 100;
        }
        
        editor.putInt(KEY_NIVEL, nivel);
        editor.putInt(KEY_XP, xpAtual);
        editor.putInt(KEY_XP_TOTAL, xpTotal);
        editor.apply();
    }
    
    public void registrarPartida(boolean venceu) {
        int partidas = getPartidas() + 1;
        editor.putInt(KEY_PARTIDAS, partidas);
        
        if (venceu) {
            int vitorias = getVitorias() + 1;
            editor.putInt(KEY_VITORIAS, vitorias);
            
            // 💵 Recompensa por vitória
            addDolares(100);
            
            // 💎 Bônus a cada 5 vitórias
            if (vitorias % 5 == 0) {
                addDiamantes(1);
            }
        }
        editor.apply();
    }
    
    // ============ RESET PARA TESTES ============
    public void resetar() {
        editor.putInt(KEY_NIVEL, 1);
        editor.putInt(KEY_XP, 0);
        editor.putInt(KEY_XP_TOTAL, 0);
        editor.putInt(KEY_PARTIDAS, 0);
        editor.putInt(KEY_VITORIAS, 0);
        editor.putInt(KEY_DOLARES, 1000);
        editor.putInt(KEY_DIAMANTES, 10);
        editor.putInt(KEY_AVATAR, 0);
        editor.apply();
    }
}
