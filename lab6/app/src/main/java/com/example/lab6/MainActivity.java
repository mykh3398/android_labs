package com.example.lab6;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText amountEditText;
    private Spinner fromCurrencySpinner, toCurrencySpinner;
    private TextView resultText;
    private ArrayList<String> history;
    private ArrayAdapter<String> historyAdapter;
    private ListView historyListView;

    private OkHttpClient client;
    private static final String API_KEY = "a7bab1784fa8b56dfa8d1b7576b0f4bd";

    private static final String HISTORY_KEY = "history_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountEditText = findViewById(R.id.amountEditText);
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner);
        resultText = findViewById(R.id.resultText);
        historyListView = findViewById(R.id.historyListView);

        history = new ArrayList<>();
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, history);

        client = new OkHttpClient();

        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(currencyAdapter);
        toCurrencySpinner.setAdapter(currencyAdapter);

        historyListView.setAdapter(historyAdapter);

        if (savedInstanceState != null) {
            ArrayList<String> savedHistory = savedInstanceState.getStringArrayList(HISTORY_KEY);
            if (savedHistory != null) {
                history.clear();
                history.addAll(savedHistory);
                historyAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(HISTORY_KEY, history);
    }

    public void onConvertClick(View view) {
        String amountText = amountEditText.getText().toString();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Введіть кількість для конвертації", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountText);
        String fromCurrency = fromCurrencySpinner.getSelectedItem().toString();
        String toCurrency = toCurrencySpinner.getSelectedItem().toString();

        String url = "https://api.currencylayer.com/live?access_key=" + API_KEY +
                "&currencies=" + toCurrency +
                "&source=" + fromCurrency;

        // запит до API CurrencyLayer
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    resultText.setText("Помилка з'єднання");
                    Log.e("MainActivity", "Помилка з'єднання", e);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Log.d("API Response", json);
                    try {
                        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

                        if (obj != null && obj.has("quotes")) {
                            JsonObject quotes = obj.getAsJsonObject("quotes");

                            String quoteKey = fromCurrency + toCurrency;

                            if (quotes != null && quotes.has(quoteKey)) {
                                double rate = quotes.get(quoteKey).getAsDouble();
                                double result = amount * rate;

                                runOnUiThread(() -> {
                                    String text = amount + " " + fromCurrency + " = " + result + " " + toCurrency;
                                    resultText.setText(text);

                                    history.add(0, text);
                                    historyAdapter.notifyDataSetChanged();
                                });
                            } else {
                                runOnUiThread(() -> resultText.setText("Невірна відповідь: відсутні курси для валюти"));
                            }
                        } else {
                            runOnUiThread(() -> resultText.setText("Невірна відповідь: відсутні курси"));
                        }
                    } catch (Exception e) {
                        Log.e("MainActivity", "Помилка при розборі відповіді", e);
                        runOnUiThread(() -> resultText.setText("Помилка при розборі відповіді: " + e.getMessage()));
                    }
                } else {
                    runOnUiThread(() -> resultText.setText("Помилка відповіді від сервера"));
                }
            }
        });
    }
}
