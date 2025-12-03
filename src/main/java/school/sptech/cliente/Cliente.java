package school.sptech.cliente;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

public class Cliente {
    private String componente;
    private String idMaquina;
    public String urlPost;
    public String valorLeitura;

    HttpClient client = HttpClient.newHttpClient();

    public Cliente(String componente, String idMaquina, String valorLeitura) {
        this.componente = componente;
        this.idMaquina = idMaquina;
        this.valorLeitura = valorLeitura;
        this.urlPost = "http://98.83.124.125:8080/chamadosJira";
    }

    public HttpRequest FetchPOST() {
        Gson gson = new Gson();
        String json = gson.toJson(mapValoresGson());
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlPost))
                .header("Content-Type", "application/json; charset=utf-8")
                .timeout(Duration.ofSeconds(10))
                .POST(body)
                .build();
        return request;
    }

    public String respostaPost(HttpRequest requisicaoTipo) throws Exception, InterruptedException {
        HttpResponse<String> resposta = this.client.send(
                requisicaoTipo,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        try {
            if(resposta.statusCode() == 201) {
                System.out.println("Sucesso!");
                return resposta.body();
            } else {
                System.out.println("ERRO!");
                System.out.println(resposta.statusCode());
                System.out.println(resposta.body());
            }
        } catch(Exception e){
            System.out.println("ERRO no Try");
        }
        System.out.println(resposta.statusCode());
        return resposta.body();
    }

    public Map<String, String> mapValoresGson(){
        Map<String, String> dados = new HashMap<>();
        dados.put("nomeChamado", "CHAMADO");
        dados.put("descricaoChamado", "Alerta: " + this.componente + " | Valor: " + this.valorLeitura);
        dados.put("idMaquina", this.idMaquina);

        return dados;
    }
}