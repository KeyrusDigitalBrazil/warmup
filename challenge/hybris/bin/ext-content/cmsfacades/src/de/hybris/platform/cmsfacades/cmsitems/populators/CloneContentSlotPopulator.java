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
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.platform.cms2.cloning.strategy.impl.ContentSlotCloningStrategy;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CLONE_COMPONENTS;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CONTENT_SLOT_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_PAGE_UUID;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * CloneContentSlot populator for cmsfacades used to clone a ContentSlot
 */
public class CloneContentSlotPopulator implements Populator<Map<String, Object>, ItemModel>
{
	private ContentSlotCloningStrategy contentSlotCloningStrategy;
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Override
	public void populate(final Map<String, Object> source, final ItemModel itemModel) throws ConversionException
	{
		if (itemModel == null)
		{
			throw new ConversionException("Item Model used in the populator should not be null.");
		}
		if (source == null)
		{
			throw new ConversionException("Source map used in the populator should not be null.");
		}

		final String sourcePageUUID = (String) source.get(FIELD_PAGE_UUID);
		final String sourceContentSlotUUID = (String) source.get(FIELD_CONTENT_SLOT_UUID);
		final Object cloneComponents = source.get(FIELD_CLONE_COMPONENTS);

		if (isNotBlank(sourcePageUUID) && isNotBlank(sourceContentSlotUUID) && !Objects.isNull(cloneComponents))
		{
			try {
				final AbstractPageModel sourcePage = getUniqueItemIdentifierService()
						.getItemModel(sourcePageUUID, AbstractPageModel.class)
						.orElseThrow(() -> new ConversionException("Unique identifier not present [" + sourcePageUUID + "]."));

				final ContentSlotModel sourceContentSlot = getUniqueItemIdentifierService()
						.getItemModel(sourceContentSlotUUID, ContentSlotModel.class)
						.orElseThrow(() -> new ConversionException("Unique identifier not present [" + sourceContentSlotUUID + "]."));

				final Map<String, Object> context = new HashMap<>();
				context.put(Cms2Constants.PAGE_CONTEXT_KEY, sourcePage);
				context.put(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, cloneComponents);

				getContentSlotCloningStrategy().clone(sourceContentSlot, Optional.of((ContentSlotModel) itemModel), Optional.of(context));
			} catch (final CMSItemNotFoundException e) {
				throw new ConversionException("CMS Item not found", e);
			}
		}
		else if (Stream.of(isNotBlank(sourcePageUUID), isNotBlank(sourceContentSlotUUID), !Objects.isNull(cloneComponents)).anyMatch(b -> b))
		{
			throw new ConversionException("Incomplete arguments when pageUuid or contentSlotUuid or cloneComponents parameter is empty.");
		}
	}

	protected ContentSlotCloningStrategy getContentSlotCloningStrategy()
	{
		return contentSlotCloningStrategy;
	}

	@Required
	public void setContentSlotCloningStrategy(final ContentSlotCloningStrategy contentSlotCloningStrategy)
	{
		this.contentSlotCloningStrategy = contentSlotCloningStrategy;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService() {
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService) {
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}
}
