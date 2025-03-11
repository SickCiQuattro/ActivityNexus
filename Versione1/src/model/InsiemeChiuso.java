package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta un insieme chiuso di proposte che formano un ciclo di scambio
 * valido.
 * <p>
 * Un insieme chiuso contiene tutte le proposte coinvolte in un ciclo di scambio
 * e
 * traccia gli scambi specifici che devono avvenire tra i fruitori.
 */
public class InsiemeChiuso implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Proposta> proposte;
    private final List<Scambio> scambi;
    private final LocalDateTime dataCreazione;
    private boolean notificato;

    /**
     * Crea un nuovo insieme chiuso.
     */
    public InsiemeChiuso() {
        this.proposte = new ArrayList<>();
        this.scambi = new ArrayList<>();
        this.dataCreazione = LocalDateTime.now();
        this.notificato = false;
    }

    /**
     * Aggiunge una proposta all'insieme chiuso.
     * 
     * @param proposta la proposta da aggiungere
     */
    public void aggiungiProposta(Proposta proposta) {
        if (!proposte.contains(proposta)) {
            proposte.add(proposta);
            proposta.setInsiemeChiuso(this);
        }
    }

    /**
     * Aggiunge uno scambio all'insieme chiuso.
     * 
     * @param scambio lo scambio da aggiungere
     */
    public void aggiungiScambio(Scambio scambio) {
        scambi.add(scambio);
    }

    /**
     * Restituisce la lista di proposte nell'insieme chiuso.
     * 
     * @return le proposte nell'insieme chiuso
     */
    public List<Proposta> getProposte() {
        return Collections.unmodifiableList(proposte);
    }

    /**
     * Restituisce la lista di scambi nell'insieme chiuso.
     * 
     * @return gli scambi nell'insieme chiuso
     */
    public List<Scambio> getScambi() {
        return Collections.unmodifiableList(scambi);
    }

    /**
     * Restituisce la data di creazione dell'insieme chiuso.
     * 
     * @return la data di creazione
     */
    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    /**
     * Imposta lo stato di notifica dell'insieme chiuso.
     * 
     * @param notificato se l'insieme è stato notificato
     */
    public void setNotificato(boolean notificato) {
        this.notificato = notificato;
    }

    /**
     * Verifica se l'insieme chiuso è stato notificato.
     * 
     * @return true se è stato notificato, false altrimenti
     */
    public boolean isNotificato() {
        return notificato;
    }

}