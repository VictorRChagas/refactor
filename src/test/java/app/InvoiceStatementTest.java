package app;

import br.com.Invoice;
import br.com.Main;
import br.com.Play;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvoiceStatementTest {

    @Test
    void shouldReturnStatementForBigCo() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Read and parse JSON files
        String invoiceJson = new String(Files.readAllBytes(Paths.get("src/main/java/br/com/invoices.json")));
        String playsJson = new String(Files.readAllBytes(Paths.get("src/main/java/br/com/plays.json")));

        Invoice invoice = mapper.readValue(invoiceJson, Invoice.class);
        Map<String, Play> plays = mapper.readValue(playsJson, new TypeReference<>() {});

        String expected = """
        Statement for BigCo
        Hamlet: 650.00 (55 seats)
        As You Like It: 580.00 (35 seats)
        Othello: 500.00 (40 seats)'
        Amount owed is 1730.00
        You earned 47 credits
        """;

        String received = Main.statement(invoice, plays);
        assertEquals(expected.trim(), received.trim());
    }
}
