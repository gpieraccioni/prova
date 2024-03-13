package it.qilab.sonarfixer.httputil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;

/**
 * Classe per verificare se una risposta HTTP ha avuto successo (cioè ha uno stato compreso tra 200 e 399) 
 * o se ha generato un errore (cioè ha uno stato inferiore a 200 o maggiore o uguale a 400). 
 * Se si verifica un errore nella risposta HTTP, viene lanciata un'eccezione HttpResponseException che 
 * può essere catturata e gestita dal chiamante del metodo.
 */
public class HttpResponseValidator {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RED = "\u001B[31m";
	
    public static void validateResponse(HttpResponse response) throws HttpResponseException {
    	int status = response.getStatusLine().getStatusCode();
	    String coloredStatus;

	    if (status == 200) {
	        coloredStatus = ANSI_GREEN + status + ANSI_RESET;
	    } else if (status >= 400 && status < 600) {
	        coloredStatus = ANSI_RED + status + ANSI_RESET;
	    } else {
	        coloredStatus = String.valueOf(status);
	    }

	    System.out.println("stato: " + coloredStatus);

	    if (status < 200 || status >= 400) {
	        throw new HttpResponseException(status, response.getStatusLine().getReasonPhrase());
	    }
    }
}