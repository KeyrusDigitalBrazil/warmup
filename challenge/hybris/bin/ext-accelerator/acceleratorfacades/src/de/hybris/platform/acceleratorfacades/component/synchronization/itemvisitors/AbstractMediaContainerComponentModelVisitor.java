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
package de.hybris.platform.acceleratorfacades.component.synchronization.itemvisitors;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VISITORS_CTX_LOCALES;

import de.hybris.platform.acceleratorcms.model.components.AbstractMediaContainerComponentModel;
import de.hybris.platform.cmsfacades.synchronization.itemvisitors.AbstractCMSComponentModelVisitor;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;


/**
 * Abstract class for visiting {@link AbstractMediaContainerComponentModel} models for the cms synchronization service
 * to work properly. In this implementation, it will collect items given by the {@link AbstractCMSComponentModelVisitor}
 * and for collecting the media for the given component.
 *
 * @param <MEDIACONTAINERTYPE>
 *           the media container type that extends {@link AbstractMediaContainerComponentModel}
 */
public abstract class AbstractMediaContainerComponentModelVisitor<MEDIACONTAINERTYPE extends AbstractMediaContainerComponentModel>
		extends AbstractCMSComponentModelVisitor<MEDIACONTAINERTYPE>
{

	@Override
	public List<ItemModel> visit(final MEDIACONTAINERTYPE source, final List<ItemModel> path, final Map<String, Object> ctx)
	{
		final List<ItemModel> collectedItems = super.visit(source, path, ctx);
		Optional.ofNullable(ctx.get(VISITORS_CTX_LOCALES)) //
				.map(o -> (List<Locale>) o) //
				.ifPresent(locales -> locales //
						.stream() //
						.map(locale -> source.getMedia(locale)).filter(mediaContainer -> mediaContainer != null)
						.forEach(mediaContainer -> collectedItems.add(mediaContainer)) //
		);
		return collectedItems;
	}

}
