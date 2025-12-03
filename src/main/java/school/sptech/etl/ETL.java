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

    public ETL() {
        resultadoQuery = new ArrayList<>();
    }

    public List<String[]> tratandoDadosCsv(List<String[]> csvParaTratamento) {

        List<String[]> csvPronto = new ArrayList<>();

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

        List<String> alertasJaEnviados = new ArrayList<>();

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

                if (macCsv.trim().contains(macBanco.trim())) {
                    try {
                        double limite = Double.parseDouble(valorLimiteStr.replace(",", "."));
                        double valorMedido = 0.0;
                        boolean estourou = false;
                        boolean componenteEncontrado = false;


                        if (nomeComponente.toLowerCase().contains("cpu")) {
                            valorMedido = Double.parseDouble(linhaCsv[2].replace(",", "."));
                            componenteEncontrado = true;
                        } else if (nomeComponente.toLowerCase().contains("ram")) {
                            valorMedido = Double.parseDouble(linhaCsv[3].replace(",", "."));
                            componenteEncontrado = true;
                        } else if (nomeComponente.toLowerCase().contains("disco")) {
                            valorMedido = Double.parseDouble(linhaCsv[4].replace(",", "."));
                            componenteEncontrado = true;
                        } else if (nomeComponente.toLowerCase().contains("enviados")) {
                            valorMedido = Double.parseDouble(linhaCsv[6].replace(",", "."));
                            componenteEncontrado = true;
                        } else if (nomeComponente.toLowerCase().contains("recebidos")) {
                            valorMedido = Double.parseDouble(linhaCsv[7].replace(",", "."));
                            componenteEncontrado = true;
                        } else if (nomeComponente.toLowerCase().contains("perdidos")) {
                            valorMedido = Double.parseDouble(linhaCsv[8].replace(",", "."));
                            componenteEncontrado = true;
                        }

                        if (componenteEncontrado) {
                            if (valorMedido > limite) estourou = true;

                            if (estourou) {
                                essaLinhaGerouAlerta = true;
                                String chaveUnicaAlerta = macCsv.trim() + "-" + nomeComponente.trim();

                                if (!alertasJaEnviados.contains(chaveUnicaAlerta)) {
                                    System.out.println("ALERTA NOVO! Enviando Jira: " + chaveUnicaAlerta + " | Valor: " + valorMedido);

                                    Cliente clienteJira = new Cliente(
                                            nomeComponente,
                                            macCsv,
                                            String.valueOf(valorMedido)
                                    );
                                    clienteJira.respostaPost(clienteJira.FetchPOST());
                                    alertasJaEnviados.add(chaveUnicaAlerta);
                                } else {
                                    System.out.println("Alerta repetido ignorado para a máquina/componente: " + chaveUnicaAlerta);
                                }

                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Erro processamento: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            if (essaLinhaGerouAlerta) {
                csvAlertas.add(linhaCsv);
            }
        }

        lerCsv.escreverCsv(csvAlertas);
        System.out.println("Processamento concluído. Total de alertas no CSV: " + (csvAlertas.size() - 1));

        for (int i = 0; i < csvAlertas.size(); i++) {
            System.out.println("Linha Alerta " + i + ": " + Arrays.toString(csvAlertas.get(i)));
        }

        return csvAlertas;
    }
}