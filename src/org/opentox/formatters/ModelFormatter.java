package org.opentox.formatters;

import com.hp.hpl.jena.ontology.OntModel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opentox.media.OpenToxMediaType;
import org.opentox.resource.AbstractResource.Directories;
import org.opentox.namespaces.Namespace;
import org.opentox.util.RepresentationFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

/**
 * Reads a stored or online model (in RDF format) and returns it in a prefered
 * MIME type (format).
 * @author Sopasakis Pantelis
 */
public class ModelFormatter extends AbstractModelFormatter {

    public ModelFormatter(int model_id){
        super(model_id);
    }

    @Override
    public Representation getRepresentation(MediaType mediatype) {
        Representation rep = null;
        if (    (MediaType.TEXT_PLAIN.equals(mediatype))||
                (MediaType.APPLICATION_RDF_XML.equals(mediatype))||
                (MediaType.APPLICATION_RDF_TURTLE.equals(mediatype))||
                (OpenToxMediaType.TEXT_TRIPLE.equals(mediatype))||
                (OpenToxMediaType.TEXT_N3.equals(mediatype))||
                (MediaType.TEXT_HTML.equals(mediatype))
        ){
            String Lang="RDF/XML";
                    if (MediaType.APPLICATION_RDF_TURTLE.equals(mediatype)){
                        Lang="TTL";
                    }else if (OpenToxMediaType.TEXT_TRIPLE.equals(mediatype)){
                        Lang="N-TRIPLE";
                    }else if (OpenToxMediaType.TEXT_N3.equals(mediatype)){
                        Lang="N3";
                    }
            try {
                FileInputStream in = new FileInputStream(Directories.modelRdfDir + "/" + model_id);
                try {

                    OntModel jenaModel = Namespace.createModel();
                    jenaModel.read(in, null);
                    ByteArrayOutputStream str = new ByteArrayOutputStream();
                    jenaModel.write(str, Lang);
                    rep = new StringRepresentation(str.toString());
                } catch (Exception ex) {
                    errorRep.append(ex, "Model Parsing Error: Model could not be parsed successfully!", Status.SERVER_ERROR_INTERNAL);
                }
            } catch (FileNotFoundException ex) {
                errorRep.append(ex, "Model not found!", Status.CLIENT_ERROR_NOT_FOUND);
            }
        }else if (MediaType.APPLICATION_XML.equals(mediatype)){
            RepresentationFactory rf = new RepresentationFactory(Directories.modelPmmlDir+"/"+model_id);
            try {
                rep = new StringRepresentation(rf.getString().toString(), mediatype);
            } catch (FileNotFoundException ex) {
                errorRep.append(ex, "Model not found!", Status.CLIENT_ERROR_NOT_FOUND);
            } catch (IOException ex) {
                errorRep.append(ex, "Input/Output error while reading a model file.", Status.SERVER_ERROR_INTERNAL);
                Logger.getLogger(ModelFormatter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return errorRep.getErrorLevel()==0 ? (StringRepresentation) rep : errorRep;
    }

}
