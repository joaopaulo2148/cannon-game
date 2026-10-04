package com.deitel.cannongame;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

// Ranking local: guarda as maiores pontuações em SharedPreferences
public class HighScores {
    private static final String PREFS_NAME = "cannon_highscores";
    private static final String KEY_PREFIX = "score_";
    public static final int MAX_SCORES = 5; // quantidade de posições do ranking

    private HighScores() { } // classe utilitária: não é instanciada

    // retorna as pontuações salvas, da maior para a menor (sem zeros)
    public static List<Integer> getScores(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        List<Integer> scores = new ArrayList<>();

        for (int i = 0; i < MAX_SCORES; i++) {
            int value = prefs.getInt(KEY_PREFIX + i, 0);
            if (value > 0)
                scores.add(value);
        }

        return scores;
    }

    // insere a pontuação no ranking e retorna a posição (0 = primeiro lugar)
    // ou -1 se a pontuação não entrou no ranking
    public static int addScore(Context context, int score) {
        if (score <= 0)
            return -1; // pontuação zero não entra no ranking

        List<Integer> scores = getScores(context);

        // encontra a posição de inserção (empate fica abaixo do existente)
        int position = 0;
        while (position < scores.size() && scores.get(position) >= score)
            position++;

        if (position >= MAX_SCORES)
            return -1; // não entrou no top

        scores.add(position, score);
        while (scores.size() > MAX_SCORES)
            scores.remove(scores.size() - 1); // mantém só as maiores

        // grava o ranking atualizado
        SharedPreferences.Editor editor =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        for (int i = 0; i < MAX_SCORES; i++)
            editor.putInt(KEY_PREFIX + i, i < scores.size() ? scores.get(i) : 0);
        editor.apply();

        return position;
    }
}