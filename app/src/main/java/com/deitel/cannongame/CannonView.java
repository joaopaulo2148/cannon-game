// CannonView.java
// Exibe e controla o Cannon Game
package com.deitel.cannongame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class CannonView extends SurfaceView
   implements SurfaceHolder.Callback {

   private static final String TAG = "CannonView"; // para logs de erro

   // constantes do jogo
   public static final int MISS_PENALTY = 2; // segundos descontados ao errar
   public static final int HIT_REWARD = 3; // segundos somados ao acertar

   // constantes do Cannon
   public static final double CANNON_BASE_RADIUS_PERCENT = 3.0 / 40;
   public static final double CANNON_BARREL_WIDTH_PERCENT = 3.0 / 40;
   public static final double CANNON_BARREL_LENGTH_PERCENT = 1.0 / 10;

   // constantes da Cannonball
   public static final double CANNONBALL_RADIUS_PERCENT = 3.0 / 80;
   public static final double CANNONBALL_SPEED_PERCENT = 3.0 / 2;

   // constantes dos Targets
   public static final double TARGET_WIDTH_PERCENT = 1.0 / 40;
   public static final double TARGET_LENGTH_PERCENT = 3.0 / 20;
   public static final double TARGET_FIRST_X_PERCENT = 3.0 / 5;
   public static final double TARGET_SPACING_PERCENT = 1.0 / 60;
   public static final double TARGET_PIECES = 9;
   public static final double TARGET_MIN_SPEED_PERCENT = 3.0 / 4;
   public static final double TARGET_MAX_SPEED_PERCENT = 6.0 / 4;

   // constantes do Blocker
   public static final double BLOCKER_WIDTH_PERCENT = 1.0 / 40;
   public static final double BLOCKER_LENGTH_PERCENT = 1.0 / 4;
   public static final double BLOCKER_X_PERCENT = 1.0 / 2;
   public static final double BLOCKER_SPEED_PERCENT = 1.0;

   // tamanho do texto: 1/18 da altura da tela
   public static final double TEXT_SIZE_PERCENT = 1.0 / 18;

   private CannonThread cannonThread; // controla o game loop
   private Activity activity; // para exibir o dialog na thread da GUI
   private boolean dialogIsDisplayed = false;

   // objetos do jogo
   private Cannon cannon;
   private Blocker blocker;
   private ArrayList<Target> targets;

   // dimensões
   private int screenWidth;
   private int screenHeight;

   // variáveis do game loop e estatísticas
   private boolean gameOver; // o jogo terminou?
   private double timeLeft; // tempo restante em segundos
   private int shotsFired; // tiros disparados
   private double totalElapsedTime; // segundos decorridos

   // constantes e variáveis para os sons
   public static final int TARGET_SOUND_ID = 0;
   public static final int CANNON_SOUND_ID = 1;
   public static final int BLOCKER_SOUND_ID = 2;
   private SoundPool soundPool; // toca os efeitos sonoros
   private SparseIntArray soundMap; // mapeia IDs para o SoundPool

   // Paints usados para desenhar na tela
   private Paint textPaint; // desenha o texto
   private Paint backgroundPaint; // limpa a área de desenho

   // construtor
   public CannonView(Context context, AttributeSet attrs) {
      super(context, attrs); // chama o construtor da superclasse
      activity = (Activity) context; // guarda referência à MainActivity

      // registra o listener SurfaceHolder.Callback
      getHolder().addCallback(this);

      // configura os atributos de áudio para áudio de jogo
      AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
      attrBuilder.setUsage(AudioAttributes.USAGE_GAME);

      // inicializa o SoundPool para tocar os três efeitos sonoros
      SoundPool.Builder builder = new SoundPool.Builder();
      builder.setMaxStreams(1);
      builder.setAudioAttributes(attrBuilder.build());
      soundPool = builder.build();

      // cria o mapa de sons e pré-carrega os sons
      soundMap = new SparseIntArray(3);
      soundMap.put(TARGET_SOUND_ID,
         soundPool.load(context, R.raw.target_hit, 1));
      soundMap.put(CANNON_SOUND_ID,
         soundPool.load(context, R.raw.cannon_fire, 1));
      soundMap.put(BLOCKER_SOUND_ID,
         soundPool.load(context, R.raw.blocker_hit, 1));

      textPaint = new Paint();
      backgroundPaint = new Paint();
      backgroundPaint.setColor(Color.WHITE);
   }

   // chamado quando o tamanho da SurfaceView muda,
   // como quando ela é adicionada à hierarquia de Views
   @Override
   protected void onSizeChanged(int w, int h, int oldw, int oldh) {
      super.onSizeChanged(w, h, oldw, oldh);

      screenWidth = w; // guarda a largura da CannonView
      screenHeight = h; // guarda a altura da CannonView

      // configura as propriedades do texto
      textPaint.setTextSize((int) (TEXT_SIZE_PERCENT * screenHeight));
      textPaint.setAntiAlias(true); // suaviza o texto
   }

   // largura da tela do jogo
   public int getScreenWidth() {
      return screenWidth;
   }

   // altura da tela do jogo
   public int getScreenHeight() {
      return screenHeight;
   }

   // toca o som com o soundId dado em soundMap
   public void playSound(int soundId) {
      if (soundPool != null) // evita NullPointerException após releaseResources
         soundPool.play(soundMap.get(soundId), 1, 1, 1, 0, 1f);
   }

   // reinicia todos os elementos da tela e começa um novo jogo
   public void newGame() {
      // constrói um novo Cannon
      cannon = new Cannon(this,
         (int) (CANNON_BASE_RADIUS_PERCENT * screenHeight),
         (int) (CANNON_BARREL_LENGTH_PERCENT * screenWidth),
         (int) (CANNON_BARREL_WIDTH_PERCENT * screenHeight));

      Random random = new Random(); // para velocidades aleatórias
      targets = new ArrayList<>(); // nova lista de Targets

      // posição x do primeiro Target a partir da esquerda
      int targetX = (int) (TARGET_FIRST_X_PERCENT * screenWidth);

      // coordenada Y dos Targets (centralizados verticalmente)
      int targetY = (int) ((0.5 - TARGET_LENGTH_PERCENT / 2) *
         screenHeight);

      // adiciona TARGET_PIECES Targets à lista
      for (int n = 0; n < TARGET_PIECES; n++) {

         // velocidade aleatória entre os valores mínimo e máximo
         double velocity = screenHeight * (random.nextDouble() *
            (TARGET_MAX_SPEED_PERCENT - TARGET_MIN_SPEED_PERCENT) +
            TARGET_MIN_SPEED_PERCENT);

         // alterna as cores dos Targets entre dark e light
         int color = (n % 2 == 0) ?
            getResources().getColor(R.color.dark,
               getContext().getTheme()) :
            getResources().getColor(R.color.light,
               getContext().getTheme());

         velocity *= -1; // inverte a velocidade inicial do próximo Target

         // cria e adiciona um novo Target à lista
         targets.add(new Target(this, color, HIT_REWARD, targetX, targetY,
            (int) (TARGET_WIDTH_PERCENT * screenWidth),
            (int) (TARGET_LENGTH_PERCENT * screenHeight),
            (int) velocity));

         // desloca a coordenada x para posicionar o próximo Target
         targetX += (TARGET_WIDTH_PERCENT + TARGET_SPACING_PERCENT) *
            screenWidth;
      }

      // cria um novo Blocker
      blocker = new Blocker(this, Color.BLACK, MISS_PENALTY,
         (int) (BLOCKER_X_PERCENT * screenWidth),
         (int) ((0.5 - BLOCKER_LENGTH_PERCENT / 2) * screenHeight),
         (int) (BLOCKER_WIDTH_PERCENT * screenWidth),
         (int) (BLOCKER_LENGTH_PERCENT * screenHeight),
         (float) (BLOCKER_SPEED_PERCENT * screenHeight));

      timeLeft = 10; // começa a contagem em 10 segundos

      shotsFired = 0; // número inicial de tiros
      totalElapsedTime = 0.0; // tempo decorrido zerado

      if (gameOver) { // inicia novo jogo após o término do anterior
         gameOver = false; // o jogo não terminou
         cannonThread = new CannonThread(getHolder()); // cria a thread
         cannonThread.start(); // inicia a thread do game loop
      }

      hideSystemBars();
   }

   // chamado repetidamente pela CannonThread para atualizar os elementos
   private void updatePositions(double elapsedTimeMS) {
      double interval = elapsedTimeMS / 1000.0; // converte para segundos

      // atualiza a posição da bola se ela estiver na tela
      if (cannon.getCannonball() != null)
         cannon.getCannonball().update(interval);

      blocker.update(interval); // atualiza a posição do blocker

      for (GameElement target : targets)
         target.update(interval); // atualiza a posição dos alvos

      timeLeft -= interval; // subtrai do tempo restante

      // se o cronômetro chegou a zero
      if (timeLeft <= 0) {
         timeLeft = 0.0;
         gameOver = true; // o jogo acabou
         cannonThread.setRunning(false); // termina a thread
         showGameOverDialog(R.string.lose); // exibe o dialog de derrota
      }

      // se todos os alvos foram atingidos
      if (targets.isEmpty()) {
         cannonThread.setRunning(false); // termina a thread
         showGameOverDialog(R.string.win); // exibe o dialog de vitória
         gameOver = true;
      }
   }

   // alinha o cano e dispara a Cannonball se não houver outra na tela
   public void alignAndFireCannonball(MotionEvent event) {
      // obtém a posição do toque nesta view
      Point touchPoint = new Point((int) event.getX(),
         (int) event.getY());

      // calcula a distância do toque ao centro da tela no eixo y
      double centerMinusY = (screenHeight / 2 - touchPoint.y);

      double angle = 0; // inicializa o ângulo com 0

      // calcula o ângulo que o cano faz com a horizontal
      angle = Math.atan2(touchPoint.x, centerMinusY);

      // aponta o cano para o ponto tocado
      cannon.align(angle);

      // dispara a Cannonball se não houver outra na tela
      if (cannon.getCannonball() == null ||
         !cannon.getCannonball().isOnScreen()) {
         cannon.fireCannonball();
         ++shotsFired;
      }
   }

   // exibe um AlertDialog quando o jogo termina
   private void showGameOverDialog(final int messageId) {
      // DialogFragment que exibe as estatísticas e inicia novo jogo
      final DialogFragment gameResult =
         new DialogFragment() {
            // cria um AlertDialog e o retorna
            @Override
            public Dialog onCreateDialog(Bundle bundle) {
               // cria dialog exibindo o String resource de messageId
               AlertDialog.Builder builder =
                  new AlertDialog.Builder(getActivity());
               builder.setTitle(getResources().getString(messageId));

               // exibe tiros disparados e tempo total
               builder.setMessage(getResources().getString(
                  R.string.results_format, shotsFired, totalElapsedTime));
               builder.setPositiveButton(R.string.reset_game,
                  new DialogInterface.OnClickListener() {
                     // chamado quando o botão "Reset Game" é pressionado
                     @Override
                     public void onClick(DialogInterface dialog,
                        int which) {
                        dialogIsDisplayed = false;
                        newGame(); // configura e inicia novo jogo
                     }
                  }
               );

               return builder.create(); // retorna o AlertDialog
            }
         };

      // na thread da GUI, usa o FragmentManager para exibir o DialogFragment
      activity.runOnUiThread(
         new Runnable() {
            public void run() {
               showSystemBars(); // sai do modo imersivo
               dialogIsDisplayed = true;
               gameResult.setCancelable(false); // dialog modal
               gameResult.show(activity.getFragmentManager(), "results");
            }
         }
      );
   }

   // desenha o jogo no Canvas dado
   public void drawGameElements(Canvas canvas) {
      // limpa o fundo
      canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
         backgroundPaint);

      // exibe o tempo restante
      canvas.drawText(getResources().getString(
         R.string.time_remaining_format, timeLeft), 50, 100, textPaint);

      cannon.draw(canvas); // desenha o canhão

      // desenha os GameElements
      if (cannon.getCannonball() != null &&
         cannon.getCannonball().isOnScreen())
         cannon.getCannonball().draw(canvas);

      blocker.draw(canvas); // desenha o blocker

      // desenha todos os Targets
      for (GameElement target : targets)
         target.draw(canvas);
   }

   // verifica se a bola colide com o Blocker ou com algum Target
   // e trata as colisões
   public void testForCollisions() {
      // remove os alvos com os quais a Cannonball colide
      if (cannon.getCannonball() != null &&
         cannon.getCannonball().isOnScreen()) {
         for (int n = 0; n < targets.size(); n++) {
            if (cannon.getCannonball().collidesWith(targets.get(n))) {
               targets.get(n).playSound(); // toca o som de alvo atingido

               // soma a recompensa ao tempo restante
               timeLeft += targets.get(n).getHitReward();

               cannon.removeCannonball(); // remove a bola do jogo
               targets.remove(n); // remove o alvo atingido
               --n; // garante que o novo alvo n seja testado
               break;
            }
         }
      }
      else { // remove a Cannonball se ela não deve estar na tela
         cannon.removeCannonball();
      }

      // verifica se a bola colide com o blocker
      if (cannon.getCannonball() != null &&
         cannon.getCannonball().collidesWith(blocker)) {
         blocker.playSound(); // toca o som do Blocker

         // inverte a direção da bola
         cannon.getCannonball().reverseVelocityX();

         // desconta a penalidade do blocker do tempo restante
         timeLeft -= blocker.getMissPenalty();
      }
   }

   // para o jogo: chamado pelo onPause do MainActivityFragment
   public void stopGame() {
      if (cannonThread != null)
         cannonThread.setRunning(false); // manda a thread terminar
   }

   // libera recursos: chamado pelo onDestroy do MainActivityFragment
   public void releaseResources() {
      if (soundPool != null) {
         soundPool.release(); // libera todos os recursos do SoundPool
         soundPool = null;
      }
   }

   // chamado quando a superfície muda de tamanho
   @Override
   public void surfaceChanged(SurfaceHolder holder, int format,
      int width, int height) { }

   // chamado quando a superfície é criada pela primeira vez
   @Override
   public void surfaceCreated(SurfaceHolder holder) {
      if (!dialogIsDisplayed) {
         newGame(); // configura e inicia um novo jogo
         cannonThread = new CannonThread(holder); // cria a thread
         cannonThread.setRunning(true); // inicia o jogo
         cannonThread.start(); // inicia a thread do game loop
      }
   }

   // chamado quando a superfície é destruída
   @Override
   public void surfaceDestroyed(SurfaceHolder holder) {
      // garante que a thread termine corretamente
      boolean retry = true;
      cannonThread.setRunning(false); // termina a cannonThread

      while (retry) {
         try {
            cannonThread.join(); // espera a cannonThread terminar
            retry = false;
         }
         catch (InterruptedException e) {
            Log.e(TAG, "Thread interrupted", e);
         }
      }
   }

   // chamado quando o usuário toca a tela
   @Override
   public boolean onTouchEvent(MotionEvent e) {
      // obtém o int que representa o tipo de ação do evento
      int action = e.getAction();

      // o usuário tocou ou arrastou o dedo pela tela
      if (action == MotionEvent.ACTION_DOWN ||
         action == MotionEvent.ACTION_MOVE) {
         // dispara a bola em direção ao ponto tocado
         alignAndFireCannonball(e);
      }

      return true;
   }

   // Subclasse de Thread que controla o game loop
   private class CannonThread extends Thread {
      private SurfaceHolder surfaceHolder; // para manipular o canvas
      private boolean threadIsRunning = true; // executando por padrão

      // inicializa o surface holder
      public CannonThread(SurfaceHolder holder) {
         surfaceHolder = holder;
         setName("CannonThread");
      }

      // altera o estado de execução
      public void setRunning(boolean running) {
         threadIsRunning = running;
      }

      // controla o game loop
      @Override
      public void run() {
         Canvas canvas = null; // usado para desenhar
         long previousFrameTime = System.currentTimeMillis();

         while (threadIsRunning) {
            try {
               // obtém o Canvas para desenho exclusivo desta thread
               canvas = surfaceHolder.lockCanvas(null);

               // trava o surfaceHolder para desenhar
               synchronized(surfaceHolder) {
                  long currentTime = System.currentTimeMillis();
                  double elapsedTimeMS = currentTime - previousFrameTime;
                  totalElapsedTime += elapsedTimeMS / 1000.0;
                  updatePositions(elapsedTimeMS); // atualiza o estado do jogo
                  testForCollisions(); // testa colisões dos GameElements
                  if (canvas != null)
                     drawGameElements(canvas); // desenha usando o canvas
                  previousFrameTime = currentTime; // atualiza o tempo anterior
               }
            }
            finally {
               // exibe o conteúdo do canvas na CannonView
               // e libera o Canvas para outras threads
               if (canvas != null)
                  surfaceHolder.unlockCanvasAndPost(canvas);
            }
         }
      }
   }

   // oculta as barras do sistema e a app bar (modo imersivo)
   private void hideSystemBars() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
         setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_IMMERSIVE);
   }

   // exibe as barras do sistema e a app bar
   private void showSystemBars() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
         setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
   }
}
