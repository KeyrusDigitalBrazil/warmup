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
package de.hybris.platform.cmsfacades.navigations.validator.predicate;

import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;

/**
 * Validates if a given {@code ItemModel} points to a CMS Item that contains a {@link de.hybris.platform.cms2.jalo.navigation.CMSNavigationNode}.
 * @deprecated since 1811 - no longer needed
 */
@Deprecated
public class ValidEntryItemModelPredicate implements Predicate<ItemModel>
{
	
	private TypeService typeService;
	
	@Override
	public boolean test(final ItemModel target)
	{
		final long attributesOfNavigationNodeCount = getTypeService()
				.getAttributeDescriptorsForType(getTypeService().getComposedTypeForCode(target.getItemtype()))
				.stream()
				.filter(attributeDescriptorModel -> CMSNavigationNodeModel._TYPECODE.equals(attributeDescriptorModel.getAttributeType().getCode()))
				.count();
		return attributesOfNavigationNodeCount == 0;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
}
