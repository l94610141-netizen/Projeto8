package com.projeto8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

public class TelaMenu extends View {
    private Paint tinta = new Paint();
    private GerenciadorJogador jogador;
    
    // Botões
    private RectF btnJogar, btnPerfil, btnLoja, btnSair;
    
    // Avatar
    private float avatarX = 100, avatarY = 80, avatarRaio = 45;
    
    private static final int COR_FUNDO = Color.parseColor("#0a0a1a");
    private static final int COR_DOURADO = Color.parseColor("#FFD700");
    private static final int COR_VERDE = Color.parseColor("#2ECC71");
    private static final int COR_AZUL = Color.parseColor("#3498DB");
    private static final int COR_VERMELHO = Color.parseColor("#E74C3C");
    private static final int COR_CINZA = Color.parseColor("#2a2a4a");

    public TelaMenu(Context context) {
        super(context);
        jogador = new GerenciadorJogador(context);
        setBackgroundColor(COR_FUNDO);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        float cx = w / 2f;
        float cy = h / 2f;
        
        float larguraBotao = 220;
        float alturaBotao = 65;
        float inicioY = cy - alturaBotao / 2;
        
        btnJogar = new RectF(cx - 150, inicioY - 80, cx + 150, inicioY - 80 + 80);
        btnPerfil = new RectF(cx - 360, inicioY, cx - 360 + larguraBotao, inicioY + alturaBotao);
        btnLoja = new RectF(cx + 140, inicioY, cx + 140 + larguraBotao, inicioY + alturaBotao);
        btnSair = new RectF(cx - 100, inicioY + 100, cx + 100, inicioY + 100 + alturaBotao);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        desenharFundo(canvas);
        desenharAvatar(canvas);
        desenharInfoTopo(canvas);
        desenharTitulo(canvas);
        desenharBotao(canvas, btnJogar, "🏆 JOGAR", COR_DOURADO, Color.BLACK, true);
        desenharBotao(canvas, btnPerfil, "👤 PERFIL", COR_AZUL, Color.WHITE, false);
        desenharBotao(canvas, btnLoja, "🛒 LOJA", COR_VERDE, Color.BLACK, false);
        desenharBotao(canvas, btnSair, "🚪 SAIR", COR_VERMELHO, Color.WHITE, false);
        
        tinta.setColor(Color.parseColor("#444466"));
        tinta.setTextSize(18);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("v1.0", getWidth() - 80, getHeight() - 30, tinta);
    }
    
