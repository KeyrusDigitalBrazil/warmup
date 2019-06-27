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
package de.hybris.backoffice.apiregistrybackofficeactions;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistrybackoffice.actions.RegisterExposedDestinationAction;
import de.hybris.platform.apiregistryservices.exceptions.ApiRegistrationException;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.services.ApiRegistrationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;


@UnitTest
@RunWith(PowerMockRunner.class)
@PrepareForTest(
{ LoggerFactory.class })
@PowerMockIgnore(
{ "org.apache.logging.log4j.*" })
public class RegisterExposedDestinationActionTest extends AbstractActionUnitTest<RegisterExposedDestinationAction>
{
	private static final String UNKNOWN_API = "unknownApiType";
	private static final String TEST_API_NAME = "test name";
	private static final String TEST_MESSAGE_CONFIRM = "Are you sure you want to register [{0}] API on Kyma?";

	@Mock
	private ActionContext<ExposedDestinationModel> ctx;

	@Mock
	private ApiRegistrationService apiRegistrationService;

	@Mock
	private ExposedDestinationModel destinationModel;

	@InjectMocks
	private RegisterExposedDestinationAction action = new RegisterExposedDestinationAction();

	@Override
	public RegisterExposedDestinationAction getActionInstance()
	{
		return action;
	}

	@Before
	public void setUp() throws ApiRegistrationException
	{
		MockitoAnnotations.initMocks(this);

		PowerMockito.mockStatic(LoggerFactory.class);
		final Logger logger = mock(Logger.class);

		doNothing().when(apiRegistrationService).registerExposedDestination(any());

		when(destinationModel.getId()).thenReturn(TEST_API_NAME);
		when(destinationModel.isActive()).thenReturn(true);

		when(ctx.getData()).thenReturn(destinationModel);
		when(ctx.getLabel(any(), any())).thenReturn(TEST_MESSAGE_CONFIRM);
	}

	@Test
	public void testCannotPerformWithNull() throws ApiRegistrationException
	{
		when(ctx.getData()).thenReturn(null);

		assertThat(action.canPerform(ctx)).isFalse();
		verify(apiRegistrationService, never()).registerExposedDestination(null);
	}


	@Test
	public void testCannotPerformWithExportFlagFalse() throws ApiRegistrationException
	{
		when(destinationModel.isActive()).thenReturn(false);

		assertThat(action.canPerform(ctx)).isFalse();
		verify(apiRegistrationService, never()).registerExposedDestination(null);
	}

	@Test
	public void testWebserviceRegistration() throws  ApiRegistrationException {
		when(destinationModel.getAdditionalProperties()).thenReturn(ImmutableMap.of("type", "web"));

		assertThat(action.canPerform(ctx)).isTrue();
		assertEquals(ActionResult.SUCCESS, action.perform(ctx).getResultCode());
		verify(apiRegistrationService, times(1)).registerExposedDestination(destinationModel);
	}

	@Test
	public void testEventsRegistration() throws  ApiRegistrationException {
		when(destinationModel.getAdditionalProperties()).thenReturn(ImmutableMap.of("type", "events"));

		assertThat(action.canPerform(ctx)).isTrue();
		assertEquals(ActionResult.SUCCESS, action.perform(ctx).getResultCode());
		verify(apiRegistrationService, times(1)).registerExposedDestination(destinationModel);
	}

	@Test
	public void testConformationMessage()
	{
		assertEquals(TEST_MESSAGE_CONFIRM, action.getConfirmationMessage(ctx));
	}
}
