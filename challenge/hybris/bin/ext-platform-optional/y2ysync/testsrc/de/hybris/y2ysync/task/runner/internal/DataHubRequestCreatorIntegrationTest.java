/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.y2ysync.task.runner.internal;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.beangenerator.definitions.xml.Property;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import javax.annotation.Resource;

import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.util.Config;
import org.junit.After;
import org.junit.Test;


public class DataHubRequestCreatorIntegrationTest extends ServicelayerBaseTest
{

	@Resource
	private DataHubRequestCreator dataHubRequestCreator;
    private final PropertyConfigSwitcher y2ySyncWebRootProperty = new PropertyConfigSwitcher("y2ysync.webroot");
    private final PropertyConfigSwitcher y2ySyncHomeUrlProperty = new PropertyConfigSwitcher("y2ysync.home.url");

	final String defaultHomeUrl = Config.getString("y2ysync.home.url", "http://localhost:9001");
	final String defaultWebRoot = Config.getString("y2ysync.webroot", "/y2ysync");

	@After
	public void tearDown() throws Exception
	{
		y2ySyncWebRootProperty.switchBackToDefault();
        y2ySyncHomeUrlProperty.switchBackToDefault();
	}

	@Test
	public void shouldReturnDefaultJunitTenantY2YSyncWebRoot() throws Exception
	{
		// when
		final String webRoot = dataHubRequestCreator.getY2YSyncWebRoot();

		// then
		assertThat(webRoot).isEqualTo(defaultHomeUrl + defaultWebRoot);
	}

	@Test
	public void shouldReturnCustomY2YSyncWebRoot() throws Exception
	{
        // given
        y2ySyncWebRootProperty.switchToValue("/y2ysync_custom");

		// when
		final String webRoot = dataHubRequestCreator.getY2YSyncWebRoot();

		// then
		assertThat(webRoot).isEqualTo(defaultHomeUrl + "/y2ysync_custom");
	}

	@Test
	public void shouldReturnDefaultY2YSyncHomeUrl() throws Exception
	{
        // when
        final String homeUrl = dataHubRequestCreator.getHomeUrl();

        // then
        assertThat(homeUrl).isEqualTo(defaultHomeUrl);
	}

    @Test
    public void shouldReturnCustomY2YSyncHomeUrl() throws Exception
    {
    	// given
        y2ySyncHomeUrlProperty.switchToValue("http://192.168.1.5:8080");

    	// when
        final String homeUrl = dataHubRequestCreator.getHomeUrl();

        // then
        assertThat(homeUrl).isEqualTo("http://192.168.1.5:8080");
    }
}
