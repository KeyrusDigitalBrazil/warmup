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
package com.hybris.backoffice.solrsearch.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.tx.AfterSaveEvent;

import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.backoffice.solrsearch.services.BackofficeFacetSearchConfigService;


@IntegrationTest
public class SolrIndexingAfterSaveListenerTest extends ServicelayerTransactionalTest
{
	public static final PK PK_ = PK.fromLong(1L);
	public static final String TYPE_1 = "Type1";

	@InjectMocks
	private final SolrIndexingAfterSaveListener listener = new SolrIndexingAfterSaveListener();

	private final AfterSaveEvent UPDATE_EVENT = new AfterSaveEvent(PK_, AfterSaveEvent.UPDATE);

	private final AfterSaveEvent REMOVE_EVENT = new AfterSaveEvent(PK_, AfterSaveEvent.REMOVE);

	@Mock
	private BackofficeFacetSearchConfigService backofficeFacetSearchConfigService;
	@Mock
	private ModelService modelService;

	@Mock
	private TypeService typeService;
	@Mock
	private ItemModel itemModel;
	@Mock
	private SolrIndexSynchronizationStrategy solrIndexSynchronizationStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		listener.setIgnoredTypeCodes(new TreeSet<>());
		when(itemModel.getItemtype()).thenReturn(TYPE_1);
		when(modelService.get(PK_)).thenReturn(itemModel);
		when(Boolean.valueOf(typeService.isAssignableFrom(anyString(), anyString())))
				.thenReturn(Boolean.FALSE);
	}

	@Test
	public void testAfterSaveUpdateEvent()
	{
		when(Boolean.valueOf(backofficeFacetSearchConfigService.isSolrSearchConfiguredForType(TYPE_1)))
				.thenReturn(Boolean.TRUE);
		when(listener.findTypeCode(SolrIndexingAfterSaveListener.SolrIndexOperation.CHANGE, PK_)).thenReturn(TYPE_1);
		listener.afterSave(Collections.singletonList(UPDATE_EVENT));
		ArgumentCaptor<List> pkList = ArgumentCaptor.forClass(List.class);
		verify(solrIndexSynchronizationStrategy).updateItems(eq(TYPE_1), pkList.capture());
		assertThat(pkList.getValue()).hasSize(1);
		assertThat(pkList.getValue()).containsExactly(PK_);
	}

	@Test
	public void testAfterSaveUpdateEventNonIndexedType()
	{
		when(Boolean.valueOf(backofficeFacetSearchConfigService.isSolrSearchConfiguredForType(TYPE_1)))
				.thenReturn(Boolean.FALSE);
		listener.afterSave(Collections.singletonList(UPDATE_EVENT));
		Mockito.verifyNoMoreInteractions(solrIndexSynchronizationStrategy);
	}

	@Test
	public void testAfterSaveCreateRemoveEvents()
	{
		when(Boolean.valueOf(backofficeFacetSearchConfigService.isSolrSearchConfiguredForType(ItemModel._TYPECODE)))
				.thenReturn(Boolean.TRUE);
		listener.afterSave(Collections.singletonList(REMOVE_EVENT));
		ArgumentCaptor<List> pkList = ArgumentCaptor.forClass(List.class);
		verify(solrIndexSynchronizationStrategy).removeItems(eq(ItemModel._TYPECODE), pkList.capture());
		assertThat(pkList.getValue()).hasSize(1);
		assertThat(pkList.getValue()).containsExactly(PK_);
	}
}
