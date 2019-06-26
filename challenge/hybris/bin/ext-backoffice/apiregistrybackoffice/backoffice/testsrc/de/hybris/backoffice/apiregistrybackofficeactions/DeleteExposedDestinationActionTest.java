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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectDeletionException;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistrybackoffice.actions.DeleteExposedDestinationAction;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacadeOperationResult;
import com.hybris.cockpitng.dataaccess.facades.object.impl.DefaultObjectFacade;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;


@UnitTest
public class DeleteExposedDestinationActionTest extends AbstractActionUnitTest<DeleteExposedDestinationAction>
{
	private static final String TEST_TARGET_UID = "Test targetUid";
	private static final String TEST_MESSAGE_CONFIRM = "Are you sure you want to delete API configuration(s)?";

	@Mock
	private ActionContext<Object> actionContext;

	@Mock
	private ExposedDestinationModel destinationModel;

	@Mock
	private ExposedDestinationModel destinationModel2;

	private List destModels;

	@Mock
	private NotificationService notificationService;

	@Mock
	private DefaultObjectFacade objectFacade;

	@InjectMocks
	private DeleteExposedDestinationAction deleteExposedDestinationAction = new DeleteExposedDestinationAction();

	@Override
	public DeleteExposedDestinationAction getActionInstance()
	{
		return deleteExposedDestinationAction;
	}

	@Before
	public void setup() throws ObjectNotFoundException,ObjectDeletionException
	{
		MockitoAnnotations.initMocks(this);

		when(actionContext.getData()).thenReturn(destinationModel);
		when(destinationModel.getTargetId()).thenReturn(TEST_TARGET_UID);
		when(destinationModel2.getTargetId()).thenReturn(TEST_TARGET_UID);

		doNothing().when(notificationService).notifyUser(anyString(), anyString(), any(), any());
		final ObjectFacadeOperationResult<ExposedDestinationModel> result = new ObjectFacadeOperationResult<>();
		result.addSuccessfulObject(destinationModel);
		
		doReturn(result).when(objectFacade).reload(anyCollection());
		doReturn(result).when(objectFacade).delete(anyCollection());
		destModels = Arrays.asList(destinationModel, destinationModel2);
	}

	@Test
	public void testRegisteredApi()
	{
		assertEquals(ActionResult.ERROR, deleteExposedDestinationAction.perform(actionContext).getResultCode());

		when(destinationModel.getTargetId()).thenReturn(null);
		assertEquals(ActionResult.SUCCESS, deleteExposedDestinationAction.perform(actionContext).getResultCode());
	}

	@Test
	public void testRegisteredApiList()
	{
		when(actionContext.getData()).thenReturn(destModels);
		assertEquals(ActionResult.ERROR, deleteExposedDestinationAction.perform(actionContext).getResultCode());

		when(destinationModel.getTargetId()).thenReturn(null);
		assertEquals(ActionResult.ERROR, deleteExposedDestinationAction.perform(actionContext).getResultCode());

		when(destinationModel2.getTargetId()).thenReturn(null);
		assertEquals(ActionResult.SUCCESS, deleteExposedDestinationAction.perform(actionContext).getResultCode());
	}

	@Test
	public void testConformationMessage()
	{
		when(actionContext.getLabel(any())).thenReturn(TEST_MESSAGE_CONFIRM);
		assertEquals(TEST_MESSAGE_CONFIRM, deleteExposedDestinationAction.getConfirmationMessage(actionContext));
	}
}
