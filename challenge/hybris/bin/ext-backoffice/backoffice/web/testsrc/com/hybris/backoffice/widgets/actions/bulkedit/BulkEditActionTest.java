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
package com.hybris.backoffice.widgets.actions.bulkedit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.google.common.collect.Lists;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacadeOperationResult;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;
import com.hybris.cockpitng.util.type.BackofficeTypeUtils;


public class BulkEditActionTest extends AbstractActionUnitTest<BulkEditAction>
{
	@InjectMocks
	@Spy
	private BulkEditAction bulkEditAction;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private BackofficeTypeUtils backofficeTypeUtils;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private ModelService modelService;

	@Override
	public BulkEditAction getActionInstance()
	{
		return bulkEditAction;
	}

	@Test
	public void testCanPerformWhenUserHasPermissions()
	{
		final ActionContext<Collection> actionContext = mock(ActionContext.class);
		final List<Object> selectedItems = Lists.newArrayList(1, 2, 3);
		when(objectFacade.reload(selectedItems)).thenReturn(createReloadResult(selectedItems));
		when(permissionFacade.canChangeInstances(selectedItems)).thenReturn(true);
		when(actionContext.getData()).thenReturn(selectedItems);

		assertThat(getActionInstance().canPerform(actionContext)).isTrue();

	}

	protected ObjectFacadeOperationResult<Object> createReloadResult(final List<Object> selectedItems)
	{
		final ObjectFacadeOperationResult<Object> reloadResult = new ObjectFacadeOperationResult<>();
		selectedItems.forEach(reloadResult::addSuccessfulObject);
		return reloadResult;
	}

	@Test
	public void testCannotPerformWhenUserDoesNotHavePermissions()
	{
		final ActionContext<Collection> actionContext = mock(ActionContext.class);
		final List<Object> selectedItems = Lists.newArrayList(1, 2, 3);
		when(objectFacade.reload(selectedItems)).thenReturn(createReloadResult(selectedItems));
		when(permissionFacade.canChangeInstances(selectedItems)).thenReturn(false);
		when(actionContext.getData()).thenReturn(selectedItems);

		assertThat(getActionInstance().canPerform(actionContext)).isFalse();

	}

	@Test
	public void testCannotPerformWhenListIsEmpty()
	{
		final ActionContext<Collection> actionContext = mock(ActionContext.class);
		final List<Object> selectedItems = Collections.emptyList();
		when(objectFacade.reload(selectedItems)).thenReturn(createReloadResult(selectedItems));
		when(permissionFacade.canChangeInstances(selectedItems)).thenReturn(true);
		when(actionContext.getData()).thenReturn(selectedItems);

		assertThat(getActionInstance().canPerform(actionContext)).isFalse();
	}

	@Test
	public void testOnPerformSocketWithContextIsSent()
	{
		final ActionContext<Collection> actionContext = mock(ActionContext.class);
		final List<Object> selectedItems = Lists.newArrayList(1, 2, 3);
		when(objectFacade.reload(selectedItems)).thenReturn(createReloadResult(selectedItems));
		when(permissionFacade.canChangeInstances(selectedItems)).thenReturn(true);
		when(actionContext.getData()).thenReturn(selectedItems);

		when(backofficeTypeUtils.findClosestSuperType(selectedItems)).thenReturn("superType");

		getActionInstance().perform(actionContext);
		verify(getActionInstance()).sendOutput(eq(BulkEditAction.SOCKET_OUT_BULK_EDIT_CTX), argThat(new ArgumentMatcher<Map>()
		{
			@Override
			public boolean matches(final Object o)
			{
				final Map<String, Object> ctx = (Map<String, Object>) o;
				return "superType".equals(ctx.get(BulkEditAction.CTX_TYPE_CODE))
						&& CollectionUtils.isEqualCollection(selectedItems, (Collection<?>) ctx.get(BulkEditAction.CTX_ITEMS_TO_EDIT));
			}
		}));

	}

	@Test
	public void testNeedsConfirmation()
	{
		final ActionContext<Collection> actionContext = mock(ActionContext.class);
		final List<Object> selectedItems = Lists.newArrayList(1, 2, 3);
		when(objectFacade.reload(selectedItems)).thenReturn(createReloadResult(selectedItems));
		when(actionContext.getData()).thenReturn(selectedItems);

		when(actionContext.getParameter(BulkEditAction.PARAM_CONFIRMATION_THRESHOLD)).thenReturn("2");
		assertThat(getActionInstance().needsConfirmation(actionContext)).isTrue();

		when(actionContext.getParameter(BulkEditAction.PARAM_CONFIRMATION_THRESHOLD)).thenReturn("3");
		assertThat(getActionInstance().needsConfirmation(actionContext)).isFalse();
	}


}
