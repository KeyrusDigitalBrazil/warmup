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
package de.hybris.platform.cmsfacades.types.populator;

import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate {@link de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData#setI18nKey(String)}
 * using the following nomenclature:</br>
 * <tt>[prefix].[item type].[suffix]</tt>
 *
 * <p>
 * The I18N key nomenclature is made up of the following chunks:</br>
 * <ul>
 * <li>prefix: a string provided via spring injection to help</li>
 * <li>suffix: a string provided via spring injection to help</li>
 * <li>item type: the platform type code that the given attribute belongs to</li>
 * </ul>
 * </p>
 */
public class I18nComponentTypePopulator implements Populator<ComposedTypeModel, ComponentTypeData>
{
	private static final String DOT = ".";
	private String prefix;
	private String suffix;

	@Override
	public void populate(final ComposedTypeModel source, final ComponentTypeData target) throws ConversionException
	{
		final String i18nKey = getPrefix() + DOT + source.getCode() + DOT + getSuffix();
		target.setI18nKey(i18nKey.toLowerCase());
	}

	protected String getPrefix()
	{
		return prefix;
	}

	@Required
	public void setPrefix(final String prefix)
	{
		this.prefix = prefix;
	}

	protected String getSuffix()
	{
		return suffix;
	}

	@Required
	public void setSuffix(final String suffix)
	{
		this.suffix = suffix;
	}

}
