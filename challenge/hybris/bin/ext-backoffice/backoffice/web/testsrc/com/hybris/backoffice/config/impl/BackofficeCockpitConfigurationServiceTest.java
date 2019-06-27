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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.MockSessionService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.hybris.backoffice.model.BackofficeConfigurationMediaModel;
import com.hybris.cockpitng.core.config.CockpitConfigurationException;
import com.hybris.cockpitng.core.config.impl.cache.ConfigurationCache;


public class BackofficeCockpitConfigurationServiceTest
{

	@Spy
	@InjectMocks
	private BackofficeCockpitConfigurationService backofficeCockpitConfigurationService;

	@InjectMocks
	@Spy
	private DefaultBackofficeConfigurationMediaHelper backofficeConfigurationMediaHelper;

	@Spy
	private DefaultMediaCockpitConfigurationPersistenceStrategy mediaCockpitConfigurationPersistenceStrategy;

	@Mock
	private MediaService mediaService;

	@Mock
	private ModelService modelService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private UserService userService;

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private MockSessionService sessionService;

	@Mock
	private EmployeeModel admin;

	@Mock
	private TimeService timeService;

	@Mock
	private SearchRestrictionService searchRestrictionService;

	@Before
	public void setUp()
	{
		initMocks(this);
		backofficeCockpitConfigurationService.setSessionService(sessionService);
		backofficeCockpitConfigurationService.setBackofficeConfigurationMediaHelper(backofficeConfigurationMediaHelper);
		backofficeCockpitConfigurationService.setPersistenceStrategy(mediaCockpitConfigurationPersistenceStrategy);
		mediaCockpitConfigurationPersistenceStrategy.setBackofficeConfigurationMediaHelper(backofficeConfigurationMediaHelper);
		mediaCockpitConfigurationPersistenceStrategy.setUserService(userService);
		mediaCockpitConfigurationPersistenceStrategy.setSessionService(sessionService);
		when(userService.getAdminUser()).thenReturn(admin);
		when(mediaService.getFolder(any())).thenReturn(mock(MediaFolderModel.class));
	}

	@Test
	public void getCockpitNGConfigTest() throws CockpitConfigurationException
	{
		final MediaModel media = mock(MediaModel.class);
		media.setCode(anyString());
		when(mediaService.getMedia(anyString())).thenReturn(media);
		assertNotNull(backofficeCockpitConfigurationService.getCockpitNGConfig());
		assertEquals(media, backofficeCockpitConfigurationService.getCockpitNGConfig());
	}

	@Test
	public void storeRootConfig()
	{
		final ConfigurationCache cache = mock(ConfigurationCache.class);
		final SessionService sessionService = mock(SessionService.class);
		final com.hybris.cockpitng.core.config.impl.jaxb.Config config =mock(com.hybris.cockpitng.core.config.impl.jaxb.Config.class);

		backofficeCockpitConfigurationService.setConfigurationCache(cache);
		backofficeCockpitConfigurationService.setSessionService(sessionService);

		final long timeInMills = System.currentTimeMillis();
		when(timeService.getCurrentTime()).thenReturn(new Date(timeInMills));
		when(modelService.create(BackofficeConfigurationMediaModel.class)).thenReturn(mock(BackofficeConfigurationMediaModel.class));

		doReturn(mock(ByteArrayOutputStream.class)).when(mediaCockpitConfigurationPersistenceStrategy).getConfigurationOutputStream();

		try
		{
			backofficeCockpitConfigurationService.storeRootConfig(config);
		}
		catch (final CockpitConfigurationException e)
		{
			fail("Could not store Configuration");
		}

		verify(cache).cacheRootConfiguration(eq(config), anyLong());
	}

	@Test
	public void shouldVerifySecureFolderAssignmentOnGetCockpitNGConfig()
			throws CockpitConfigurationException
	{
		//given
		final MediaModel media = mock(MediaModel.class);
		when(mediaService.getMedia(anyString())).thenReturn(media);

		//when
		backofficeCockpitConfigurationService.getCockpitNGConfig();

		//then
		verify(backofficeConfigurationMediaHelper).assureSecureFolderAssignment(media);
	}

}
