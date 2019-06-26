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

import static de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel.ACTIVEFROM;
import static de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel.ACTIVEUNTIL;
import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.INVALID_DATE_RANGE;

import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the validator for {@link CMSTimeRestrictionModel}
 */
public class DefaultCMSTimeRestrictionValidator implements Validator<CMSTimeRestrictionModel>
{

	private ValidationErrorsProvider validationErrorsProvider;

	@Override
	public void validate(final CMSTimeRestrictionModel validatee)
	{
		if (Objects.isNull(validatee.getActiveFrom()))
		{
			getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
					.field(ACTIVEFROM) //
					.errorCode(FIELD_REQUIRED) //
					.build());
		}
		if (Objects.isNull(validatee.getActiveUntil()))
		{
			getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
					.field(ACTIVEUNTIL) //
					.errorCode(FIELD_REQUIRED) //
					.build());
		}

		if (Objects.nonNull(validatee.getActiveFrom()) && Objects.nonNull(validatee.getActiveUntil())
				&& (validatee.getActiveUntil().before(validatee.getActiveFrom())
						|| validatee.getActiveUntil().equals(validatee.getActiveFrom())))
		{
			getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
					.field(ACTIVEUNTIL) //
					.errorCode(INVALID_DATE_RANGE) //
					.build());
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
