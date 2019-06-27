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
package de.hybris.platform.cmsfacades.cmsitems.impl;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import de.hybris.platform.cmsfacades.cmsitems.CMSItemValidator;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CMSItemValidator} Performs all Validators from validatorMap that are part of the
 * same type hierarchy.
 */
public class DefaultCMSItemValidator implements CMSItemValidator<ItemModel>
{

	private TypeService typeService;
	private Map<String, Validator> validatorMap;

	@Override
	public void validate(final ItemModel validatee)
	{
		if (validatee == null)
		{
			return;
		}

		final Set<String> supportedItemTypes = getValidatorMap().keySet();

		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(validatee.getItemtype());

		final List<String> familyTypeCodes = composedType.getAllSuperTypes() //
				.stream() //
				.map(ComposedTypeModel::getCode) //
				.collect(toList());
		familyTypeCodes.add(composedType.getCode());

		final Set<String> validTypeCodes = supportedItemTypes.stream() //
				.filter(typeCode -> familyTypeCodes.contains(typeCode)) //
				.collect(toCollection(LinkedHashSet::new));

		validTypeCodes //
				.stream() //
				.map(typeCode -> getValidatorMap().get(typeCode)) //
				.forEach(validator -> validator.validate(validatee));
	}

	protected Map<String, Validator> getValidatorMap()
	{
		return validatorMap;
	}

	@Required
	public void setValidatorMap(final Map<String, Validator> validatorMap)
	{
		this.validatorMap = validatorMap;
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
