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

import de.hybris.platform.cmsfacades.common.predicate.ContentPageLabelOrIdExistsPredicate;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.dto.RenderingPageValidationDto;
import de.hybris.platform.cmsfacades.rendering.validators.page.RenderingPageChecker;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

import java.util.function.Predicate;


/**
 * Implementation of {@link RenderingPageChecker} to validate Content page attributes of {@link RenderingPageValidationDto}.
 */
public class RenderingContentPageChecker implements RenderingPageChecker
{
	private Predicate<String> pagePredicate;

	private ContentPageLabelOrIdExistsPredicate contentPageLabelOrIdExistsPredicate;

	@Override
	public Predicate<String> getConstrainedBy()
	{
		return pagePredicate;
	}

	@Override
	public void verify(RenderingPageValidationDto validationDto, Errors errors)
	{
		if (validationDto.getPageLabelOrId() != null && getContentPageLabelOrIdExistsPredicate().negate()
				.test(validationDto.getPageLabelOrId()))
		{
			errors.rejectValue("pageLabelOrId", CmsfacadesConstants.INVALID_PAGE_LABEL_OR_ID);
		}
	}

	protected Predicate<String> getPagePredicate()
	{
		return pagePredicate;
	}

	@Required
	public void setPagePredicate(Predicate<String> pagePredicate)
	{
		this.pagePredicate = pagePredicate;
	}

	protected ContentPageLabelOrIdExistsPredicate getContentPageLabelOrIdExistsPredicate()
	{
		return contentPageLabelOrIdExistsPredicate;
	}

	@Required
	public void setContentPageLabelOrIdExistsPredicate(
			ContentPageLabelOrIdExistsPredicate contentPageLabelOrIdExistsPredicate)
	{
		this.contentPageLabelOrIdExistsPredicate = contentPageLabelOrIdExistsPredicate;
	}
}
