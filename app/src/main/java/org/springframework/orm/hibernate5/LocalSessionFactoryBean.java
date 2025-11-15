package org.springframework.orm.hibernate5;

import javax.sql.DataSource;
import java.util.Properties;

public class LocalSessionFactoryBean {
    private DataSource dataSource;
    private String[] packagesToScan;
    private Properties hibernateProperties;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setPackagesToScan(String packages) {
        this.packagesToScan = new String[]{packages};
    }

    public void setHibernateProperties(Properties props) {
        this.hibernateProperties = props;
    }

    public Object getObject() {
        return null; // stub
    }
}
