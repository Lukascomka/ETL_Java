package school.sptech.csv;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LerCsv {

        public List<String[]> leituraCsv(String nomeCsv) {
            List<String[]> dados = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(nomeCsv), StandardCharsets.UTF_8))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    String[] colunas = linha.split(";(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    dados.add(colunas);
                }
            } catch (IOException e) {
                System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            }
            System.out.println("Total de linhas lidas: " + dados.size());

            for (int i = 0; i < dados.size(); i++) {
                System.out.println(Arrays.toString(dados.get(i)));
            }

            return dados;
        }

    public List<String[]> escreverCsv(String novoNomeCsv, List<String[]> linhas) {
        List<String[]> dados = new ArrayList<>();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(novoNomeCsv), StandardCharsets.UTF_8))) {
            for (String[] linha : linhas) {
                String linhaCsv = String.join(";", linha);
                writer.write(linhaCsv);
                writer.newLine();
                dados.add(linha);
            }
        } catch (IOException e) {
            System.out.println("Erro ao escrever o arquivo: " + e);
        }
        System.out.println("Total de linhas lidas: " + dados.size());
        for(int i =0; i<dados.size(); i++){
            System.out.println(Arrays.toString(dados.get(i)));
        }
        return dados;
    }
    public void escreverCsvSobrescrevendo(String novoNomeCsv, List<String[]> linhas) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(novoNomeCsv, false), StandardCharsets.UTF_8))) {

            for (String[] linha : linhas) {
                String linhaCsv = String.join(";", linha);
                writer.write(linhaCsv);
                writer.newLine();

            }
            System.out.println("Arquivo CSV criado:" + novoNomeCsv);
        } catch (IOException e) {
            System.out.println("Erro ao escrever o arquivo: " + e);
        }

    }
}
