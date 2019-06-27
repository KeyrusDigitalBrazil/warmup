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
package de.hybris.platform.ruleengineservices.setup.tasks.impl;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.ruleengineservices.setup.tasks.MigrationTask;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class Migration64TaskTest
{
	@InjectMocks
	private MigrationTo64Procedure migrationTo64Procedure;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private MigrationTask task1;
	@Mock
	private MigrationTask task2;
	@Mock
	private SearchResult searchResults;
	@Mock
	private SystemSetupContext context;

	@Before
	public void setUp() throws Exception
	{
		migrationTo64Procedure.setMigrationTasks(newArrayList(task1,task2));
	}

	@Test
	public void shouldSkipTaskExecutionIfNotRequred() throws Exception
	{
		//given
		given(flexibleSearchService.search(MigrationTo64Procedure.SELECT_RULES_WITHOUT_VERSION)).willReturn(searchResults);
		given(searchResults.getTotalCount()).willReturn(Integer.valueOf(0));
		//when
		migrationTo64Procedure.execute(context);
		//then
		verify(task1, never()).execute(context);
		verify(task2, never()).execute(context);
	}

	@Test
	public void shouldExecuteMigrationTaskIfRequred() throws Exception
	{
		//given
		given(flexibleSearchService.search(MigrationTo64Procedure.SELECT_RULES_WITHOUT_VERSION)).willReturn(searchResults);
		given(searchResults.getTotalCount()).willReturn(Integer.valueOf(1));
		//when
		migrationTo64Procedure.execute(context);
		//then
		verify(task1, times(1)).execute(context);
		verify(task2, times(1)).execute(context);
	}
}
