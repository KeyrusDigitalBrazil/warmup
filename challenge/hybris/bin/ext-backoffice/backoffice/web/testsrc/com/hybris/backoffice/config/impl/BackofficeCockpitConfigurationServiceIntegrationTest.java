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
package com.hybris.backoffice.config.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.jalo.user.Employee;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@IntegrationTest
public class BackofficeCockpitConfigurationServiceIntegrationTest extends ServicelayerTransactionalTest
{

	@InjectMocks
	private BackofficeCockpitConfigurationService configurationService = new BackofficeCockpitConfigurationService();

	@Resource
	private ModelService modelService;
	@Mock
	private SessionService sessionService;
	@Mock
	private UserService userService;
	@Mock
	private ModelService mockedModelService;
	@Mock
	private BackofficeConfigurationMediaHelper backofficeConfigurationMediaHelper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final EmployeeModel user = mock(EmployeeModel.class);
		when(mockedModelService.getSource(user)).thenReturn(mock(Employee.class));
		when(userService.getCurrentUser()).thenReturn(user);
		when(userService.getAdminUser()).thenReturn(user);
		when(mockedModelService.create(any(Class.class)))
				.thenAnswer(invocationOnMock -> modelService.create((Class) invocationOnMock.getArguments()[0]));
		doAnswer(invocationOnMock -> {
			modelService.save(invocationOnMock.getArguments()[0]);
			return null;
		}).when(mockedModelService).save(any());

		when(sessionService.executeInLocalView(any(SessionExecutionBody.class), any())).thenAnswer(invocationOnMock ->
		{
			SessionExecutionBody body = (SessionExecutionBody) invocationOnMock.getArguments()[0];
			return body.execute();
		});
	}
}