package it.qilab.sonarfixer.httputil;

import com.google.gson.Gson;

import it.qilab.sonarfixer.model.BaseModel;

/**
 * Classe per semplificare la deserializzazione di stringhe JSON in oggetti Java. 
 * Utilizzando il metodo wrap, puoi passare una stringa JSON e la classe specifica in cui 
 * desideri deserializzare la stringa, e otterrai l'oggetto Java corrispondente. 
 * L'istanza di Gson (GSON) è creata una sola volta e può essere riutilizzata per 
 * tutte le operazioni di deserializzazione nell'applicazione.
 */
public class HttpResponseWrapper {
    public static final Gson GSON = new Gson();

    public static <T extends BaseModel> T wrap(String json, Class<T> cls) {
        return GSON.fromJson(json,cls);
    }
}
