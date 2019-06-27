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
package de.hybris.platform.cmsfacades.version.validator;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_LABEL;

import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.CMSVersionData;

import java.util.function.BiPredicate;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validates fields of {@link CMSVersionData} for a create operation
 */
public class CreateCMSVersionValidator implements Validator
{
	private BiPredicate<String, String> labelExistsInCMSVersionsPredicate;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return CMSVersionData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final CMSVersionData cmsVersionData = (CMSVersionData) obj;

		ValidationUtils.rejectIfEmpty(errors, FIELD_LABEL, CmsfacadesConstants.FIELD_REQUIRED);

		if (getLabelExistsInCMSVersionsPredicate().test(cmsVersionData.getItemUUID(), cmsVersionData.getLabel()))
		{
			errors.rejectValue(FIELD_LABEL, CmsfacadesConstants.FIELD_ALREADY_EXIST);
		}
	}

	protected BiPredicate<String, String> getLabelExistsInCMSVersionsPredicate()
	{
		return labelExistsInCMSVersionsPredicate;
	}

	@Required
	public void setLabelExistsInCMSVersionsPredicate(final BiPredicate<String, String> labelExistsInCMSVersionsPredicate)
	{
		this.labelExistsInCMSVersionsPredicate = labelExistsInCMSVersionsPredicate;
	}
}
