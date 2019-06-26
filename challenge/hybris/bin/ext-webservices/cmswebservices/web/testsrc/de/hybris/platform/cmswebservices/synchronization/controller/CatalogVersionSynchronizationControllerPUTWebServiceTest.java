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
package de.hybris.platform.cmswebservices.synchronization.controller;

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_APPLE;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.SyncJobData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CatalogVersionSynchronizationControllerPUTWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String ENDPOINT = "/v1/catalogs/{catalogId}/versions/{sourceVersionId}/synchronizations/versions/{targetVersionId}";
	private static final String CATALOG_UID = "testCatalogSync";
	private static final String SOURCE_VERSION = "staged";
	private static final String TARGET_VERSION = "online";
	private static final String CATALOG_KEY = "catalogId";
	private static final String SOURCE_VERSION_KEY = "sourceVersionId";
	private static final String TARGET_VERSION_KEY = "targetVersionId";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	private CatalogVersionModel sourceCatalogVersion;
	private CatalogVersionModel targetCatalogVersion;

	@Before
	public void setup()
	{
		sourceCatalogVersion = catalogVersionModelMother.createCatalogVersionModel(CATALOG_UID, SOURCE_VERSION);
		targetCatalogVersion = catalogVersionModelMother.createCatalogVersionModel(CATALOG_UID, TARGET_VERSION);
	}

	@Test
	public void testCreateSynchronization() throws Exception
	{
		catalogVersionModelMother.createCatalogSynchronizationItemJobModel(sourceCatalogVersion, targetCatalogVersion, true);

		final Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put(CATALOG_KEY, CATALOG_UID);
		uriVariables.put(SOURCE_VERSION_KEY, SOURCE_VERSION);
		uriVariables.put(TARGET_VERSION_KEY, TARGET_VERSION);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, uriVariables)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(new SyncJobData(), MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, response);

		final Response getResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, uriVariables)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, getResponse);

		final SyncJobData syncJobData = response.readEntity(SyncJobData.class);
		assertNotNull(syncJobData);
		final String[] expectedStatusValues = allCronJobStatusCodes();
		final String[] expectedResultsValues = allCronJobResultsCodes();

		assertThat(upperCase(syncJobData.getSyncStatus()), isOneOf(expectedStatusValues));
		assertThat(upperCase(syncJobData.getSyncResult()), isOneOf(expectedResultsValues));

		final Date now = DateTime.now().toDate();
		assertEquals(new SimpleDateFormat("dd MMM yyyy HH").format(syncJobData.getCreationDate()),
				new SimpleDateFormat("dd MMM yyyy HH").format(now));
		assertThat(now.getTime() - syncJobData.getCreationDate().getTime(), lessThan(90000L));

	}

	protected String[] allCronJobStatusCodes() {
		final Collection<String> allCodes = new ArrayList<>();
		for (final CronJobStatus cronJobStatus : CronJobStatus.values()) {
			allCodes.add(upperCase(cronJobStatus.getCode()));
		}
		return allCodes.toArray(new String[allCodes.size()]);
	}

	protected String[] allCronJobResultsCodes() {
		final Collection<String> allResults = new ArrayList<>();
		for (final CronJobResult cronJobResult : CronJobResult.values()) {
			allResults.add(upperCase(cronJobResult.getCode()));
		}
		return allResults.toArray(new String[allResults.size()]);
	}

	@Test
	public void testCreateSynchronizationFailDueToRunningCronJob() throws Exception
	{
		catalogVersionModelMother.createCatalogSyncronizationSyncItemCronJobModel(sourceCatalogVersion, targetCatalogVersion);

		final Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put(CATALOG_KEY, CATALOG_UID);
		uriVariables.put(SOURCE_VERSION_KEY, SOURCE_VERSION);
		uriVariables.put(TARGET_VERSION_KEY, TARGET_VERSION);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, uriVariables)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(new SyncJobData(), MediaType.APPLICATION_JSON));

		assertResponse(Status.CONFLICT, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors(), hasSize(1));
	}

	@Test
	public void testCreateSynchronizationFailDueToInexistingCatalog() throws Exception
	{
		final Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put(CATALOG_KEY, ID_APPLE.name());
		uriVariables.put(SOURCE_VERSION_KEY, STAGED.getVersion());
		uriVariables.put(TARGET_VERSION_KEY, "CATALOG_NOT_CREATED");

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, uriVariables)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(new SyncJobData(), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors(), hasSize(2));
	}

}
