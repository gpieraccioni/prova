package it.qilab.sonarfixer.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.qilab.sonarfixer.fixer.Container_Action_ViolatedLine;

public class MapAggregator {
    private Map<String, List<Container_Action_ViolatedLine>> aggregatedMap = new HashMap<>();

    public void aggregate(Map<String, List<Container_Action_ViolatedLine>> fixerMap) {
        for (Map.Entry<String, List<Container_Action_ViolatedLine>> entry : fixerMap.entrySet()) {
            String key = entry.getKey();
            List<Container_Action_ViolatedLine> valueList = entry.getValue();

            for (Container_Action_ViolatedLine container : valueList) {
                String action = container.getAction();
                int line = container.getViolatedLine();

                if (!aggregatedMap.containsKey(key)) {
                    aggregatedMap.put(key, new ArrayList<>());
                }

                aggregatedMap.get(key).add(new Container_Action_ViolatedLine(action, line));
            }
        }
    }
    
    public void clearAggregatedMap() {
        aggregatedMap.clear();
    }
    
    public Map<String, List<Container_Action_ViolatedLine>> getAggregatedMap() {
        return aggregatedMap;
    }
    
    public void fixAllIssues()
    {
    	Set<String> modifiedLinesSet = new HashSet<>(); // Insieme per tenere traccia delle righe modificate
    	for (Map.Entry<String, List<Container_Action_ViolatedLine>> entry : aggregatedMap.entrySet()) {
            String key = entry.getKey();
            List<Container_Action_ViolatedLine> containerList = entry.getValue();

            List<String> lines;
            try {
                lines = Files.readAllLines(Paths.get(key));
            } catch (IOException e) {
                e.printStackTrace();
                continue; // Passa alla prossima chiave in caso di errore
            }

            List<String> modifiedLines = new ArrayList<>(lines);

            // Esegui prima le azioni "remove"
            for (Container_Action_ViolatedLine container : containerList) {
                if ("remove".equals(container.getAction())) {
                	int line = container.getViolatedLine();
                    modifiedLines.set(line - 1, ""); // L'indice della lista parte da 0
                }
            }
            
            // Esegui le azioni "replace"
            for (Container_Action_ViolatedLine container : containerList) {
                if (container.getAction().startsWith("replace:")) {
                    int line = container.getViolatedLine();                  
                    String[] parts = container.getAction().split(":", 2); // Divido solo al primo ":"
                    if (parts.length == 2) {
                        String textToReplace = parts[1]; // Prendo la parte dopo il ":"
                        modifiedLines.set(line - 1, textToReplace); // L'indice della lista parte da 0
                    }
                }
            }
            
            // Esegui le azioni "add"
            for (Container_Action_ViolatedLine container : containerList) {
                if (container.getAction().startsWith("add:")) {
                    int line = container.getViolatedLine();
                    String[] parts = container.getAction().split(":", 2); // Divido solo al primo ":"
                    if (parts.length == 2) {
                        String textToAdd = parts[1]; // Prendo la parte dopo il ":"
                        modifiedLines.add(line, textToAdd); // L'indice della lista parte da 0
                    }
                }
            }
       
            try {
                Files.write(Paths.get(key), modifiedLines);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}