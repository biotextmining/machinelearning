package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.util.List;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLAssociationProcess;

/**
 * 
 * Feature generated columns interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLFeatureColumns<O> {
	
	public List<O> getBioTMLObjects();
	
	/**
	 * 
	 * Method to get the heads names of the features generated.
	 * 
	 * @return Set of head feature names.
	 */
	public Set<String> getUIDs();
	
	/**
	 * 
	 * Method to get the column by feature head name.
	 * Each string is an feature attribute of one row.
	 * 
	 * @param featureUID - Feature head name.
	 * @return List of attributes of each instance. 
	 */
	public List<String> getFeatureColumByUID(String featureUID);
	
	/**
	 * 
	 * Method to add a BioTMLObject feature into last row of referent uID column
	 * 
	 * @param tokenFeature Generated token feature 
	 * @param uID Column UID.
	 */
	public void addBioTMLObjectFeature(String bioTMLObjectFeature, String uID);
	
	/**
	 * 
	 * Method to retrieve all features form a BioTMLObject index. 
	 * 
	 * @param tokenIndex Token index of the column tokens initialization
	 * @return
	 */
	public List<String> getBioTMLObjectFeatures(int bioTMLObjectIndex);
	
	/**
	 * 
	 * Method to update all token features given a UID column.
	 * 
	 * @param tokenFeatures List of all token features.
	 * @param uID Column UID to update.
	 */
	public void updateBioTMLObjectFeatures(List<String> bioTMLObjectFeatures, String uID);
	
	/**
	 * 
	 * Method to update all token features columns using a {@link BioTMLAssociationProcess}.
	 * 
	 * @param tokenAnnotProcess {@link BioTMLAssociationProcess} to update the columns.
	 */
	public void updateBioTMLObjectFeaturesUsingAssociationProcess(BioTMLAssociationProcess bioTMLObjectAnnotProcess);
	
	/**
	 * 
	 * Method to define a feature column as a tabbed multifeature column.
	 * 
	 * @param uID Column UID.
	 */
	public void setUIDhasMultiFeatureColumn(String uID);
	
	/**
	 * 
	 * Method that verifies if the column feature has multiple attributes in the same instance.
	 * The attributes are tab separated.
	 * 
	 * @return Boolean that validates the multiple attribute feature.
	 */
	public boolean isMultiFeatureColumn(String featureUID);
	
}