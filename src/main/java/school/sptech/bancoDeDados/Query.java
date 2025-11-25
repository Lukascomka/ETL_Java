package school.sptech.bancoDeDados;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Query {

    private Statement statement;

    public List<String[]> buscarParametrosEATM()throws SQLException{

        List<String[]> listaResultados = new ArrayList<>();
        Conexao_MySql conexaoBanco = new Conexao_MySql();


        String query = String.format(
                "SELECT \n" +
                        "    c.Macaddress AS macaddress,\n" +
                        "    c.codigoCaixa,\n" +
                        "    comp.Nome_Componente,\n" +
                        "    p.Valor_Parametrizado\n" +
                        "\t\tFROM Caixa c\n" +
                        "\t\tINNER JOIN Caixa_Componente cc ON c.Id_Caixa = cc.Fk_Caixa\n" +
                        "\t\tINNER JOIN Componentes comp ON comp.Id_Componente = cc.Fk_Componente\n" +
                        "\t\tINNER JOIN Parametros p ON p.Fk_Componente = comp.Id_Componente; "
        );
        System.out.println("\n"+"query no banco de dados = "+query);
        try (Connection conn = conexaoBanco.getConexao();
             Statement statement = conn.createStatement();
             ResultSet resultado = statement.executeQuery(query)) {

            while (resultado.next()) {
                String[] linhaVetor = new String[4];

                linhaVetor[0] = resultado.getString("macaddress");
                linhaVetor[1] = resultado.getString("codigoCaixa");
                linhaVetor[2] = resultado.getString("Nome_Componente");
                linhaVetor[3] = resultado.getString("Valor_Parametrizado");

                listaResultados.add(linhaVetor);

            }

        } catch (SQLException e) {
            System.out.println("Erro ao executar query: " + e.getMessage());
        }
        for (int i =0; i < listaResultados.size(); i++) {
            System.out.println("VALORES BANCO DE DADOS = "+ Arrays.toString(listaResultados.get(i)));
        }
        return listaResultados;
    }
}

