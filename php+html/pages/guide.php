 <div id="content">
        <!--
Main Body of the page
-->
        <div id="page">
          <div id="intro">
            <h2>
               Complete Guide to the Services 
            </h2>
            <p align="justify">
               A detailed guide to the services is provided. This is intended to help both experienced and begineer users. For all implemented GET methods over a URI we provide the supported mediatypes (e.g. HTML, XML). For all implemented POST methods we provide the set of parameters and their default values. Delete methods always assume administrative skills. The guide consists of seven parts: 
            </p>
            <ol>
              <li>
                <a href="#sec1">Model Training Algorithms</a>
              </li>
              <li>
                <a href="#sec3">Operations involving Models</a>
              </li>
            </ol>
            <br/>
            <br/>
            <br/>
            <br/>
          </div>
          <div id="sec1" >
            <h2>
              <!-- Section : 1 - Algorithms -->  1. Algorithms for Training Models
            </h2>
	<br/><br/>
	    <h3>
		API
	    </h3>
		The OpenTox API for Algorithm Services can be found <a href="http://opentox.org/dev/apis/api-1.1/Algorithm">here</a>.
		<br/><br/>
            <h3>
               List of involved URIs: 
            </h3>
            <ol>
              <li>
                 <? echo($algorithmurl) ?>
              </li>
              <li>
                 <? echo($svmtrainurl) ?>
              </li>
              <li>
                 <? echo($svctrainurl) ?>
              </li>
	      <li>
		 <?echo($mlrtrainurl) ?>
              </li>
            </ol>
            <br/>
            <br/>
	 	The specifications for the <a href="http://opentox.org/dev/apis/api-1.1/Algorithm">Algorithm services</a> are available online at the OpenTox API page. 
	 	In OpenTox we have specified an ontology for all available algorithms which can be found <a href="http://opentox.org/dev/apis/api-1.1/Algorithms">here</a>.
	   <br/><br/><br/>
            <h3>
               cURL commands 
            </h3>
            <br/>
            <p align="justify">

               Get a URI list of all available algorithms (including classification, regression and preprocessing). 
              <br/><br/>
              <code>curl -v -X GET -H &quot;Accept:text/uri-list&quot; <? echo ($algorithmurl)?> </code>
	      Also, the list of algorithms in available in text/html.<br/><br/><br/>

               Get a representation of an algorithm in a prefered MIME type:             
              <br/><br/>
              <code>curl -v -X GET -H &quot;Accept:PreferedMimeType&quot; <?echo($algorithmurl)?>/{id} </code>
              <br/>
	      where {id} stands for the id of the algorithm and the list of supported MIMEs is:
		<ol>
		    <li>application/rdf+xml (default)</li>
                    <li>application/x-turtle</li>
		    <li>text/x-triple</li>
		    <li>text/rdf+n3</li>
		    <li>application/json</li>
		    <li>text/xml</li>
		    <li>text/x-yaml</li>
		</ol>
              <br/>
              <br/>              
		By means of a training algorithm service, train a regression or a classification model
		<br/><br/>
		<code>curl -v -X POST -d 'dataset_uri=http://someserver.com/dataset/{dataset_id}&amp;target=http://example.org/feature/{feature_id}' http://opentox.ntua.gr:3000/algorithm/{algorithm_id}</code><br/>
	        Where {algorithm_id} is the id of the training algorithm (see http://opentox.ntua.gr:3000/algorithm for reference ). 
		The URI http://someserver.com/dataset/{dataset_id} should be substituted by any URI pointing to an RDF representation of a 
		dataset which complies to the <a href="http://opentox.org/dev/apis/api-1.1/dataset">OpenTox specifications</a> 
		for datasets and the POSTed variable target is a <a href="http://opentox.org/dev/apis/api-1.1/Feature">feature</a> of the 
		dataset that should be used as target (class variable). Here is a realistic example of a curl command you can use on your own to train an mlr model: 
		<br/><br/>
		<code>curl -X POST -d 'dataset_uri=http://opentox.ntua.gr/ds.rdf&target=http://sth.com/feature/1' http://opentox.ntua.gr:3000/algorithm/mlr
</code>
		<br/><br/>

		<h3>Status Codes</h3>
		<ol>
		  <li>200: The request has succeeded!</li>
		  <li>404: The algorithm you were looking for, cannot be found on the server. Check you syntax. The URI http://opentox.ntua.gr;3000/algorithm/svm is different from http://opentox.ntua.gr:3000/algorithm/Svm (with a capital S).</li>
		  <li>400: Bad Request! This status code is returned in POST operations, i.e. when you're trying to train a model providing a dataset uri,a target feature uri and other tuning parameters. There are many reasons for such a status code; for example if you provide a gamma parameter which is negative or is not numeric, or if the target you provided is not a feature of the dataset etc. An explanatory message is always thrown.</li>
		  <li>500: Internal Server Error! The server encountered an internal error. Please contact the system administrators via e-mail or report the issue at this
			<a href="http://github.com/sopasakis/yaqp/issues">issue tracker</a>.
		</ol>
           <br/>            
           <a href="#page">(Back to Top)</a>

<br/><br/><br/><br/>
          </div>

	<div id="sec2">
	  <h2>2. Operations Involving Models</h2>
		<br/><br/>
	<h3>API</h3>
	The OpenTox API for model services can be found <a href="http://opentox.org/dev/apis/api-1.1/Model">here</a>.
	<br/><br/>
	<h3>List Of Involved URIs</h3>	
        </div>
<br/><br/><br/><br/>
<!--
End of Main Body
-->
        </div>
