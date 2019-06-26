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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistrybackoffice.actions.UnregisterExposedDestinationAction;
import de.hybris.platform.apiregistryservices.exceptions.ApiRegistrationException;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.services.ApiRegistrationService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;


@UnitTest
public class UnregisterApiSpecificationActionTest extends AbstractActionUnitTest<UnregisterExposedDestinationAction>
{
	private static final String TEST_API_NAME = "Test name";
	private static final String TEST_EXCEPTION_MESSAGE = "Test exception message";
	private static final String TEST_MESSAGE_CONFIRM = "Are you sure you want to unregister [{0}] API on Kyma?";

	@Mock
	private ActionContext<ExposedDestinationModel> actionContext;

	@Mock
	private ApiRegistrationService apiRegistrationService;

	@Mock
	private ExposedDestinationModel destinationModel;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private UnregisterExposedDestinationAction unregisterApiSpecificationAction = new UnregisterExposedDestinationAction();

	@Override
	public UnregisterExposedDestinationAction getActionInstance()
	{
		return unregisterApiSpecificationAction;
	}

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		when(actionContext.getData()).thenReturn(destinationModel);
		when(destinationModel.getId()).thenReturn(TEST_API_NAME);
	}

	@Test
	public void testCannotPerformWithNullAction() throws ApiRegistrationException
	{
		when(actionContext.getData()).thenReturn(null);

		assertThat(unregisterApiSpecificationAction.canPerform(actionContext)).isFalse();
		verify(apiRegistrationService, never()).unregisterExposedDestination(null);
	}

	@Test
	public void testApiUnregistration() throws ApiRegistrationException
	{
		doNothing().when(apiRegistrationService).unregisterExposedDestination(any());
		doNothing().when(notificationService).notifyUser(anyString(), anyString(), any(), any());

		assertThat(unregisterApiSpecificationAction.canPerform(actionContext)).isTrue();
		assertEquals(ActionResult.SUCCESS, unregisterApiSpecificationAction.perform(actionContext).getResultCode());
		verify(apiRegistrationService, times(1)).unregisterExposedDestination(destinationModel);
	}

	@Test
	public void testApiUnregistrationWithEmptyTargetUid() throws ApiRegistrationException
	{
		doThrow(new IllegalArgumentException()).when(apiRegistrationService).unregisterExposedDestination(any());
		doNothing().when(notificationService).notifyUser(anyString(), anyString(), any(), any());

		assertThat(unregisterApiSpecificationAction.canPerform(actionContext)).isTrue();
		assertEquals(ActionResult.ERROR, unregisterApiSpecificationAction.perform(actionContext).getResultCode());
		verify(apiRegistrationService, times(1)).unregisterExposedDestination(destinationModel);
	}

	@Test
	public void testApiUnregistrationWithRestClientException() throws ApiRegistrationException
	{
		doThrow(new RestClientException(TEST_EXCEPTION_MESSAGE)).when(apiRegistrationService).unregisterExposedDestination(any());
		doNothing().when(notificationService).notifyUser(anyString(), anyString(), any(), any());

		assertThat(unregisterApiSpecificationAction.canPerform(actionContext)).isTrue();
		assertEquals(ActionResult.ERROR, unregisterApiSpecificationAction.perform(actionContext).getResultCode());
		verify(apiRegistrationService, times(1)).unregisterExposedDestination(destinationModel);
	}


	@Test
	public void testConformationMessage()
	{
		when(actionContext.getLabel(any(), any())).thenReturn(TEST_MESSAGE_CONFIRM);
		assertEquals(TEST_MESSAGE_CONFIRM, unregisterApiSpecificationAction.getConfirmationMessage(actionContext));
	}
}
