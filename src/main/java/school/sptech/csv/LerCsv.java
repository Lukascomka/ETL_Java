package school.sptech.csv;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LerCsv {

    public List<String[]> leituraCsv(InputStream inputStream) {
        List<String[]> dados = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] colunas = linha.split(";(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                dados.add(colunas);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o stream do S3", e);
        }
        System.out.println("Total de linhas lidas do S3: " + dados.size());
        return dados;
    }
    public InputStream escreverCsv(List<String[]> linhas) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            for (String[] linha : linhas) {
                String linhaCsv = String.join(";", linha);
                writer.write(linhaCsv);
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar CSV na memória", e);
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}