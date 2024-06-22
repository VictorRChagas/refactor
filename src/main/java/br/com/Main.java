package br.com;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Read and parse JSON files
            String invoicesJson = new String(Files.readAllBytes(Paths.get("src/main/java/br/com/invoices.json")));
            String playsJson = new String(Files.readAllBytes(Paths.get("src/main/java/br/com/plays.json")));

            Invoice invoice = mapper.readValue(invoicesJson, Invoice.class);
            Map<String, Play> plays = mapper.readValue(playsJson, new TypeReference<Map<String, Play>>() {
            });

            String result = statement(invoice, plays);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String statement(Invoice invoice, Map<String, Play> plays) {
        int totalAmount = 0;
        int volumeCredits = 0;
        StringBuilder result = new StringBuilder("Statement for " + invoice.customer() + "\n");

        for (Performance perf : invoice.performances()) {
            Play play = plays.get(perf.playID());
            int thisAmount = 0;

            switch (play.type()) {
                case "tragedy":
                    thisAmount = 40000;
                    if (perf.audience() > 30) {
                        thisAmount += 1000 * (perf.audience() - 30);
                    }
                    break;
                case "comedy":
                    thisAmount = 30000;
                    if (perf.audience() > 20) {
                        thisAmount += 10000 + 500 * (perf.audience() - 20);
                    }
                    thisAmount += 300 * perf.audience();
                    break;
                default:
                    throw new IllegalArgumentException("unknown type: " + play.type());
            }

            // add volume credits
            volumeCredits += Math.max(perf.audience() - 30, 0);
            // add extra credit for every ten comedy attendees
            if ("comedy".equals(play.type())) volumeCredits += Math.floor(perf.audience() / 5);

            // print line for this order
            result.append(String.format(Locale.US, "%s: %.2f (%d seats)\n", play.name(), (thisAmount / 100.0), perf.audience()));
            totalAmount += thisAmount;
        }

        result.append(String.format(Locale.US, "Amount owed is %.2f\n", totalAmount / 100.0));
        result.append(String.format("You earned %d credits\n", volumeCredits));
        return result.toString();
    }
}

