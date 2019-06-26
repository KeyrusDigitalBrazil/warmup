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
package com.hybris.backoffice.solrsearch.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.solrsearch.model.SolrModifiedItemModel;


@RunWith(MockitoJUnitRunner.class)
public class CronJobSolrIndexSynchronizationStrategyTest
{
	private static final String TEST_TYPE_CODE = "testTypeCode";
	private static final long TEST_PK = 1_000_000L;

	@Mock
	private ModelService modelService;

	@InjectMocks
	@Spy
	private CronJobSolrIndexSynchronizationStrategy cronJobSolrIndexSynchronizationStrategy;

	@Before
	public void setUp() throws Exception
	{
		final SolrModifiedItemModel modifiedItem = mock(SolrModifiedItemModel.class);
		when(modelService.create(SolrModifiedItemModel.class)).thenReturn(modifiedItem);
	}

	@Test
	public void shouldAddModifiedItemWhenSettingIsSetToTrue() throws Exception
	{
		//given
		doReturn(true).when(cronJobSolrIndexSynchronizationStrategy).shouldUpdateModifiedItem();

		//when
		cronJobSolrIndexSynchronizationStrategy.updateItem(TEST_TYPE_CODE, TEST_PK);

		ArgumentCaptor<List> pkList = ArgumentCaptor.forClass(List.class);
		//then
		verify(cronJobSolrIndexSynchronizationStrategy, times(1)).addModifiedItems(eq(TEST_TYPE_CODE), pkList.capture(), any());
		assertThat(pkList.getValue()).hasSize(1);
		assertThat(pkList.getValue()).containsExactly(PK.fromLong(TEST_PK));
	}

	@Test
	public void shouldNotAddModifiedItemWhenSettingIsSetToFalse() throws Exception
	{
		//given
		doReturn(false).when(cronJobSolrIndexSynchronizationStrategy).shouldUpdateModifiedItem();

		//when
		cronJobSolrIndexSynchronizationStrategy.updateItem(TEST_TYPE_CODE, TEST_PK);

		//then
		verify(cronJobSolrIndexSynchronizationStrategy, never()).addModifiedItem(eq(TEST_TYPE_CODE), eq(TEST_PK), any());
	}

}
