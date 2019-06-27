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
package de.hybris.platform.personalizationservices.cronjob;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.jobs.GenericMaintenanceJobPerformable;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessCleanupCronJobModel;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


public class CxPersonalizationProcessCleanupCronJobIntegrationTest extends ServicelayerTest
{

	@Resource(mappedName = "cleanupCxPersonalizationProcessJobsPerformable")
	private GenericMaintenanceJobPerformable cleanupCxPersonalizationProcessJobsPerformable;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private CxPersonalizationProcessCleanupCronJobModel cronJob;

	@Before
	public void setup() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		createCoreData();
		importData(new ClasspathImpExResource("/personalizationservices/test/testdata_personalizationprocesscleanupjob.impex", "UTF-8"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenCronJobIsNull()
	{
		// given
		final CxPersonalizationProcessCleanupCronJobModel cronJob = null;

		//when
		cleanupCxPersonalizationProcessJobsPerformable.perform(cronJob);
	}

	@Test
	public void shouldRemoveOnlySucceededProcesses()
	{
		final Set<ProcessState> processStates = Sets.newHashSet(ProcessState.SUCCEEDED);
		final int existingCxProcessesForStatesBeforeCleanup = countExistingCxProcessesForStates(processStates);
		final int countAllCxProcessesBeforeCleanup = countAllCxProcesses();

		// given
		given(cronJob.getProcessStates()).willReturn(processStates);
		given(cronJob.getMaxProcessAge()).willReturn("-1");

		// when
		final PerformResult performResult = cleanupCxPersonalizationProcessJobsPerformable.perform(cronJob);

		// then
		assertThat(existingCxProcessesForStatesBeforeCleanup).isGreaterThan(0);
		assertThat(countAllCxProcessesBeforeCleanup).isGreaterThan(0);
		assertThat(performResult).isNotNull();
		assertThat(performResult.getResult()).isEqualTo(CronJobResult.SUCCESS);
		assertThat(countExistingCxProcessesForStates(processStates)).isEqualTo(0);
		assertThat(countAllCxProcesses()).isEqualTo(countAllCxProcessesBeforeCleanup - existingCxProcessesForStatesBeforeCleanup);
	}

	@Test
	public void shouldRemoveOnlyInactiveProcesses()
	{
		final Set<ProcessState> processStates = Sets.newHashSet(ProcessState.SUCCEEDED, ProcessState.ERROR, ProcessState.FAILED);
		final int existingCxProcessesForStatesBeforeCleanup = countExistingCxProcessesForStates(processStates);
		final int countAllCxProcessesBeforeCleanup = countAllCxProcesses();

		// given
		given(cronJob.getProcessStates()).willReturn(processStates);
		given(cronJob.getMaxProcessAge()).willReturn("-1");

		// when
		final PerformResult performResult = cleanupCxPersonalizationProcessJobsPerformable.perform(cronJob);

		// then
		assertThat(existingCxProcessesForStatesBeforeCleanup).isGreaterThan(0);
		assertThat(countAllCxProcessesBeforeCleanup).isGreaterThan(0);
		assertThat(performResult).isNotNull();
		assertThat(performResult.getResult()).isEqualTo(CronJobResult.SUCCESS);
		assertThat(countExistingCxProcessesForStates(processStates)).isEqualTo(0);
		assertThat(countAllCxProcesses()).isEqualTo(countAllCxProcessesBeforeCleanup - existingCxProcessesForStatesBeforeCleanup);
	}

	private int countAllCxProcesses()
	{
		final String query = "SELECT COUNT( distinct {p." + CxPersonalizationProcessModel.PK + "} ) FROM {" + CxPersonalizationProcessModel._TYPECODE + " as p}";

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query.toString());
		fsq.setResultClassList(Collections.singletonList(Integer.class));

		final SearchResult<Integer> result = flexibleSearchService.search(fsq);

		return result.getResult().get(0).intValue();
	}

	private int countExistingCxProcessesForStates(final Collection<ProcessState> processStates)
	{
		final String query = "SELECT COUNT( distinct {p." + CxPersonalizationProcessModel.PK + "} ) FROM {" + CxPersonalizationProcessModel._TYPECODE + " as p}" //
				+ "WHERE {p." + CxPersonalizationProcessModel.STATE + "} IN (?states) ";

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("states", processStates);

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query.toString(), params);
		fsq.setResultClassList(Collections.singletonList(Integer.class));

		final SearchResult<Integer> result = flexibleSearchService.search(fsq);

		return result.getResult().get(0).intValue();
	}

}
