// Cannon.java
// Representa o Cannon e dispara a Cannonball
package com.deitel.cannongame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Cannon {
   private int baseRadius; // raio da base do canhão
   private int barrelLength; // comprimento do cano
   private Point barrelEnd = new Point(); // ponto final do cano
   private double barrelAngle; // ângulo do cano
   private Cannonball cannonball; // a bola de canhão disparada
   private Paint paint = new Paint(); // Paint usado para desenhar o canhão
   private CannonView view; // view que contém o Cannon

   // construtor
   public Cannon(CannonView view, int baseRadius, int barrelLength,
      int barrelWidth) {
      this.view = view;
      this.baseRadius = baseRadius;
      this.barrelLength = barrelLength;
      paint.setStrokeWidth(barrelWidth); // define a largura do cano
      paint.setColor(Color.BLACK); // o canhão é preto
      align(Math.PI / 2); // cano inicialmente apontando para a direita
   }

   // alinha o cano do canhão ao ângulo dado
   public void align(double barrelAngle) {
      this.barrelAngle = barrelAngle;
      barrelEnd.x = (int) (barrelLength * Math.sin(barrelAngle));
      barrelEnd.y = (int) (-barrelLength * Math.cos(barrelAngle)) +
         view.getScreenHeight() / 2;
   }

   // cria e dispara a Cannonball na direção para onde o Cannon aponta
   public void fireCannonball() {
      // calcula a componente x da velocidade
      int velocityX = (int) (CannonView.CANNONBALL_SPEED_PERCENT *
         view.getScreenWidth() * Math.sin(barrelAngle));

      // calcula a componente y da velocidade
      int velocityY = (int) (CannonView.CANNONBALL_SPEED_PERCENT *
         view.getScreenWidth() * -Math.cos(barrelAngle));

      // calcula o raio da Cannonball
      int radius = (int) (view.getScreenHeight() *
         CannonView.CANNONBALL_RADIUS_PERCENT);

      // constrói a Cannonball e a posiciona dentro do Cannon
      cannonball = new Cannonball(view, Color.BLACK,
         CannonView.CANNON_SOUND_ID, -radius,
         view.getScreenHeight() / 2 - radius, radius, velocityX,
         velocityY);

      cannonball.playSound(); // toca o som de disparo
   }

   // desenha o Cannon no Canvas
   public void draw(Canvas canvas) {
      // desenha o cano
      canvas.drawLine(0, view.getScreenHeight() / 2, barrelEnd.x,
         barrelEnd.y, paint);

      // desenha a base
      canvas.drawCircle(0, (int) view.getScreenHeight() / 2,
         (int) baseRadius, paint);
   }

   // retorna a Cannonball disparada por este Cannon
   public Cannonball getCannonball() {
      return cannonball;
   }

   // remove a Cannonball do jogo
   public void removeCannonball() {
      cannonball = null;
   }
}
