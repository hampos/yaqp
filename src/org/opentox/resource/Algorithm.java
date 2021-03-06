package org.opentox.resource;

import org.opentox.algorithm.trainer.SvcTrainer;
import org.opentox.algorithm.trainer.SvmTrainer;
import org.opentox.algorithm.trainer.MlrTrainer;
import org.opentox.algorithm.AlgorithmEnum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.opentox.media.OpenToxMediaType;
import org.opentox.algorithm.reporting.AlgorithmReporter;

import org.opentox.algorithm.reporting.AlgorithmReporter.*;
import org.opentox.interfaces.IAcceptsRepresentation;
import org.opentox.interfaces.IAlgorithmReporter;
import org.opentox.interfaces.IProvidesHttpAccess;
import org.opentox.interfaces.ITrainer;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 *
 * @author OpenTox - http://www.opentox.org/
 * @author Sopasakis Pantelis
 * @author Sarimveis Harry
 * @version 1.3.3 (Last update: Dec 20, 2009)
 */
public class Algorithm extends OTResource
        implements IAcceptsRepresentation, IProvidesHttpAccess {

    private static final long serialVersionUID = 8992374761250990L;
    /**
     * The id of the regression algorithm.
     * This can be either mlr or svm.
     */
    private volatile AlgorithmEnum algorithm;

    /**
     * Initialize the resource. Supported Variants are:
     * <ul>
     * <li>application/rdf+xml (default)</li>
     * <li>text/uri-list</li>
     * <li>text/xml</li>
     * <li>text/x-yaml</li>
     * <li>application/json</li>
     * <li>application/x-turtle</li>
     * </ul>
     * Allowed Methods are:
     * <ul>
     * <li>GET</li>
     * <li>POST</li>
     * </ul>
     * URI:<br/>
     * http://opentox.ntua.gr:3000/algorithm/id
     * @throws ResourceException
     */
    @Override
    public void doInit() throws ResourceException {
        super.doInit();
        Collection<Method> allowedMethods = new ArrayList<Method>();
        allowedMethods.add(Method.GET);
        allowedMethods.add(Method.POST);
        getAllowedMethods().addAll(allowedMethods);

        List<Variant> variants = new ArrayList<Variant>();
        /** default variant : **/
        variants.add(new Variant(MediaType.APPLICATION_RDF_XML));  //-- (application/rdf+xml)
        /** other supported variants: **/
        variants.add(new Variant(MediaType.TEXT_URI_LIST));
        variants.add(new Variant(MediaType.TEXT_XML));
        variants.add(new Variant(OpenToxMediaType.TEXT_YAML));
        variants.add(new Variant(MediaType.APPLICATION_JSON));
        variants.add(new Variant(MediaType.APPLICATION_RDF_TURTLE));  //-- (application/x-turtle)
        variants.add(new Variant(OpenToxMediaType.TEXT_TRIPLE));  //-- (text/x-triple)
        variants.add(new Variant(OpenToxMediaType.TEXT_N3));  //-- (text/rdf+n3)
        getVariants().put(Method.GET, variants);

        /** The algorithm id can be one of {svm, mlr, svc} **/
        String alg = Reference.decode(getRequest().getAttributes().get("id").toString());
        algorithm = AlgorithmEnum.getAlgorithmEnum(alg);

    }

    /**
     * Implementation of the GET method.
     * Returns XML representations for the supported regression algorithms
     * @param variant
     * @return XML representation of algorithm
     */
    @Override
    public Representation get(Variant variant) {


        Representation representation = null;
        MediaType mediatype = variant.getMediaType();

        if (mediatype.equals(MediaType.TEXT_URI_LIST)) {
            ReferenceList list = new ReferenceList();
            list.add(getReference());
            representation = list.getTextRepresentation();
        } else {
            switch (algorithm) {
                case svm:
                case mlr:
                case svc:
                    IAlgorithmReporter algorithm_reporter = new AlgorithmReporter();
                    representation = algorithm_reporter.formatedRepresntation(mediatype, algorithm);
                    break;
                default:
                    getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                    representation = new StringRepresentation("Algorithm Not Found!\n", MediaType.TEXT_PLAIN);
                    break;
            }
        }
        return representation;
    }

    /**
     *
     * @param entity POSTed data
     * @return Representation
     * @throws ResourceException
     */
    @Override
    public Representation post(Representation entity)
            throws ResourceException {


        Representation representation = null;
        Status status = Status.SUCCESS_ACCEPTED;

        ITrainer trainer = null;


        switch (algorithm) {
            case mlr:
                trainer = new MlrTrainer(new Form(entity), this);
                representation = trainer.train();
                break;
            case svm:
                trainer = new SvmTrainer(new Form(entity), this);
                representation = trainer.train();
                break;
            case svc:
                trainer = new SvcTrainer(new Form(entity), this);
                representation = trainer.train();
                break;
            default:
                representation = new StringRepresentation("Unknown Algorithm (404)!\n",
                        MediaType.TEXT_PLAIN);
                status = Status.CLIENT_ERROR_NOT_FOUND;
                getResponse().setStatus(status);
                return new StringRepresentation("Algorithm Not Found!\n");
        }


        getResponse().
                setStatus(trainer.getErrorRep().getStatus().getCode() == 202
                ? Status.SUCCESS_OK : trainer.getErrorRep().getStatus());
        return representation;
    }
}
