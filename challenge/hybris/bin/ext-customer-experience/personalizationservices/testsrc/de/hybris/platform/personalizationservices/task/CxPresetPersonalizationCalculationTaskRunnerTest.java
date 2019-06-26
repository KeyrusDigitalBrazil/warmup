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
package de.hybris.platform.personalizationservices.task;

import static de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants.CONTEXT_CATALOG_VERSIONS_KEY;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class CxPresetPersonalizationCalculationTaskRunnerTest extends ServicelayerTransactionalTest
{

	@Resource
	private CxPresetPersonalizationCalculationTaskRunner cxPresetPersonalizationCalculationTaskRunner;
	@Resource
	private ModelService modelService;
	@Resource
	private TaskService taskService;
	@Mock
	private CxService cxService;
	@Mock
	private CatalogVersionModel catalogVersion;

	@Before
	public void setup() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(catalogVersion.getPk()).thenReturn(PK.fromLong(1l));
		cxPresetPersonalizationCalculationTaskRunner.setCxService(cxService);
	}

	@Test
	public void testPresetPersonalizationCalculation() throws Exception
	{
		//given
		final Collection<CatalogVersionModel> catalogVersions = Collections.singleton(catalogVersion);
		final Map<String, Object> contextMap = createContext(catalogVersions);

		//when
		final TaskModel task = modelService.create(TaskModel.class);
		task.setContext(contextMap);
		cxPresetPersonalizationCalculationTaskRunner.run(taskService, task);

		//then
		Mockito.verify(cxPresetPersonalizationCalculationTaskRunner.getCxService(), Mockito.times(1))
				.calculateAndStoreDefaultPersonalization(catalogVersions);

	}

	protected Map<String, Object> createContext(final Collection<CatalogVersionModel> catalogVersions)
	{
		final Map<String, Object> contextMap = new HashMap<>();
		contextMap.put(CONTEXT_CATALOG_VERSIONS_KEY, catalogVersions);
		return contextMap;
	}

}
