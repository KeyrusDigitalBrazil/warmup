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
package com.hybris.backoffice.widgets.selectivesync.detailsview;

import static com.hybris.cockpitng.testing.util.CockpitTestUtil.findAllChildren;
import static com.hybris.cockpitng.testing.util.CockpitTestUtil.simulateEvent;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.SyncAttributeDescriptorConfigModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.impl.LabelElement;

import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class DetailsViewTest
{
	private DetailsView detailsView;

	@Mock
	private DetailsViewAttributeValueChangeListener mockedDetailsViewAttributeValueChangeListener;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@Before
	public void before()
	{
		CockpitTestUtil.mockZkEnvironment();
		when(permissionFacade.canReadInstanceProperty(any(), any())).thenReturn(true);
		when(permissionFacade.canChangeInstanceProperty(any(), any())).thenReturn(true);
		when(widgetInstanceManager.getLabel("syncAttribute.detailView.synchronize")).thenReturn("synchronize");
		when(widgetInstanceManager.getLabel("syncAttribute.detailView.copyByValue")).thenReturn("copyByValue");
		when(widgetInstanceManager.getLabel("syncAttribute.detailView.untranslatableValue")).thenReturn("untranslatableValue");
		when(widgetInstanceManager.getLabel("syncAttribute.detailView.partiallyTranslatable")).thenReturn("partiallyTranslatable");
		final DetailsView.CreationContext creationContext = new DetailsView.CreationContext();
		creationContext.setWidgetInstanceManager(widgetInstanceManager);
		creationContext.setEditable(true);
		creationContext.setPermissionFacade(permissionFacade);
		detailsView = new DetailsView(creationContext);
	}

	@Test
	public void shouldDisplayDetailsView()
	{
		final SyncAttributeDescriptorConfigModel syncAttributeDescriptorConfigModel = new SyncAttributeDescriptorConfigModel();
		final int expectedNumberOfCheckboxes = 4;

		// when
		detailsView.display(syncAttributeDescriptorConfigModel, null);

		// then
		assertThat(findAllChildren(detailsView, Checkbox.class).count()).isEqualTo(expectedNumberOfCheckboxes);
		assertThat(findAllChildren(detailsView, Checkbox.class).map(LabelElement::getLabel).collect(toList()))
				.containsOnly("synchronize", "copyByValue", "untranslatableValue", "partiallyTranslatable");
	}

	@Test
	public void shouldHandleSynchronizeAttributeSelection()
	{
		// given
		final SyncAttributeDescriptorConfigModel syncAttributeDescriptorConfigModel = new SyncAttributeDescriptorConfigModel();
		syncAttributeDescriptorConfigModel.setIncludedInSync(true);

		detailsView.display(syncAttributeDescriptorConfigModel, mockedDetailsViewAttributeValueChangeListener);
		final Checkbox syncAttributeCheckbox = getAttributeByYtestid(detailsView, "details_view_sync_attribute_checkbox");

		// when
		simulateEvent(syncAttributeCheckbox, Events.ON_CHECK, null);

		// then
		assertThat(syncAttributeCheckbox.isChecked()).isTrue();
		verify(mockedDetailsViewAttributeValueChangeListener).attributeChanged(syncAttributeDescriptorConfigModel,
				SyncAttributeDescriptorConfigModel.INCLUDEDINSYNC, true);
	}

	@Test
	public void shouldHandleCopyByValueAttribute()
	{
		// given
		final SyncAttributeDescriptorConfigModel syncAttributeDescriptorConfigModel = new SyncAttributeDescriptorConfigModel();
		syncAttributeDescriptorConfigModel.setCopyByValue(true);

		detailsView.display(syncAttributeDescriptorConfigModel, mockedDetailsViewAttributeValueChangeListener);
		final Checkbox copyByValueAttributeCheckbox = getAttributeByYtestid(detailsView,
				"details_view_copy_by_value_attribute_checkbox");

		// when
		simulateEvent(copyByValueAttributeCheckbox, Events.ON_CHECK, null);

		// then
		assertThat(copyByValueAttributeCheckbox.isChecked()).isTrue();
		verify(mockedDetailsViewAttributeValueChangeListener).attributeChanged(syncAttributeDescriptorConfigModel,
				SyncAttributeDescriptorConfigModel.COPYBYVALUE, true);
	}

	@Test
	public void shouldHandleUntranslatableAttribute()
	{
		// given
		final SyncAttributeDescriptorConfigModel syncAttributeDescriptorConfigModel = new SyncAttributeDescriptorConfigModel();
		syncAttributeDescriptorConfigModel.setUntranslatable(true);

		detailsView.display(syncAttributeDescriptorConfigModel, mockedDetailsViewAttributeValueChangeListener);
		final Checkbox untranslatableAttributeCheckbox = getAttributeByYtestid(detailsView,
				"details_view_untranslatable_attribute_checkbox");

		// when
		simulateEvent(untranslatableAttributeCheckbox, Events.ON_CHECK, null);

		// then
		assertThat(untranslatableAttributeCheckbox.isChecked()).isTrue();
		verify(mockedDetailsViewAttributeValueChangeListener).attributeChanged(syncAttributeDescriptorConfigModel,
				SyncAttributeDescriptorConfigModel.UNTRANSLATABLE, true);
	}

	@Test
	public void shouldHandlePartiallyTranslatableAttribute()
	{
		// given
		final SyncAttributeDescriptorConfigModel syncAttributeDescriptorConfigModel = new SyncAttributeDescriptorConfigModel();
		syncAttributeDescriptorConfigModel.setPartiallyTranslatable(true);

		detailsView.display(syncAttributeDescriptorConfigModel, mockedDetailsViewAttributeValueChangeListener);
		final Checkbox partiallyTranslatableAttributeCheckbox = getAttributeByYtestid(detailsView,
				"details_view_partially_translatable_attribute_checkbox");

		// when
		simulateEvent(partiallyTranslatableAttributeCheckbox, Events.ON_CHECK, null);

		// then
		assertThat(partiallyTranslatableAttributeCheckbox.isChecked()).isTrue();
		verify(mockedDetailsViewAttributeValueChangeListener).attributeChanged(syncAttributeDescriptorConfigModel,
				SyncAttributeDescriptorConfigModel.PARTIALLYTRANSLATABLE, true);
	}

	private Checkbox getAttributeByYtestid(final DetailsView detailsView, final String ytestid)
	{
		return findAllChildren(detailsView, Checkbox.class).filter(checkbox -> checkbox.getAttribute("ytestid").equals(ytestid))
				.findFirst().orElseThrow(AssertionError::new);
	}

	@Test
	public void shouldClearItsChildren()
	{
		// given
		detailsView.appendChild(new Label("stub label"));

		// when
		detailsView.clearView();

		// then
		assertThat(detailsView.getChildren()).isEmpty();
	}

	@Test
	public void shouldDisableCheckboxes()
	{
		// given
		when(permissionFacade.canChangeInstanceProperty(any(), any())).thenReturn(false);
		final DetailsView.CreationContext creationContext = new DetailsView.CreationContext();
		creationContext.setWidgetInstanceManager(widgetInstanceManager);
		creationContext.setEditable(false);
		creationContext.setPermissionFacade(permissionFacade);
		detailsView = new DetailsView(creationContext);

		// when
		detailsView.display(new SyncAttributeDescriptorConfigModel(), null);

		// then
		assertThat(getAttributeByYtestid(detailsView, DetailsView.YTESTID_COPY_BY_VALUE_ATTRIBUTE).isDisabled()).isTrue();
		assertThat(getAttributeByYtestid(detailsView, DetailsView.YTESTID_PARTIALLY_TRANSLATABLE_ATTRIBUTE).isDisabled()).isTrue();
		assertThat(getAttributeByYtestid(detailsView, DetailsView.YTESTID_SYNC_ATTRIBUTE).isDisabled()).isTrue();
		assertThat(getAttributeByYtestid(detailsView, DetailsView.YTESTID_UNTRANSLATABLE_ATTRIBUTE).isDisabled()).isTrue();
	}

	@Test
	public void shouldEnableCheckboxes()
	{
		// given

		// when
		detailsView.display(new SyncAttributeDescriptorConfigModel(), null);

		// then
		assertThat(getAttributeByYtestid(detailsView, DetailsView.YTESTID_COPY_BY_VALUE_ATTRIBUTE).isDisabled()).isFalse();
		assertThat(getAttributeByYtestid(detailsView, DetailsView.YTESTID_PARTIALLY_TRANSLATABLE_ATTRIBUTE).isDisabled()).isFalse();
		assertThat(getAttributeByYtestid(detailsView, DetailsView.YTESTID_SYNC_ATTRIBUTE).isDisabled()).isFalse();
		assertThat(getAttributeByYtestid(detailsView, DetailsView.YTESTID_UNTRANSLATABLE_ATTRIBUTE).isDisabled()).isFalse();
	}

	@Test
	public void shouldRenderNoReadAccess()
	{
		// given
		when(permissionFacade.canReadInstanceProperty(any(), any())).thenReturn(false);
		when(permissionFacade.canChangeInstanceProperty(any(), any())).thenReturn(false);
		final DetailsView.CreationContext creationContext = new DetailsView.CreationContext();
		creationContext.setWidgetInstanceManager(widgetInstanceManager);
		creationContext.setEditable(false);
		creationContext.setPermissionFacade(permissionFacade);
		detailsView = new DetailsView(creationContext);

		// when
		detailsView.display(new SyncAttributeDescriptorConfigModel(), null);

		// then
		final long count = findAllChildren(detailsView, Label.class)//
				.filter(label -> DetailsView.YTESTID_NO_READ_ACCESS_LABEL.equals(label.getAttribute("ytestid")))//
				.count();
		assertThat(count).isEqualTo(4);
	}
}
