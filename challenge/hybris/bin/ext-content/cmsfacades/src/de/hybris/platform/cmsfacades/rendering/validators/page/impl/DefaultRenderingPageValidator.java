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
package de.hybris.platform.cmsfacades.rendering.validators.page.impl;

import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.dto.RenderingPageValidationDto;
import de.hybris.platform.cmsfacades.rendering.validators.page.RenderingPageChecker;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.function.Predicate;


/**
 * Validator to validate attributes used to extract a page for rendering.
 */
public class DefaultRenderingPageValidator implements Validator
{
	private Predicate<String> typeCodeExistsPredicate;

	private List<RenderingPageChecker> renderingPageCheckers;

	@Override
	public boolean supports(Class<?> clazz)
	{
		return RenderingPageValidationDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors)
	{
		RenderingPageValidationDto validationDto = (RenderingPageValidationDto) obj;

		ValidationUtils.rejectIfEmpty(errors, "pageTypeCode", CmsfacadesConstants.FIELD_REQUIRED);

		if (getTypeCodeExistsPredicate().test(validationDto.getPageTypeCode()))
		{
			checkAbstractPage(validationDto, errors);
		}
		else
		{
			errors.rejectValue("pageTypeCode", CmsfacadesConstants.FIELD_NOT_ALLOWED);
		}
	}

	protected void checkAbstractPage(final RenderingPageValidationDto renderPageValidationDto,
			final Errors errors)
	{
		getRenderingPageCheckers().stream()
				.filter(checker -> checker.getConstrainedBy().test(renderPageValidationDto.getPageTypeCode()))
				.forEach(checker -> checker.verify(renderPageValidationDto, errors));
	}

	protected Predicate<String> getTypeCodeExistsPredicate()
	{
		return typeCodeExistsPredicate;
	}

	@Required
	public void setTypeCodeExistsPredicate(Predicate<String> typeCodeExistsPredicate)
	{
		this.typeCodeExistsPredicate = typeCodeExistsPredicate;
	}

	protected List<RenderingPageChecker> getRenderingPageCheckers()
	{
		return renderingPageCheckers;
	}

	@Required
	public void setRenderingPageCheckers(
			List<RenderingPageChecker> renderingPageCheckers)
	{
		this.renderingPageCheckers = renderingPageCheckers;
	}
}
