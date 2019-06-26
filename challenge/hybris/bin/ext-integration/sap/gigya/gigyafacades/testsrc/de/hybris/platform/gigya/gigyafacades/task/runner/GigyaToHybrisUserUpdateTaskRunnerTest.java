/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.gigya.gigyafacades.task.runner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSyncDirection;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gigya.socialize.GSResponse;


/**
 * Test class for GigyaToHybrisUserUpdateTaskRunner
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaToHybrisUserUpdateTaskRunnerTest
{

	@InjectMocks
	private final GigyaToHybrisUserUpdateTaskRunner taskRunner = new GigyaToHybrisUserUpdateTaskRunner();

	@Mock
	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	@Mock
	private ModelService modelService;

	@Mock
	private GigyaService gigyaService;

	@Mock
	private Converter<GSResponse, CustomerModel> gigyaUserReverseConverter;

	@Mock
	private TaskModel taskModel;

	@Mock
	private TaskService taskService;

	@Mock
	private CustomerModel gigyaUser;

	@Mock
	private GigyaFieldMappingModel gigyaFieldMappingModel;

	@Mock
	private GigyaFieldMappingModel anotherGigyaFieldMappingModel;

	@Mock
	private GSResponse gsResponse;

	@Test
	public void testWhenContextItemIsNull()
	{
		Mockito.when(taskModel.getContextItem()).thenReturn(null);

		taskRunner.run(taskService, taskModel);

		Mockito.verifyZeroInteractions(gigyaFieldMappingGenericDao);
	}

	@Test
	public void testWhenContextItemExistButNoMappings()
	{
		Mockito.when(taskModel.getContextItem()).thenReturn(gigyaUser);
		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(null);

		taskRunner.run(taskService, taskModel);

		Mockito.verifyZeroInteractions(gigyaService);
		Mockito.verifyZeroInteractions(modelService);
	}

	@Test
	public void testWhenContextItemExistAndMappingsExistWithIncorrectSyncDirection()
	{
		Mockito.when(taskModel.getContextItem()).thenReturn(gigyaUser);
		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(gigyaFieldMappingModel));
		Mockito.when(gigyaFieldMappingModel.getSyncDirection()).thenReturn(null);

		taskRunner.run(taskService, taskModel);

		Mockito.verifyZeroInteractions(gigyaService);
		Mockito.verifyZeroInteractions(modelService);
	}

	@Test
	public void testWhenContextItemExistAndMappingsExistWithcorrectSyncDirectionG2H() throws GigyaApiException
	{
		Mockito.when(taskModel.getContextItem()).thenReturn(gigyaUser);
		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(gigyaFieldMappingModel));
		Mockito.when(gigyaFieldMappingModel.getSyncDirection()).thenReturn(GigyaSyncDirection.G2H);
		Mockito.when(gigyaFieldMappingModel.getGigyaAttributeName()).thenReturn("sample.attribute");
		Mockito.when(gigyaService.callRawGigyaApiWithConfig(Mockito.anyString(), Mockito.anyMap(), Mockito.any(), Mockito.anyInt(),
				Mockito.anyInt())).thenReturn(gsResponse);

		taskRunner.run(taskService, taskModel);

		Mockito.verify(gigyaUserReverseConverter).convert(gsResponse, gigyaUser);
		Mockito.verify(modelService).save(gigyaUser);
	}

	@Test
	public void testWhenContextItemExistAndMappingsExistWithcorrectSyncDirectionBOTHAndMultipleMappings() throws GigyaApiException
	{
		Mockito.when(taskModel.getContextItem()).thenReturn(gigyaUser);
		final List<GigyaFieldMappingModel> fieldMappings = new ArrayList<>();
		fieldMappings.add(gigyaFieldMappingModel);
		fieldMappings.add(anotherGigyaFieldMappingModel);

		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(fieldMappings);
		Mockito.when(gigyaFieldMappingModel.getSyncDirection()).thenReturn(GigyaSyncDirection.BOTH);
		Mockito.when(gigyaFieldMappingModel.getGigyaAttributeName()).thenReturn("sample.attribute");

		Mockito.when(anotherGigyaFieldMappingModel.getSyncDirection()).thenReturn(GigyaSyncDirection.BOTH);
		Mockito.when(anotherGigyaFieldMappingModel.getGigyaAttributeName()).thenReturn("anotherAttr");

		Mockito.when(gigyaService.callRawGigyaApiWithConfig(Mockito.anyString(), Mockito.anyMap(), Mockito.any(), Mockito.anyInt(),
				Mockito.anyInt())).thenReturn(gsResponse);

		taskRunner.run(taskService, taskModel);

		Mockito.verify(gigyaUserReverseConverter).convert(gsResponse, gigyaUser);
		Mockito.verify(modelService).save(gigyaUser);
	}

	@Test
	public void testWhenContextItemExistAndMappingsExistWithcorrectSyncDirectionBOTHAndMultipleMappingsWIthIncorrectDirection()
			throws GigyaApiException
	{
		Mockito.when(taskModel.getContextItem()).thenReturn(gigyaUser);
		final List<GigyaFieldMappingModel> fieldMappings = new ArrayList<>();
		fieldMappings.add(gigyaFieldMappingModel);
		fieldMappings.add(anotherGigyaFieldMappingModel);

		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(fieldMappings);
		Mockito.when(gigyaFieldMappingModel.getSyncDirection()).thenReturn(GigyaSyncDirection.BOTH);
		Mockito.when(gigyaFieldMappingModel.getGigyaAttributeName()).thenReturn("sample.attribute");

		Mockito.when(anotherGigyaFieldMappingModel.getSyncDirection()).thenReturn(GigyaSyncDirection.H2G);
		Mockito.when(anotherGigyaFieldMappingModel.getGigyaAttributeName()).thenReturn("anotherAttr");

		Mockito.when(gigyaService.callRawGigyaApiWithConfig(Mockito.anyString(), Mockito.anyMap(), Mockito.any(), Mockito.anyInt(),
				Mockito.anyInt())).thenReturn(gsResponse);

		taskRunner.run(taskService, taskModel);

		Mockito.verify(gigyaUserReverseConverter).convert(gsResponse, gigyaUser);
		Mockito.verify(modelService).save(gigyaUser);
	}

}
