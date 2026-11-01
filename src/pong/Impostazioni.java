// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 2: le impostazioni. Scadenza: domenica 1 novembre 2026.
package pong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Le impostazioni di una partita di Pong: quanti punti servono per vincere, con quale scarto
 * minimo, a che velocità va la palla. È un record (lezione 4): una volta create, non cambiano.
 *
 * IL PEZZO MANCANTE: il costruttore che rifiuta i valori sbagliati e il metodo leggi.
 * Ogni metodo ha il suo contratto, nel commento sopra. I test ufficiali (cartella test)
 * controllano i contratti.
 */
public record Impostazioni(int puntiPerVincere, int scartoMinimo, int velocitaPalla) {

    /** Le impostazioni standard: 11 punti per vincere, scarto minimo 2, velocità della palla 5. */
    public static final Impostazioni STANDARD = new Impostazioni(11, 2, 5);

    /**
     * Il costruttore (lezione 7: un record che nasce giusto).
     * Promette: se i tre valori sono nei loro intervalli, le impostazioni con quei valori.
     * Lancia IllegalArgumentException se puntiPerVincere non è fra 1 e 99, se scartoMinimo
     * non è fra 1 e 10, o se velocitaPalla non è fra 1 e 20 (estremi compresi). Il messaggio
     * dice quale valore è sbagliato e perché.
     */
    public Impostazioni {
        if (puntiPerVincere < 1 || puntiPerVincere > 99) {
            throw new IllegalArgumentException("puntiPerVincere non valido (" + puntiPerVincere + "): deve essere compreso tra 1 e 99");
        }
        if (scartoMinimo < 1 || scartoMinimo > 10) {
            throw new IllegalArgumentException("scartoMinimo non valido (" + scartoMinimo + "): deve essere compreso tra 1 e 10");
        }
        if (velocitaPalla < 1 || velocitaPalla > 20) {
            throw new IllegalArgumentException("velocitaPalla non valida (" + velocitaPalla + "): deve essere compresa tra 1 e 20");
        }
     }

    /**
     * Legge le impostazioni dalle righe di un file, una impostazione per riga.
     * Pretende: righe non è null e non contiene null.
     * Promette:
     *  - le righe vuote (o di soli spazi) e quelle che, tolti gli spazi iniziali, cominciano
     *    con # sono ignorate;
     *  - ogni altra riga è nella forma  chiave = valore , con spazi qualsiasi attorno alla chiave,
     *    al segno = e al valore; le chiavi sono puntiPerVincere, scartoMinimo, velocitaPalla;
     *  - un'impostazione che nelle righe non c'è prende il valore standard;
     *  - il risultato sono le impostazioni lette.
     * Lancia IllegalArgumentException, con un messaggio che comincia con «riga N: » (N è il numero
     * della riga, contando da 1 tutte le righe, anche quelle vuote e i commenti), se in una riga:
     *  1. manca il segno = ;
     *  2. la chiave non è una delle tre;
     *  3. il valore non è un numero intero;
     *  4. la stessa chiave c'era già in una riga precedente;
     *  5. il valore è fuori dal suo intervallo (gli intervalli del costruttore).
     * Se ci sono errori in più righe, il messaggio è quello della riga più in alto.
     */
    public static Impostazioni leggi(List<String> righe) {
        Objects.requireNonNull(righe, "La lista delle righe non può essere null");

        int punti = STANDARD.puntiPerVincere();
        int scarto = STANDARD.scartoMinimo();
        int velocita = STANDARD.velocitaPalla();

        boolean puntiImpostati = false;
        boolean scartoImpostato = false;
        boolean velocitaImpostata = false;

        for (int i = 0; i < righe.size(); i++) {
            String riga = righe.get(i);
            if (riga == null) {
                throw new IllegalArgumentException("riga " + (i + 1) + ": la riga non può essere null");
            }

            String rigaTrim = riga.trim();
            if (rigaTrim.isEmpty() || rigaTrim.startsWith("#")) {
                continue;
            }

            // Usiamo riga.split("=", 2):
            String[] splitParti = riga.split("=", 2);
            if (splitParti.length != 2) {
                throw new IllegalArgumentException("riga " + (i + 1) + ": manca il segno =");
            }

            String chiave = splitParti[0].trim();
            String valore = splitParti[1].trim();

            int val;
            try {
                val = Integer.parseInt(valore);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("riga " + (i + 1) + ": il valore non è un numero intero");
            }

            switch (chiave) {
                case "puntiPerVincere":
                    if (puntiImpostati) {
                        throw new IllegalArgumentException("riga " + (i + 1) + ": la chiave puntiPerVincere è già presente");
                    }
                    if (val < 1 || val > 99) {
                        throw new IllegalArgumentException("riga " + (i + 1) + ": puntiPerVincere fuori intervallo");
                    }
                    punti = val;
                    puntiImpostati = true;
                    break;

                case "scartoMinimo":
                    if (scartoImpostato) {
                        throw new IllegalArgumentException("riga " + (i + 1) + ": la chiave scartoMinimo è già presente");
                    }
                    if (val < 1 || val > 10) {
                        throw new IllegalArgumentException("riga " + (i + 1) + ": scartoMinimo fuori intervallo");
                    }
                    scarto = val;
                    scartoImpostato = true;
                    break;

                case "velocitaPalla":
                    if (velocitaImpostata) {
                        throw new IllegalArgumentException("riga " + (i + 1) + ": la chiave velocitaPalla è già presente");
                    }
                    if (val < 1 || val > 20) {
                        throw new IllegalArgumentException("riga " + (i + 1) + ": velocitaPalla fuori intervallo");
                    }
                    velocita = val;
                    velocitaImpostata = true;
                    break;

                default:
                    throw new IllegalArgumentException("riga " + (i + 1) + ": chiave sconosciuta");
            }
        }

        return new Impostazioni(punti, scarto, velocita);
    }

    /**
     * Legge le impostazioni da un file di testo.
     * Promette: come leggi, con le righe del file.
     * Lancia IOException se il file non si può leggere (per esempio non esiste): non la cattura,
     * la lascia a chi chiama. Lancia IllegalArgumentException come leggi.
     */
    public static Impostazioni daFile(Path file) throws IOException {
        return leggi(Files.readAllLines(file));
    }
}