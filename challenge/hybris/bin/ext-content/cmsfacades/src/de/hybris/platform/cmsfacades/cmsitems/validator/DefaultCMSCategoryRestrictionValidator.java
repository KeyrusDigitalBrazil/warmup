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
package de.hybris.platform.cmsfacades.cmsitems.validator;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_MIN_VIOLATED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static java.util.Objects.isNull;

import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the validator for {@link CMSCategoryRestrictionModel}
 */
public class DefaultCMSCategoryRestrictionValidator implements Validator<CMSCategoryRestrictionModel>
{
	
	private ValidationErrorsProvider validationErrorsProvider;
	
	@Override
	public void validate(final CMSCategoryRestrictionModel validatee)
	{

		if (isNull(validatee.getCategories()))
		{
			getValidationErrorsProvider().getCurrentValidationErrors().add(
					newValidationErrorBuilder() //
							.field(CMSCategoryRestrictionModel.CATEGORIES) //
							.errorCode(FIELD_REQUIRED) //
							.build()
			);
		}
		else if(validatee.getCategories().isEmpty())
		{
			getValidationErrorsProvider().getCurrentValidationErrors().add(
					newValidationErrorBuilder() //
							.field(CMSCategoryRestrictionModel.CATEGORIES) //
							.errorCode(FIELD_MIN_VIOLATED) //
							.build()
			);
		}
	}

	protected ValidationErrorsProvider getValidationErrorsProvider()
	{
		return validationErrorsProvider;
	}

	@Required
	public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider)
	{
		this.validationErrorsProvider = validationErrorsProvider;
	}
}
