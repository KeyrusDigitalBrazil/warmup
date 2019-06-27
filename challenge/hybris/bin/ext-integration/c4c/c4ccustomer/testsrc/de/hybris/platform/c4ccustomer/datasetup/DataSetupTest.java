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
package de.hybris.platform.c4ccustomer.datasetup;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.scripting.enums.ScriptType;
import de.hybris.platform.scripting.model.ScriptModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.junit.Before;
import org.junit.Test;


/**
 * Essential data setup tests.
 */
@UnitTest
public class DataSetupTest
{
	private DataSetup dataSetup;
	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setup()
	{
		modelService = mock(ModelService.class);
		flexibleSearchService = mock(FlexibleSearchService.class);
		doReturn(new ScriptModel()).when(modelService).create(ScriptModel.class);
		dataSetup = new DataSetup();
		dataSetup.setModelService(modelService);
		dataSetup.setFlexibleSearchService(flexibleSearchService);
	}

	/**
	 * Check if all scripts are available.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void shouldFindResources()
	{
		when(flexibleSearchService.getModelByExample(any(ScriptModel.class))).thenThrow(ModelNotFoundException.class);
		doAnswer(invocationOnMock ->
		{
			final Object arg = invocationOnMock.getArguments()[0];
			assertThat("Wrong argument of ModelService#save", arg, instanceOf(ScriptModel.class));
			final ScriptModel script = (ScriptModel) arg;
			assertThat("Incorrect type of script", script.getScriptType(), is(ScriptType.GROOVY));
			assertThat("Unexpected script content", script.getContent(), containsString("flexibleSearchService.search"));
			// save is a void-returning method, so it doesn't matter what to return
			return null;
		}).when(modelService).save(any(ScriptModel.class));
		dataSetup.createEssentialData();
	}


	/**
	 * Check if when updatingSystem no new item is created.
	 */
	@Test
	public void shouldUseCurrentScript()
	{
		final ScriptModel scriptModel = mock(ScriptModel.class);
		when(scriptModel.getCode()).thenReturn("c4cSync");
		when(scriptModel.getScriptType()).thenReturn(ScriptType.GROOVY);
		when(flexibleSearchService.getModelByExample(any(ScriptModel.class))).thenReturn(scriptModel);

		dataSetup.createEssentialData();

		verify(flexibleSearchService).getModelByExample(any(ScriptModel.class));
		verify(modelService, never()).create(ScriptModel.class);
		verify(scriptModel).setContent(anyString());
		verify(modelService).save(scriptModel);
	}
}
