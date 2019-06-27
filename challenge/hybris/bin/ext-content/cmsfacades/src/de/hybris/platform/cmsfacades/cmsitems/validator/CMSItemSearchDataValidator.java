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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.INVALID_TYPECODE_COMBINATION;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.INVALID_TYPECODE_VALUE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import de.hybris.platform.cms2.cmsitems.service.CMSItemSearchService;
import de.hybris.platform.cmsfacades.data.CMSItemSearchData;


/**
 * CMSItemSearchDataValidator validates CMSItemSearchData objects, which are the search parameters
 * used to perform {@link de.hybris.platform.cms2.cmsitems.service.CMSItemSearchService} searches
 */
public class CMSItemSearchDataValidator implements Validator
{
	private static final String COMMA = ",";
	public static final String FIELD_TYPECODE = "typeCode";
	public static final String FIELD_NAME_CATALOG_ID = "catalogId";
	public static final String FIELD_NAME_CATALOG_VERSION = "catalogVersion";

	private CMSItemSearchService cmsItemSearchService;

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return CMSItemSearchData.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		ValidationUtils.rejectIfEmpty(errors, FIELD_NAME_CATALOG_ID, FIELD_REQUIRED);
		ValidationUtils.rejectIfEmpty(errors, FIELD_NAME_CATALOG_VERSION, FIELD_REQUIRED);

		final CMSItemSearchData itemSearchData = (CMSItemSearchData) target;
		if (Objects.nonNull(itemSearchData.getTypeCodes()) && Objects.nonNull(itemSearchData.getTypeCode()))
		{
			errors.rejectValue(FIELD_TYPECODE, INVALID_TYPECODE_COMBINATION);
		}

		final List<String> typeCodes = getTypeCodesFromSearchData(itemSearchData);
		if (CollectionUtils.isNotEmpty(typeCodes) && !getCmsItemSearchService().hasCommonAncestorForTypeCodes(typeCodes))
		{
			errors.rejectValue(FIELD_TYPECODE, INVALID_TYPECODE_VALUE, new Object[] { typeCodes }, null);
		}
	}

	/**
	 * Get the list of typeCodes given the cmsItemSearchData.
	 * @param cmsItemSearchData
	 * @return the list of typeCodes
	 */
	protected List<String> getTypeCodesFromSearchData(final CMSItemSearchData cmsItemSearchData)
	{
		final String typeCodes = StringUtils.isNotBlank(cmsItemSearchData.getTypeCode()) ? cmsItemSearchData.getTypeCode() : cmsItemSearchData.getTypeCodes();
		return getTypeCodesList(typeCodes);
	}

	/**
	 * Get the list of typeCodes given the typeCodes {@link String}
	 * @param typeCodes
	 * @return the list of typeCodes
	 */
	protected List<String> getTypeCodesList(final String typeCodes)
	{
		if (StringUtils.isNotBlank(typeCodes))
		{
			return Arrays.stream(typeCodes.split(COMMA)).map(String::trim).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	protected CMSItemSearchService getCmsItemSearchService()
	{
		return cmsItemSearchService;
	}

	@Required
	public void setCmsItemSearchService(final CMSItemSearchService cmsItemSearchService)
	{
		this.cmsItemSearchService = cmsItemSearchService;
	}

}
