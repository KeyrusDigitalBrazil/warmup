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
package com.hybris.backoffice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.events.AfterInitializationEndEvent;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.JspContext;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.hybris.backoffice.config.impl.BackofficeCockpitConfigurationService;
import com.hybris.cockpitng.core.persistence.impl.XMLWidgetPersistenceService;
import com.hybris.cockpitng.modules.core.impl.CockpitModuleComponentDefinitionService;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.util.WidgetUtils;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class DefaultBackofficeStartupHandlerTest extends AbstractCockpitngUnitTest<DefaultBackofficeStartupHandler>
{

	public static final String BACKOFFICE_PROJECT_DATA_PARAMETER_KEY = "backoffice_sample";
	public static final String BACKOFFICE_COCKPITNG_RESETEVERYTHING_ENABLED = "backoffice.cockpitng.reseteverything.enabled";

	@Spy
	@InjectMocks
	private DefaultBackofficeStartupHandler handler;

	@Mock
	private XMLWidgetPersistenceService widgetPersistenceService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private BackofficeCockpitConfigurationService cockpitConfigurationService;
	@Mock
	private WidgetUtils widgetUtils;
	@Mock
	private CockpitModuleComponentDefinitionService cockpitComponentDefinitionService;
	@Mock
	private AfterInitializationEndEvent event;
	@Mock
	private JspContext context;
	@Mock
	private HttpServletRequest request;
	@Mock
	private Configuration configuration;
	@Mock
	private Transaction tx;

	@Before
	public void setUp()
	{
		initMocks(this);
		when(event.getCtx()).thenReturn(context);
		when(context.getServletRequest()).thenReturn(request);
		when(request.getParameter(BACKOFFICE_PROJECT_DATA_PARAMETER_KEY)).thenReturn("true");

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.containsKey(BACKOFFICE_COCKPITNG_RESETEVERYTHING_ENABLED)).thenReturn(true);
		when(configuration.getBoolean(BACKOFFICE_COCKPITNG_RESETEVERYTHING_ENABLED)).thenReturn(true);

		doReturn(tx).when(handler).getCurrentTransaction();
	}

	@Test
	public void isBackofficeProjectDataUpdateEnabled()
	{
		assertThat(handler.isBackofficeProjectDataUpdate(event)).isTrue();
	}

	@Test
	public void isBackofficeProjectDataUpdateDisabled()
	{
		when(request.getParameter(BACKOFFICE_PROJECT_DATA_PARAMETER_KEY)).thenReturn(null);
		assertThat(handler.isBackofficeProjectDataUpdate(event)).isFalse();
	}

	@Test
	public void isResetEverythingEnabled()
	{
		assertThat(handler.isResetEverythingEnabled()).isTrue();

		verify(configurationService).getConfiguration();
		verify(configuration).containsKey(BACKOFFICE_COCKPITNG_RESETEVERYTHING_ENABLED);
		verify(configuration).getBoolean(BACKOFFICE_COCKPITNG_RESETEVERYTHING_ENABLED);
	}

	@Test
	public void resetBackofficeWidgetsConfiguration()
	{
		handler.resetBackofficeWidgetsConfiguration(event);

		verify(tx).begin();
		verify(widgetUtils).refreshWidgetLibrary();
		verify(cockpitComponentDefinitionService).reloadDefinitions();
		verify(widgetPersistenceService).resetToDefaults();
		verify(cockpitConfigurationService).resetToDefaults();
		verify(widgetUtils).clearWidgetLibrary();
		verify(tx).commit();
	}

}
