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
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.function.Predicate;

/**
 * Predicate to verify whether qualifier is (urlLink or external) AND the enclosing type contains both of them.
 */
public class PartOfLinkToggleAttributePredicate implements Predicate<AttributeDescriptorModel>
{
	private Predicate<ComposedTypeModel> cmsComposedTypeContainsLinkTogglePredicate;

	@Override
	public boolean test(final AttributeDescriptorModel attributeDescriptor)
	{
		ComposedTypeModel composedTypeModel = attributeDescriptor.getDeclaringEnclosingType();

		if (composedTypeModel != null && (attributeDescriptor.getQualifier().equals(CmsfacadesConstants.FIELD_URL_LINK_NAME)) ||
				attributeDescriptor.getQualifier().equals(CmsfacadesConstants.FIELD_EXTERNAL_NAME))
		{
			return getCmsComposedTypeContainsLinkTogglePredicate().test(composedTypeModel);
		}
		return false;
	}

	protected Predicate<ComposedTypeModel> getCmsComposedTypeContainsLinkTogglePredicate()
	{
		return cmsComposedTypeContainsLinkTogglePredicate;
	}

	@Required
	public void setCmsComposedTypeContainsLinkTogglePredicate(
			Predicate<ComposedTypeModel> cmsComposedTypeContainsLinkTogglePredicate)
	{
		this.cmsComposedTypeContainsLinkTogglePredicate = cmsComposedTypeContainsLinkTogglePredicate;
	}
}

