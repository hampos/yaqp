/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opentox.Resources.Algorithms;

import java.util.ArrayList;
import org.opentox.MediaTypes.OpenToxMediaType;
import org.restlet.representation.StringRepresentation;

/**
 *
 * @author OpenTox - http://www.opentox.org
 * @author Sopasakis Pantelis
 * @author Sarimveis Harry
 */
public class AlgorithmYamlFormater extends AbstractAlgorithmFormater{


    public StringRepresentation getStringRepresentation() {
        StringBuilder builder = new StringBuilder();
        builder.append("---\nAlgorithm:\n");
        builder.append("    name : " + title + "\n");
        builder.append("    id : " + identifier + "\n");
        builder.append("    AlgorithmType : " + algorithmType + "\n");
        builder.append("    Parameters:\n");
        if (Parameters[0].length != 3) {
            System.err.println("ERROR!!! Invalid Parameters Element!");
        } else {
            for (int i = 0; i < Parameters.length; i++) {
                builder.append("        -" + Parameters[i][0] + ":\n");
                builder.append("            type:" + Parameters[i][1]);
                builder.append("            defaultValue:" + Parameters[i][2]);
            }
        }
        builder.append("    statisticsSupported:\n");
        if (!statisticsSupported.isEmpty()) {
            for (int i = 0; i < statisticsSupported.size(); i++) {
                builder.append("            -" + statisticsSupported.get(i) + "\n");
            }
        }
        return new StringRepresentation(builder.toString(), OpenToxMediaType.APPLICATION_YAML);
    }

}