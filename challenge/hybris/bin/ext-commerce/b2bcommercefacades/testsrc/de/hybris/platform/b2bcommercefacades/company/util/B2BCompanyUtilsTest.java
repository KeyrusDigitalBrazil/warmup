/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2bcommercefacades.company.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;


/**
 * Unit tests for {@link B2BCompanyUtils}.
 */
@UnitTest
public class B2BCompanyUtilsTest
{
	private static final String CODE = "code";

	@Test
	public void testCreateB2BSelectionData()
	{
		final B2BSelectionData b2BSelectionData = new B2BSelectionData();
		b2BSelectionData.setId(CODE);
		b2BSelectionData.setNormalizedCode(CODE);
		b2BSelectionData.setSelected(true);
		b2BSelectionData.setActive(true);

		final B2BSelectionData result = B2BCompanyUtils.createB2BSelectionData(b2BSelectionData.getId(),
				b2BSelectionData.isSelected(), b2BSelectionData.isActive());
		assertB2BSelectionDataFields(b2BSelectionData, result);
	}

	@Test
	public void testCreateB2BSelectionDataNormalizeCode()
	{
		final B2BSelectionData b2BSelectionData = new B2BSelectionData();
		b2BSelectionData.setId("a#b@");
		b2BSelectionData.setNormalizedCode("a" + B2BCompanyUtils.NORMALIZED_CHAR + "b" + B2BCompanyUtils.NORMALIZED_CHAR);
		b2BSelectionData.setSelected(true);
		b2BSelectionData.setActive(true);

		final B2BSelectionData result = B2BCompanyUtils.createB2BSelectionData(b2BSelectionData.getId(),
				b2BSelectionData.isSelected(), b2BSelectionData.isActive());
		assertB2BSelectionDataFields(b2BSelectionData, result);
	}

	@Test
	public void testConvertPageData()
	{
		final SearchPageData<String> input = new SearchPageData<String>();
		final List<String> inputResults = Arrays.asList(new String[]
		{ "1", "2" });
		input.setResults(inputResults);
		input.setPagination(new PaginationData());
		input.setSorts(new ArrayList<SortData>());
		final Converter<String, Integer> converter = new String2Integer();

		final SearchPageData<Integer> output = B2BCompanyUtils.convertPageData(input, converter);

		assertNotNull(output);
		assertEquals(input.getPagination(), output.getPagination());
		assertEquals(input.getSorts(), output.getSorts());
		final List<Integer> outputResults = output.getResults();
		assertNotNull(outputResults);
		assertEquals(inputResults.size(), outputResults.size());
		for (int i = 0; i < outputResults.size(); i++)
		{
			assertEquals(converter.convert(inputResults.get(i)), outputResults.get(i));
		}
	}

	@Test
	public void testPopulateRolesForCustomer()
	{
		final B2BCustomerModel customerModel = new B2BCustomerModel();
		final PrincipalGroupModel[] groupsArray =
		{ buildPrincipalGroupModel(B2BConstants.B2BAPPROVERGROUP), buildPrincipalGroupModel(B2BConstants.B2BMANAGERGROUP) };
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(Arrays.asList(groupsArray));
		customerModel.setGroups(groups);

		final B2BSelectionData result = B2BCompanyUtils.populateRolesForCustomer(customerModel, new B2BSelectionData());
		assertNotNull(result);
		assertNotNull(result.getRoles());
		assertEquals(groupsArray.length, result.getRoles().size());
	}

	@Test
	public void testPopulateRolesForCustomerExcludesUnits()
	{
		final B2BCustomerModel customerModel = new B2BCustomerModel();
		final PrincipalGroupModel[] groupsArray =
		{ new B2BUnitModel(), buildPrincipalGroupModel(B2BConstants.B2BAPPROVERGROUP), new B2BUnitModel() };
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(Arrays.asList(groupsArray));
		customerModel.setGroups(groups);

		final B2BSelectionData result = B2BCompanyUtils.populateRolesForCustomer(customerModel, new B2BSelectionData());
		assertNotNull(result);
		assertNotNull(result.getRoles());
		assertEquals(1, result.getRoles().size());
	}

	@Test
	public void testPopulateRolesForCustomerExcludesUserGroups()
	{
		final B2BCustomerModel customerModel = new B2BCustomerModel();
		final PrincipalGroupModel[] groupsArray =
		{ new B2BUserGroupModel(), buildPrincipalGroupModel(B2BConstants.B2BAPPROVERGROUP), new B2BUserGroupModel() };
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(Arrays.asList(groupsArray));
		customerModel.setGroups(groups);

		final B2BSelectionData result = B2BCompanyUtils.populateRolesForCustomer(customerModel, new B2BSelectionData());
		assertNotNull(result);
		assertNotNull(result.getRoles());
		assertEquals(1, result.getRoles().size());
	}

	@Test
	public void testPopulateRolesForCustomerExcludesAll()
	{
		final B2BCustomerModel customerModel = new B2BCustomerModel();
		final PrincipalGroupModel[] groupsArray =
		{ new B2BUnitModel(), new PrincipalGroupModel(), new B2BUserGroupModel() };
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(Arrays.asList(groupsArray));
		customerModel.setGroups(groups);

		final B2BSelectionData result = B2BCompanyUtils.populateRolesForCustomer(customerModel, new B2BSelectionData());
		assertNotNull(result);
		assertNotNull(result.getRoles());
		assertTrue(result.getRoles().isEmpty());
	}

	@Test
	public void testPopulateRolesForCustomerEmpty()
	{
		final B2BCustomerModel customerModel = new B2BCustomerModel();
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		customerModel.setGroups(groups);

		final B2BSelectionData result = B2BCompanyUtils.populateRolesForCustomer(customerModel, new B2BSelectionData());
		assertNotNull(result);
		assertNotNull(result.getRoles());
	}

	private void assertB2BSelectionDataFields(final B2BSelectionData expected, final B2BSelectionData actual)
	{
		assertNotNull(actual);
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getNormalizedCode(), actual.getNormalizedCode());
		assertEquals(Boolean.valueOf(expected.isSelected()), Boolean.valueOf(actual.isSelected()));
		assertEquals(Boolean.valueOf(expected.isActive()), Boolean.valueOf(actual.isActive()));
	}

	private class String2Integer implements Converter<String, Integer>
	{

		@Override
		public Integer convert(final String arg0) throws ConversionException
		{
			return Integer.valueOf(arg0);
		}

		@Override
		public Integer convert(final String arg0, final Integer arg1) throws ConversionException
		{
			return convert(arg0);
		}
	}

	private PrincipalGroupModel buildPrincipalGroupModel(final String groupName)
	{
		final PrincipalGroupModel principalGroupModel = new PrincipalGroupModel();
		principalGroupModel.setUid(groupName);
		return principalGroupModel;
	}
}
