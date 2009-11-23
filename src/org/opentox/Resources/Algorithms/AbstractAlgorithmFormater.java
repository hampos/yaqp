package org.opentox.Resources.Algorithms;

import java.util.ArrayList;
import org.restlet.representation.StringRepresentation;

/**
 *
 * @author OpenTox - http://www.opentox.org
 * @author Sopasakis Pantelis
 * @author Sarimveis Harry
 */
public abstract class AbstractAlgorithmFormater{

    
    public AlgorithmMetaInf metainf;

    protected String
            rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
            dc="http://purl.org/dc/elements/1.1/",
            ot="http://www.opentox.org/api/1.1#",
            xsd="http://www.w3.org/2001/XMLSchema#",
            rdfs="http://www.w3.org/2000/01/rdf-schema#",
            owl="http://www.w3.org/2002/07/owl#";

    /**
     * Constructor of the Abstract Formater.
     */
    public AbstractAlgorithmFormater() {}

    public abstract StringRepresentation getStringRepresentation();
    
}
