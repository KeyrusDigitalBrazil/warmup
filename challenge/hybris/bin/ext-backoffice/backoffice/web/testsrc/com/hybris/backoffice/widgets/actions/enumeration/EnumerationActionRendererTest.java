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
package com.hybris.backoffice.widgets.actions.enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Div;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Radio;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionListener;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.util.type.BackofficeTypeUtils;


@RunWith(MockitoJUnitRunner.class)
public class EnumerationActionRendererTest
{

	@Mock
	private LabelService labelService;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private BackofficeTypeUtils typeUtils;
	@Spy
	@InjectMocks
	private EnumerationActionRenderer renderer;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
	}

	@Test
	public void shouldClickingOnSpecificRadioSetAssociativeValueInContext()
	{
		// given
		final Radio radio1 = new Radio("radio1");
		final Radio radio2 = new Radio("radio2");
		final HybrisEnumValue hybrisEnumValue1 = ArticleApprovalStatus.APPROVED;
		final HybrisEnumValue hybrisEnumValue2 = ArticleApprovalStatus.UNAPPROVED;

		final ActionContext<Collection<Object>> context = new ActionContext<>(null, null, Maps.newHashMap(), null);

		// when
		renderer.attachListenerToMenuitem(//
				ImmutableMap.of(//
						radio1, hybrisEnumValue1, //
						radio2, hybrisEnumValue2), //
				context);

		CockpitTestUtil.simulateEvent(radio2, Events.ON_CHECK, null);

		// then
		assertThat(context.getParameter(EnumerationAction.ENUMERATION_KEY)).isEqualTo(hybrisEnumValue2);
	}


	@Test
	public void shouldRadioButtonsBeOrderedByLabels()
	{
		// given
		final List<HybrisEnumValue> hybrisEnumValues = Lists.newArrayList(ArticleApprovalStatus.APPROVED,
				ArticleApprovalStatus.UNAPPROVED, ArticleApprovalStatus.CHECK);
		hybrisEnumValues.forEach(
				hybrisEnumValue -> given(labelService.getObjectLabel(hybrisEnumValue)).willReturn(hybrisEnumValue.getCode()));
		doReturn(hybrisEnumValues).when(renderer).prepareEnums(any(), any());

		// when
		final HtmlBasedComponent htmlBasedComponent = renderer.createEnumList(hybrisEnumValues, mock(ActionContext.class),
				event -> { //
					// DO NOTHING
				});

		// then
		assertThat(htmlBasedComponent.getFirstChild().getChildren())//
				.filteredOn(Radio.class::isInstance)//
				.extracting(e -> ((Radio) e).getLabel())//
				.containsExactly(//
						ArticleApprovalStatus.APPROVED.getCode(), //
						ArticleApprovalStatus.CHECK.getCode(), //
						ArticleApprovalStatus.UNAPPROVED.getCode()//
		);
	}

	@Test
	public void testShouldRenderAsMenu() throws TypeNotFoundException
	{
		// given
		final CockpitAction action = mock(CockpitAction.class);
		final ActionListener actionListener = mock(ActionListener.class);

		final List<HybrisEnumValue> availableEnums = mockApprovalStatusInProductDataType();
		final ArrayList<Object> data = Lists.newArrayList(1, 2, 3);
		when(typeUtils.findClosestSuperType(data)).thenReturn(ProductModel._TYPECODE);

		final ActionContext actionContext = mockMenuModeActionContextWithData(data);
		when(action.canPerform(actionContext)).thenReturn(true);

		//when
		final Div parent = new Div();
		renderer.render(parent, action, actionContext, false, actionListener);

		//then
		final List<Component> menuItems = Selectors.find(parent,
				"." + EnumerationActionRenderer.SCLASS_YW_ENUMERATION_ACTION_MENU_POPUP_MENUITEM);
		assertThat(menuItems).hasSize(availableEnums.size());
		assertThat(menuItems.stream().map(menuItem -> ((Menuitem) menuItem).getLabel())).containsOnly(
				ArticleApprovalStatus.APPROVED.getCode(), ArticleApprovalStatus.UNAPPROVED.getCode(),
				ArticleApprovalStatus.CHECK.getCode());
	}

	@Test
	public void testShouldRenderEmptyMenuWhenCannotPerform() throws TypeNotFoundException
	{
		// given
		final CockpitAction action = mock(CockpitAction.class);
		final ActionListener actionListener = mock(ActionListener.class);

		mockApprovalStatusInProductDataType();
		final ArrayList<Object> data = Lists.newArrayList(1, 2, 3);
		when(typeUtils.findClosestSuperType(data)).thenReturn(ProductModel._TYPECODE);

		final ActionContext actionContext = mockMenuModeActionContextWithData(data);
		when(action.canPerform(actionContext)).thenReturn(false);

		//when
		final Div parent = new Div();
		renderer.render(parent, action, actionContext, false, actionListener);

		//then
		final List<Component> menuItems = Selectors.find(parent,
				"." + EnumerationActionRenderer.SCLASS_YW_ENUMERATION_ACTION_MENU_POPUP_MENUITEM);
		assertThat(menuItems).isEmpty();
	}

	protected List<HybrisEnumValue> mockApprovalStatusInProductDataType() throws TypeNotFoundException
	{
		final List<HybrisEnumValue> availableEnums = Lists.newArrayList(ArticleApprovalStatus.APPROVED,
				ArticleApprovalStatus.UNAPPROVED, ArticleApprovalStatus.CHECK);
		doAnswer(inv -> ((HybrisEnumValue) inv.getArguments()[0]).getCode()).when(labelService)
				.getObjectLabel(any(HybrisEnumValue.class));

		final DataType approvalDataType = mock(DataType.class);
		when(approvalDataType.isEnum()).thenReturn(true);
		when(approvalDataType.getCode()).thenReturn(ArticleApprovalStatus._TYPECODE);
		when(enumerationService.getEnumerationValues(ArticleApprovalStatus._TYPECODE)).thenReturn(availableEnums);
		final DataAttribute approvalAttribute = mock(DataAttribute.class);
		when(approvalAttribute.getDefinedType()).thenReturn(approvalDataType);

		final DataType productDataType = mock(DataType.class);
		when(productDataType.getAttribute(ProductModel.APPROVALSTATUS)).thenReturn(approvalAttribute);
		when(typeFacade.load(ProductModel._TYPECODE)).thenReturn(productDataType);

		return availableEnums;
	}

	protected ActionContext mockMenuModeActionContextWithData(final List<Object> data)
	{
		final ActionContext actionContext = mock(ActionContext.class);
		when(actionContext.getData()).thenReturn(data);
		when(actionContext.getParameter(ActionContext.VIEW_MODE_PARAM)).thenReturn(EnumerationActionRenderer.VIEW_MODE_MENU);
		when(actionContext.getParameter(EnumerationAction.PARAMETER_QUALIFIER)).thenReturn(ProductModel.APPROVALSTATUS);
		return actionContext;
	}

}
