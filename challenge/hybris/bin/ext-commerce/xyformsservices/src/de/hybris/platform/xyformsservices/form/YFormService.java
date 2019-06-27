/*
* [y] hybris Platform
*
* Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*/
package de.hybris.platform.xyformsservices.form;

import de.hybris.platform.xyformsservices.enums.YFormDataTypeEnum;
import de.hybris.platform.xyformsservices.enums.YFormDefinitionStatusEnum;
import de.hybris.platform.xyformsservices.exception.YFormServiceException;
import de.hybris.platform.xyformsservices.model.YFormDataModel;
import de.hybris.platform.xyformsservices.model.YFormDefinitionModel;

import java.util.List;


/**
 * Implements methods for managing yForms.
 */
public interface YFormService
{
	/**
	 * For a given applicationId and formId a form definition is returned.
	 *
	 * @param applicationId
	 * 			the application id of the form definition
	 * @param formId
	 * 			the form id of the form definition
	 * @return form definition with the specific parameters
	 * @throws YFormServiceException if definition is not found
	 */
	public YFormDefinitionModel getYFormDefinition(final String applicationId, final String formId) throws YFormServiceException;

	/**
	 * Only for testing purposes, shouldn't be used to get the latest YFormDefinition
	 *
	 * @param applicationId
	 * 			the application id of the form definition
	 * @param formId
	 * 			the form id of the form definition
	 * @param version
	 * 			the version of the form definition
	 * @return form definition with the specific parameters
	 * @throws YFormServiceException if definition is not found
	 */
	public YFormDefinitionModel getYFormDefinition(String applicationId, String formId, int version) throws YFormServiceException;


	/**
	 * Update YFormDefinition if it exists in the database.
	 *
	 * @param applicationId
	 * 			the application id of the form definition
	 * @param formId
	 * 			the form id of the form definition
	 * @param content
	 * 			the content of the form definition
	 * @param documentId
	 *				the document id of the form definition
	 * @return the updated form definition model
	 * @throws YFormServiceException if form definition cannot be saved
	 */
	public YFormDefinitionModel updateYFormDefinition(final String applicationId, final String formId, final String content,
			final String documentId) throws YFormServiceException;

	/**
	 * Create a new YFormDefinition with specific parameters.
	 *
	 * @param applicationId
	 * 			the application id of the form definition, should exist in a catalog
	 * @param formId
	 * 			the form id of the form definition
	 * @param title
	 * 			the tile of the form definition
	 * @param description
	 * 			the description of the form definition
	 * @param content
	 * 			the content of the form definition
	 * @param documentId
	 * 			the document id of the form definition
	 * @return created form definition model
	 * @throws YFormServiceException if form definition cannot be created
	 */
	public YFormDefinitionModel createYFormDefinition(final String applicationId, final String formId, final String title,
			final String description, final String content, final String documentId) throws YFormServiceException;

	/**
	 * Return form data model with specific id and type.
	 *
	 * @param formDataId
	 * 			the id of the form data
	 * @param type
	 * 			the type of the form data
	 * @return form data model with specific parameters
	 * @throws YFormServiceException if form data is not found
	 */
	public YFormDataModel getYFormData(final String formDataId, final YFormDataTypeEnum type) throws YFormServiceException;

	/**
	 * Create or Update YFormDataModel.
	 *
	 * @param applicationId
	 * 			the application id of the form data
	 * @param formId
	 * 			the form id of the form data
	 * @param formDataId
	 * 			the form data id of the form data
	 * @param type
	 * 			the type of the form data
	 * @param refId
	 *				the reference id of the form data
	 * @param content
	 *				the content of the form data
	 * @return created or updated form data model
	 * @throws YFormServiceException if form data cannot be created or updated
	 */
	public YFormDataModel createOrUpdateYFormData(final String applicationId, final String formId, final String formDataId,
			final YFormDataTypeEnum type, final String refId, final String content) throws YFormServiceException;

