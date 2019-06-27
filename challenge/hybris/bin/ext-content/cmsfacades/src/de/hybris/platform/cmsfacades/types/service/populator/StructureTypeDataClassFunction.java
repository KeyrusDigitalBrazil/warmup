/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.types.service.populator;

import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Function that returns the Class that corresponds to the {@link ComposedTypeModel} type passed as an argument.
 */
public class StructureTypeDataClassFunction implements Function<ComposedTypeModel, Class>
{

	private static final Logger LOGGER = LoggerFactory.getLogger(StructureTypeDataClassFunction.class);

	private String typeClassPackage;
	private String typeClassSuffix;

	/*
	 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exceptions) : It is
	 * perfectly acceptable not to handle "e" here
	 */
	@SuppressWarnings("squid:S1166")
	@Override
	public Class apply(final ComposedTypeModel composedType)
	{
		final String classFullPathName = getTypeClassPackage() + "." + composedType.getCode() + getTypeClassSuffix();
		try
		{
			return Class.forName(classFullPathName);
		}
		catch (final ClassNotFoundException e)
		{
			LOGGER.info("Could not load type class for type " + composedType + ". " + classFullPathName + " does not exist.");
			return null;
		}
	}


	protected String getTypeClassPackage()
	{
		return typeClassPackage;
	}

	@Required
	public void setTypeClassPackage(final String typeClassPackage)
	{
		this.typeClassPackage = typeClassPackage;
	}

	protected String getTypeClassSuffix()
	{
		return typeClassSuffix;
	}

	@Required
	public void setTypeClassSuffix(final String typeClassSuffix)
	{
		this.typeClassSuffix = typeClassSuffix;
	}
}
