package com.zaxxer.hikari;

import java.util.Properties;

// Configuración básica de HikariCP

public class HikariConfig {

    private final Properties dataSourceProperties = new Properties();

    public HikariConfig() {
    }

    public void setJdbcUrl(String url) {
        // stub
    }

    public void setUsername(String username) {
        // stub
    }

    public void setPassword(String password) {
        // stub
    }

    public void setDriverClassName(String driverClassName) {
        // stub
    }

    public void setMaximumPoolSize(int size) {
        // stub
    }

    public void setMinimumIdle(int minIdle) {
        // stub
    }

    public void addDataSourceProperty(String key, Object value) {
        if (key != null && value != null) {
            dataSourceProperties.put(key, value.toString());
        }
    }

    public Properties getDataSourceProperties() {
        return dataSourceProperties;
    }
}
