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
package de.hybris.platform.xyformsfacades.form;

import de.hybris.platform.xyformsfacades.data.YFormDataData;
import de.hybris.platform.xyformsfacades.data.YFormDefinitionData;
import de.hybris.platform.xyformsfacades.strategy.preprocessor.YFormPreprocessorStrategy;
import de.hybris.platform.xyformsservices.enums.YFormDataActionEnum;
import de.hybris.platform.xyformsservices.enums.YFormDataTypeEnum;
import de.hybris.platform.xyformsservices.enums.YFormDefinitionStatusEnum;
import de.hybris.platform.xyformsservices.exception.YFormServiceException;

import java.util.Map;


/**
 * Form Facade to handle yForm definitions and yForm data.
 */
public interface YFormFacade
{
	/**
	 * Generates a new form Data id.
	 *
	 * @return generated form data id as string.
	 */
	public String getNewFormDataId();

	/**
	 * For a given application id and form id a form definition is returned.
	 *
	 * @param applicationId
	 * 			the application id to search form definition for
	 * @param formId
	 * 			the form id to search form definition for
	 * @return form definition with given form id and application id
	 */
	public YFormDefinitionData getYFormDefinition(final String applicationId, final String formId) throws YFormServiceException;

	/**
	 * This method is available for testing purposes, to get the latest YFormDefinition use other method.
	 *
	 * @param applicationId
	 * 			the application id to search form definition for
	 * @param formId
	 * 			the form id to search form definition for
	 * @param version
	 * 			form definition version
	 *	@return form definition with specific parameters
	 * @throws YFormServiceException if form definition cannot with specified parameters be resolved
	 */
	public YFormDefinitionData getYFormDefinition(String applicationId, String formId, int version) throws YFormServiceException;

	/**
	 * For a given document id a form definition is returned.
	 *
	 * @param documentId
	 * 			the document id to search form definition for
	 * @return form definition with given document id
	 */
	public YFormDefinitionData getYFormDefinition(final String documentId) throws YFormServiceException;

	/**
	 * Create a new YFormDefinition with specific parameters.
	 *
	 * @param applicationId
	 * 			the application id of the form definition
	 * @param formId
	 * 			the form id of the form definition
	 * @param content
	 *				the content of the form definition
	 * @param documentId
	 * 			the document id of the form definition
	 * @return created form definition POJO
	 * @throws YFormServiceException if form definition cannot be created
	 */
	public YFormDefinitionData createYFormDefinition(final String applicationId, final String formId, final String content,
			final String documentId) throws YFormServiceException;

	/**
	 * Update YFormDefinition if it exists in the database.
	 *
	 * @param applicationId
	 * 			the application id of the form definition
	 * @param formId
	 * 			the form id of the form definition
	 * @param content
	 *				the content of the form definition
	 * @param documentId
	 * 			the document id of the form definition
	 * @return YFormDefinitionData updated form definition POJO
	 * @throws YFormServiceException if form definition cannot be updated
	 */
	public YFormDefinitionData updateYFormDefinition(final String applicationId, final String formId, final String content,
			final String documentId) throws YFormServiceException;

	/**
	 * For a given id a form data is returned. First the DRAFT version if there one, else the DATA version of it.
	 *
	 * @param formDataId
	 * 			the form data id to get data for
	 *	@return form data with given id
	 * @throws YFormServiceException if form data cannot be found
	 */
	public YFormDataData getYFormData(String formDataId) throws YFormServiceException;

	/**
	 * For a given id and type a form data is returned.
	 *
	 * @param formDataId
	 * 			the form data id to get data for
	 * @param type
	 * 			the type of the form data
	 * @return form data POJO with given id and type
	 * @throws YFormServiceException if form data cannot be found
	 */
	public YFormDataData getYFormData(final String formDataId, final YFormDataTypeEnum type) throws YFormServiceException;

