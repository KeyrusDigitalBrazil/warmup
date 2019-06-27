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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cmsitems.service.CMSItemSearchService;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.CMSItemSearchData;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSItemSearchDataValidatorTest
{
	private final String MOCK_CATALOG_ID = "catId";
	private final String MOCK_CATALOG_VERSION = "catVer";
	private final String MISSING_PARAM = null;

	@InjectMocks
	private CMSItemSearchDataValidator validator;

	@Mock
	private CMSItemSearchService cmsItemSearchService;

	@Before
	public void setup()
	{
		when(cmsItemSearchService.hasCommonAncestorForTypeCodes(anyList())).thenReturn(true);
	}

	@Test
	public void validationPassesWhenCatalogInformationIsProvided()
	{
		final CMSItemSearchData data = createCMSItemSearchParamsData(MOCK_CATALOG_ID, MOCK_CATALOG_VERSION);
		final Errors errors = new BeanPropertyBindingResult(data,
				data.getClass().getSimpleName());

		validator.validate(data, errors);
		assertThat(errors.getFieldErrorCount(), is(0));
	}

	@Test
	public void validationFailsWhenCatalogIdIsMissing()
	{
		final CMSItemSearchData data = createCMSItemSearchParamsData(MISSING_PARAM, MOCK_CATALOG_VERSION);
		final Errors errors = new BeanPropertyBindingResult(data,
				data.getClass().getSimpleName());

		validator.validate(data, errors);
		assertThat(errors.getFieldErrorCount(), is(1));
		assertThat(errors.getFieldErrors().get(0).getCode(), is(CmsfacadesConstants.FIELD_REQUIRED));
		assertThat(errors.getFieldErrors().get(0).getField(), is(CMSItemSearchDataValidator.FIELD_NAME_CATALOG_ID));
	}

	@Test
	public void validationFailsWhenCatalogVersionIsMissing()
	{
		final CMSItemSearchData data = createCMSItemSearchParamsData(MOCK_CATALOG_ID, MISSING_PARAM);
		final Errors errors = new BeanPropertyBindingResult(data,
				data.getClass().getSimpleName());

		validator.validate(data, errors);
		assertThat(errors.getFieldErrorCount(), is(1));
		assertThat(errors.getFieldErrors().get(0).getCode(), is(CmsfacadesConstants.FIELD_REQUIRED));
		assertThat(errors.getFieldErrors().get(0).getField(), is(CMSItemSearchDataValidator.FIELD_NAME_CATALOG_VERSION));
	}

	@Test
	public void validationFailIfBothTypeCodeAndTypeCodesAreProvided()
	{
		final CMSItemSearchData data = createCMSItemSearchParamsData(MOCK_CATALOG_ID, MOCK_CATALOG_VERSION);
		data.setTypeCode("type1");
		data.setTypeCodes("type1");

		final Errors errors = new BeanPropertyBindingResult(data,
				data.getClass().getSimpleName());

		validator.validate(data, errors);

		assertThat(errors.getFieldErrorCount(), is(1));
		assertThat(errors.getFieldErrors().get(0).getCode(), is(CmsfacadesConstants.INVALID_TYPECODE_COMBINATION));
		assertThat(errors.getFieldErrors().get(0).getField(), is(CMSItemSearchDataValidator.FIELD_TYPECODE));
	}

	@Test
	public void validationFailIfNoCommonAncestorForTypeCodesIsFound()
	{
		when(cmsItemSearchService.hasCommonAncestorForTypeCodes(anyList())).thenReturn(false);

		final CMSItemSearchData data = createCMSItemSearchParamsData(MOCK_CATALOG_ID, MOCK_CATALOG_VERSION);
		data.setTypeCode("type1");

		final Errors errors = new BeanPropertyBindingResult(data,
				data.getClass().getSimpleName());

		validator.validate(data, errors);

		assertThat(errors.getFieldErrorCount(), is(1));
		assertThat(errors.getFieldErrors().get(0).getCode(), is(CmsfacadesConstants.INVALID_TYPECODE_VALUE));
		assertThat(errors.getFieldErrors().get(0).getField(), is(CMSItemSearchDataValidator.FIELD_TYPECODE));
	}

	@Test
	public void testGetTypeCodesFromSearchDataMultiTypeCodes()
	{
		final CMSItemSearchData data = new CMSItemSearchData();
		data.setTypeCodes("type1, type2");

		final List<String> typeCodes = validator.getTypeCodesFromSearchData(data);

		assertThat(typeCodes.size(), is(2));
		assertThat(typeCodes.get(0), equalTo("type1"));
		assertThat(typeCodes.get(1), equalTo("type2"));
	}

	@Test
	public void testGetTypeCodesFromSearchDataSingleTypeCode()
	{
		final CMSItemSearchData data = new CMSItemSearchData();
		data.setTypeCode("type1");

		final List<String> typeCodes = validator.getTypeCodesFromSearchData(data);

		assertThat(typeCodes.size(), is(1));
		assertThat(typeCodes.get(0), equalTo("type1"));
	}

	protected CMSItemSearchData createCMSItemSearchParamsData(final String catalogId, final String catalogVersion)
	{
		final CMSItemSearchData data = new CMSItemSearchData();
		data.setCatalogId(catalogId);
		data.setCatalogVersion(catalogVersion);
		return data;
	}
}
