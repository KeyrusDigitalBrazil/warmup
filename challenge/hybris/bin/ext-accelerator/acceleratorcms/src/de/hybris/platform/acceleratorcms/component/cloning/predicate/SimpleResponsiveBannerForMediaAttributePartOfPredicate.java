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
package de.hybris.platform.acceleratorcms.component.cloning.predicate;

import de.hybris.platform.acceleratorcms.model.components.SimpleResponsiveBannerComponentModel;
import de.hybris.platform.core.model.ItemModel;

import java.util.function.BiPredicate;

import org.apache.log4j.Logger;


/**
 * Predicate to test if the {@code MEDIA} qualifier of the {@code SimpleResponsiveBannerComponentModel} component should
 * be treated as {@code partOf} during the deep cloning process.
 * <p>
 * When the expression evaluates to {@code TRUE}, the qualifier of the component will be treated as partOf and the
 * qualifier's object will be deep copied; otherwise copy by reference.
 */
public class SimpleResponsiveBannerForMediaAttributePartOfPredicate implements BiPredicate<ItemModel, String>
{
	private static final Logger LOG = Logger.getLogger(SimpleResponsiveBannerForMediaAttributePartOfPredicate.class);

	@Override
	public boolean test(final ItemModel component, final String qualifier)
	{
		final boolean treatAsPartOf = (component instanceof SimpleResponsiveBannerComponentModel)
				&& qualifier.equals(SimpleResponsiveBannerComponentModel.MEDIA);
		if (treatAsPartOf)
		{
			LOG.debug("Should treatAsPartOf for : " + component.getItemtype() + " - " + qualifier);
		}
		return treatAsPartOf;
	}

}