	/**
	 * For the given application id, form id, reference id and form type a form data is returned.
	 *
	 * @param applicationId
	 * 			the application id of the form data
	 * @param formId
	 * 			the form id to get data for
	 * @param refId
	 * 			the reference id
	 * @param type
	 * 			the type of the form data
	 * @return form data POJO with given application id, form id, reference id and type
	 * @throws YFormServiceException if form data cannot be found
	 */
	public YFormDataData getYFormData(final String applicationId, final String formId, final String refId,
			final YFormDataTypeEnum type) throws YFormServiceException;

	/**
	 * For the given application id, form id and reference id a form data is returned.
	 *
	 * @param applicationId
	 * 			the application id of the form data
	 * @param formId
	 * 			the form id to get data for
	 * @param refId
	 * 			the reference id
	 * @return form data POJO with given application id, form id and reference id
	 * @throws YFormServiceException if form data cannot be found
	 */
	public YFormDataData getYFormData(final String applicationId, final String formId, final String refId)
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
	 * @return form data POJO with given parameters
	 * @throws YFormServiceException if form data cannot be created
	 */
	public YFormDataData createYFormData(final String applicationId, final String formId, final String formDataId,
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
	 * @return updated form data POJO
	 * @throws YFormServiceException if form data cannot be updated
	 */
	public YFormDataData updateYFormData(final String formDataId, final YFormDataTypeEnum type, final String content)
			throws YFormServiceException;

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
	 * @param formDataContent
	 *				the content of the form data
	 * @return created or updated form data POJO
	 * @throws YFormServiceException if form data cannot be created or updated
	 */
	public YFormDataData createOrUpdateYFormData(final String applicationId, final String formId, final String formDataId,
			final YFormDataTypeEnum type, final String refId, final String formDataContent) throws YFormServiceException;

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
	 * @param formDataContent
	 *				the content of the form data
	 * @return created or updated form data POJO
	 * @throws YFormServiceException if form data cannot be created or updated
	 */
	public YFormDataData createOrUpdateYFormData(final String applicationId, final String formId, final String formDataId,
			final YFormDataTypeEnum type, final String formDataContent) throws YFormServiceException;

	/**
	 * For a given application id, form id and form data id an inline form definition is returned.<br/>
	 * The inline form definition can be injected in to a page instead of getting entire xform tagged between
	 * <code><xhtml></xhtml></code>
	 *
	 * @param applicationId
	 * 			the application id of the definition
	 * @param formId
	 * 			the form id of the definition
	 *	@param formDataId
	 *				the form data id of the definition
	 * @return the inline form definition as string
	 * @throws YFormServiceException if inline definition cannot be returned
	 */
	public String getInlineFormHtml(final String applicationId, final String formId, final String formDataId)
			throws YFormServiceException;

	/**
	 * For a given application id and form id an inline form definition is returned.<br/>
	 * The inline form definition can be injected in to a page instead of getting entire xform tagged between
	 * <code><xhtml></xhtml></code>
	 *
	 * @param applicationId
	 * 			the application id of the definition
	 * @param formId
	 * 			the form id of the definition
	 * @return the inline form definition as string
	 * @throws YFormServiceException if inline definition cannot be returned
	 */
	public String getInlineFormHtml(String applicationId, String formId) throws YFormServiceException;

	/**
	 * For a given application id, form id, form data id and action an inline form definition is returned.<br/>
	 * The inline form definition can be injected in to a page instead of getting entire xform tagged between
	 * <code><xhtml></xhtml></code>
	 *
	 * @param applicationId
	 * 			the application id of the definition
	 * @param formId
	 * 			the form id of the definition
	 * @param action
	 * 			the action on the definition
	 * @param formDataId
	 *				the form data id of the definition
	 * @return the inline form definition as string
	 * @throws YFormServiceException if inline definition cannot be returned
	 */
	public String getInlineFormHtml(String applicationId, String formId, YFormDataActionEnum action, String formDataId)
			throws YFormServiceException;

	/**
	 * For a given application id, form id, form data id, action and strategy an inline form definition is returned.<br/>
	 * The inline form definition can be injected in to a page instead of getting entire xform tagged between
	 * <code><xhtml></xhtml></code>
	 *
	 * @param applicationId
	 * 			the application id of the definition
	 * @param formId
	 * 			the form id of the definition
	 * @param action
	 * 			the action on the definition
	 * @param formDataId
	 *				the form data id of the definition
	 *	@param strategy
	 *				form preprocessor strategy
	 * @return the inline form definition as string
	 * @throws YFormServiceException if inline definition cannot be returned
	 */
	public String getInlineFormHtml(String applicationId, String formId, YFormDataActionEnum action, String formDataId,
			YFormPreprocessorStrategy strategy) throws YFormServiceException;

	/**
	 * For a given application id, form id, form data id, action and strategy (plus parameters) an inline form definition
	 * is returned.<br/>
	 * The inline form definition can be injected in to a page instead of getting entire xform tagged between
	 * <code><xhtml></xhtml></code>
	 *
	 * @param applicationId
	 * 			the application id of the definition
	 * @param formId
	 * 			the form id of the definition
	 * @param action
	 * 			the action on the definition
	 * @param formDataId
	 *				the form data id of the definition
	 *	@param strategy
	 *				form preprocessor strategy
	 *	@param params
	 *				map with parameters for preprocessor strategy
	 * @return the inline form definition as string
	 * @throws YFormServiceException if inline definition cannot be returned
	 */
	String getInlineFormHtml(String applicationId, String formId, YFormDataActionEnum action, String formDataId,
			YFormPreprocessorStrategy strategy, Map<String, Object> params) throws YFormServiceException;

	/**
	 * Returns the content of form data with specific application id, form id and form data id.
	 *
	 * @param applicationId
	 * 			the application id of the form data
	 * @param formId
	 * 			the form id of the form data
	 * @param formDataId
	 * 			the id of the form data
	 *	@return the content of form data
	 * @throws YFormServiceException if form data was not found or its content is invalid
	 */
	public String getFormDataContent(String applicationId, String formId, String formDataId) throws YFormServiceException;

	/**
	 * For a given application id and form id a form data content template (an empty content generated from
	 * YFormDefinition with given form id) is returned.
	 *
	 * @param applicationId
	 * 			the application id of the form definition
	 * @param formId
	 * 			the form id of the form definition
	 * @return form data content template for definition with specified parameters
	 * @throws YFormServiceException if there is no YFormDefinition with given applicationId and formId
	 * or YFormDefinition has wrong content
	 */
	public String getFormDataContentTemplate(String applicationId, String formId) throws YFormServiceException;

	/**
	 * Creates the form data counterpart form definition with specific application id, form id and version number.
	 *
	 * @param applicationId
	 * 			the application id of the form definition
	 * @param formId
	 * 			the form id of the form definition
	 * @param version
	 * 			the version number of the form definition
	 *	@return the form id of created form data
	 * @throws YFormServiceException if form definition cannot be found or updated or form data cannot be created
	 */
	String recreateYFormDefinitionCounterpart(String applicationId, String formId, int version) throws YFormServiceException;

	/**
	 * Changes the state of a form definition. This will include all related versions.
	 *
	 * @param applicationId
	 * 			the application id of the form definition
	 *	@param formId
	 *				the form id of the form definition
	 * @param status
	 * 			new status of the form definition
	 * @throws YFormServiceException if form definition cannot be found or updated
	 */
	public void setFormDefinitionStatus(String applicationId, String formId, YFormDefinitionStatusEnum status)
			throws YFormServiceException;

	/**
	 * Indicates if the given Form Data is valid or not.
	 *
	 * @param applicationId
	 * 			the application id of the form data
	 * @param formId
	 * 			the form id of the form data
	 * @param formDataId
	 * 			the id of the form data
	 *	@return true if form data is valid, false otherwise
	 * @throws YFormServiceException if form data cannot be found
	 */
	boolean validate(String applicationId, String formId, String formDataId) throws YFormServiceException;

}