	/**
	 * Update YFormData content of form data with specific id and type.
	 *
	 * @param formDataId
	 * 			the id of the form data
	 * @param type
	 * 			the type of the form data
	 * @param content
	 * 			new content of the form data
	 * @return updated form data model
	 * @throws YFormServiceException if form data cannot be updated
	 */
	public YFormDataModel updateYFormData(final String formDataId, final YFormDataTypeEnum type, final String content)
			throws YFormServiceException;

	/**
	 * Creates YFormData if it doesn't exist and assigns it to the corresponding YFormDefinition.
	 *
	 * @param applicationId
	 * 			the application id of the form data
	 * @param formId
	 * 			the form id of the form data
	 * @param formDataId
	 * 			the form data id of the form data
	 *	@param type
	 *				the type of the form data
	 *	@param refId
	 *				the reference id of the form data
	 * @param content
	 *				the content of the form data
	 * @return created form data model with given parameters
	 * @throws YFormServiceException if form data cannot be created
	 */
	public YFormDataModel createYFormData(final String applicationId, final String formId, final String formDataId,
			final YFormDataTypeEnum type, final String refId, final String content) throws YFormServiceException;


	/**
	 * Creates YFormData if it doesn't exist already and assigns it to the corresponding YFormDefinition.
	 *
	 * @param applicationId
	 * 			the application id of the form data
	 * @param formId
	 * 			the form id of the form data
	 * @param formDataId
	 * 			the form data id of the form data
	 *	@param type
	 *				the type of the form data
	 *	@param refId
	 *				the reference id of the form data
	 * @param content
	 *				the content of the form data
	 *	@param ownerApplicationId
	 *				the id of the owner application
	 *	@param ownerFormId
	 *				the id of the owner form
	 * @return created form data model with given parameters
	 * @throws YFormServiceException if form data cannot be created
	 */
	public YFormDataModel createYFormData(final String applicationId, final String formId, final String formDataId,
			final YFormDataTypeEnum type, final String refId, final String content, String ownerApplicationId, String ownerFormId)
					throws YFormServiceException;

	/**
	 * Creates YFormData if it doesn't exist already and assigns it to the corresponding YFormDefinition.
	 *
	 * @param applicationId
	 * 			the application id of the form data
	 * @param formId
	 * 			the form id of the form data
	 * @param formDataId
	 * 			the form data id of the form data
	 *	@param type
	 *				the type of the form data
	 *	@param refId
	 *				the reference id of the form data
	 * @param content
	 *				the content of the form data
	 *	@param ownerApplicationId
	 *				the id of the owner application
	 *	@param ownerFormId
	 *				the id of the owner form
	 *	@param system
	 *				the system flag of the form data
	 * @return created form data model with given parameters
	 * @throws YFormServiceException if form data cannot be created
	 */
	public YFormDataModel createYFormData(final String applicationId, final String formId, final String formDataId,
			final YFormDataTypeEnum type, final String refId, final String content, String ownerApplicationId, String ownerFormId,
			boolean system) throws YFormServiceException;

	/**
	 * Return the form data model with the specified parameters.
	 *
	 * @param applicationId
	 * 			the application id of the form data
	 * @param formId
	 * 			the form id of the form data
	 * @param refId
	 *				the reference id of the form data
	 * @param type
	 *				the type of the form data
	 * @return the form data model with the specified parameters
	 * @throws YFormServiceException if form data is not found
	 */
	YFormDataModel getYFormData(final String applicationId, final String formId, final String refId, final YFormDataTypeEnum type)
			throws YFormServiceException;

	/**
	 * Return a list of the form data models with specified reference id.
	 *
	 * @param refId
	 * 		the reference id of the form data models
	 * @return the list of the form data models with specified reference id
	 */
	List<YFormDataModel> getYFormDataByRefId(final String refId);

	/**
	 * Set the status for all the form definitions with specific application id and form id.
	 *
	 * @param formId
	 * 			the form id of the form definition
	 *	@param applicationId
	 *				the application id of the form definition
	 * @param status
	 * 			the new status of the form definition
	 */
	public void setFormDefinitionStatus(String applicationId, String formId, YFormDefinitionStatusEnum status);

}
