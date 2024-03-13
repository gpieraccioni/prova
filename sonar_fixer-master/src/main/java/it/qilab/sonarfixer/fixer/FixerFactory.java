package it.qilab.sonarfixer.fixer;

import java.io.IOException;

import it.qilab.sonarfixer.model.Issues;

public class FixerFactory {
	
	/**
     * Metodo della Factory per creare fixer alle regole specifiche
     * @param
     *  - ruleKey: id della regola Sonar Qube (es: "java:S1068")
     *  - issues: oggetto issues che contiene tutte le violazioni specifiche riguardanti la ruleKey desiderata nel progetto desiderato
     *  - localDir: stringa che rappresenta la directory locale in cui risiede il progetto locale clonato da gitlab.
     *  			Es: "C:/Users/lbiondi/Desktop/" 
     * @return return RuleFIxer se esiste un oggetto RuleFixer per la regola specificata altrimenti return null
     * @throws IOException
     */
    public static RuleFixer createFixer(String ruleKey, Issues issues, String localDir, String componentKeys) {
        if ("java:S1068".equals(ruleKey)) {
            return new S1068Fixer(issues, localDir, componentKeys);
        }
        else if ("java:S125".equals(ruleKey)) {
            return new S125Fixer(issues, localDir,componentKeys);
        }
        else if ("xml:S125".equals(ruleKey)) {
            return new XML_S125Fixer(issues, localDir,componentKeys);
        }
        else if ("java:S1118".equals(ruleKey)) {
            return new S1118Fixer(issues, localDir,componentKeys);
        }
        else if ("java:S1144".equals(ruleKey)) {
            return new S1144Fixer(issues, localDir,componentKeys);
        }
        else if ("docker:S6476".equals(ruleKey)) {
            return new Docker_S6476Fixer(issues, localDir,componentKeys);
        }
        else if ("java:S108".equals(ruleKey)) {
            return new S108Fixer(issues, localDir,componentKeys);
        }

        return null; 
    }
}

