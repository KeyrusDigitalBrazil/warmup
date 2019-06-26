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
package de.hybris.platform.cmsfacades.types.populator;

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Collection;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populator aimed at setting all necessary information for the receiving end to build a dropdown widget:
 * <ul>
 * <li>identifies the cmsStructureType as {@link #EDITABLE_DROPDOWN}</li>
 * <li>marks the dropdown to be paged by default</li>
 * <li>marks the dropdown to be a multi select if the proeprty getter is a {@link Collection}</li>
 * </ul>
 */
public class DropdownComponentTypeAttributePopulator implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{

	private static final String EDITABLE_DROPDOWN = "EditableDropdown";

	private Predicate<AttributeDescriptorModel> isCollectionPredicate;

	@Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target)
			throws ConversionException
	{
		target.setCmsStructureType(EDITABLE_DROPDOWN);
		target.setPaged(true);
		target.setCollection(getIsCollectionPredicate().test(source));

	}

	protected Predicate<AttributeDescriptorModel> getIsCollectionPredicate()
	{
		return isCollectionPredicate;
	}

	@Required
	public void setIsCollectionPredicate(Predicate<AttributeDescriptorModel> isCollectionPredicate)
	{
		this.isCollectionPredicate = isCollectionPredicate;
	}
}
