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
package com.hybris.backoffice.widgets.actions.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zhtml.Div;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;

import com.google.common.collect.Lists;
import com.hybris.backoffice.workflow.WorkflowsTypeFacade;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionListener;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;



@RunWith(MockitoJUnitRunner.class)
public class AddWorkflowAttachmentActionRendererTest
{
	@Mock
	private WorkflowsTypeFacade workflowsTypeFacade;
	@Mock
	private LabelService labelService;
	@InjectMocks
	private AddWorkflowAttachmentActionRenderer renderer;
	@Mock
	private ActionListener actionListener;
	@Mock
	private CockpitAction cockpitAction;
	@Mock
	private ActionContext actionContext;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		final ComposedTypeModel productType = mockComposedType(ProductModel._TYPECODE);
		final ComposedTypeModel categoryType = mockComposedType(CategoryModel._TYPECODE);
		when(workflowsTypeFacade.getSupportedAttachmentTypes()).thenReturn(Lists.newArrayList(productType, categoryType));

	}

	protected ComposedTypeModel mockComposedType(final String typecode)
	{
		final ComposedTypeModel ct = mock(ComposedTypeModel.class);
		when(ct.getCode()).thenReturn(typecode);
		when(labelService.getShortObjectLabel(ct)).thenReturn(typecode);
		return ct;
	}

	@Test
	public void testTypeParamSetOnContext() throws Exception
	{
		final EventListener eventListener = renderer.createEventListener(cockpitAction, actionContext, actionListener);

		final Div parent = new Div();

		eventListener.onEvent(new Event(Events.ON_CLICK, parent));

		final Component menuPopup = parent.query("." + AddWorkflowAttachmentActionRenderer.SCLASS_YA_ATTACHMENT_TYPE_SLECTOR);
		assertThat(menuPopup).isNotNull();
		assertThat(menuPopup.getChildren()).hasSize(2);
		final Optional<Component> productItemMenu = menuPopup.getChildren().stream()
				.filter(item -> ((Menuitem) item).getLabel().equals(ProductModel._TYPECODE)).findAny();
		assertThat(productItemMenu.isPresent()).isTrue();
		productItemMenu.ifPresent(component -> CockpitTestUtil.simulateEvent(component, Events.ON_CLICK, null));
		verify(actionContext).setParameter(AddWorkflowAttachmentAction.PARAM_ATTACHMENT_TYPE, ProductModel._TYPECODE);
	}
}
