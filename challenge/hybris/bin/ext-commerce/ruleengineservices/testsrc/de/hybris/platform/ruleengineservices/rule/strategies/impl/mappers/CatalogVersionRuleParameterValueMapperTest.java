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
package de.hybris.platform.ruleengineservices.rule.strategies.impl.mappers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.daos.CatalogVersionDao;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CatalogVersionRuleParameterValueMapperTest
{
	private static final String ANY_VALID_STRING = "any::String";
	private static final String ANY_INVALID_STRING = "anyString";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private CatalogVersionDao catalogVersionDao;

	@InjectMocks
	private final CatalogVersionRuleParameterValueMapper mapper = new CatalogVersionRuleParameterValueMapper();

	@Before
	public void setUp()
	{
		mapper.setDelimiter("::");
	}

	@Test
	public void nullTestFromString()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.fromString(null);
	}

	@Test
	public void invalidtestFromString()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.fromString(ANY_INVALID_STRING);
	}

	@Test
	public void nullTestToString()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.toString(null);
	}

	@Test
	public void noCatalogVersionFoundTest()
	{
		//given
		BDDMockito.given(catalogVersionDao.findCatalogVersions(Mockito.anyString(), Mockito.anyString())).willReturn(
				Collections.emptyList());

		//expect
		expectedException.expect(RuleParameterValueMapperException.class);

		//when
		mapper.fromString(ANY_VALID_STRING);
	}

	@Test
	public void mappedCatalogVersionTest()
	{
		final CatalogVersionModel catalogVersionModelFromDao = Mockito.mock(CatalogVersionModel.class);
		final Collection<CatalogVersionModel> catalogVersionsModelFromDao = Collections
				.singletonList(catalogVersionModelFromDao);
		BDDMockito.given(catalogVersionDao.findCatalogVersions(Mockito.anyString(), Mockito.anyString())).willReturn(
				catalogVersionsModelFromDao);

		//when
		final CatalogVersionModel mappedCatalogVersion = mapper.fromString(ANY_VALID_STRING);

		//then
		Assert.assertEquals(catalogVersionModelFromDao, mappedCatalogVersion);
	}
}
