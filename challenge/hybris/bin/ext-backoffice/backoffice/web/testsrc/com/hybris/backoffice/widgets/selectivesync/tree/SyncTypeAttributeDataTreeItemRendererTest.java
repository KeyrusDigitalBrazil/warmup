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
package com.hybris.backoffice.widgets.selectivesync.tree;

import static com.hybris.cockpitng.testing.util.CockpitTestUtil.findChild;
import static com.hybris.cockpitng.testing.util.CockpitTestUtil.simulateEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.SyncAttributeDescriptorConfigModel;

import java.util.ArrayList;
import java.util.Collection;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import com.hybris.backoffice.widgets.selectivesync.detailsview.DetailsView;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.BeanLookup;
import com.hybris.cockpitng.testing.util.BeanLookupFactory;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SyncTypeAttributeDataTreeItemRendererTest
{
	private static final String LABEL = "label";
	private static final String LABEL_NO_READ_ACCESS = ""; //no read access label is created by static method Label.getLabel(), i don't won't to add another mock library only to mock static method.

	@Mock
	private SyncTypeAttributeDataTreeNode mockedTypeNode;
	@Mock
	private SyncTypeAttributeDataTreeNode mockedAttributeNode;
	@Mock
	private SelectiveSyncModelChangeListener mockedSelectiveSyncModelChangeListener;
	@Mock
	private SyncAttributeTreeModel mockedDataModel;
	@Mock
	private SyncAttributeDescriptorConfigModel mockedAttributeData;
	@Mock
	private DetailsView mockedDetailsView;
	@Mock
	private Checkbox mockedCheckbox;
	@Mock
	private PermissionFacade permissionFacade;
	private SyncTypeAttributeDataTreeItemRenderer.CreationContext creationContext;
	@Mock
	private LabelService labelService;

	@Before
	public void before()
	{
		CockpitTestUtil.mockZkEnvironment();

		final BeanLookup beanLookup = BeanLookupFactory.createBeanLookup()
				.registerBean("labelService", labelService).getLookup();
		CockpitTestUtil.mockBeanLookup(beanLookup);
		when(labelService.getObjectLabel(any())).thenAnswer(answer -> {
			if (answer.getArguments().length > 0)
			{
				final Object argument = answer.getArguments()[0];
				if (argument instanceof ComposedTypeModel)
				{
					return ((ComposedTypeModel) argument).getName();
				}
				else if (argument instanceof SyncAttributeDescriptorConfigModel)
				{
					return ((SyncAttributeDescriptorConfigModel) argument).getAttributeDescriptor().getName();
				}
			}
			return "label";
		});

		when(permissionFacade.canReadInstance(any())).thenReturn(true);
		when(permissionFacade.canChangeProperty(any(), any())).thenReturn(true);

		creationContext = new SyncTypeAttributeDataTreeItemRenderer.CreationContext();
		creationContext.setPermissionFacade(permissionFacade);
		creationContext.setDataModel(mockedDataModel);
		creationContext.setEditable(true);
		creationContext.setSelectiveSyncModelChangeListener(mockedSelectiveSyncModelChangeListener);
		creationContext.setDetailsView(mockedDetailsView);
	}

	@Test
	public void shouldRenderTypeNodeInTree() throws Exception
	{
		// given
		final Treeitem treeitem = new Treeitem();

		given(mockedTypeNode.isType()).willReturn(true);
		given(mockedTypeNode.getCheckbox()).willReturn(mockedCheckbox);
		given(mockedTypeNode.getText()).willReturn(LABEL);

		final SyncTypeAttributeDataTreeItemRenderer syncTypeAttributeDataTreeItemRenderer = prepareStubbedRenderer(true);

		// when
		syncTypeAttributeDataTreeItemRenderer.render(treeitem, mockedTypeNode, 0);

		// then
		assertThat(findChild(treeitem, Treerow.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Treecell.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Label.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Label.class).get().getValue()).isEqualTo(LABEL);
	}

	@Test
	public void shouldHandleTypeNodeSelection() throws Exception
	{
		// given
		final Treeitem treeitem = new Treeitem();
		final Checkbox checkbox = prepareUncheckedCheckbox();
		final Collection<SyncAttributeDescriptorConfigModel> data = new ArrayList<>();

		given(mockedTypeNode.isType()).willReturn(true);
		given(mockedTypeNode.getCheckbox()).willReturn(checkbox);

		given(mockedDataModel.getOriginalData()).willReturn(data);

		final SyncTypeAttributeDataTreeItemRenderer syncTypeAttributeDataTreeItemRenderer = new SyncTypeAttributeDataTreeItemRenderer(
                creationContext);

		syncTypeAttributeDataTreeItemRenderer.render(treeitem, mockedTypeNode, 0);

		// when, type node is checked
		simulateEvent(checkbox, new CheckEvent(Events.ON_CHECK, checkbox, true));

		// then
		verify(mockedDataModel).nodeOnCheck(any(), eq(true));
		verify(mockedDetailsView).clearView();
		verify(mockedSelectiveSyncModelChangeListener).onValueChanged(syncTypeAttributeDataTreeItemRenderer, data);
	}

	@Test
	public void shouldRenderAttributeNodeInTree() throws Exception
	{
		// given
		final Treeitem treeitem = new Treeitem();

		final Checkbox checkbox = prepareUncheckedCheckbox();

		given(mockedAttributeData.getIncludedInSync()).willReturn(true);

		given(mockedAttributeNode.isAttribute()).willReturn(true);
		given(mockedAttributeNode.getCheckbox()).willReturn(checkbox);
		given(mockedAttributeNode.getText()).willReturn(LABEL);
		given(mockedAttributeNode.getData()).willReturn(mockedAttributeData);

		final SyncTypeAttributeDataTreeItemRenderer syncTypeAttributeDataTreeItemRenderer = prepareStubbedRenderer(true);

		// when
		syncTypeAttributeDataTreeItemRenderer.render(treeitem, mockedAttributeNode, 0);

		// then
		assertThat(findChild(treeitem, Treerow.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Treecell.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Label.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Label.class).get().getValue()).isEqualTo(LABEL);
		assertThat(checkbox.isChecked()).isTrue(); // attribute is included in sync
	}

	@Test
	public void shouldHandleAttributeNodeSelection() throws Exception
	{
		// given
		final Treeitem treeitem = new Treeitem();
		final Checkbox checkbox = prepareUncheckedCheckbox();

		given(mockedAttributeData.getIncludedInSync()).willReturn(true);

		final Collection<SyncAttributeDescriptorConfigModel> data = new ArrayList<>();

		given(mockedAttributeNode.isAttribute()).willReturn(true);
		given(mockedAttributeNode.getCheckbox()).willReturn(checkbox);
		given(mockedAttributeNode.getData()).willReturn(mockedAttributeData);

		given(mockedDataModel.getOriginalData()).willReturn(data);

		final SyncTypeAttributeDataTreeItemRenderer syncTypeAttributeDataTreeItemRenderer = new SyncTypeAttributeDataTreeItemRenderer(
                creationContext);

		syncTypeAttributeDataTreeItemRenderer.render(treeitem, mockedAttributeNode, 0);

		// when attribute is selected
		simulateEvent(checkbox, new CheckEvent(Events.ON_CHECK, checkbox, true));

		// then
		verify(mockedDataModel).nodeOnCheck(any(), eq(true)); // attribute is included in sync
		verify(mockedDetailsView).display(eq(mockedAttributeData), any());
		verify(mockedSelectiveSyncModelChangeListener).onValueChanged(syncTypeAttributeDataTreeItemRenderer, data);
	}

	private Checkbox prepareUncheckedCheckbox()
	{
		final Checkbox checkbox = new Checkbox();
		checkbox.setChecked(false);
		return checkbox;
	}

	@Test
	public void shouldRenderEditableTypeNode() throws Exception
	{
		// given
		final Treeitem treeitem = new Treeitem();

		given(mockedTypeNode.isType()).willReturn(true);
		given(mockedTypeNode.getCheckbox()).willReturn(mockedCheckbox);

		final SyncTypeAttributeDataTreeItemRenderer syncTypeAttributeDataTreeItemRenderer = prepareStubbedRenderer(true);

		// when
		syncTypeAttributeDataTreeItemRenderer.render(treeitem, mockedTypeNode, 0);

		// then
		verify(mockedCheckbox).setDisabled(false);
	}

	@Test
	public void shouldRenderNonEditableTypeNode() throws Exception
	{
		// given
		final Treeitem treeitem = new Treeitem();

		given(mockedTypeNode.isType()).willReturn(true);
		given(mockedTypeNode.getCheckbox()).willReturn(mockedCheckbox);

		final SyncTypeAttributeDataTreeItemRenderer syncTypeAttributeDataTreeItemRenderer = prepareStubbedRenderer(false);

		// when
		syncTypeAttributeDataTreeItemRenderer.render(treeitem, mockedTypeNode, 0);

		// then
		verify(mockedCheckbox).setDisabled(true);
	}

	@Test
	public void shouldRenderNonEditableTypeNodeWithoutPermissionToChangeProperty() throws Exception
	{
		// given
		final Treeitem treeitem = new Treeitem();
		when(permissionFacade.canChangeProperty(any(), any())).thenReturn(false);

		given(mockedTypeNode.isType()).willReturn(true);
		given(mockedTypeNode.getCheckbox()).willReturn(mockedCheckbox);

		final SyncTypeAttributeDataTreeItemRenderer syncTypeAttributeDataTreeItemRenderer = prepareStubbedRenderer(true);

		// when
		syncTypeAttributeDataTreeItemRenderer.render(treeitem, mockedTypeNode, 0);

		// then
		verify(mockedCheckbox).setDisabled(true);
	}


	@Test
	public void shouldRenderNoReadAccessTypeNodeInTree() throws Exception
	{
		// given
		final Treeitem treeitem = new Treeitem();

		given(mockedTypeNode.isType()).willReturn(true);
		given(mockedTypeNode.getCheckbox()).willReturn(mockedCheckbox);
		given(mockedTypeNode.getText()).willReturn(LABEL);

		final SyncTypeAttributeDataTreeItemRenderer syncTypeAttributeDataTreeItemRenderer = prepareStubbedRenderer(true);

		when(permissionFacade.canReadInstance(any())).thenReturn(false);

		// when
		syncTypeAttributeDataTreeItemRenderer.render(treeitem, mockedTypeNode, 0);

		// then
		assertThat(findChild(treeitem, Treerow.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Treecell.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Label.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Label.class).get().getValue()).isEqualTo(LABEL_NO_READ_ACCESS);
	}

	@Test
	public void shouldRenderNoReadAccessAttributeNodeInTree() throws Exception
	{
		// given
		final Treeitem treeitem = new Treeitem();

		final Checkbox checkbox = prepareUncheckedCheckbox();

		given(mockedAttributeData.getIncludedInSync()).willReturn(true);

		given(mockedAttributeNode.isAttribute()).willReturn(true);
		given(mockedAttributeNode.getCheckbox()).willReturn(checkbox);
		given(mockedAttributeNode.getText()).willReturn(LABEL);
		given(mockedAttributeNode.getData()).willReturn(mockedAttributeData);

		final SyncTypeAttributeDataTreeItemRenderer syncTypeAttributeDataTreeItemRenderer = prepareStubbedRenderer(true);

		when(permissionFacade.canReadInstance(any())).thenReturn(false);

		// when
		syncTypeAttributeDataTreeItemRenderer.render(treeitem, mockedAttributeNode, 0);

		// then
		assertThat(findChild(treeitem, Treerow.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Treecell.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Label.class).isPresent()).isTrue();
		assertThat(findChild(treeitem, Label.class).get().getValue()).isEqualTo(LABEL_NO_READ_ACCESS);
	}

	private SyncTypeAttributeDataTreeItemRenderer prepareStubbedRenderer(final boolean editable)
	{
		creationContext = new SyncTypeAttributeDataTreeItemRenderer.CreationContext();
		creationContext.setPermissionFacade(permissionFacade);
		creationContext.setDataModel(mock(SyncAttributeTreeModel.class));
		creationContext.setEditable(editable);
		creationContext.setSelectiveSyncModelChangeListener(mock(SelectiveSyncModelChangeListener.class));
		creationContext.setDetailsView(mock(DetailsView.class));
		return new SyncTypeAttributeDataTreeItemRenderer(creationContext);
	}

	@Test
	public void shouldOpenTypeNodeAfterClick() throws Exception
	{
		// given
		final Treeitem treeitem = new Treeitem();
		treeitem.setOpen(false);
		final Checkbox checkbox = prepareUncheckedCheckbox();

		given(mockedTypeNode.isType()).willReturn(true);
		given(mockedTypeNode.getCheckbox()).willReturn(checkbox);
		final SyncTypeAttributeDataTreeItemRenderer syncTypeAttributeDataTreeItemRenderer = new SyncTypeAttributeDataTreeItemRenderer(
				creationContext);

		syncTypeAttributeDataTreeItemRenderer.render(treeitem, mockedTypeNode, 0);

		final Treecell cell = findChild(treeitem, Treecell.class).orElseThrow(() -> new Exception("can't find tree cell"));

		// when, type node is checked
		simulateEvent(cell, new MouseEvent(Events.ON_CLICK, cell));

		// then
		assertThat(treeitem.isOpen()).isTrue();
	}

	@Test
	public void shouldNotOpenAttributeNodeAfterClick() throws Exception
	{
		// given
		final Treeitem treeitem = new Treeitem();
		treeitem.setOpen(false);
		given(mockedTypeNode.isType()).willReturn(false);

		final SyncTypeAttributeDataTreeItemRenderer syncTypeAttributeDataTreeItemRenderer = new SyncTypeAttributeDataTreeItemRenderer(
				creationContext);

		syncTypeAttributeDataTreeItemRenderer.render(treeitem, mockedTypeNode, 0);

		final Treecell cell = findChild(treeitem, Treecell.class).orElseThrow(() -> new Exception("can't find tree cell"));

		// when, attribute node is click
		simulateEvent(cell, new MouseEvent(Events.ON_CLICK, cell));

		// then
		assertThat(treeitem.isOpen()).isFalse();
	}
}
