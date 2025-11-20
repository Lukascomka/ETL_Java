package school.sptech;

import school.sptech.bancoDeDados.Conexao_MySql;
import school.sptech.bancoDeDados.Query;
import school.sptech.csv.LerCsv;
import school.sptech.etl.ETL;
import java.util.List;



public class Main {
    public static void main(String[] args) throws Exception {
       String csvCapMaquina = "/home/lukascomka/Área de trabalho/ETL/capturaMaquina-11-2025-80160640191877.csv";
       String csvCapProcesso = "/home/lukascomka/Área de trabalho/ETL/capturaProcesso-11-2025-80160640191877.csv";


        Conexao_MySql mySql = new Conexao_MySql();

        mySql.getDataSource().getConnection();
        Query query = new Query();
        List<String[]> resultados = query.buscarParametrosEATM();
        query.buscarParametrosEATM();


        LerCsv lerCsv = new LerCsv();
        ETL etl = new ETL();

         lerCsv.leituraCsv(csvCapMaquina);
         List<String[]> ok = (List<String[]>) lerCsv.leituraCsv(csvCapMaquina);

        List<String[]> tratadoCsv =  etl.tratandoDadosCsv(ok);

        etl.compararBancoDeDados(etl.tratandoDadosCsv(tratadoCsv));

    }
}