    private void desenharFundo(Canvas canvas) {
        tinta.setColor(COR_FUNDO);
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), tinta);
        
        float margem = 40;
        tinta.setColor(Color.parseColor("#1a2a3a"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(margem, margem, getWidth() - margem, getHeight() - margem, 30, 30, tinta);
        
        tinta.setColor(Color.parseColor("#2d7d46"));
        tinta.setStrokeWidth(5);
        tinta.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(margem + 20, margem + 20, getWidth() - margem - 20, getHeight() - margem - 20, 20, 20, tinta);
        
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        tinta.setColor(Color.argb(50, 255, 255, 255));
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(2);
        canvas.drawCircle(cx, cy, 200, tinta);
        canvas.drawCircle(cx, cy, 400, tinta);
    }
    
    private void desenharAvatar(Canvas canvas) {
        // Círculo do avatar
        tinta.setColor(COR_DOURADO);
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawCircle(avatarX, avatarY, avatarRaio, tinta);
        
        tinta.setColor(Color.WHITE);
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(4);
        canvas.drawCircle(avatarX, avatarY, avatarRaio, tinta);
        
        // Emoji do avatar
        tinta.setColor(Color.BLACK);
        tinta.setStyle(Paint.Style.FILL);
        tinta.setTextSize(avatarRaio * 1.2f);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(jogador.getAvatarEmoji(), avatarX, avatarY + avatarRaio * 0.4f, tinta);
        
        // Brilho
        tinta.setColor(Color.argb(100, 255, 255, 255));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawCircle(avatarX - avatarRaio * 0.3f, avatarY - avatarRaio * 0.3f, avatarRaio * 0.3f, tinta);
    }
    
    private void desenharInfoTopo(Canvas canvas) {
        // Nome/Nível
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(22);
        tinta.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Projeto 8", avatarX + avatarRaio + 20, avatarY - 10, tinta);
        
        tinta.setColor(COR_DOURADO);
        tinta.setTextSize(18);
        canvas.drawText("⭐ Nível " + jogador.getNivel(), avatarX + avatarRaio + 20, avatarY + 25, tinta);
        
        // 💵 Dólares e 💎 Diamantes (topo direito)
        String saldo = "💵 " + jogador.getDolares() + "  💎 " + jogador.getDiamantes();
        tinta.setColor(COR_DOURADO);
        tinta.setTextSize(28);
        tinta.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(saldo, getWidth() - 30, 80, tinta);
        
        // XP
        String xpText = "⭐ " + jogador.getXp() + "/" + jogador.getXpProximoNivel() + " XP";
        tinta.setColor(Color.GRAY);
        tinta.setTextSize(18);
        canvas.drawText(xpText, getWidth() - 30, 110, tinta);
    }
    
    private void desenharTitulo(Canvas canvas) {
        float cx = getWidth() / 2f;
        
        tinta.setColor(Color.argb(50, 0, 0, 0));
        tinta.setTextSize(55);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("8 BALL POOL", cx + 3, 190 + 3, tinta);
        
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(55);
        tinta.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("8 BALL POOL", cx, 190, tinta);
        
        tinta.setColor(COR_DOURADO);
        tinta.setTextSize(22);
        tinta.setTypeface(Typeface.DEFAULT);
        canvas.drawText("★ Projeto 8 ★", cx, 225, tinta);
        
        tinta.setColor(COR_DOURADO);
        tinta.setStrokeWidth(2);
        tinta.setStyle(Paint.Style.STROKE);
        canvas.drawLine(cx - 150, 235, cx + 150, 235, tinta);
    }
    
    private void desenharBotao(Canvas canvas, RectF rect, String texto, int cor, int corTexto, boolean destaque) {
        if (destaque) {
            float pulse = (float) (1.0 + 0.03 * Math.sin(System.currentTimeMillis() / 500.0));
            float cx = rect.centerX();
            float cy = rect.centerY();
            float w = rect.width() * pulse;
            float h = rect.height() * pulse;
            rect = new RectF(cx - w/2, cy - h/2, cx + w/2, cy + h/2);
        }
        
        tinta.setColor(Color.argb(80, 0, 0, 0));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect.left + 4, rect.top + 4, rect.right + 4, rect.bottom + 4, 15, 15, tinta);
        
        tinta.setColor(cor);
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect, 15, 15, tinta);
        
        tinta.setColor(Color.argb(100, 255, 255, 255));
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(2);
        canvas.drawRoundRect(rect, 15, 15, tinta);
        
        tinta.setColor(corTexto);
        tinta.setTextSize(28);
        tinta.setTextAlign(Paint.Align.CENTER);
        tinta.setTypeface(destaque ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        canvas.drawText(texto, rect.centerX(), rect.centerY() + 10, tinta);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            float x = e.getX(), y = e.getY();
            
            // Avatar -> Perfil
            float dx = x - avatarX;
            float dy = y - avatarY;
            if (Math.sqrt(dx*dx + dy*dy) < avatarRaio + 20) {
                ((MainActivity) getContext()).trocarTela(new TelaPerfil(getContext()));
                return true;
            }
            
            if (btnJogar != null && btnJogar.contains(x, y)) {
                ((MainActivity) getContext()).trocarTela(new TelaMesas(getContext()));
                return true;
            }
            
            if (btnPerfil != null && btnPerfil.contains(x, y)) {
                ((MainActivity) getContext()).trocarTela(new TelaPerfil(getContext()));
                return true;
            }
            
            if (btnLoja != null && btnLoja.contains(x, y)) {
                android.widget.Toast.makeText(getContext(), "🛒 Loja em breve!", android.widget.Toast.LENGTH_SHORT).show();
                return true;
            }
            
            if (btnSair != null && btnSair.contains(x, y)) {
                android.os.Process.killProcess(android.os.Process.myPid());
                return true;
            }
        }
        return true;
    }
}
