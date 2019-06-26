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
package com.hybris.backoffice.listeners;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.AfterSaveEvent;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.catalogversioneventhandling.AvailableCatalogVersionsTag;


@RunWith(MockitoJUnitRunner.class)
public class CatalogVersionAfterSaveListenerTest
{
	private static final int CATALOG_VERSION_DEPLOYMENT_CODE = 601;

	@Mock
	private ModelService modelService;

	@Mock
	private AvailableCatalogVersionsTag availableCatalogVersionsTag;

	@InjectMocks
	@Spy
	private CatalogVersionAfterSaveListener testSubject;

	@Test
	public void shouldProceedEventsWhenPlatformIsReady()
	{
		//given
		setPlatformReady();

		//when
		testSubject.afterSave(getEventsWithCreateCatalogVersionEvents());

		//then
		verify(testSubject).handleEvent();
	}

	@Test
	public void shouldNotProceedEventsWhenPlatformIsNotInitialized()
	{
		//given
		setPlatformNotInitialized();

		//when
		testSubject.afterSave(getEventsWithCreateCatalogVersionEvents());

		//then
		verify(testSubject, never()).handleEvent();
	}

	@Test
	public void shouldProceedCreateAndRemoveEventsWhenPlatformIsInitialized()
	{
		//given
		setPlatformReady();

		//when
		testSubject.afterSave(getEventsWithCreateAndRemoveCatalogVersionEvents());

		//then
		verify(testSubject).handleEvent();
	}

	private void setPlatformReady()
	{
		when(testSubject.shouldPerform()).thenReturn(true);
	}

	private void setPlatformNotInitialized()
	{
		when(testSubject.shouldPerform()).thenReturn(false);
	}

	private Collection<AfterSaveEvent> getEventsWithCreateCatalogVersionEvents()
	{
		final AfterSaveEvent createEventMock = mock(AfterSaveEvent.class);
		when(createEventMock.getType()).thenReturn(AfterSaveEvent.CREATE);
		final PK pk = PK.createFixedUUIDPK(CATALOG_VERSION_DEPLOYMENT_CODE, 1);
		when(createEventMock.getPk()).thenReturn(pk);

		final Collection<AfterSaveEvent> events = new ArrayList<>();
		events.add(createEventMock);

		when(modelService.get(pk)).thenReturn(mock(CatalogVersionModel.class));

		return events;
	}

	private Collection<AfterSaveEvent> getEventsWithRemoveCatalogVersionEvents()
	{
		final AfterSaveEvent removeEventMock = mock(AfterSaveEvent.class);
		when(removeEventMock.getType()).thenReturn(AfterSaveEvent.REMOVE);
		final PK pk = PK.createFixedUUIDPK(CATALOG_VERSION_DEPLOYMENT_CODE, 1);
		when(removeEventMock.getPk()).thenReturn(pk);

		final Collection<AfterSaveEvent> events = new ArrayList<>();
		events.add(removeEventMock);

		when(modelService.get(pk)).thenReturn(mock(CatalogVersionModel.class));

		return events;
	}

	private Collection<AfterSaveEvent> getEventsWithCreateAndRemoveCatalogVersionEvents()
	{
		final AfterSaveEvent removeEventMock = mock(AfterSaveEvent.class);
		when(removeEventMock.getType()).thenReturn(AfterSaveEvent.REMOVE);
		final PK removePk = PK.createFixedUUIDPK(CATALOG_VERSION_DEPLOYMENT_CODE, 1);
		when(removeEventMock.getPk()).thenReturn(removePk);

		final AfterSaveEvent createEventMock = mock(AfterSaveEvent.class);
		when(createEventMock.getType()).thenReturn(AfterSaveEvent.CREATE);
		final PK createPk = PK.createFixedUUIDPK(CATALOG_VERSION_DEPLOYMENT_CODE, 2);
		when(createEventMock.getPk()).thenReturn(createPk);

		final Collection<AfterSaveEvent> events = new ArrayList<>();
		events.add(createEventMock);
		events.add(removeEventMock);

		when(modelService.get(createPk)).thenReturn(mock(CatalogVersionModel.class));
		when(modelService.get(removePk)).thenReturn(mock(CatalogVersionModel.class));

		return events;
	}
}
