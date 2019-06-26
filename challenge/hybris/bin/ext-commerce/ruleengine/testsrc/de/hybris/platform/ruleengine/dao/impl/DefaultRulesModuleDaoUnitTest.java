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
package de.hybris.platform.ruleengine.dao.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRulesModuleDaoUnitTest
{
	@InjectMocks
	private DefaultRulesModuleDao rulesModuleDao;
	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Test
	public void testFindByNameAndVersion()
	{
		final AbstractRulesModuleModel rulesModule = new AbstractRulesModuleModel();

		when(flexibleSearchService.<AbstractRulesModuleModel> searchUnique(any(FlexibleSearchQuery.class))).thenReturn(rulesModule);

		final AbstractRulesModuleModel ruleModule = rulesModuleDao.findByNameAndVersion("TEST_NAME", 0l);
		assertThat(ruleModule).isNotNull().isInstanceOf(AbstractRulesModuleModel.class).isEqualTo(ruleModule);
	}

	@Test
	public void testFindByNameAndVersionModelNotFound()
	{
		when(flexibleSearchService.<AbstractRulesModuleModel> searchUnique(any(FlexibleSearchQuery.class)))
				.thenThrow(ModelNotFoundException.class);

		final AbstractRulesModuleModel ruleModule = rulesModuleDao.findByNameAndVersion("TEST_NAME", 0l);
		assertThat(ruleModule).isNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindByNameAndVersionModelNullName()
	{
		rulesModuleDao.findByNameAndVersion(null, 0l);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindByNameAndVersionModelNegativeVersion()
	{
		rulesModuleDao.findByNameAndVersion("TEST_NAME", -1l);
	}

}
