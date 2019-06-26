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
import de.hybris.platform.cmsfacades.dto.ComponentTypeAndContentSlotValidationDto;

import java.util.Objects;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validate if required information like componentId, slotId and pageId were provided.
 *
 */
/**
 * Validates DTO for moving existing component in a content slot or across 2 different content slots.
 *
 * <p>
 * Rules:</br>
 * <ul>
 * <li>componentId not null</li>
 * <li>position not null</li>
 * <li>position > 0</li>
 * <li>pageId not null</li>
 * <li>page exists: {@link PageExistsPredicate}</li>
 * <li>content slotId not null</li>
 * <li>content slot exists: {@link ContentSlotExistsPredicate}</li>
 * <li>component exists: {@link ComponentExistsPredicate}</li>
 * <li>component type not valid for content slot: {@link ComponentTypeAllowedForContentSlotPredicate}</li>
 * </ul>
 * </p>
 */
public class UpdatePageContentSlotComponentValidator implements Validator
{
	public static final String COMPONENT_ID = "componentId";
	public static final String POSITION = "position";
	public static final String PAGE_ID = "pageId";
	public static final String SLOT_ID = "slotId";

	private Predicate<String> componentExistsInCatalogVersionsPredicate;
	private Predicate<ComponentTypeAndContentSlotValidationDto> componentTypeAllowedForContentSlotPredicate;
	private Predicate<String> contentSlotExistsInCatalogVersionsPredicate;
	private Predicate<String> pageExistsPredicate;
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

		if (Objects.isNull(target.getPosition()))
		{
			errors.rejectValue(POSITION, CmsfacadesConstants.FIELD_REQUIRED);
		}
		else if (target.getPosition() < 0)
		{
			errors.rejectValue(POSITION, CmsfacadesConstants.FIELD_MIN_VIOLATED);
		}

		if (Objects.isNull(target.getSlotId()))
		{
			errors.rejectValue(SLOT_ID, CmsfacadesConstants.FIELD_REQUIRED);
		}
		else if (!getContentSlotExistsInCatalogVersionsPredicate().test(target.getSlotId()))
		{
			errors.rejectValue(SLOT_ID, CmsfacadesConstants.FIELD_DOES_NOT_EXIST);
		}

		if (Objects.isNull(target.getComponentId()))
		{
			errors.rejectValue(COMPONENT_ID, CmsfacadesConstants.FIELD_REQUIRED);
		}
		else if (!getComponentExistsInCatalogVersionsPredicate().test(target.getComponentId()))
		{
			errors.rejectValue(COMPONENT_ID, CmsfacadesConstants.FIELD_DOES_NOT_EXIST);
		}

		if (Objects.isNull(target.getPageId()))
		{
			errors.rejectValue(PAGE_ID, CmsfacadesConstants.FIELD_REQUIRED);
		}
		else if (!getPageExistsPredicate().test(target.getPageId()))
		{
			errors.rejectValue(PAGE_ID, CmsfacadesConstants.FIELD_DOES_NOT_EXIST);
		}

		if (errors.getFieldErrorCount(PAGE_ID) + errors.getFieldErrorCount(COMPONENT_ID) == 0)
		{
			final ComponentAndContentSlotValidationDto componentAndContentSlotDto = getValidationDtoFactory()
					.buildComponentAndContentSlotValidationDto(target.getComponentId(), target.getSlotId());

			final ComponentTypeAndContentSlotValidationDto componentTypeAndContentSlotDto = getValidationDtoFactory()
					.buildComponentTypeAndContentSlotValidationDto(componentAndContentSlotDto.getComponent().getItemtype(),
							target.getSlotId(), target.getPageId());

			if (!getComponentTypeAllowedForContentSlotPredicate().test(componentTypeAndContentSlotDto))
			{
				errors.rejectValue(COMPONENT_ID, CmsfacadesConstants.FIELD_NOT_ALLOWED);
			}
		}

	}

	protected Predicate<String> getComponentExistsInCatalogVersionsPredicate()
	{
		return componentExistsInCatalogVersionsPredicate;
	}

	@Required
	public void setComponentExistsInCatalogVersionsPredicate(final Predicate<String> componentExistsInCatalogVersionsPredicate)
	{
		this.componentExistsInCatalogVersionsPredicate = componentExistsInCatalogVersionsPredicate;
	}

	protected Predicate<ComponentTypeAndContentSlotValidationDto> getComponentTypeAllowedForContentSlotPredicate()
	{
		return componentTypeAllowedForContentSlotPredicate;
	}

	@Required
	public void setComponentTypeAllowedForContentSlotPredicate(
			final Predicate<ComponentTypeAndContentSlotValidationDto> componentTypeAllowedForContentSlotPredicate)
	{
		this.componentTypeAllowedForContentSlotPredicate = componentTypeAllowedForContentSlotPredicate;
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

	protected Predicate<String> getContentSlotExistsInCatalogVersionsPredicate()
	{
		return contentSlotExistsInCatalogVersionsPredicate;
	}

	@Required
	public void setContentSlotExistsInCatalogVersionsPredicate(final Predicate<String> contentSlotExistsInCatalogVersionsPredicate)
	{
		this.contentSlotExistsInCatalogVersionsPredicate = contentSlotExistsInCatalogVersionsPredicate;
	}

	protected Predicate<String> getPageExistsPredicate()
	{
		return pageExistsPredicate;
	}

	@Required
	public void setPageExistsPredicate(final Predicate<String> pageExistsPredicate)
	{
		this.pageExistsPredicate = pageExistsPredicate;
	}

}
