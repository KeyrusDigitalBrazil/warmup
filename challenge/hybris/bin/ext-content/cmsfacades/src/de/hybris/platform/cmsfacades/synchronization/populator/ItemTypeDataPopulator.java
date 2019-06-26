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
package de.hybris.platform.cmsfacades.synchronization.populator;

import de.hybris.platform.cmsfacades.data.ItemTypeData;
import de.hybris.platform.cmsfacades.data.SyncItemInfoJobStatusData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;

/**
 * Simple class for populating {@link ItemTypeData} from {@link SyncItemInfoJobStatusData}.
 */
public class ItemTypeDataPopulator implements Populator<SyncItemInfoJobStatusData, ItemTypeData>
{
	private static final String DOT = ".";
	private String prefix;
	private String suffix;

	@Override
	public void populate(final SyncItemInfoJobStatusData source, final ItemTypeData target)
			throws ConversionException
	{
		final String i18nKey = getPrefix() + DOT + source.getItem().getItemtype() + DOT + getSuffix();
		target.setI18nKey(i18nKey.toLowerCase());
		target.setItemType(source.getItem().getItemtype());
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
