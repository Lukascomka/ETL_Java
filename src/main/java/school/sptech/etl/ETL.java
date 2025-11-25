package school.sptech.etl;

import school.sptech.bancoDeDados.Query;
import school.sptech.cliente.Cliente;
import school.sptech.csv.LerCsv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ETL {
    List<Query> resultadoQuery;
    List<String[]> csvleitura;
    List<String[]> comparacao;
    LerCsv lerCsv = new LerCsv();
    Query query = new Query();

    public ETL(){
        resultadoQuery = new ArrayList<>();
    }

    public List<String[]> tratandoDadosCsv(List<String[]> csvParaTratamento) {

        List<String[]> csvPronto = new ArrayList<>();

        String regexVerificaSeEhDado = "^[0-9].*";



        for (String[] linhaVetor : csvParaTratamento) {
            for (int i = 0; i < linhaVetor.length; i++) {

                linhaVetor[i] = linhaVetor[i].replace("\"", "").trim();

                linhaVetor[i] = linhaVetor[i].replaceAll("(\\.\\d{2})\\d+", "$1");
            }

            csvPronto.add(linhaVetor);
        }

        for (int i = 0; i < csvPronto.size(); i++) {
            System.out.println(Arrays.toString(csvPronto.get(i)));
        }

        return csvPronto;
    }

    public List<String[]> compararBancoDeDados(List<String[]> csvParaComparado) throws Exception {
        List<String[]> csvAlertas = new ArrayList<>();
        csvleitura = csvParaComparado;
        comparacao = query.buscarParametrosEATM();


        if (!csvleitura.isEmpty()) {
            csvAlertas.add(csvleitura.get(0));
        }
        for (int i = 1; i < csvleitura.size(); i++) {
            String[] linhaCsv = csvleitura.get(i);
            String macCsv = linhaCsv[0];
            boolean essaLinhaGerouAlerta = false;
            for (int j = 0; j < comparacao.size(); j++) {
                String[] linhaBanco = comparacao.get(j);
                String macBanco = linhaBanco[0];
                String nomeComponente = linhaBanco[2];
                String valorLimiteStr = linhaBanco[3];

                if (macCsv.contains(macBanco)) {
                    try {
                        double limite = Double.parseDouble(valorLimiteStr.replace(",", "."));
                        double valorMedido = 0.0;
                        boolean estourou = false;


                        if (nomeComponente.toLowerCase().contains("cpu")) {
                            valorMedido = Double.parseDouble(linhaCsv[2].replace(",", "."));
                            if (valorMedido > limite) estourou = true;
                        }
                        else if (nomeComponente.toLowerCase().contains("ram")) {
                            valorMedido = Double.parseDouble(linhaCsv[3].replace(",", "."));
                            if (valorMedido > limite) estourou = true;
                        }
                        else if (nomeComponente.toLowerCase().contains("disco")) {
                            valorMedido = Double.parseDouble(linhaCsv[4].replace(",", "."));
                            if (valorMedido > limite) estourou = true;
                        }
                        else if (nomeComponente.toLowerCase().contains("rede")) {
                            valorMedido = Double.parseDouble(linhaCsv[5].replace(",", "."));
                            if (valorMedido > limite) estourou = true;
                        }

                        if (estourou) {
                            essaLinhaGerouAlerta = true;
                            System.out.println("ALERTA! Enviando para Jira: " + nomeComponente + " | Valor: " + valorMedido);
                            String nomeChamado = nomeComponente;
                            Cliente clienteJira = new Cliente(
                                    nomeComponente,
                                    macCsv,
                                    String.valueOf(valorMedido)
                            );
                            clienteJira.respostaPost(clienteJira.FetchPOST());
                        }
                    } catch (Exception e) {
                         System.out.println("Erro de conversão: " + e.getMessage());
                    }
                }
            }
            if (essaLinhaGerouAlerta) {
                csvAlertas.add(linhaCsv);
            }
        }
        lerCsv.escreverCsv( csvAlertas);
        return csvAlertas;
    }
}
