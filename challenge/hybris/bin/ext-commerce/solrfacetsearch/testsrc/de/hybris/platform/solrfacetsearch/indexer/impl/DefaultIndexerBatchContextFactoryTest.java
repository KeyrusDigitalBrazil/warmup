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
package de.hybris.platform.solrfacetsearch.indexer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.common.ListenersFactory;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.ExtendedIndexerBatchListener;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext.Status;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultIndexerBatchContextFactoryTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private SessionService sessionService;

	@Mock
	private JaloSession rawSession;

	@Mock
	private ListenersFactory listenersFactory;

	@Mock
	private IndexerBatchListener listener1;

	@Mock
	private ExtendedIndexerBatchListener extendedListener1;

	@Mock
	private IndexerBatchListener listener2;

	@Mock
	private ExtendedIndexerBatchListener extendedListener2;

	private long indexOperationId;
	private IndexOperation indexOperation;
	private boolean externalIndexOperation;
	private FacetSearchConfig facetSearchConfig;
	private IndexedType indexedType;
	private Collection<IndexedProperty> indexedProperties;

	private DefaultIndexerBatchContextFactory indexerBatchContextFactory;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		when(sessionService.getRawSession(any(Session.class))).thenReturn(rawSession);

		indexOperationId = 1;
		indexOperation = IndexOperation.FULL;
		externalIndexOperation = false;
		facetSearchConfig = new FacetSearchConfig();
		indexedType = new IndexedType();
		indexedProperties = Collections.emptyList();

		indexerBatchContextFactory = new DefaultIndexerBatchContextFactory();
		indexerBatchContextFactory.setSessionService(sessionService);
		indexerBatchContextFactory.setListenersFactory(listenersFactory);
	}

	@Test
	public void createContext() throws Exception
	{
		// when
		final IndexerBatchContext batchContext = indexerBatchContextFactory.createContext(indexOperationId, indexOperation,
				externalIndexOperation, facetSearchConfig, indexedType, indexedProperties);
		indexerBatchContextFactory.prepareContext();

		// then
		assertNotNull(batchContext);
		assertEquals(Status.CREATED, batchContext.getStatus());
	}

	@Test
	public void initializeContext() throws Exception
	{
		// given
		final IndexerBatchContext batchContext = indexerBatchContextFactory.createContext(indexOperationId, indexOperation,
				externalIndexOperation, facetSearchConfig, indexedType, indexedProperties);
		indexerBatchContextFactory.prepareContext();

		// when
		indexerBatchContextFactory.initializeContext();

		// then
		assertEquals(Status.EXECUTING, batchContext.getStatus());
	}

	@Test
	public void doesNotHaveCurrentContext() throws Exception
	{
		// expect
		expectedException.expect(IllegalStateException.class);

		// when
		indexerBatchContextFactory.getContext();
	}

	@Test
	public void hasCurrentContext() throws Exception
	{
		// given
		final IndexerBatchContext expectedBatchContext = indexerBatchContextFactory.createContext(indexOperationId, indexOperation,
				externalIndexOperation, facetSearchConfig, indexedType, indexedProperties);

		// when
		final IndexerBatchContext batchContext = indexerBatchContextFactory.getContext();

		// then
		assertNotNull(batchContext);
		assertSame(expectedBatchContext, batchContext);
	}

	@Test
	public void destroyContext() throws Exception
	{
		// given
		final IndexerBatchContext batchContext = indexerBatchContextFactory.createContext(indexOperationId, indexOperation,
				externalIndexOperation, facetSearchConfig, indexedType, indexedProperties);

		// when
		indexerBatchContextFactory.destroyContext();

		// then
		assertEquals(Status.COMPLETED, batchContext.getStatus());
	}

	@Test
	public void destroyContextAfterException() throws Exception
	{
		// given
		final IndexerBatchContext batchContext = indexerBatchContextFactory.createContext(indexOperationId, indexOperation,
				externalIndexOperation, facetSearchConfig, indexedType, indexedProperties);

		// when
		indexerBatchContextFactory.destroyContext(new RuntimeException());

		// then
		assertEquals(Status.FAILED, batchContext.getStatus());
	}

	@Test
	public void noListenerConfigured() throws Exception
	{
		// given
		final List<IndexerBatchListener> listeners = Collections.emptyList();

		when(listenersFactory.getListeners(facetSearchConfig, indexedType, IndexerBatchListener.class)).thenReturn(listeners);

		final IndexerBatchContext batchContext = indexerBatchContextFactory.createContext(indexOperationId, indexOperation,
				externalIndexOperation, facetSearchConfig, indexedType, indexedProperties);

		// when
		indexerBatchContextFactory.prepareContext();
		indexerBatchContextFactory.initializeContext();
		indexerBatchContextFactory.destroyContext();

		// then
		verify(extendedListener1, never()).afterPrepareContext(batchContext);
		verify(extendedListener2, never()).afterPrepareContext(batchContext);
		verify(listener1, never()).beforeBatch(batchContext);
		verify(listener2, never()).beforeBatch(batchContext);
		verify(listener2, never()).afterBatch(batchContext);
		verify(listener1, never()).afterBatch(batchContext);
	}

	@Test
	public void runBeforeBatchListener() throws Exception
	{
		// given
		final List<IndexerBatchListener> listeners = Arrays.asList(listener1);

		when(listenersFactory.getListeners(facetSearchConfig, indexedType, IndexerBatchListener.class)).thenReturn(listeners);

		final IndexerBatchContext batchContext = indexerBatchContextFactory.createContext(indexOperationId, indexOperation,
				externalIndexOperation, facetSearchConfig, indexedType, indexedProperties);
		indexerBatchContextFactory.prepareContext();

		// when
		indexerBatchContextFactory.initializeContext();

		// then
		final InOrder inOrder = inOrder(listener1);
		inOrder.verify(listener1).beforeBatch(batchContext);
		inOrder.verify(listener1, never()).afterBatch(batchContext);
	}

	@Test
	public void runListeners() throws Exception
	{
		// given
		final List<ExtendedIndexerBatchListener> extendedListeners = Arrays.asList(extendedListener1);
		final List<IndexerBatchListener> listeners = Arrays.asList(listener1);

		when(listenersFactory.getListeners(facetSearchConfig, indexedType, ExtendedIndexerBatchListener.class)).thenReturn(extendedListeners);
		when(listenersFactory.getListeners(facetSearchConfig, indexedType, IndexerBatchListener.class)).thenReturn(listeners);

		final IndexerBatchContext batchContext = indexerBatchContextFactory.createContext(indexOperationId, indexOperation,
				externalIndexOperation, facetSearchConfig, indexedType, indexedProperties);

		// when
		indexerBatchContextFactory.prepareContext();
		indexerBatchContextFactory.initializeContext();
		indexerBatchContextFactory.destroyContext();

		// then
		final InOrder inOrder = inOrder(extendedListener1, listener1);
		inOrder.verify(extendedListener1).afterPrepareContext(batchContext);
		inOrder.verify(listener1).beforeBatch(batchContext);
		inOrder.verify(listener1).afterBatch(batchContext);
	}

	@Test
	public void runAfterBatchErrorListener() throws Exception
	{
		// given
		final List<ExtendedIndexerBatchListener> extendedListeners = Arrays.asList(extendedListener1);
		final List<IndexerBatchListener> listeners = Arrays.asList(listener1);

		when(listenersFactory.getListeners(facetSearchConfig, indexedType, ExtendedIndexerBatchListener.class)).thenReturn(extendedListeners);
		when(listenersFactory.getListeners(facetSearchConfig, indexedType, IndexerBatchListener.class)).thenReturn(listeners);

		final IndexerBatchContext batchContext = indexerBatchContextFactory.createContext(indexOperationId, indexOperation,
				externalIndexOperation, facetSearchConfig, indexedType, indexedProperties);

		// when
		indexerBatchContextFactory.prepareContext();
		indexerBatchContextFactory.initializeContext();
		indexerBatchContextFactory.destroyContext(new Exception());

		// then
		final InOrder inOrder = inOrder(extendedListener1, listener1);
		inOrder.verify(extendedListener1).afterPrepareContext(batchContext);
		inOrder.verify(listener1).beforeBatch(batchContext);
		inOrder.verify(listener1).afterBatchError(batchContext);
	}

	@Test
	public void listenersRunInCorrectOrder() throws Exception
	{
		// given
		final List<ExtendedIndexerBatchListener> extendedListeners = Arrays.asList(extendedListener2, extendedListener1);
		final List<IndexerBatchListener> listeners = Arrays.asList(listener2, listener1);

		when(listenersFactory.getListeners(facetSearchConfig, indexedType, ExtendedIndexerBatchListener.class)).thenReturn(extendedListeners);
		when(listenersFactory.getListeners(facetSearchConfig, indexedType, IndexerBatchListener.class)).thenReturn(listeners);

		final IndexerBatchContext batchContext = indexerBatchContextFactory.createContext(indexOperationId, indexOperation,
				externalIndexOperation, facetSearchConfig, indexedType, indexedProperties);

		// when
		indexerBatchContextFactory.prepareContext();
		indexerBatchContextFactory.initializeContext();
		indexerBatchContextFactory.destroyContext();

		// then
		final InOrder inOrder = inOrder(extendedListener1, extendedListener2, listener1, listener2);
		inOrder.verify(extendedListener2).afterPrepareContext(batchContext);
		inOrder.verify(extendedListener1).afterPrepareContext(batchContext);
		inOrder.verify(listener2).beforeBatch(batchContext);
		inOrder.verify(listener1).beforeBatch(batchContext);
		inOrder.verify(listener1).afterBatch(batchContext);
		inOrder.verify(listener2).afterBatch(batchContext);
	}
}
