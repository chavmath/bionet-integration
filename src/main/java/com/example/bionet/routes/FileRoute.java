package com.example.bionet.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileRoute extends RouteBuilder {
    @Override
    public void configure() {

        // Manejo global de errores
        onException(Exception.class)
                .handled(true)
                .log("Archivo enviado a /error: ${header.CamelFileName} | Motivo: ${exception.message}")
                .to("file:error");

        from("file:input-labs?noop=true&include=.*\\.csv")
                .routeId("csv-file-ingestion")
                .log("📥 Procesando archivo: ${header.CamelFileName}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) {
                        String content = exchange.getIn().getBody(String.class);
                        String[] lines = content.split("\n");

                        if (lines.length < 2 || !lines[0].toLowerCase().contains("paciente_id")) {
                            throw new IllegalArgumentException("Encabezado faltante o archivo vacío");
                        }

                        StringBuilder insertCommands = new StringBuilder();

                        for (int i = 1; i < lines.length; i++) {
                            String[] fields = lines[i].split(",");

                            if (fields.length < 5 ||
                                    fields[0].trim().isEmpty() ||
                                    fields[1].trim().isEmpty() ||
                                    fields[2].trim().isEmpty() ||
                                    fields[3].trim().isEmpty() ||
                                    fields[4].trim().isEmpty()) {
                                throw new IllegalArgumentException("Fila incompleta en línea " + (i + 1));
                            }

                            String sql = String.format(
                                    "INSERT INTO resultados_examenes (laboratorio_id, paciente_id, tipo_examen, resultado, fecha_examen) "
                                            +
                                            "VALUES (%d, '%s', '%s', '%s', '%s');",
                                    Integer.parseInt(fields[0].trim()),
                                    fields[1].trim(),
                                    fields[2].trim(),
                                    fields[3].trim(),
                                    fields[4].trim());

                            insertCommands.append(sql).append("\n");
                        }

                        exchange.getIn().setBody(insertCommands.toString());
                    }
                })
                .log("✅ Archivo válido: ${header.CamelFileName}")
                .split(body().tokenize("\n")).streaming()
                .to("jdbc:dataSource")
                .end()
                .to("file:processed");
    }
}
