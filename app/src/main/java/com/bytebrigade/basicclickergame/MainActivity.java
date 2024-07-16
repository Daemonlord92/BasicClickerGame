package com.bytebrigade.basicclickergame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "IdleGamePrefs";
    private static final String KEY_HERO_LEVEL = "heroLevel";
    private static final String KEY_GOLD = "gold";
    private static final String KEY_HELP_COUNT = "helpCount";
    private static final String KEY_ENEMIES_DEFEATED = "enemiesDefeated";

    private int heroLevel = 1;
    private int gold = 0;
    private int helpCount = 0;
    private int enemyHealth = 10;
    private int enemiesDefeated = 0;

    private TextView tvHeroLevel;
    private TextView tvGold;
    private TextView tvHelpCount;
    private TextView tvEnemyHealth;

    private Handler handler = new Handler();
    private Runnable goldRunnable = new Runnable() {
        @Override
        public void run() {
            gold += helpCount;
            tvGold.setText("Gold: " + gold);
            handler.postDelayed(this, 1000); // 1 second delay
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHeroLevel = findViewById(R.id.tvHeroLevel);
        tvGold = findViewById(R.id.tvGold);
        tvHelpCount = findViewById(R.id.tvHelpCount);
        tvEnemyHealth = findViewById(R.id.tvEnemyHealth);

        Button btnClick = findViewById(R.id.btnClick);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gold++;
                tvGold.setText("Gold: " + gold);
            }
        });

        Button btnHireHelp = findViewById(R.id.btnHireHelp);
        btnHireHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gold >= 10) {
                    gold -= 10;
                    helpCount++;
                    tvHelpCount.setText("Help: " + helpCount);
                    tvGold.setText("Gold: " + gold);
                }
            }
        });

        Button btnAttack = findViewById(R.id.btnAttack);
        btnAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attackEnemy();
            }
        });

        // Load saved game state
        loadGameState();

        handler.postDelayed(goldRunnable, 1000); // Start generating gold every second
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save game state
        saveGameState();
    }

    private void attackEnemy() {
        enemyHealth--;
        if (enemyHealth <= 0) {
            gold += 5; // Reward for defeating the enemy
            enemiesDefeated++;
            enemyHealth = 10; // Reset enemy health
            if (enemiesDefeated % 5 == 0) {
                heroLevel++;
            }
            tvGold.setText("Gold: " + gold);
            tvHeroLevel.setText("Hero Level: " + heroLevel);
        }
        tvEnemyHealth.setText("Enemy Health: " + enemyHealth);
    }

    private void saveGameState() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_HERO_LEVEL, heroLevel);
        editor.putInt(KEY_GOLD, gold);
        editor.putInt(KEY_HELP_COUNT, helpCount);
        editor.putInt(KEY_ENEMIES_DEFEATED, enemiesDefeated);
        editor.apply();
    }

    private void loadGameState() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        heroLevel = sharedPreferences.getInt(KEY_HERO_LEVEL, 1);
        gold = sharedPreferences.getInt(KEY_GOLD, 0);
        helpCount = sharedPreferences.getInt(KEY_HELP_COUNT, 0);
        enemiesDefeated = sharedPreferences.getInt(KEY_ENEMIES_DEFEATED, 0);

        // Update UI with loaded values
        tvHeroLevel.setText("Hero Level: " + heroLevel);
        tvGold.setText("Gold: " + gold);
        tvHelpCount.setText("Help: " + helpCount);
        tvEnemyHealth.setText("Enemy Health: " + enemyHealth);
    }
}