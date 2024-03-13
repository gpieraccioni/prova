package it.qilab.sonarfixer.webapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;

import it.qilab.sonarfixer.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SonarServer {
    private final Logger logger;
    private final SonarClient client;

    public SonarServer(URI serverUri, String username, String passwordOrToken) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.client = new SonarClientImpl(serverUri, username, passwordOrToken);
    }

    public User getUser(String login) throws IOException {
        return client.getUserClient().getUser(login);
    }
    
    public Users getUsers(String p, String ps) throws IOException {
        return client.getUserClient().getUsers(p, ps);
    }
    
    public Issues getIssues(String componentId, String ruleKey) throws IOException {
    	return client.getIssueClient().getAllIssuesFromRuleKey(componentId, ruleKey);
    }
    
    public CeComponentAnalysis getCurrentComponentAnalysis(String componentId) throws IOException {
    	return client.getCeClient().getCurrentComponentCe(componentId);
    }
    
    public void executeScanner(String projectKey, String sonarHostUrl, String sonarLogin, String workingDirectory, boolean check) {
    	    String osName = System.getProperty("os.name").toLowerCase();

    	    String mvnCommand = "mvn";  

    	    if (osName.contains("win")) {
    	        mvnCommand = "mvn.cmd"; 
    	    }
    	    
    	    ProcessBuilder processBuilder = new ProcessBuilder(mvnCommand,
    	    		"clean",
    	    		"verify",
    	    	    "sonar:sonar",
    	    	    "-DskipTests",
    	    	    "-Dsonar.projectName=" + projectKey,
    	    	    "-Dsonar.projectKey=" + projectKey,
    	    	    "-Dsonar.sources=src/main/java",
    	    	    "-Dsonar.java.binaries=target/classes",
    	    	    "-Dsonar.host.url=" + sonarHostUrl,
    	    	    "-Dsonar.login=" + sonarLogin);
            
            processBuilder.directory(new File(workingDirectory));
            processBuilder.redirectErrorStream(true);
            System.out.println("");
            System.out.println("\u001B[34m"+"--- NEW SCAN STARTED ---"+"\u001B[0m");

    	try {
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            if(check) {
            	while ((line = bufferedReader.readLine()) != null) {
            		System.out.println(line);
            	}
            }
            else {
            	System.out.println("     ... RUNNING ...    ");
            	while ((line = bufferedReader.readLine()) != null) {
            		
            	}
            }
            process.waitFor();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("\u001B[34m"+"---- SCAN  FINISHED ----"+"\u001B[0m");
    }
    
    public void getScannerAnalysisOnComponent(String componentId) throws IOException {
        System.out.println("");
        System.out.println("\u001B[34m" + "-------------- RESULT --------------" + "\u001B[0m");
        CeComponentAnalysis currentComponentAnalysis = client.getCeClient().getCurrentComponentCe(componentId);
        System.out.println("Id scansione: " + currentComponentAnalysis.getCurrent().getId());
        System.out.println("type: " + currentComponentAnalysis.getCurrent().getType());
        System.out.println("Stato: " + currentComponentAnalysis.getCurrent().getStatus());
        long totalMilliseconds = currentComponentAnalysis.getCurrent().getExecutionTimeMs();
        long totalSeconds = totalMilliseconds / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long milliseconds = totalMilliseconds % 1000;
        String formattedTime = String.format("%d:%02d.%03d", minutes, seconds, milliseconds);
        System.out.println("Tempo totale: " + formattedTime + "s");
        String startDateTimeString = currentComponentAnalysis.getCurrent().getSubmittedAt();
        
        // Rimuovi l'offset "+0000" dalla stringa della data e dell'ora
        String formattedStartDateTime = startDateTimeString.substring(0, startDateTimeString.length() - 5);
        
        // Analizza la data e l'ora manualmente
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(formattedStartDateTime, formatter);
        
        // Converti in ZonedDateTime con offset UTC
        ZonedDateTime utcDateTime = localDateTime.atZone(ZoneId.of("UTC"));
        
        // Converti in orario italiano (Europe/Rome)
        ZoneId italianZone = ZoneId.of("Europe/Rome");
        ZonedDateTime dateTimeItalian = utcDateTime.withZoneSameInstant(italianZone);
        
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = dateTimeItalian.format(outputFormatter);
        System.out.println("Inizio: " + formattedDateTime);
        System.out.println("\u001B[34m" + "------------------------------------" + "\u001B[0m");
        System.out.println("");
        
    }
    
    public ScanInfo getScanInfo(String componentId) throws IOException {
    	CeComponentAnalysis currentComponentAnalysis = client.getCeClient().getCurrentComponentCe(componentId);
    	String scanId ="00000000";
        String status = "SUCCESS";
    	if(currentComponentAnalysis.getCurrent() !=null) {
    		scanId = currentComponentAnalysis.getCurrent().getId();
	        status = currentComponentAnalysis.getCurrent().getStatus();
    	}
        return new ScanInfo(scanId, status);
    	
    }
}
 
