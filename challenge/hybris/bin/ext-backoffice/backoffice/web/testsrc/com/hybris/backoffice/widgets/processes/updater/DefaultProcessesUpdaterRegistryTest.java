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
package com.hybris.backoffice.widgets.processes.updater;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import com.google.common.collect.Lists;
import com.hybris.cockpitng.components.Widgetslot;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.util.WidgetUtils;


@RunWith(MockitoJUnitRunner.class)
public class DefaultProcessesUpdaterRegistryTest
{
	private static final String EVENT_ONE = "eventOne";
	@Mock
	private WidgetUtils widgetUtils;
	@Mock
	private Widgetslot widgetslot;
	@Mock
	private Consumer<String> cronJobUpdateConsumer;
	@InjectMocks
	private DefaultProcessesUpdaterRegistry registry;

	private List<ProcessesUpdater> processesUpdaters;


	@Before
	public void setUp()
	{
		processesUpdaters = new ArrayList<>();
		registry.setProcessesUpdaters(processesUpdaters);
	}

	@Test
	public void testCommonEventScopeIsUsed()
	{
		//given
		mockUpdater(EVENT_ONE, CockpitEvent.APPLICATION, "a");
		mockUpdater(EVENT_ONE, CockpitEvent.DESKTOP, "b");
		mockUpdater(EVENT_ONE, CockpitEvent.APPLICATION, "c");

		//when
		registry.registerGlobalEventListeners(widgetslot, cronJobUpdateConsumer);

		//then
		verify(widgetUtils).addGlobalEventListener(eq(EVENT_ONE), same(widgetslot),
				any(DefaultProcessesUpdaterRegistry.ComposedEventListener.class), eq(CockpitEvent.APPLICATION));
		verify(widgetUtils, never()).addGlobalEventListener(eq(EVENT_ONE), same(widgetslot),
				any(DefaultProcessesUpdaterRegistry.ComposedEventListener.class), eq(CockpitEvent.DESKTOP));

	}

	@Test
	public void testFireAllListenersForTheSameEvent() throws Exception
	{
		//given
		final ProcessesUpdater updaterScopeApp = mockUpdater(EVENT_ONE, CockpitEvent.APPLICATION, "a");
		final ProcessesUpdater updater2ScopeApp = mockUpdater(EVENT_ONE, CockpitEvent.APPLICATION, "a");
		final ProcessesUpdater updaterScopeDesktop = mockUpdater(EVENT_ONE, CockpitEvent.DESKTOP, "b");

		//when
		registry.registerGlobalEventListeners(widgetslot, cronJobUpdateConsumer);

		//then
		final ArgumentCaptor<EventListener> eventListener = ArgumentCaptor.forClass(EventListener.class);
		verify(widgetUtils).addGlobalEventListener(eq(EVENT_ONE), same(widgetslot), eventListener.capture(),
				eq(CockpitEvent.APPLICATION));

		assertThat(eventListener).isNotNull();
		//given
		final CockpitEvent cockpitEvent = mock(CockpitEvent.class);
		final Event event = mock(Event.class);
		when(event.getData()).thenReturn(cockpitEvent);
		//when
		eventListener.getValue().onEvent(event);
		//then
		verify(cronJobUpdateConsumer).accept("a");
		verify(cronJobUpdateConsumer).accept("b");

		verify(updaterScopeDesktop).onEvent(cockpitEvent);
		verify(updaterScopeApp).onEvent(cockpitEvent);
		verify(updater2ScopeApp).onEvent(cockpitEvent);

	}

	private ProcessesUpdater mockUpdater(final String eventName, final String scope, final String updateCronJobCode)
	{
		final ProcessesUpdater updater = mock(ProcessesUpdater.class);
		when(updater.getEventName()).thenReturn(eventName);
		when(updater.getEventScope()).thenReturn(scope);
		when(updater.onEvent(any())).thenReturn(Lists.newArrayList(updateCronJobCode));
		processesUpdaters.add(updater);
		return updater;
	}
}
