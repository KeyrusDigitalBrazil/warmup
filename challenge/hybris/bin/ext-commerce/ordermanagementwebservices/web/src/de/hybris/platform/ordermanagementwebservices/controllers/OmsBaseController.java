/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.ordermanagementwebservices.controllers;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import javax.annotation.Resource;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Abstract class containing basic controller functionality
 */
public abstract class OmsBaseController
{
	public static final String DEFAULT_FIELD_SET = "DEFAULT";
	public static final String DEFAULT_CURRENT_PAGE = "0";
	public static final String DEFAULT_PAGE_SIZE = "10";
	public static final String DEFAULT_SORT = "asc";

	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;

	/**
	 * Creates a pageableData with provided page, pageSize and sort
	 *
	 * @param page
	 *           current page number
	 * @param pageSize
	 *           number of items in a page
	 * @param sort
	 *           sorting the results ascending or descending
	 * @return a pageableData
	 */
	protected PageableData createPageable(final int page, final int pageSize, final String sort)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(page);
		pageableData.setPageSize(pageSize);
		pageableData.setSort(sort);
		return pageableData;
	}

	/**
	 * Validates the object by using the passed validator
	 *
	 * @param object
	 * 			the object ot be validated
	 * @param objectName
	 * 			the object name
	 * @param validator
	 * 			validator which will validate the object
	 */
	protected void validate(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}
}
