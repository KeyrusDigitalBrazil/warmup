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

import static com.hybris.backoffice.sync.renderers.SyncRenderConstants.YW_IMAGE_ATTRIBUTE_SYNC_STATUS_ERROR;
import static com.hybris.backoffice.sync.renderers.SyncRenderConstants.YW_IMAGE_ATTRIBUTE_SYNC_STATUS_IN_SYNC;
import static com.hybris.backoffice.sync.renderers.SyncRenderConstants.YW_IMAGE_ATTRIBUTE_SYNC_STATUS_LOADING;
import static com.hybris.backoffice.sync.renderers.SyncRenderConstants.YW_IMAGE_ATTRIBUTE_SYNC_STATUS_OUT_OF_SYNC;
import static com.hybris.backoffice.sync.renderers.SyncRenderConstants.YW_IMAGE_ATTRIBUTE_SYNC_STATUS_UNDEFINED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;

import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.impl.XulElement;

import com.hybris.backoffice.sync.facades.SynchronizationFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.lazyloading.DefaultLazyTaskResult;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;


@RunWith(MockitoJUnitRunner.class)
public class SyncStatusCellRendererTest
{

	@Mock
	private ItemModel itemModel;

	@Mock
	private SynchronizationFacade facade;

	@Mock
	private WidgetComponentRenderer<XulElement, Object, ItemModel> partialSyncInfoRenderer;

	@Mock
	private WidgetInstanceManager wim;

	@Spy
	@InjectMocks
	private SyncStatusCellRenderer renderer;

	private final Listcell listcell = new Listcell();
	private final Object config = new Object();
	private final ItemModel data = new ItemModel();
	private final DataType dataType = DataType.NULL;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
	}

	@Test
	public void testLoadingDataCallsFacade()
	{
		// when
		renderer.loadData(null, itemModel, null);

		// then
		verify(facade).isInSync(eq(itemModel), any(Map.class));
	}

	@Test
	public void testRenderBeforeLoadAddsLoadingStatusSpan()
	{
		// when
		renderer.renderBeforeLoad(listcell, null, null, null, null);

		// then
		final Component component = listcell.query("." + YW_IMAGE_ATTRIBUTE_SYNC_STATUS_LOADING);
		assertThat(component).isNotNull();
	}

	@Test
	public void testRenderAfterLoadRemovesLoadingStatusSpan()
	{
		// given
		renderer.renderBeforeLoad(listcell, null, null, null, null);

		// when
		renderer.renderAfterLoad(listcell, null, null, null, null, DefaultLazyTaskResult.success(Optional.of(Boolean.TRUE)));

		// then
		final Component component = listcell.query("." + YW_IMAGE_ATTRIBUTE_SYNC_STATUS_LOADING);
		assertThat(component).isNull();
	}

	@Test
	public void testRenderAfterLoadReplacesSpanWithInSync()
	{
		// given
		renderer.renderBeforeLoad(listcell, null, null, null, null);

		// when
		renderer.renderAfterLoad(listcell, null, null, null, null, DefaultLazyTaskResult.success(Optional.of(Boolean.TRUE)));

		// then
		final Component component = listcell.query("." + YW_IMAGE_ATTRIBUTE_SYNC_STATUS_IN_SYNC);
		assertThat(component).isNotNull();
		verify(partialSyncInfoRenderer).render(any(), any(), any(), any(), any());
	}

	@Test
	public void testRenderAfterLoadReplacesSpanWithOutOfSync()
	{
		// given

		renderer.renderBeforeLoad(listcell, null, null, null, null);

		// when
		renderer.renderAfterLoad(listcell, null, null, null, null, DefaultLazyTaskResult.success(Optional.of(Boolean.FALSE)));

		// then
		final Component component = listcell.query("." + YW_IMAGE_ATTRIBUTE_SYNC_STATUS_OUT_OF_SYNC);
		assertThat(component).isNotNull();
		verify(partialSyncInfoRenderer).render(any(), any(), any(), any(), any());
	}

	@Test
	public void testRenderAfterLoadReplacesSpanWithUndefined()
	{
		// given

		renderer.renderBeforeLoad(listcell, null, null, null, null);

		// when
		renderer.renderAfterLoad(listcell, null, null, null, null, DefaultLazyTaskResult.success(Optional.empty()));

		// then
		final Component component = listcell.query("." + YW_IMAGE_ATTRIBUTE_SYNC_STATUS_UNDEFINED);
		assertThat(component).isNotNull();
		verify(partialSyncInfoRenderer, never()).render(any(), any(), any(), any(), any());
	}

	@Test
	public void testRenderAfterLoadReplacesSpanWithError()
	{
		// given
		renderer.renderBeforeLoad(listcell, null, null, null, null);

		// when
		renderer.renderAfterLoad(listcell, null, null, null, null, DefaultLazyTaskResult.failure());

		// then
		final Component component = listcell.query("." + YW_IMAGE_ATTRIBUTE_SYNC_STATUS_ERROR);
		assertThat(component).isNotNull();
		verify(partialSyncInfoRenderer, never()).render(any(), any(), any(), any(), any());
	}

	@Test
	public void testNotLazyRenderByDefault() throws Exception
	{
		// given
		final Boolean inSync = Boolean.TRUE;
		when(facade.isInSync(eq(data), any())).thenReturn(Optional.of(inSync));
		final ArgumentCaptor<DefaultLazyTaskResult> result = ArgumentCaptor.forClass(DefaultLazyTaskResult.class);

		// when
		renderer.render(listcell, config, data, dataType, wim);

		// then
		verify(renderer).loadData(config, data, dataType);
		verify(renderer).renderAfterLoad(eq(listcell), eq(config), eq(data), eq(dataType), eq(wim), result.capture());
		assertThat(result.getValue().isSuccess()).isTrue();
		assertThat(result.getValue().get()).isEqualTo(Optional.of(inSync));
	}

	@Test
	public void shouldFireComponentRendered()
	{
		// when
		renderer.render(listcell, config, data, dataType, wim);

		// then
		verify(renderer).fireComponentRendered(listcell, config, data);
	}

}
