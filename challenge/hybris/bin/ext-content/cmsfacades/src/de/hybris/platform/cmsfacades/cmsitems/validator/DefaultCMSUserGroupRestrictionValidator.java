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

import static de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel.USERGROUPS;
import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_MIN_VIOLATED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static java.util.Objects.nonNull;

import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the validator for {@link CMSUserGroupRestrictionModel}
 */
public class DefaultCMSUserGroupRestrictionValidator implements Validator<CMSUserGroupRestrictionModel>
{
	
	private ValidationErrorsProvider validationErrorsProvider;
	
	@Override
	public void validate(final CMSUserGroupRestrictionModel validatee)
	{
		if (nonNull(validatee.getUserGroups()))
		{
			if (validatee.getUserGroups().isEmpty())
			{
				getValidationErrorsProvider().getCurrentValidationErrors().add(
						newValidationErrorBuilder() //
								.field(USERGROUPS) //
								.errorCode(FIELD_MIN_VIOLATED) //
								.build()
				);
			}
		}
		else 
		{
			getValidationErrorsProvider().getCurrentValidationErrors().add(
					newValidationErrorBuilder() //
							.field(USERGROUPS) //
							.errorCode(FIELD_REQUIRED) //
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
