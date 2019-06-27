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
package com.hybris.backoffice.widgets.actions.excel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.google.common.collect.Sets;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.util.type.BackofficeTypeUtils;


public class ExcelExportActionTest extends AbstractActionUnitTest<ExcelExportAction>
{
	@InjectMocks
	@Spy
	private ExcelExportAction excelExportAction;
	@Mock
	private TypeService typeService;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private BackofficeTypeUtils backofficeTypeUtils;
	private WidgetModel model;


	@Override
	public ExcelExportAction getActionInstance()
	{
		return excelExportAction;
	}

	@Before
	public void setUp()
	{
		when(typeService.isAssignableFrom(ItemModel._TYPECODE, ProductModel._TYPECODE)).thenReturn(true);
		CockpitTestUtil.mockZkEnvironment();
		model = CockpitTestUtil.mockWidgetModel();
		doReturn(100).when(excelExportAction).getExportMaxRows(any());
		doNothing().when(excelExportAction).showMaxRowsExceeded(any(ActionContext.class), anyInt());
	}

	@Test
	public void canImportProduct()
	{
		when(permissionFacade.canReadType(ProductModel._TYPECODE)).thenReturn(true);

		final ActionContext<String> actionContext = new ActionContext<>(ProductModel._TYPECODE, null, null, null);
		final WidgetModel model = CockpitTestUtil.mockWidgetModel();
		actionContext.setParameter(ActionContext.PARENT_WIDGET_MODEL, model);
		model.setValue(ExcelExportAction.MODEL_SELECTED_OBJECTS, Sets.newHashSet(new ProductModel()));
		assertThat(getActionInstance().canPerform(actionContext)).isTrue();
	}

	@Test
	public void cannotImportProduct()
	{
		when(permissionFacade.canReadType(ProductModel._TYPECODE)).thenReturn(false);

		final ActionContext<String> actionContext = new ActionContext<>(ProductModel._TYPECODE, null, null, null);
		final WidgetModel model = CockpitTestUtil.mockWidgetModel();
		actionContext.setParameter(ActionContext.PARENT_WIDGET_MODEL, model);
		model.setValue(ExcelExportAction.MODEL_SELECTED_OBJECTS, Sets.newHashSet(new ProductModel()));

		assertThat(getActionInstance().canPerform(actionContext)).isFalse();
	}

	@Test
	public void cannotImportPojo()
	{
		final ActionContext<String> actionContext = new ActionContext<>("SomePojo", null, null, null);
		actionContext.setParameter(ActionContext.PARENT_WIDGET_MODEL, model);

		assertThat(getActionInstance().canPerform(actionContext)).isFalse();
	}

	@Test
	public void testSelectedItemsPassedToExport()
	{
		// given
		final ActionContext<String> ctx = new ActionContext<>(ProductModel._TYPECODE, null, getParameters(5, 100), null);

		// when
		final String actionResult = excelExportAction.perform(ctx).getResultCode();

		// then
		assertThat(actionResult).isEqualTo(ActionResult.SUCCESS);
		verify(componentWidgetAdapter).sendOutput(eq(ExcelExportAction.SOCKET_OUT_ITEMS_TO_EXPORT),
				argThat(new ArgumentMatcher<Object>()
				{
					@Override
					public boolean matches(final Object o)
					{
						return ((Pageable) o).getTotalCount() == 5;
					}
				}), same(excelExportAction));
	}

	@Test
	public void testAllItemsPassedToExport()
	{
		// given
		final ActionContext<String> ctx = new ActionContext<>(ProductModel._TYPECODE, null, getParameters(0, 100), null);

		// when
		final String actionResult = excelExportAction.perform(ctx).getResultCode();

		// then
		assertThat(actionResult).isEqualTo(ActionResult.SUCCESS);
		verify(componentWidgetAdapter).sendOutput(eq(ExcelExportAction.SOCKET_OUT_ITEMS_TO_EXPORT),
				argThat(new ArgumentMatcher<Object>()
				{
					@Override
					public boolean matches(final Object o)
					{
						return ((Pageable) o).getTotalCount() == 100;
					}
				}), same(excelExportAction));
	}

