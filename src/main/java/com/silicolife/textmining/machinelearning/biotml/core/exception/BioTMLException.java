package com.silicolife.textmining.machinelearning.biotml.core.exception;

/**
 * 
 * BioTML Exception handling class.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class BioTMLException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private String[] errors = {
			"The corpus don't have the a document with the given ID!",//id 0
			"The document don't have a token with the inserted offsets.",//id 1
			"The document don't have tokens with the inserted offsets.",//id 2
			"The inserted token offsets don't match with the sentence token offsets.",//id 3
			"The inserted start and end tokens list offsets don't match with the sentence token offsets.",//id 4
			"The input model is not compatible!",//id 5
			"The training matrix row wasn't inserted!",//id 6
			"The temporary file matrix wasn't initializated",//id 7
			"Failed to save the matrix file!",//id 8
			"The feature type don't have a class registered in features manager!",//id 9
			"Theres a problem in feature module management.",//id 10
			"There is a problem with the ClearNLP parser!",//id 11
			"There is a problem cleaning the models memory of ClearNLP!",//id 12
			"There is a problem with the OpenNLP!",//id 13
			"TokenTextCharFeatures Module has a problem!",//id 14
			"There was a problem loading the models from file.",//id 15
			"There was a problem loading the data from model file.",//id 16
			"There was a problem loading the model file.",//id 17
			"There was a problem writing the temporary directory to extract the multi model file.",//id 18
			"There was a problem writing the temporary directory to write the multi model file.",//id 19
			"There was a problem writing the model(s) to file.",//id 20
			"The model corpus wasn't loaded and is null",//id 21
			"Theres a problem with muli-threading on cross-validation", //id 22
			"The matrix data wasn't loaded and is null!",//id 23
			"Provided model is not in supported model format.",//id 24
			"The given annotation is not present in the relation!", //id 25
			"The inserted set of annotations is invalid. A relation must have at least two annotations!", //id 26
			"Features generation failed! The inserted token list is empty.", //id 27
			"The relation don't have an annotation with the inputed offsets.", //id 28
			"The corpus don't have a annotation with the given offsets and document ID.", //id 29
			"The annotation clue offsets are not correct. Please verify it!" //id 30
	};
	
	private String getError(int id){
		return errors[id];
	}

    /**
     * 
     * Constructor with exception message.
     * 
     * @param err Associated exception message.
     */
	public BioTMLException(String err)
	{
		super(err);
	}
	
    /**
     * 
     * Constructor with exception object.
     * 
     * @param exc Associated exception object.
     */
	public BioTMLException(Exception exc)
	{
		super(exc);
	}
	
    /**
     * 
     * Constructor with message and throwable exception.
     * 
     * @param err Associated exception message.
     * @param thr Associated throwable exception object.
     */
	public BioTMLException(String err, Throwable thr)
	{
		super(err, thr);
	}
	
    /**
     * 
     * Constructor with exception string ID.
     * 
     * @param id Associated exception string ID.
     */
	public BioTMLException(int id){
		if(id<errors.length){
			new BioTMLException(getError(id));
		}
		new BioTMLException("The Exception ID don't have a known exception string.\n Please add the exception string to the BioTMLException class or verify the given ID.");
	}
	
    /**
     * 
     * Constructor with exception string ID.
     * 
     * @param id Associated exception string ID.
     * @param thr Associated throwable exception object.
     */
	public BioTMLException(int id, Throwable thr)
	{
		if(id<errors.length){
			new BioTMLException(getError(id), thr);
		}
		new BioTMLException("The Exception ID don't have a known exception string.\n Please add the exception string to the BioTMLException class or verify the given ID.");
	}

}