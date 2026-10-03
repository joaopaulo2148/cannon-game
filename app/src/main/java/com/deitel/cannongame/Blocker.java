// Blocker.java
// Subclasse de GameElement customizada para o Blocker
package com.deitel.cannongame;

public class Blocker extends GameElement {
   private int missPenalty; // penalidade de erro deste Blocker

   // construtor
   public Blocker(CannonView view, int color, int missPenalty, int x,
      int y, int width, int length, float velocityY) {
      super(view, color, CannonView.BLOCKER_SOUND_ID, x, y, width, length,
         velocityY);
      this.missPenalty = missPenalty;
   }

   // retorna a penalidade deste Blocker
   public int getMissPenalty() {
      return missPenalty;
   }
}
