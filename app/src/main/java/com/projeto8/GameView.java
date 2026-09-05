package com.projeto8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class GameView extends View {
    private Paint tinta = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint tintaTaco = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint tintaMira = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GerenciadorJogador jogador;
    private String nomeCidade;
    
    // ============ CLASSE BOLA ============
    class Bola {
        float x, y, raio, vx, vy;
        int cor, numero;
        boolean branca;
        Paint paintBola = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint paintBrilho = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint paintNumero = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint paintSombra = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        Bola(float x, float y, float raio, int cor, int numero, boolean branca) {
            this.x = x;
            this.y = y;
            this.raio = raio;
            this.cor = cor;
            this.numero = numero;
            this.branca = branca;
            this.vx = 0;
            this.vy = 0;
            
            paintBola.setAntiAlias(true);
            paintBola.setStyle(Paint.Style.FILL);
            paintBrilho.setAntiAlias(true);
            paintBrilho.setStyle(Paint.Style.FILL);
            paintBrilho.setColor(Color.argb(80, 255, 255, 255));
            paintNumero.setAntiAlias(true);
            paintNumero.setStyle(Paint.Style.FILL);
            paintNumero.setColor(Color.WHITE);
            paintNumero.setTextAlign(Paint.Align.CENTER);
            paintSombra.setAntiAlias(true);
            paintSombra.setStyle(Paint.Style.FILL);
        }
        
        void atualizar() {
            x += vx;
            y += vy;
            vx *= 0.985f;
            vy *= 0.985f;
            if (Math.abs(vx) < 0.01f) vx = 0;
            if (Math.abs(vy) < 0.01f) vy = 0;
        }
        
        void desenhar(Canvas canvas) {
            paintSombra.setColor(Color.argb(60, 0, 0, 0));
            canvas.drawCircle(x + 3, y + 4, raio, paintSombra);
            
            if (branca) {
                RadialGradient gradiente = new RadialGradient(
                    x - raio * 0.3f, y - raio * 0.3f, raio * 1.5f,
                    Color.WHITE, Color.parseColor("#CCCCCC"), Shader.TileMode.CLAMP
                );
                paintBola.setShader(gradiente);
                canvas.drawCircle(x, y, raio, paintBola);
                paintBola.setShader(null);
                paintBola.setColor(Color.BLACK);
                paintBola.setStyle(Paint.Style.STROKE);
                paintBola.setStrokeWidth(2);
                canvas.drawCircle(x, y, raio, paintBola);
                paintBola.setStyle(Paint.Style.FILL);
            } else {
                RadialGradient gradiente = new RadialGradient(
                    x - raio * 0.3f, y - raio * 0.3f, raio * 1.5f,
                    clarearCor(cor, 80), escurecerCor(cor, 80), Shader.TileMode.CLAMP
                );
                paintBola.setShader(gradiente);
                canvas.drawCircle(x, y, raio, paintBola);
                paintBola.setShader(null);
                paintBola.setColor(escurecerCor(cor, 120));
                paintBola.setStyle(Paint.Style.STROKE);
                paintBola.setStrokeWidth(1.5f);
                canvas.drawCircle(x, y, raio, paintBola);
                paintBola.setStyle(Paint.Style.FILL);
            }
            
            paintBrilho.setColor(Color.argb(120, 255, 255, 255));
            canvas.drawCircle(x - raio * 0.3f, y - raio * 0.35f, raio * 0.4f, paintBrilho);
            paintBrilho.setColor(Color.argb(60, 255, 255, 255));
            canvas.drawCircle(x - raio * 0.15f, y - raio * 0.5f, raio * 0.25f, paintBrilho);
            
            if (!branca && numero > 0) {
                paintNumero.setColor(Color.BLACK);
                paintNumero.setAlpha(100);
                paintNumero.setTextSize(raio * 1.0f);
                canvas.drawText(String.valueOf(numero), x + 1, y + raio * 0.35f + 1, paintNumero);
                paintNumero.setColor(Color.WHITE);
                paintNumero.setAlpha(255);
                paintNumero.setTextSize(raio * 1.0f);
                canvas.drawText(String.valueOf(numero), x, y + raio * 0.35f, paintNumero);
            }
        }
        
        private int clarearCor(int cor, int q) {
            return Color.rgb(
                Math.min(255, Color.red(cor) + q),
                Math.min(255, Color.green(cor) + q),
                Math.min(255, Color.blue(cor) + q)
            );
        }
        private int escurecerCor(int cor, int q) {
            return Color.rgb(
                Math.max(0, Color.red(cor) - q),
                Math.max(0, Color.green(cor) - q),
                Math.max(0, Color.blue(cor) - q)
            );
        }
        boolean colidiu(Bola outra) {
            float dx = x - outra.x, dy = y - outra.y;
            return Math.sqrt(dx*dx + dy*dy) < raio + outra.raio;
        }
    }
    
    // ============ VARIÁVEIS DO JOGO ============
    ArrayList<Bola> bolas = new ArrayList<>();
    Bola bolaBranca;
    int bolasAfundadas = 0;
    boolean jogoFinalizado = false, venceu = false, breakFeito = false;
    float startX = -1, startY = -1;
    boolean arrastando = false;
    float potencia = 0;
    
    // 6 CAÇAPAS
    float[][] cacapas = new float[6][2];
    float raioCacapa = 30;
    
    public GameView(Context context, String cidade) {
        super(context);
        this.nomeCidade = cidade;
        this.jogador = new GerenciadorJogador(context);
        iniciarJogo();
    }
    
    private void iniciarJogo() {
        bolas.clear();
        bolasAfundadas = 0;
        jogoFinalizado = false;
        venceu = false;
        breakFeito = false;
        
        float w = getWidth();
        float h = getHeight();
        float cx = w - 200;
        float cy = h / 2;
        
        // Bola branca
        bolaBranca = new Bola(250, cy, 14, Color.WHITE, 0, true);
        bolas.add(bolaBranca);
        
        // Formação triangular
        float e = 25;
        float[][] pos = {
            {cx, cy}, {cx+e, cy-13}, {cx+e, cy+13},
            {cx+e*2, cy-26}, {cx+e*2, cy}, {cx+e*2, cy+26},
            {cx+e*3, cy-39}, {cx+e*3, cy-13}, {cx+e*3, cy+13}, {cx+e*3, cy+39},
            {cx+e*4, cy-52}, {cx+e*4, cy-26}, {cx+e*4, cy}, {cx+e*4, cy+26}, {cx+e*4, cy+52}
        };
        int[][] config = {
            {1, Color.YELLOW}, {2, Color.BLUE}, {3, Color.RED}, {4, Color.MAGENTA},
            {5, Color.parseColor("#FF8800")}, {6, Color.GREEN}, {7, Color.parseColor("#8B0000")},
            {8, Color.BLACK}, {9, Color.YELLOW}, {10, Color.BLUE},
            {11, Color.RED}, {12, Color.MAGENTA}, {13, Color.parseColor("#FF8800")},
            {14, Color.GREEN}, {15, Color.parseColor("#8B0000")}
        };
        for (int i = 0; i < 15; i++) {
            bolas.add(new Bola(pos[i][0], pos[i][1], 13, config[i][1], config[i][0], false));
        }
        
        // 6 CAÇAPAS
        float m = 50;
        cacapas[0] = new float[]{m, m};           // Canto superior esquerdo
        cacapas[1] = new float[]{w/2, m};         // Meio superior
        cacapas[2] = new float[]{w - m, m};       // Canto superior direito
        cacapas[3] = new float[]{m, h - m};       // Canto inferior esquerdo
        cacapas[4] = new float[]{w/2, h - m};     // Meio inferior
        cacapas[5] = new float[]{w - m, h - m};   // Canto inferior direito
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#1a1a2e"));
        
        desenharCabecalho(canvas);
        
        float m = 50, w = getWidth(), h = getHeight();
        
        // Mesa
        tinta.setColor(Color.parseColor("#2d7d46"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRect(m, 50, w - m, h - 50, tinta);
        tinta.setColor(Color.parseColor("#8B4513"));
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(12);
        canvas.drawRect(m, 50, w - m, h - 50, tinta);
        
        // 6 Caçapas
        tinta.setColor(Color.BLACK);
        tinta.setStyle(Paint.Style.FILL);
        for (float[] c : cacapas) {
            canvas.drawCircle(c[0], c[1], raioCacapa, tinta);
        }
        
        if (!jogoFinalizado) atualizarFisica();
        
        // Desenhar bolas
        for (Bola b : bolas) b.desenhar(canvas);
        
        // ===== MIRA (LINHA PONTILHADA) =====
        if (!jogoFinalizado && !breakFeito) {
            desenharMira(canvas);
        }
        
        // ===== TACO =====
        if (!jogoFinalizado) {
            float angulo = 0;
            if (arrastando && startX != -1) {
                float dx = startX - bolaBranca.x;
                float dy = startY - bolaBranca.y;
                angulo = (float) Math.atan2(dy, dx);
                potencia = Math.min((float) Math.sqrt(dx*dx + dy*dy) / 15, 15);
            }
            desenharTaco(canvas, bolaBranca.x, bolaBranca.y, angulo);
        }
        
        // ===== BARRA DE FORÇA =====
        if (arrastando && startX != -1 && !jogoFinalizado) {
            desenharBarraForca(canvas);
        }
        
        // Mensagem Break
        if (!breakFeito && !jogoFinalizado) {
            tinta.setColor(Color.argb(150, 255, 255, 255));
            tinta.setTextSize(45);
            tinta.setTextAlign(Paint.Align.CENTER);
            tinta.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            canvas.drawText("🔥 FAÇA O BREAK!", w/2, 130, tinta);
        }
        
        if (jogoFinalizado) desenharFimJogo(canvas);
        
        postInvalidateDelayed(16);
    }
    
    // ============================================================
    // 🎯 MIRA (LINHA PONTILHADA)
    // ============================================================
    private void desenharMira(Canvas canvas) {
        if (bolaBranca.vx != 0 || bolaBranca.vy != 0) return;
        
        float angulo = 0;
        if (arrastando && startX != -1 && startY != -1) {
            angulo = (float) Math.atan2(startY - bolaBranca.y, startX - bolaBranca.x);
        } else {
            angulo = 0;
        }
        
        tintaMira.setColor(Color.argb(150, 255, 255, 255));
        tintaMira.setStyle(Paint.Style.STROKE);
        tintaMira.setStrokeWidth(2);
        
        // Linha pontilhada
        float comprimento = 200;
        float passo = 15;
        float espaco = 10;
        float x = bolaBranca.x, y = bolaBranca.y;
        float total = 0;
        
        while (total < comprimento) {
            float dx = (float) Math.cos(angulo) * passo;
            float dy = (float) Math.sin(angulo) * passo;
            x += dx;
            y += dy;
            total += passo;
            if (total % (passo + espaco) < passo) {
                canvas.drawLine(x, y, x + dx * 0.5f, y + dy * 0.5f, tintaMira);
            }
        }
    }
    
    // ============================================================
    // 🏒 TACO DE MADEIRA
    // ============================================================
    private void desenharTaco(Canvas canvas, float cx, float cy, float angulo) {
        float tamanho = 280 - potencia * 5;
        float espessura = 14;
        float dist = 35 + potencia * 4;
        
        float startX = cx - (float) Math.cos(angulo) * dist;
        float startY = cy - (float) Math.sin(angulo) * dist;
        float endX = startX - (float) Math.cos(angulo) * tamanho;
        float endY = startY - (float) Math.sin(angulo) * tamanho;
        
        canvas.save();
        canvas.translate(startX, startY);
        canvas.rotate((float) Math.toDegrees(angulo) + 180);
        
        // Sombra
        tintaTaco.setColor(Color.argb(60, 0, 0, 0));
        tintaTaco.setStyle(Paint.Style.STROKE);
        tintaTaco.setStrokeWidth(espessura + 8);
        tintaTaco.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(0, 4, -tamanho, 4, tintaTaco);
        
        // Corpo do taco (madeira)
        LinearGradient grad = new LinearGradient(
            0, -espessura/2, -tamanho, espessura/2,
            new int[]{
                Color.parseColor("#D4A574"),
                Color.parseColor("#C4956A"),
                Color.parseColor("#B8860B"),
                Color.parseColor("#D4A574")
            },
            new float[]{0f, 0.3f, 0.6f, 1f},
            Shader.TileMode.CLAMP
        );
        tintaTaco.setShader(grad);
        tintaTaco.setStyle(Paint.Style.STROKE);
        tintaTaco.setStrokeWidth(espessura);
        tintaTaco.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(0, 0, -tamanho, 0, tintaTaco);
        tintaTaco.setShader(null);
        
        // Ponta do taco (branca)
        float pontaSize = 20;
        tintaTaco.setColor(Color.parseColor("#F5F5F5"));
        tintaTaco.setStrokeWidth(espessura * 0.5f);
        canvas.drawLine(-pontaSize, 0, 0, 0, tintaTaco);
        
        // Anéis decorativos
        int[][] aneis = {{30, Color.parseColor("#8B6914")}, {80, Color.parseColor("#8B6914")}};
        for (int[] a : aneis) {
            tintaTaco.setColor(a[1]);
            tintaTaco.setStrokeWidth(2);
            canvas.drawLine(-a[0], -espessura/2 - 2, -a[0], espessura/2 + 2, tintaTaco);
        }
        
        // Empunhadura (grip)
        float gripSize = tamanho * 0.25f;
        tintaTaco.setColor(Color.parseColor("#5D4037"));
        tintaTaco.setStrokeWidth(espessura * 0.6f);
        canvas.drawLine(-tamanho, 0, -tamanho + gripSize, 0, tintaTaco);
        
        // Detalhes da empunhadura
        for (int i = 0; i < 6; i++) {
            float pos = -tamanho + gripSize * (i + 1) / 7;
            tintaTaco.setColor(Color.argb(60, 0, 0, 0));
            tintaTaco.setStrokeWidth(2);
            canvas.drawLine(pos, -espessura/2, pos, espessura/2, tintaTaco);
        }
        
        canvas.restore();
    }
    
    // ============================================================
    // 📊 BARRA DE FORÇA
    // ============================================================
    private void desenharBarraForca(Canvas canvas) {
        float x = 50, y = getHeight() - 180;
        float largura = 35, altura = 150;
        
        // Fundo da barra
        tinta.setColor(Color.parseColor("#2a2a4a"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(x, y, x + largura, y + altura, 10, 10, tinta);
        
        // Preenchimento da força
        int cor = potencia > 10 ? Color.RED : potencia > 6 ? Color.YELLOW : Color.GREEN;
        float preenchimento = (potencia / 15) * altura;
        tinta.setColor(cor);
        canvas.drawRoundRect(x + 3, y + altura - preenchimento + 3, x + largura - 3, y + altura - 3, 8, 8, tinta);
        
        // Borda
        tinta.setColor(Color.WHITE);
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(2);
        canvas.drawRoundRect(x, y, x + largura, y + altura, 10, 10, tinta);
        
        // Texto da potência
        tinta.setColor(Color.WHITE);
        tinta.setStyle(Paint.Style.FILL);
        tinta.setTextSize(14);
        tinta.setTextAlign(Paint.Align.CENTER);
        int percentual = (int)((potencia / 15) * 100);
        canvas.drawText(percentual + "%", x + largura/2, y + altura + 25, tinta);
        
        // Label
        tinta.setTextSize(12);
        tinta.setColor(Color.GRAY);
        canvas.drawText("FORÇA", x + largura/2, y - 10, tinta);
    }
    
    // ============================================================
    // 🧠 FÍSICA
    // ============================================================
    private void atualizarFisica() {
        float m = 50, w = getWidth(), h = getHeight();
        
        boolean todasParadas = true;
        for (Bola b : bolas) {
            if (b.vx != 0 || b.vy != 0) { todasParadas = false; break; }
        }
        if (!breakFeito && !todasParadas) breakFeito = true;
        
        for (Bola b : bolas) {
            b.atualizar();
            
            if (b.x - b.raio < m) { b.x = m + b.raio; b.vx = -b.vx * 0.8f; }
            if (b.x + b.raio > w - m) { b.x = w - m - b.raio; b.vx = -b.vx * 0.8f; }
            if (b.y - b.raio < 50) { b.y = 50 + b.raio; b.vy = -b.vy * 0.8f; }
            if (b.y + b.raio > h - 50) { b.y = h - 50 - b.raio; b.vy = -b.vy * 0.8f; }
            
            // Verificar 6 caçapas
            for (float[] c : cacapas) {
                float dx = b.x - c[0], dy = b.y - c[1];
                if (Math.sqrt(dx*dx + dy*dy) < raioCacapa) {
                    if (b.branca) {
                        b.x = 250; b.y = h/2; b.vx = 0; b.vy = 0;
                    } else {
                        bolas.remove(b);
                        bolasAfundadas++;
                        if (bolasAfundadas >= 15) {
                            jogoFinalizado = true; venceu = true;
                            jogador.addXp(100);
                            jogador.registrarPartida(true);
                        }
                        return;
                    }
                }
            }
        }
        
        // Colisões entre bolas
        for (int i = 0; i < bolas.size(); i++) {
            for (int j = i+1; j < bolas.size(); j++) {
                Bola a = bolas.get(i), b = bolas.get(j);
                if (a.colidiu(b)) {
                    float dx = a.x - b.x, dy = a.y - b.y;
                    float dist = (float) Math.sqrt(dx*dx + dy*dy);
                    if (dist == 0) continue;
                    
                    float overlap = (a.raio + b.raio - dist) / 2;
                    a.x += (dx/dist) * overlap;
                    a.y += (dy/dist) * overlap;
                    b.x -= (dx/dist) * overlap;
                    b.y -= (dy/dist) * overlap;
                    
                    float dvx = a.vx - b.vx, dvy = a.vy - b.vy;
                    float dvn = dvx * (dx/dist) + dvy * (dy/dist);
                    if (dvn > 0) continue;
                    
                    float impulso = dvn;
                    a.vx -= impulso * (dx/dist);
                    a.vy -= impulso * (dy/dist);
                    b.vx += impulso * (dx/dist);
                    b.vy += impulso * (dy/dist);
                }
            }
        }
        
        // Verificar derrota
        todasParadas = true;
        for (Bola b : bolas) {
            if (b.vx != 0 || b.vy != 0) { todasParadas = false; break; }
        }
        if (todasParadas && bolas.size() == 1 && bolas.get(0).branca && bolasAfundadas < 15) {
            jogoFinalizado = true; venceu = false;
            jogador.addXp(20);
            jogador.registrarPartida(false);
        }
    }
    
    // ============================================================
    // 🎨 CABEÇALHO E FIM DE JOGO
    // ============================================================
    private void desenharCabecalho(Canvas canvas) {
        float ax = 90, ay = 40, ar = 30;
        tinta.setColor(Color.parseColor("#FFD700"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawCircle(ax, ay, ar, tinta);
        tinta.setColor(Color.BLACK);
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(3);
        canvas.drawCircle(ax, ay, ar, tinta);
        tinta.setColor(Color.BLACK);
        tinta.setStyle(Paint.Style.FILL);
        tinta.setTextSize(35);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(jogador.getAvatarEmoji(), ax, ay + 12, tinta);
        
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(18);
        tinta.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("⭐ Nível " + jogador.getNivel(), ax + ar + 15, ay - 5, tinta);
        tinta.setColor(Color.GRAY);
        tinta.setTextSize(14);
        canvas.drawText(jogador.getXp() + "/" + jogador.getXpProximoNivel() + " XP", ax + ar + 15, ay + 18, tinta);
        
        tinta.setColor(Color.parseColor("#FFD700"));
        tinta.setTextSize(18);
        tinta.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("💵 " + jogador.getDolares() + "  💎 " + jogador.getDiamantes(), getWidth() - 20, 30, tinta);
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(16);
        canvas.drawText("🎯 " + bolasAfundadas + "/15", getWidth() - 20, 55, tinta);
    }
    
    private void desenharFimJogo(Canvas canvas) {
        tinta.setColor(Color.argb(200, 0, 0, 0));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), tinta);
        
        tinta.setColor(venceu ? Color.parseColor("#FFD700") : Color.RED);
        tinta.setTextSize(60);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(venceu ? "🏆 VITÓRIA!" : "💔 DERROTA", getWidth()/2, getHeight()/2 - 50, tinta);
        
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(25);
        canvas.drawText("⭐ +" + (venceu ? 100 : 20) + " XP", getWidth()/2, getHeight()/2 + 20, tinta);
        
        if (venceu) {
            tinta.setColor(Color.parseColor("#FFD700"));
            tinta.setTextSize(22);
            canvas.drawText("💵 +100  💎 +1", getWidth()/2, getHeight()/2 + 55, tinta);
        }
        
        float bx = getWidth()/2 - 120, by = getHeight()/2 + 90;
        tinta.setColor(Color.parseColor("#2ECC71"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(bx, by, bx + 240, by + 60, 20, 20, tinta);
        tinta.setColor(Color.BLACK);
        tinta.setTextSize(25);
        canvas.drawText("🔄 JOGAR NOVAMENTE", getWidth()/2, by + 40, tinta);
        
        by = getHeight()/2 + 170;
        tinta.setColor(Color.parseColor("#333366"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(bx, by, bx + 240, by + 60, 20, 20, tinta);
        tinta.setColor(Color.WHITE);
        canvas.drawText("🏠 MENU", getWidth()/2, by + 40, tinta);
    }

    // ============================================================
    // 👆 EVENTOS DE TOQUE
    // ============================================================
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (jogoFinalizado) {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                float x = e.getX(), y = e.getY();
                float bx = getWidth()/2 - 120, by;
                
                by = getHeight()/2 + 90;
                if (x > bx && x < bx + 240 && y > by && y < by + 60) {
                    iniciarJogo();
                    invalidate();
                    return true;
                }
                by = getHeight()/2 + 170;
                if (x > bx && x < bx + 240 && y > by && y < by + 60) {
                    ((MainActivity) getContext()).trocarTela(new TelaMenu(getContext()));
                    return true;
                }
            }
            return true;
        }
        
        float x = e.getX(), y = e.getY();
        
        // Avatar -> Perfil
        float ax = 90, ay = 40, ar = 30;
        float dx = x - ax, dy = y - ay;
        if (e.getAction() == MotionEvent.ACTION_UP && Math.sqrt(dx*dx + dy*dy) < ar + 10) {
            ((MainActivity) getContext()).trocarTela(new TelaPerfil(getContext()));
            return true;
        }
        
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dx = x - bolaBranca.x;
                dy = y - bolaBranca.y;
                if (Math.sqrt(dx*dx + dy*dy) < 50 && bolaBranca.vx == 0 && bolaBranca.vy == 0) {
                    arrastando = true;
                    startX = x;
                    startY = y;
                    potencia = 0;
                }
                return true;
                
            case MotionEvent.ACTION_MOVE:
                if (arrastando) {
                    startX = x;
                    startY = y;
                    float dx2 = startX - bolaBranca.x;
                    float dy2 = startY - bolaBranca.y;
                    potencia = Math.min((float) Math.sqrt(dx2*dx2 + dy2*dy2) / 15, 15);
                    invalidate();
                }
                return true;
                
            case MotionEvent.ACTION_UP:
                if (arrastando) {
                    float dx2 = startX - bolaBranca.x;
                    float dy2 = startY - bolaBranca.y;
                    float dist = (float) Math.sqrt(dx2*dx2 + dy2*dy2);
                    if (dist > 20 && potencia > 0.5f) {
                        float p = Math.min(dist / 15, 15);
                        bolaBranca.vx = (dx2 / dist) * p;
                        bolaBranca.vy = (dy2 / dist) * p;
                    }
                    arrastando = false;
                    startX = -1;
                    startY = -1;
                    potencia = 0;
                }
                return true;
        }
        return true;
    }
}
