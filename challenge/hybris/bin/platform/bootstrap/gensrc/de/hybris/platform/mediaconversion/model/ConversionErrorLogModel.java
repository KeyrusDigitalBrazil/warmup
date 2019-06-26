/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 26/06/2019 16:55:50                         ---
 * ----------------------------------------------------------------
 *  
 * [y] hybris Platform
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.mediaconversion.model;

import de.hybris.bootstrap.annotations.Accessor;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;

/**
 * Generated model class for type ConversionErrorLog first defined at extension mediaconversion.
 */
@SuppressWarnings("all")
public class ConversionErrorLogModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "ConversionErrorLog";
	
	/**<i>Generated relation code constant for relation <code>ContainerToConversionErrorLogRel</code> defining source attribute <code>container</code> in extension <code>mediaconversion</code>.</i>*/
	public static final String _CONTAINERTOCONVERSIONERRORLOGREL = "ContainerToConversionErrorLogRel";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConversionErrorLog.targetFormat</code> attribute defined at extension <code>mediaconversion</code>. */
	public static final String TARGETFORMAT = "targetFormat";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConversionErrorLog.sourceMedia</code> attribute defined at extension <code>mediaconversion</code>. */
	public static final String SOURCEMEDIA = "sourceMedia";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConversionErrorLog.errorMessage</code> attribute defined at extension <code>mediaconversion</code>. */
	public static final String ERRORMESSAGE = "errorMessage";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConversionErrorLog.container</code> attribute defined at extension <code>mediaconversion</code>. */
	public static final String CONTAINER = "container";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public ConversionErrorLogModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public ConversionErrorLogModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _container initial attribute declared by type <code>ConversionErrorLog</code> at extension <code>mediaconversion</code>
	 * @param _targetFormat initial attribute declared by type <code>ConversionErrorLog</code> at extension <code>mediaconversion</code>
	 */
	@Deprecated
	public ConversionErrorLogModel(final MediaContainerModel _container, final ConversionMediaFormatModel _targetFormat)
	{
		super();
		setContainer(_container);
		setTargetFormat(_targetFormat);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _container initial attribute declared by type <code>ConversionErrorLog</code> at extension <code>mediaconversion</code>
	 * @param _errorMessage initial attribute declared by type <code>ConversionErrorLog</code> at extension <code>mediaconversion</code>
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _sourceMedia initial attribute declared by type <code>ConversionErrorLog</code> at extension <code>mediaconversion</code>
	 * @param _targetFormat initial attribute declared by type <code>ConversionErrorLog</code> at extension <code>mediaconversion</code>
	 */
	@Deprecated
	public ConversionErrorLogModel(final MediaContainerModel _container, final String _errorMessage, final ItemModel _owner, final MediaModel _sourceMedia, final ConversionMediaFormatModel _targetFormat)
	{
		super();
		setContainer(_container);
		setErrorMessage(_errorMessage);
		setOwner(_owner);
		setSourceMedia(_sourceMedia);
		setTargetFormat(_targetFormat);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConversionErrorLog.container</code> attribute defined at extension <code>mediaconversion</code>. 
	 * @return the container - The container for which the conversion failed.
	 */
	@Accessor(qualifier = "container", type = Accessor.Type.GETTER)
	public MediaContainerModel getContainer()
	{
		return getPersistenceContext().getPropertyValue(CONTAINER);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConversionErrorLog.errorMessage</code> attribute defined at extension <code>mediaconversion</code>. 
	 * @return the errorMessage - Technical description of the error.
	 */
	@Accessor(qualifier = "errorMessage", type = Accessor.Type.GETTER)
	public String getErrorMessage()
	{
		return getPersistenceContext().getPropertyValue(ERRORMESSAGE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConversionErrorLog.sourceMedia</code> attribute defined at extension <code>mediaconversion</code>. 
	 * @return the sourceMedia - The source Media.
	 */
	@Accessor(qualifier = "sourceMedia", type = Accessor.Type.GETTER)
	public MediaModel getSourceMedia()
	{
		return getPersistenceContext().getPropertyValue(SOURCEMEDIA);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConversionErrorLog.targetFormat</code> attribute defined at extension <code>mediaconversion</code>. 
	 * @return the targetFormat - The targeted format, for which the conversion failed.
	 */
	@Accessor(qualifier = "targetFormat", type = Accessor.Type.GETTER)
	public ConversionMediaFormatModel getTargetFormat()
	{
		return getPersistenceContext().getPropertyValue(TARGETFORMAT);
	}
	
	/**
	 * <i>Generated method</i> - Initial setter of <code>ConversionErrorLog.container</code> attribute defined at extension <code>mediaconversion</code>. Can only be used at creation of model - before first save.  
	 *  
	 * @param value the container - The container for which the conversion failed.
	 */
	@Accessor(qualifier = "container", type = Accessor.Type.SETTER)
	public void setContainer(final MediaContainerModel value)
	{
		getPersistenceContext().setPropertyValue(CONTAINER, value);
	}
	
	/**
	 * <i>Generated method</i> - Initial setter of <code>ConversionErrorLog.errorMessage</code> attribute defined at extension <code>mediaconversion</code>. Can only be used at creation of model - before first save.  
	 *  
	 * @param value the errorMessage - Technical description of the error.
	 */
	@Accessor(qualifier = "errorMessage", type = Accessor.Type.SETTER)
	public void setErrorMessage(final String value)
	{
		getPersistenceContext().setPropertyValue(ERRORMESSAGE, value);
	}
	
	/**
	 * <i>Generated method</i> - Initial setter of <code>ConversionErrorLog.sourceMedia</code> attribute defined at extension <code>mediaconversion</code>. Can only be used at creation of model - before first save.  
	 *  
	 * @param value the sourceMedia - The source Media.
	 */
	@Accessor(qualifier = "sourceMedia", type = Accessor.Type.SETTER)
	public void setSourceMedia(final MediaModel value)
	{
		getPersistenceContext().setPropertyValue(SOURCEMEDIA, value);
	}
	
	/**
	 * <i>Generated method</i> - Initial setter of <code>ConversionErrorLog.targetFormat</code> attribute defined at extension <code>mediaconversion</code>. Can only be used at creation of model - before first save.  
	 *  
	 * @param value the targetFormat - The targeted format, for which the conversion failed.
	 */
	@Accessor(qualifier = "targetFormat", type = Accessor.Type.SETTER)
	public void setTargetFormat(final ConversionMediaFormatModel value)
	{
		getPersistenceContext().setPropertyValue(TARGETFORMAT, value);
	}
	
}
