package com.example.currencyconverter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    Spinner fromSpinner, toSpinner;
    EditText inputAmount;
    TextView resultText;
    Button convertBtn;
    BottomNavigationView bottomNavBar;

    String[] currencies = {"INR", "USD", "EUR", "JPY"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Load theme
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        inputAmount = findViewById(R.id.inputAmount);
        resultText = findViewById(R.id.resultText);
        convertBtn = findViewById(R.id.convertBtn);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, currencies);

        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        convertBtn.setOnClickListener(v -> convertCurrency());

        bottomNavBar = findViewById(R.id.bottom_nav_bar);
        bottomNavBar.setSelectedItemId(R.id.home_menu); //Shows Home Menu as selected
        bottomNavBar.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.settings_menu) {
                            Intent i = new Intent(MainActivity.this, Settings.class);
                            startActivity(i);

                            //finish() is called to remove this activity from the Back Stack,
                            // so that user does not come back to this current activity on clicking Back
                            finish();
                            return true;
                        }
                        return false;
                    }
                });
    }

    @SuppressLint("DefaultLocale")
    private void convertCurrency() {
        String from = fromSpinner.getSelectedItem().toString();
        String to = toSpinner.getSelectedItem().toString();

        String amountStr = inputAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            double result = CurrencyConverter.convert(from, to, amount);
            resultText.setText(String.format("Result: %.2f", result));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount entered", Toast.LENGTH_SHORT).show();
        }
    }
}