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
package de.hybris.platform.cmsfacades.cmsitems.attributeconverters;

import de.hybris.platform.cms2.cmsitems.converter.AttributeContentConverter;
import de.hybris.platform.cms2.enums.LinkTargets;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.cms2.enums.LinkTargets.NEWWINDOW;
import static de.hybris.platform.cms2.enums.LinkTargets.SAMEWINDOW;


/**
 * Implementation of {@link AttributeContentConverter} that converts properties of type {@link LinkTargets} from and to boolean
 */
public class LinkTargetAttributeContentConverter implements AttributeContentConverter<AttributeDescriptorModel>
{
	private Predicate<AttributeDescriptorModel> predicate;

	@Override
	public Predicate<AttributeDescriptorModel> getConstrainedBy()
	{
		return getPredicate();
	}

	@Override
	public Object convertModelToData(AttributeDescriptorModel attribute, Object source)
	{
		if (source != null)
		{
			LinkTargets linkTargets = (LinkTargets) source;
			return linkTargets == NEWWINDOW;
		}
		else
		{
			return null;
		}
	}

	@Override
	public Object convertDataToModel(AttributeDescriptorModel attributeDescriptor, Object source)
	{

		if (source != null)
		{
			Boolean isNewWindow = (Boolean) source;
			return isNewWindow ? NEWWINDOW : SAMEWINDOW;
		}
		else
		{
			return null;
		}
	}

	@Required
	public void setPredicate(Predicate<AttributeDescriptorModel> predicate)
	{
		this.predicate = predicate;
	}

	protected Predicate<AttributeDescriptorModel> getPredicate()
	{
		return predicate;
	}
}
