package school.sptech;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.ObjectMetadata;

import school.sptech.csv.LerCsv;
import school.sptech.etl.ETL;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LambdaHandler implements RequestHandler<S3Event, String> {
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    @Override
    public String handleRequest(S3Event event, Context context) {

        try {
            String bucketOrigem = event.getRecords().get(0).getS3().getBucket().getName();
            String chaveArquivo = event.getRecords().get(0).getS3().getObject().getKey();

            S3Object s3Object = s3Client.getObject(bucketOrigem, chaveArquivo);
            InputStream streamDoArquivo = s3Object.getObjectContent();

            LerCsv lerCsv = new LerCsv();

            List<String[]> dados = lerCsv.leituraCsv(streamDoArquivo);

            ETL etl = new ETL();
            List<String[]> dadosTratados = etl.tratandoDadosCsv(dados);

            InputStream arquivoParaSalvar = lerCsv.escreverCsv(etl.compararBancoDeDados(dadosTratados));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("text/csv");

            String pastaBase = determinarPasta(chaveArquivo);

            LocalDateTime agora = LocalDateTime.now();
            String anoMes = agora.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            String bucketDestino = System.getenv("BUCKET_DESTINO");
            String nomeNovoArquivo = String.format("%s/%s/processado_%s",
                    pastaBase,
                    anoMes,
                    obterNomeArquivo(chaveArquivo)
            );

            s3Client.putObject(bucketDestino, nomeNovoArquivo, arquivoParaSalvar, metadata);

            context.getLogger().log("Sucesso: Arquivo salvo em " + bucketDestino + "/" + nomeNovoArquivo);
            return "Sucesso: Arquivo processado e salvo em " + bucketDestino + "/" + nomeNovoArquivo;

        } catch (Exception e) {
            context.getLogger().log("Erro Fatal: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String determinarPasta(String chaveArquivo) {
        String nomeArquivo = obterNomeArquivo(chaveArquivo).toLowerCase();

        if (nomeArquivo.contains("maquina")) {
            return "Maquina";
        } else if (nomeArquivo.contains("processo")) {
            return "Processos";
        } else {
            if (chaveArquivo.contains("Maquina/")) {
                return "Maquina";
            } else if (chaveArquivo.contains("Processos/")) {
                return "Processos";
            }
            return "Outros";
        }
    }

    private String obterNomeArquivo(String chaveCompleta) {
        if (chaveCompleta.contains("/")) {
            String[] partes = chaveCompleta.split("/");
            return partes[partes.length - 1];
        }
        return chaveCompleta;
    }
}