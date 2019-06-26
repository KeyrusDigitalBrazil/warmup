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
package de.hybris.platform.cmsfacades.pagescontentslotscomponents.validator;

import de.hybris.platform.cmsfacades.common.validator.ValidationDtoFactory;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.PageContentSlotComponentData;
import de.hybris.platform.cmsfacades.dto.ComponentAndContentSlotValidationDto;
import de.hybris.platform.cmsfacades.pagescontentslotscomponents.validator.predicate.ComponentAlreadyInContentSlotPredicate;

import java.util.Objects;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validates DTO for adding existing component to content slot.
 *
 * <p>
 * Rules:</br>
 * <li>component already in content slot: {@link ComponentAlreadyInContentSlotPredicate}</li>
 * </ul>
 * </p>
 */
public class ComponentExistsInSlotValidator implements Validator
{
	public static final String COMPONENT_ID = "componentId";

	private Predicate<ComponentAndContentSlotValidationDto> componentAlreadyInContentSlotPredicate;
	private Predicate<String> componentExistsPredicate;
	private ValidationDtoFactory validationDtoFactory;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return clazz.isAssignableFrom(PageContentSlotComponentData.class);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final PageContentSlotComponentData target = (PageContentSlotComponentData) obj;

		if (!Objects.isNull(target.getComponentId()) && getComponentExistsPredicate().test(target.getComponentId()))
		{
			final ComponentAndContentSlotValidationDto componentAndContentSlotDto = getValidationDtoFactory()
					.buildComponentAndContentSlotValidationDto(target.getComponentId(), target.getSlotId());
			if (getComponentAlreadyInContentSlotPredicate().test(componentAndContentSlotDto))
			{
				errors.rejectValue(COMPONENT_ID, CmsfacadesConstants.COMPONENT_ALREADY_EXIST_SLOT);
			}
		}
	}

	protected Predicate<ComponentAndContentSlotValidationDto> getComponentAlreadyInContentSlotPredicate()
	{
		return componentAlreadyInContentSlotPredicate;
	}

	@Required
	public void setComponentAlreadyInContentSlotPredicate(
			final Predicate<ComponentAndContentSlotValidationDto> componentAlreadyInContentSlotPredicate)
	{
		this.componentAlreadyInContentSlotPredicate = componentAlreadyInContentSlotPredicate;
	}

	protected Predicate<String> getComponentExistsPredicate()
	{
		return componentExistsPredicate;
	}

	@Required
	public void setComponentExistsPredicate(final Predicate<String> componentExistsPredicate)
	{
		this.componentExistsPredicate = componentExistsPredicate;
	}

	protected ValidationDtoFactory getValidationDtoFactory()
	{
		return validationDtoFactory;
	}

	@Required
	public void setValidationDtoFactory(final ValidationDtoFactory validationDtoFactory)
	{
		this.validationDtoFactory = validationDtoFactory;
	}
}
