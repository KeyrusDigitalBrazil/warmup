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
package de.hybris.platform.cmsfacades.rendering.populators;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_COMPONENTS;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.registry.CMSComponentContainerRegistry;
import de.hybris.platform.cms2.strategies.CMSComponentContainerStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populator used to populate container object with a list of items (components or container itself)
 * using {@link CMSComponentContainerRegistry}.
 */
public class ContainerModelToDataRenderingPopulator implements Populator<ItemModel, Map<String, Object>>
{
	private CMSComponentContainerRegistry cmsComponentContainerRegistry;

	@Override
	public void populate(ItemModel source, Map<String, Object> targetMap) throws ConversionException
	{
		if (source instanceof AbstractCMSComponentContainerModel)
		{
			final AbstractCMSComponentContainerModel container = (AbstractCMSComponentContainerModel) source;
			final CMSComponentContainerStrategy strategy = getCmsComponentContainerRegistry().getStrategy(container);

			final List<String> componentsForContainer = strategy.getDisplayComponentsForContainer(container).stream() //
					.map(CMSItemModel::getUid) //
					.collect(Collectors.toList());

			targetMap.put(FIELD_COMPONENTS, componentsForContainer);
		}
	}

	protected CMSComponentContainerRegistry getCmsComponentContainerRegistry()
	{
		return cmsComponentContainerRegistry;
	}

	@Required
	public void setCmsComponentContainerRegistry(CMSComponentContainerRegistry cmsComponentContainerRegistry)
	{
		this.cmsComponentContainerRegistry = cmsComponentContainerRegistry;
	}
}
