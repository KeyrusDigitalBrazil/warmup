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
package com.hybris.backoffice.config.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.session.SessionService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.oxm.Unmarshaller;

import com.hybris.cockpitng.core.persistence.impl.jaxb.SettingType;
import com.hybris.cockpitng.core.persistence.impl.jaxb.WidgetExtension;
import com.hybris.cockpitng.core.persistence.impl.jaxb.WidgetSetting;
import com.hybris.cockpitng.core.persistence.impl.jaxb.Widgets;
import com.hybris.cockpitng.modules.CockpitModuleConnector;
import com.hybris.cockpitng.modules.persistence.WidgetConnectionsRemover;


@RunWith(MockitoJUnitRunner.class)
public class TestingBackofficeWidgetPersistenceServiceTest
{

	private static final String WIDGET_ID = "pcmbackoffice-collectionBrowser-browser";

	private static final String FIRST_SETTING_KEY = "colConfigCtxCode";
	private static final String FIRST_SETTING_VALUE = "pcmbackoffice-listview";
	private static final SettingType FIRST_SETTING_TYPE = SettingType.STRING;

	private static final String BASE_WIDGET_CONFIG = "baseWidgetConfig";
	private static final String TEST_WIDGET_CONFIG = "testWidgetConfig";

	@Spy
	@InjectMocks
	private TestingBackofficeWidgetPersistenceService widgetPersistenceService;

	@Mock
	private Unmarshaller unmarshaller;

	@Mock
	private SessionService sessionService;

	@Mock
	private CockpitModuleConnector cockpitModuleConnector;

	@Mock
	private WidgetConnectionsRemover widgetConnectionsRemover;

	@Mock
	private BackofficeConfigurationMediaHelper backofficeConfigurationMediaHelper;

	@Before
	public void setUp() throws IOException
	{
		final Widgets widgets = mock(Widgets.class);
		final Widgets testWidgets = mock(Widgets.class);

		final WidgetSetting firstSetting = mock(WidgetSetting.class);
		when(firstSetting.getKey()).thenReturn(FIRST_SETTING_KEY);
		when(firstSetting.getValue()).thenReturn(FIRST_SETTING_VALUE);
		when(firstSetting.getType()).thenReturn(FIRST_SETTING_TYPE);

		final WidgetExtension extension = mock(WidgetExtension.class);
		when(extension.getWidgetId()).thenReturn(WIDGET_ID);
		when(extension.getSetting()).thenReturn(new ArrayList<>(Collections.singletonList(firstSetting)));

		widgets.getWidgetExtension().add(extension);
		when(widgets.getWidgetExtension()).thenReturn(new ArrayList<>(Collections.singletonList(extension)));
		when(testWidgets.getWidgetExtension()).thenReturn(new ArrayList<>(Arrays.asList(extension, extension)));

		when(unmarshaller.unmarshal(any())).thenAnswer(invocationOnMock -> {
			final StreamSource arg = invocationOnMock.getArgumentAt(0, StreamSource.class);
			final ByteArrayInputStream byteArrayInputStream = (ByteArrayInputStream) arg.getInputStream();
			final String config = IOUtils.toString(byteArrayInputStream);
			if (config.equals(BASE_WIDGET_CONFIG))
			{
				return widgets;
			}
			else if (config.equals(TEST_WIDGET_CONFIG))
			{
				return testWidgets;
			}
			return null;
		});
	}

	@Test
	public void shouldLoadWidgetsAlongWithWidgetsFromTestConfig()
	{
		//given
		widgetPersistenceService.setAdditionalWidgetConfig(TEST_WIDGET_CONFIG);

		//when
		final Widgets widgets = widgetPersistenceService.loadWidgets(new ByteArrayInputStream(BASE_WIDGET_CONFIG.getBytes()));

		//then
		assertThat(widgets.getWidgetExtension()).hasSize(3);
	}

	@Test
	public void shouldLoadOnlyBaseWidgetsWhenThereIsNoTestConfig()
	{
		//when
		final Widgets widgets = widgetPersistenceService.loadWidgets(new ByteArrayInputStream(BASE_WIDGET_CONFIG.getBytes()));

		//then
		assertThat(widgets.getWidgetExtension()).hasSize(1);
	}
}
