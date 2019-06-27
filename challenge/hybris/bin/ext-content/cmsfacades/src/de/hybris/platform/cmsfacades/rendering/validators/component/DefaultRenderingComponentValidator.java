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
package de.hybris.platform.cmsfacades.rendering.validators.component;

import de.hybris.platform.cmsfacades.common.predicate.CatalogCodeExistsPredicate;
import de.hybris.platform.cmsfacades.common.predicate.CategoryCodeExistsPredicate;
import de.hybris.platform.cmsfacades.common.predicate.ProductCodeExistsPredicate;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.dto.RenderingComponentValidationDto;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.function.Predicate;


/**
 * Validator to validate attributes used to extract a component for rendering.
 */
public class DefaultRenderingComponentValidator implements Validator
{
	private CategoryCodeExistsPredicate categoryCodeExistsPredicate;
	private ProductCodeExistsPredicate productCodeExistsPredicate;
	private CatalogCodeExistsPredicate catalogCodeExistsPredicate;

	@Override
	public boolean supports(Class<?> clazz)
	{
		return RenderingComponentValidationDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors)
	{
		RenderingComponentValidationDto validationDto = (RenderingComponentValidationDto) obj;
		checkCode(validationDto.getCatalogCode(), "catalogCode", errors, getCatalogCodeExistsPredicate());
		checkCode(validationDto.getCategoryCode(), "categoryCode", errors, getCategoryCodeExistsPredicate());
		checkCode(validationDto.getProductCode(), "productCode", errors, getProductCodeExistsPredicate());
	}

	/**
	 * Verifies whether the code exists using provided predicate.
	 * @param codeValue the code value
	 * @param fieldName the code name field
	 * @param errors the {@link Errors} object
	 * @param predicate the predicate
	 */
	protected void checkCode(final String codeValue, final String fieldName, final Errors errors,
			final Predicate<String> predicate)
	{
		if (codeValue != null && predicate.negate().test(codeValue))
		{
			errors.rejectValue(fieldName, CmsfacadesConstants.FIELD_NOT_ALLOWED);
		}
	}

	protected CategoryCodeExistsPredicate getCategoryCodeExistsPredicate()
	{
		return categoryCodeExistsPredicate;
	}

	@Required
	public void setCategoryCodeExistsPredicate(
			CategoryCodeExistsPredicate categoryCodeExistsPredicate)
	{
		this.categoryCodeExistsPredicate = categoryCodeExistsPredicate;
	}

	protected ProductCodeExistsPredicate getProductCodeExistsPredicate()
	{
		return productCodeExistsPredicate;
	}

	@Required
	public void setProductCodeExistsPredicate(
			ProductCodeExistsPredicate productCodeExistsPredicate)
	{
		this.productCodeExistsPredicate = productCodeExistsPredicate;
	}

	protected CatalogCodeExistsPredicate getCatalogCodeExistsPredicate()
	{
		return catalogCodeExistsPredicate;
	}

	@Required
	public void setCatalogCodeExistsPredicate(
			CatalogCodeExistsPredicate catalogCodeExistsPredicate)
	{
		this.catalogCodeExistsPredicate = catalogCodeExistsPredicate;
	}
}