	@Test
	public void testNeedsConfirmationAppearsWhenThresholdValueIsExceeded()
	{
		// given
		final ActionContext<String> actionContext = new ActionContext<>(ProductModel._TYPECODE, null, null, null);
		final WidgetModel model = CockpitTestUtil.mockWidgetModel();
		actionContext.setParameter(ActionContext.PARENT_WIDGET_MODEL, model);
		model.setValue(ExcelExportAction.MODEL_SELECTED_OBJECTS, Sets.newHashSet(new ProductModel()));
		assertThat(getActionInstance().needsConfirmation(actionContext)).isFalse();

		// when
		actionContext.setParameter(ExcelExportAction.PARAM_CONFIRMATION_THRESHOLD, 0);
		final boolean needsConfirmation = getActionInstance().needsConfirmation(actionContext);

		// then
		assertThat(needsConfirmation).isTrue();
	}

	@Test
	public void testDoesNotNeedConfirmationIfMaxExportRowsExceeded()
	{
		// given
		final ActionContext<String> actionContext = new ActionContext<>(ProductModel._TYPECODE, null, null, null);
		final WidgetModel model = CockpitTestUtil.mockWidgetModel();
		actionContext.setParameter(ActionContext.PARENT_WIDGET_MODEL, model);
		final HashSet<ProductModel> items = Sets.newHashSet(new ProductModel(), new ProductModel());
		model.setValue(ExcelExportAction.MODEL_SELECTED_OBJECTS, items);
		actionContext.setParameter(ExcelExportAction.PARAM_CONFIRMATION_THRESHOLD, 1);
		//when
		doReturn(items.size()).when(excelExportAction).getExportMaxRows(any());
		boolean needsConfirmation = getActionInstance().needsConfirmation(actionContext);
		//then
		assertThat(needsConfirmation).isTrue();

		// when
		doReturn(items.size() - 1).when(excelExportAction).getExportMaxRows(any());
		needsConfirmation = getActionInstance().needsConfirmation(actionContext);
		// then
		assertThat(needsConfirmation).isFalse();
	}

	@Test
	public void testNotPerformedWhenMaxExportExceeded()
	{
		// given
		final ActionContext<String> actionContext = new ActionContext<>(ProductModel._TYPECODE, null, null, null);
		final WidgetModel model = CockpitTestUtil.mockWidgetModel();
		actionContext.setParameter(ActionContext.PARENT_WIDGET_MODEL, model);
		final HashSet<ProductModel> items = Sets.newHashSet(new ProductModel(), new ProductModel());
		model.setValue(ExcelExportAction.MODEL_SELECTED_OBJECTS, items);

		//when
		doReturn(items.size()).when(excelExportAction).getExportMaxRows(any());
		//then
		final ActionResult<Pageable<? extends ItemModel>> perform = getActionInstance().perform(actionContext);
		assertThat(perform.getResultCode()).isEqualToIgnoringCase(ActionResult.SUCCESS);
		// when
		doReturn(items.size() - 1).when(excelExportAction).getExportMaxRows(any());
		final ActionResult<Pageable<? extends ItemModel>> result = getActionInstance().perform(actionContext);
		// then
		assertThat(result.getResultCode()).isEqualToIgnoringCase(ActionResult.ERROR);
		verify(excelExportAction).showMaxRowsExceeded(same(actionContext), eq(items.size()));
	}

	private Map<String, Object> getParameters(final int selectedSize, final int pageableSize)
	{
		final Map<String, Object> parameters = new HashMap<>();
		final WidgetModel widgetModel = CockpitTestUtil.mockWidgetModel();
		final Set<ItemModel> selectedObjects = new HashSet<>();
		if (selectedSize > 0)
		{
			for (int i = 0; i < selectedSize; i++)
			{
				selectedObjects.add(mock(ItemModel.class));
			}
		}
		final Pageable pageable = mock(Pageable.class);
		when(pageable.getTotalCount()).thenReturn(pageableSize);
		widgetModel.put(ExcelExportAction.MODEL_PAGEABLE, pageable);
		widgetModel.put(ExcelExportAction.MODEL_SELECTED_OBJECTS, selectedObjects);
		parameters.put(ActionContext.PARENT_WIDGET_MODEL, widgetModel);
		parameters.put(ExcelExportAction.PARAM_CONFIRMATION_THRESHOLD, 0);
		return parameters;
	}

}
