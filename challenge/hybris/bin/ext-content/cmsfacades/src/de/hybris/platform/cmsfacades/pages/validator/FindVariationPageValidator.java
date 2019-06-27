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
package de.hybris.platform.cmsfacades.pages.validator;

import static de.hybris.platform.cms2.model.pages.AbstractPageModel.DEFAULTPAGE;
import static de.hybris.platform.cms2.model.pages.AbstractPageModel.TYPECODE;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.common.predicate.TypeCodeExistsPredicate;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.google.common.base.Strings;


/**
 * Validates fields of {@link AbstractPageData} when retrieving all default or variation pages
 *
 * @deprecated since 6.6
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.6")
public class FindVariationPageValidator implements Validator
{
	private TypeCodeExistsPredicate typeCodeExistsPredicate;
	private TypeService typeService;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return AbstractPageData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final AbstractPageData pageData = (AbstractPageData) target;
		ValidationUtils.rejectIfEmpty(errors, DEFAULTPAGE, CmsfacadesConstants.FIELD_REQUIRED);
		ValidationUtils.rejectIfEmpty(errors, TYPECODE, CmsfacadesConstants.FIELD_REQUIRED);

		if (!Strings.isNullOrEmpty(pageData.getTypeCode()))
		{
			if (!getTypeCodeExistsPredicate().test(pageData.getTypeCode()))
			{
				errors.rejectValue(TYPECODE, CmsfacadesConstants.FIELD_DOES_NOT_EXIST);
			}
			else
			{
				final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(pageData.getTypeCode());
				if (!AbstractPageModel.class.isAssignableFrom(getTypeService().getModelClass(composedType)))
				{
					errors.rejectValue(TYPECODE, CmsfacadesConstants.FIELD_NOT_ALLOWED);
				}
			}
		}
	}

	protected TypeCodeExistsPredicate getTypeCodeExistsPredicate()
	{
		return typeCodeExistsPredicate;
	}

	@Required
	public void setTypeCodeExistsPredicate(final TypeCodeExistsPredicate typeCodeExistsPredicate)
	{
		this.typeCodeExistsPredicate = typeCodeExistsPredicate;
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
