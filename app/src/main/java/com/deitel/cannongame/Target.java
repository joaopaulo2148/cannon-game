// Target.java
// Subclasse de GameElement customizada para o Target, com movimentos
// complexos: senoidal, zigue-zague e aceleração progressiva
package com.deitel.cannongame;

public class Target extends GameElement {
   // tipos de movimento do alvo
   public enum Movement {
      LINEAR, // apenas sobe e desce (movimento original do livro)
      SINE, // oscila horizontalmente seguindo uma senoide
      ZIGZAG, // oscila horizontalmente em ondas triangulares (zigue-zague)
      ACCELERATING // acelera progressivamente na vertical
   }

   private static final double PERIOD = 2.0; // segundos por oscilação completa
   private static final double ACCELERATION = 0.35; // aumento de velocidade por segundo
   private static final double MAX_SPEED_FACTOR = 2.0; // limite em relação à velocidade base

   private int hitReward; // recompensa de tempo ao acertar este alvo
   private boolean light; // true: alvo da cor clara; false: da cor escura
   private Movement movement = Movement.LINEAR;
   private float amplitude; // amplitude da oscilação horizontal em pixels
   private double phase; // defasagem para os alvos não se moverem em sincronia
   private double elapsedTime = 0; // tempo de movimento acumulado
   private int lastOffsetX = 0; // deslocamento horizontal já aplicado
   private float baseSpeed; // velocidade vertical de referência (sem sinal)

   // construtor
   public Target(CannonView view, int color, int hitReward, int x, int y,
                 int width, int length, float velocityY) {
      super(view, color, CannonView.TARGET_SOUND_ID, x, y, width, length,
              velocityY);
      this.hitReward = hitReward;
      this.baseSpeed = Math.abs(velocityY);
   }

   // define o tipo de movimento, a amplitude horizontal e a defasagem
   public void setMovement(Movement movement, float amplitude, double phase) {
      this.movement = movement;
      this.amplitude = amplitude;
      this.phase = phase;
   }

   // atualiza a posição: movimento vertical original + movimento complexo
   @Override
   public void update(double interval) {
      super.update(interval); // sobe e desce, invertendo nas bordas
      elapsedTime += interval;

      double angle = 2 * Math.PI * elapsedTime / PERIOD + phase;

      switch (movement) {
         case SINE:
            // senoide: x = A * sen(wt)
            moveHorizontally(Math.sin(angle));
            break;
         case ZIGZAG:
            // onda triangular (zigue-zague): (2/pi) * arcsen(sen(wt))
            moveHorizontally((2 / Math.PI) * Math.asin(Math.sin(angle)));
            break;
         case ACCELERATING:
            // aumenta a velocidade até o limite, mantendo o sentido
            float speed = getVelocityY();
            if (Math.abs(speed) < baseSpeed * MAX_SPEED_FACTOR)
               setVelocityY((float) (speed * (1 + ACCELERATION * interval)));
            break;
         default:
            break; // LINEAR: nada a fazer
      }
   }

   // desloca o alvo para a posição horizontal desejada (-1 a 1 vezes a amplitude)
   private void moveHorizontally(double factor) {
      int newOffsetX = (int) (amplitude * factor);
      shape.offset(newOffsetX - lastOffsetX, 0);
      lastOffsetX = newOffsetX;
   }

   // a velocidade de referência acompanha o aumento de nível
   @Override
   public void scaleSpeed(float factor) {
      super.scaleSpeed(factor);
      baseSpeed *= factor;
   }

   // define se o alvo usa a cor clara ou a escura (para trocar o tema)
   public void setLight(boolean light) {
      this.light = light;
   }

   public boolean isLight() {
      return light;
   }

   // altera a recompensa de tempo (reduzida conforme o nível sobe)
   public void setHitReward(int hitReward) {
      this.hitReward = hitReward;
   }

   // retorna a recompensa deste Target
   public int getHitReward() {
      return hitReward;
   }
}