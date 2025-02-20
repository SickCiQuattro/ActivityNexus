package model;

public class ConversioneGerarchie<T> {
    private final T primo;
    private final T secondo;

    public ConversioneGerarchie(T first, T second) {
        this.primo = first;
        this.secondo = second;
    }

    public T getPrimo() {
        return primo;
    }

    public T getSecondo() {
        return secondo;
    }

// lavoro qua per capire poi dove mettere il tutto, forse nel Controller vediamo

// due nodi di gerarchie anche diverse
//nodoPartenza;
//nodoArrivo;
//
//if (nodoPartenza.root().data().equals(nodoArrivo.root().data())) {
//    return Database.getGerarchia(nodoPartenza.root()).getFattoreConversione(nodoPartenza.level(), nodoArrivo.level());
//}
//a = Database.getGerarchia(nodoPartenza.root()).getFattoreConversione(nodoPartenza.level(), Database.getGerarchia(nodoPartenza.root()).getNodoRiferimento().level()); // getNodoRiferimento().level() = getLivelloRiferimento()
//b = Database.getGerarchia(nodoArrivo.root()).getFattoreConversione(Database.getGerarchia(nodoArrivo.root()).getNodoRiferimento().level(), nodoArrivo.level());
//c = Database.getConversioneGerarchie(nodoPartenza.root().data(), nodoArrivo.root().data());
//return a * c * b;

}
