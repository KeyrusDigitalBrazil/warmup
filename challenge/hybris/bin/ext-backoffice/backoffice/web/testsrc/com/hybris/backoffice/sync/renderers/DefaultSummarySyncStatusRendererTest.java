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
package com.hybris.backoffice.sync.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zhtml.Div;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.impl.XulElement;

import com.hybris.backoffice.sync.facades.SynchronizationFacade;
import com.hybris.cockpitng.config.summaryview.jaxb.Attribute;
import com.hybris.cockpitng.core.util.impl.TypedSettingsMap;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;
import com.hybris.cockpitng.widgets.summaryview.label.AttributeLabelResolver;


@RunWith(MockitoJUnitRunner.class)
public class DefaultSummarySyncStatusRendererTest
{
	public static final String TEST_OUTPUT_SOCKET = "testOutput";
	@Mock
	private SynchronizationFacade synchronizationFacade;
	@Mock
	private WidgetComponentRenderer<XulElement, Object, ItemModel> partialSyncInfoRenderer;
	@Mock
	private ItemModel item;
	@Mock
	private DataType dataType;
	@Mock
	private WidgetInstanceManager wim;
	@Mock
	private Attribute configuration;
	@Mock
	private AttributeLabelResolver attributeLabelResolver;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private ObjectFacade objectFacade;

	@Spy
	@InjectMocks
	private DefaultSummarySyncStatusRenderer renderer;

	private Div parent;

	@Before
	public void before()
	{
		CockpitTestUtil.mockZkEnvironment();
		parent = new Div();
		when(objectFacade.isNew(any())).thenReturn(false);
		when(objectFacade.isModified(any())).thenReturn(false);
	}

	@Test
	public void testStatusUndefined()
	{
		doReturn(Optional.empty()).when(synchronizationFacade).isInSync(eq(item), anyMap());
		doReturn(new Label()).when(attributeLabelResolver).createAttributeLabel(any(), any(), any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadType(any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadInstance(any());

		renderer.render(parent, configuration, item, dataType, wim);

		final Component undefined = parent.query("." + SyncRenderConstants.YW_IMAGE_ATTRIBUTE_SYNC_STATUS_UNDEFINED);
		assertThat(undefined).isNotNull();
		verify(partialSyncInfoRenderer, never()).render(any(), any(), any(), any(), any());
	}

	@Test
	public void testStatusInSync()
	{
		doReturn(Optional.of(Boolean.TRUE)).when(synchronizationFacade).isInSync(eq(item), anyMap());
		doReturn(new Label()).when(attributeLabelResolver).createAttributeLabel(any(), any(), any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadType(any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadInstance(any());

		renderer.render(parent, configuration, item, dataType, wim);

		final Component status = parent.query("." + SyncRenderConstants.YW_IMAGE_ATTRIBUTE_SYNC_STATUS_IN_SYNC);
		assertThat(status).isNotNull();
		verify(partialSyncInfoRenderer).render(any(), any(), any(), any(), any());
	}

	@Test
	public void testStatusOutOfSync()
	{
		doReturn(Optional.of(Boolean.FALSE)).when(synchronizationFacade).isInSync(eq(item), anyMap());
		doReturn(new Label()).when(attributeLabelResolver).createAttributeLabel(any(), any(), any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadType(any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadInstance(any());

		renderer.render(parent, configuration, item, dataType, wim);

		final Component status = parent.query("." + SyncRenderConstants.YW_IMAGE_ATTRIBUTE_SYNC_STATUS_OUT_OF_SYNC);
		assertThat(status).isNotNull();
		verify(partialSyncInfoRenderer).render(any(), any(), any(), any(), any());
	}

	@Test
	public void testSyncTriggeredOnClick()
	{
		// given
		doReturn(Optional.of(Boolean.FALSE)).when(synchronizationFacade).isInSync(eq(item), anyMap());
		final TypedSettingsMap settings = new TypedSettingsMap();
		settings.put(DefaultSummarySyncStatusRenderer.SETTING_SYNC_OUTPUT_SOCKET, TEST_OUTPUT_SOCKET, String.class);
		when(wim.getWidgetSettings()).thenReturn(settings);
		doReturn(new Label()).when(attributeLabelResolver).createAttributeLabel(any(), any(), any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadType(any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadInstance(any());

		// when
		final HtmlBasedComponent container = renderer.createContainer(new Div(), configuration, item, dataType, wim);

		// when
		CockpitTestUtil.simulateEvent(container, new Event(Events.ON_CLICK));

		// then
		verify(wim).sendOutput(TEST_OUTPUT_SOCKET, item);
	}

	@Test
	public void testSyncNotTriggeredOnClick()
	{
		// given
		doReturn(Optional.of(Boolean.FALSE)).when(synchronizationFacade).isInSync(eq(item), anyMap());
		final TypedSettingsMap settings = new TypedSettingsMap();
		settings.put(DefaultSummarySyncStatusRenderer.SETTING_SYNC_OUTPUT_SOCKET, StringUtils.EMPTY, String.class);
		when(wim.getWidgetSettings()).thenReturn(settings);
		doReturn(new Label()).when(attributeLabelResolver).createAttributeLabel(any(), any(), any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadType(any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadInstance(any());

		// when
		renderer.render(parent, configuration, item, dataType, wim);
		final Component status = parent.query("." + SyncRenderConstants.YW_IMAGE_ATTRIBUTE_SYNC_STATUS_OUT_OF_SYNC);
		assertThat(settings).isNotNull();

		// when
		CockpitTestUtil.simulateEvent(status, new Event(Events.ON_CLICK));

		// then
		verify(wim, never()).sendOutput(anyString(), eq(item));
	}

	@Test
	public void testSyncLabelFromConfig()
	{
		// given
		final String label = "label.from.config";
		when(configuration.getLabel()).thenReturn(label);
		doReturn(new Label(label)).when(attributeLabelResolver).createAttributeLabel(any(), any(), any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadType(any());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadInstance(any());
		doReturn(Optional.of(Boolean.FALSE)).when(synchronizationFacade).isInSync(eq(item), anyMap());
		// when
		renderer.render(parent, configuration, item, dataType, wim);

		// then
		final Stream<Label> labels = CockpitTestUtil.findAllChildren(parent, Label.class);
		assertThat(labels.map(Label::getValue).filter(value -> value.contains(label)).findAny().isPresent()).isTrue();
	}

}
