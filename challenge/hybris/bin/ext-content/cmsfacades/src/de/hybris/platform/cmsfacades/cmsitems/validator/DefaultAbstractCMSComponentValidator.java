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

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the validator for {@link AbstractCMSComponentModel}
 */
public class DefaultAbstractCMSComponentValidator implements Validator<AbstractCMSComponentModel>
{
	
	private ValidationErrorsProvider validationErrorsProvider;
	
	@Override
	public void validate(final AbstractCMSComponentModel validatee)
	{
		// intentionally left empty
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
