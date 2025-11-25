package school.sptech.bancoDeDados;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Conexao_MySql {

    private BasicDataSource dataSource;


public Conexao_MySql() {
        dataSource = new BasicDataSource();

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        dataSource.setUrl("jdbc:mysql://3.232.22.8:3306/BlackScreen");
        dataSource.setUsername("root");
        dataSource.setPassword("urubu100");

}

    public Connection getConexao() throws SQLException {
        return dataSource.getConnection();
    }

    public BasicDataSource getDataSource() {
        return dataSource;
    }
}