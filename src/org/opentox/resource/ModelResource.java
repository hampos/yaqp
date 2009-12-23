package org.opentox.resource;

import java.io.File;
import java.net.URISyntaxException;
import org.opentox.resource.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import org.opentox.OpenToxApplication;
import org.opentox.prediction.MlrPredictor;
import org.opentox.prediction.Predictor;
import org.opentox.prediction.SvcPredictor;
import org.opentox.prediction.SvmPredictor;
import org.opentox.media.OpenToxMediaType;
import org.opentox.algorithm.AlgorithmEnum;
import org.opentox.client.opentoxClient;
import org.opentox.database.ModelsDB;
import org.opentox.formatters.ModelFormatter;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 * 
 * This resource, returns a representation of a model and supports the POST operation
 * for prediction.
 * @author OpenTox - http://www.opentox.org/
 * @author Sopasakis Pantelis
 * @author Sarimveis Harry
 * @version 1.3.3 (Last update: Dec 20, 2009)
 */
public class ModelResource extends AbstractResource {

    private static final long serialVersionUID = 26047187263491246L;
    private Status internalStatus = Status.SUCCESS_ACCEPTED;
    private String model_id;
    private AlgorithmEnum algorithm;

    /**
     * Default Class Constructor.Available MediaTypes of Variants: TEXT_XML
     * @param context
     * @param request
     * @param response
     */
    @Override
    public void doInit() throws ResourceException {
        super.doInit();
        Collection<Method> allowedMethods = new ArrayList<Method>();
        allowedMethods.add(Method.GET);
        allowedMethods.add(Method.POST);
        getAllowedMethods().addAll(allowedMethods);
        super.doInit();
        Collection<Variant> variants = new ArrayList<Variant>();
        variants.add(new Variant(MediaType.APPLICATION_RDF_XML));
        variants.add(new Variant(MediaType.APPLICATION_RDF_TURTLE));
        variants.add(new Variant(OpenToxMediaType.TEXT_TRIPLE));
        variants.add(new Variant(OpenToxMediaType.TEXT_N3));
        variants.add(new Variant(MediaType.TEXT_HTML));
        variants.add(new Variant(MediaType.APPLICATION_XML));
        getVariants().put(Method.GET, variants);
        model_id = Reference.decode(getRequest().getAttributes().get("model_id").toString());
        algorithm = ModelsDB.getAlgorithm(model_id);
    }

    /**
     *
     * @param variant
     * @return StringRepresentation
     */
    @Override
    protected Representation get(Variant variant) {
        System.out.println(variant.getMediaType());
        ModelFormatter modelFormatter = new ModelFormatter(Integer.parseInt(model_id));
        return modelFormatter.getStringRepresentation(variant.getMediaType());

    }

    @Override
    protected Representation post(Representation entity) {
        Representation rep = null;

        /** Get the posted parameters **/
        Form form = new Form(entity);

        Predictor predictor = null;
        switch (algorithm) {
            case svc:
                predictor = new SvcPredictor();
                break;
            case svm:
                predictor = new SvmPredictor();
                break;
            case mlr:
                predictor = new MlrPredictor();
                break;
        }
        rep = predictor.predict(form, model_id);

        return rep;
    }


    @Override
    protected Representation delete() {
        String responseText = null;
        try {
            if (opentoxClient.IsMimeAvailable(new URI("http://localhost:3000/model/" + model_id),
                    MediaType.TEXT_XML, false)) {
                ModelsDB.removeModel(model_id);
                File modelFile = new File(Directories.modelRdfDir + "/" + model_id);
                responseText = "The resource was detected and removed from OT database successfully!";
                if (modelFile.exists()) {
                    boolean success = false;//modelFile.renameTo(new File(Directories.trash, modelFile.getName()));
                    if (success) {
                        OpenToxApplication.opentoxLogger.severe("Model : " + model_id + " moved to trash!");
                    }
                } else {
                    OpenToxApplication.opentoxLogger.severe("Model File not found! Will not apply DELETE!");
                }
            } else {
                getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                responseText = "Model not found on the server!\n";
            }
        } catch (URISyntaxException ex) {
            responseText = "Model Not Found!\n";
            OpenToxApplication.opentoxLogger.severe("Model URI : http://localhost:3000/model/" + model_id
                    + "seems to be invalid!");
        }
        return new StringRepresentation(responseText + "\n");
    }

    public void setInternalStatus(Status status) {
        if (((internalStatus.getCode() >= 400) && (internalStatus.getCode() < 500) && status.getCode() >= 400)
                || ((internalStatus.getCode()) < 300)) {
            this.internalStatus = status;
        }
    }

    /**
     * Returns the status that the trainer suggests.
     * @return The internal status of the Trainer.
     */
    public Status getInternalStatus() {
        return internalStatus;
    }
}// End of class
