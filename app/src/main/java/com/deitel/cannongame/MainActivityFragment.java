// MainActivityFragment.java
// MainActivityFragment cria e gerencia a CannonView
package com.deitel.cannongame;

import android.media.AudioManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivityFragment extends Fragment {
   private CannonView cannonView; // view customizada que exibe o jogo

   // chamado quando a view do Fragment precisa ser criada
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);

      // infla o layout fragment_main.xml
      View view =
         inflater.inflate(R.layout.fragment_main, container, false);

      // obtém referência para a CannonView
      cannonView = (CannonView) view.findViewById(R.id.cannonView);
      return view;
   }

   // configura o controle de volume quando a Activity é criada
   @SuppressWarnings("deprecation")
   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);

      // permite que os botões de volume ajustem o volume do jogo
      getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
   }

   // quando a MainActivity é pausada, termina o jogo
   @Override
   public void onPause() {
      super.onPause();
      cannonView.stopGame(); // termina o jogo
   }

   // quando a MainActivity é destruída, libera os recursos
   @Override
   public void onDestroy() {
      super.onDestroy();
      cannonView.releaseResources();
   }
}
