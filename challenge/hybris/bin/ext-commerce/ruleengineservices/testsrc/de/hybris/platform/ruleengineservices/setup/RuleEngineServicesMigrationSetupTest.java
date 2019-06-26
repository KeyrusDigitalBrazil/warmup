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
package de.hybris.platform.ruleengineservices.setup;

import static com.google.common.collect.Lists.newArrayList;
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
public class RuleEngineServicesMigrationSetupTest
{
	@InjectMocks
	private RuleEngineServicesMigrationSetup ruleEngineServicesMigrationSetup;
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
		ruleEngineServicesMigrationSetup.setMigrationTasks(newArrayList(task1,task2));
	}

	@Test
	public void shouldExecuteMigrationTasks() throws Exception
	{
		//when
		ruleEngineServicesMigrationSetup.execute(context);
		//then
		verify(task1, times(1)).execute(context);
		verify(task2, times(1)).execute(context);
	}
}
