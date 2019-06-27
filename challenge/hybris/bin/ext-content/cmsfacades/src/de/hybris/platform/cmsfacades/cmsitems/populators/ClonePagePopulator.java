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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CLONE_COMPONENTS;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_PAGE_UUID;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.hybris.platform.cms2.cloning.strategy.impl.PageCloningStrategy;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.assertj.core.util.Maps;
import org.springframework.beans.factory.annotation.Required;

/**
 * ClonePage populator for cmsfacades used to clone a page
 */
public class ClonePagePopulator implements Populator<Map<String, Object>, ItemModel>
{
	private PageCloningStrategy pageCloningStrategy;
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
		final Object cloneComponents = source.get(FIELD_CLONE_COMPONENTS);

		if (isNotBlank(sourcePageUUID) && !Objects.isNull(cloneComponents))
		{
			try {
				final Optional<Map<String, Object>> context = Optional
						.of(Maps.newHashMap(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, cloneComponents));
				final AbstractPageModel sourcePage = getUniqueItemIdentifierService()
						.getItemModel(sourcePageUUID, AbstractPageModel.class)
						.orElseThrow(() -> new ConversionException("Unique identifier not present [" + sourcePageUUID + "]."));
				getPageCloningStrategy().clone(sourcePage, Optional.of((AbstractPageModel) itemModel), context);
			} catch (final CMSItemNotFoundException e) {
				throw new ConversionException("CMS Item not found", e);
			}
		}
		else if (isBlank(sourcePageUUID) ^ Objects.isNull(cloneComponents))
		{
			throw new ConversionException("Incomplete arguments when either pageUuid or cloneComponents parameter is empty.");
		}
	}

	@Required
	public void setPageCloningStrategy(final PageCloningStrategy pageCloningStrategy) {
		this.pageCloningStrategy = pageCloningStrategy;
	}

	protected PageCloningStrategy getPageCloningStrategy() {
		return pageCloningStrategy;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService) {
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService() {
		return uniqueItemIdentifierService;
	}
}
