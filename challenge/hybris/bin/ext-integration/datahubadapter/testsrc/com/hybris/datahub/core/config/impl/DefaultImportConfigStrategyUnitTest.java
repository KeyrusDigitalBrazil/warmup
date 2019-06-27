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
 */

package com.hybris.datahub.core.config.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.impex.ImpExResource;
import de.hybris.platform.servicelayer.impex.ImportConfig;

import com.hybris.datahub.core.dto.ItemImportTaskData;
import com.hybris.datahub.core.services.ImpExResourceFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultImportConfigStrategyUnitTest
{
	private static final ImportConfig.ValidationMode STRICT = ImportConfig.ValidationMode.STRICT;
	@InjectMocks
	private DefaultImportConfigStrategy configStrategy = new DefaultImportConfigStrategy();
	@Mock
	private ImpExResourceFactory impExResourceFactory;
	@Mock
	private ItemImportTaskData itemImportTaskData;
	@Mock
	private ImpExResource impExResource;
	private ImportConfig config;

	@Before
	public void setUp() throws Exception
	{
		when(impExResourceFactory.createResource(itemImportTaskData)).thenReturn(impExResource);
	}

	@Test(expected = ImpExException.class)
	public void testCreateResourceThrowsImpExException() throws ImpExException
	{
		when(impExResourceFactory.createResource(any(ItemImportTaskData.class))).thenThrow(new ImpExException("test"));
		configStrategy.createImportConfig(itemImportTaskData);
	}

	@Test
	public void testDistributedImpExIsTrueAndSldIsTrue() throws ImpExException
	{
		setConfigPropsAndAssert(true, true);
	}

	@Test
	public void testDistributedImpExIsTrueAndSldIsFalse() throws ImpExException
	{
		setConfigPropsAndAssert(true, false);
	}

	@Test
	public void testDistributedImpExIsFalseAndSldIsTrue() throws ImpExException
	{
		setConfigPropsAndAssert(false, true);
	}

	@Test
	public void testDistributedImpExIsFalseAndSldIsFalse() throws ImpExException
	{
		setConfigPropsAndAssert(false, false);
	}

	private void setConfigPropsAndAssert(final Boolean isDistributedImpex, final boolean isSld) throws ImpExException
	{
		configStrategy.setDistributedImpex(isDistributedImpex);
		configStrategy.setSld(isSld);
		config = configStrategy.createImportConfig(itemImportTaskData);

		assertConfigPropertiesSetCorrectly(isDistributedImpex, isSld);
	}

	private void assertConfigPropertiesSetCorrectly(final Boolean isDistImpex, final Boolean isSld)
	{
		assertThat(config.getScript()).isEqualTo(impExResource);
		assertThat(config.getValidationMode()).isEqualTo(STRICT);
		assertThat(config.isLegacyMode()).isFalse();
		assertThat(config.isDistributedImpexEnabled()).isEqualTo(isDistImpex);
		assertThat(config.isSldForData()).isEqualTo(isSld);
	}
}