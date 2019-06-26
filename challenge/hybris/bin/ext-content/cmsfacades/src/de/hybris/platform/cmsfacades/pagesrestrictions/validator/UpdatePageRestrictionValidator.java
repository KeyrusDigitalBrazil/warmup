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
package de.hybris.platform.cmsfacades.pagesrestrictions.validator;

import de.hybris.platform.cmsfacades.common.predicate.PageExistsPredicate;
import de.hybris.platform.cmsfacades.common.predicate.RestrictionExistsPredicate;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.PageRestrictionData;

import java.util.Objects;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validate if required information like restrictionId and pageId were available for each member of the provided list.
 *
 */
/**
 * Validates DTO for updating existing pages-restrictions relations.
 *
 * <p>
 * Rules:</br>
 * <ul>
 * <li>pageId not null</li>
 * <li>page exists: {@link PageExistsPredicate}</li>
 * <li>restrictionId not null</li>
 * <li>restrictionId exists: {@link RestrictionExistsPredicate}</li>
 * </ul>
 * </p>
 */
public class UpdatePageRestrictionValidator implements Validator
{

	public static final String PAGE_ID = "pageId";
	public static final String RESTRICTION_ID = "restrictionId";

	private Predicate<String> restrictionExistsPredicate;
	private Predicate<String> pageExistsPredicate;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return PageRestrictionData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final PageRestrictionData target = (PageRestrictionData) obj;

		if (Objects.isNull(target.getPageId()))
		{
			errors.rejectValue(PAGE_ID, CmsfacadesConstants.FIELD_REQUIRED);
		}
		else if (!getPageExistsPredicate().test(target.getPageId()))
		{
			errors.rejectValue(PAGE_ID, CmsfacadesConstants.FIELD_DOES_NOT_EXIST);
		}

		if (Objects.isNull(target.getRestrictionId()))
		{
			errors.rejectValue(RESTRICTION_ID, CmsfacadesConstants.FIELD_REQUIRED);
		}
		else if (!getRestrictionExistsPredicate().test(target.getRestrictionId()))
		{
			errors.rejectValue(RESTRICTION_ID, CmsfacadesConstants.FIELD_DOES_NOT_EXIST);
		}
	}

	protected Predicate<String> getRestrictionExistsPredicate()
	{
		return restrictionExistsPredicate;
	}

	@Required
	public void setRestrictionExistsPredicate(final Predicate<String> restrictionExistsPredicate)
	{
		this.restrictionExistsPredicate = restrictionExistsPredicate;
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
