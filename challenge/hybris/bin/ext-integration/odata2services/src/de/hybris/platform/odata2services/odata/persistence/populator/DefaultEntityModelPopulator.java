/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.persistence.populator;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.populator.processor.PropertyProcessor;

import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

/**
 * Default implementation for {@link EntityModelPopulator}
 */
public class DefaultEntityModelPopulator implements EntityModelPopulator
{
	private List<PropertyProcessor> propertyProcessors;

	@Override
	public void populateItem(final ItemModel item, final StorageRequest storageRequest) throws EdmException
	{
		Preconditions.checkArgument(item != null, "ItemModel cannot be null");
		Preconditions.checkArgument(storageRequest != null, "StorageRequest cannot be null");

		for (final PropertyProcessor propertyProcessor : propertyProcessors)
		{
			propertyProcessor.processItem(item, storageRequest);
		}
	}

	@Override
	public void populateEntity(final ODataEntry oDataEntry, final ItemConversionRequest conversionRequest) throws EdmException
	{
		Preconditions.checkArgument(oDataEntry != null, "ItemModel cannot be null");
		Preconditions.checkArgument(conversionRequest != null, "ItemConversionRequest cannot be null");

		for (final PropertyProcessor propertyProcessor : propertyProcessors)
		{
			propertyProcessor.processEntity(oDataEntry, conversionRequest);
		}
	}

	protected List<PropertyProcessor> getPropertyProcessors()
	{
		return propertyProcessors;
	}

	@Required
	public void setPropertyProcessors(final List<PropertyProcessor> propertyProcessors)
	{
		this.propertyProcessors = propertyProcessors;
	}
}
