package com.example.myapplication.autenticacion;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREFS = "apptienda_prefs";
    private final SharedPreferences prefs;

    public AuthManager(Context ctx) {
        this.prefs = ctx.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveSession(String email, String token, String role) {
        prefs.edit().putString("email", email).putString("token", token).putString("role", role).apply();
    }

    public void saveToken(String token) {
        prefs.edit().putString("token", token).apply();
    }

    public String getToken() { return prefs.getString("token", null); }
    public String getEmail() { return prefs.getString("email", null); }
    public String getRole() { return prefs.getString("role", null); }

    public boolean isLoggedIn() { return getToken() != null; }

    public void logout() { prefs.edit().remove("token").remove("email").remove("role").apply(); }
}
