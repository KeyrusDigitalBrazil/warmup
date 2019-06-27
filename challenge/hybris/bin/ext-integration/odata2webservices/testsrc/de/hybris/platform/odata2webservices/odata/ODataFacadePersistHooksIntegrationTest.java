/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2webservices.odata;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelDoesNotExist;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.assertBadRequestWithErrorCode;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.postRequestBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.JsonBuilder;
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions;
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder;
import de.hybris.platform.odata2webservices.odata.persistence.hooks.SamplePostPersistHook;
import de.hybris.platform.odata2webservices.odata.persistence.hooks.SamplePrePersistHook;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.AppendSpringConfiguration;

import java.util.Locale;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@IntegrationTest
@AppendSpringConfiguration("classpath:/test/odata2webservices-test-beans-spring.xml")
public class ODataFacadePersistHooksIntegrationTest extends ServicelayerTest
{
	private static final String PRE_PERSIST_HOOK = "Pre-Persist-Hook";
	private static final String POST_PERSIST_HOOK = "Post-Persist-Hook";
	private static final String NON_EXISTING_HOOK_NAME = "Non-Existing-Hook";
	private static final String PRE_HOOK_NAME = "samplePrePersistHook";
	private static final String POST_HOOK_NAME = "samplePostPersistHook";
	private static final String SERVICE_NAME = "Inbound";
	private static final String ENTITY_SET = "Catalogs";

	@Rule
	@Resource(name = PRE_HOOK_NAME)
	public SamplePrePersistHook samplePrePersistHook;
	@Rule
	@Resource(name = POST_HOOK_NAME)
	public SamplePostPersistHook samplePostPersistHook;

	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade;

	@Before
	public void setUp() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; " + SERVICE_NAME,
				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique=true]; code[unique = true]; type(code)",
				"; Inbound ; Catalog        ; Catalog",
				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; Inbound:Catalog        ; id               ; Catalog:id",
				"; Inbound:Catalog        ; name             ; Catalog:name",
				"INSERT_UPDATE Language;isocode[unique=true];name[lang=en]",
				";de;German");
	}

	@Test
	public void testHooksNotSpecified()
	{
		final ODataContext context = createContext(jsonCatalog("Some-Catalog"));

		facade.handlePost(context);

		assertThat(samplePrePersistHook.isExecuted()).isFalse();
		assertThat(samplePostPersistHook.isExecuted()).isFalse();
	}

	@Test
	public void testExecutesPrePersistHook()
	{
		samplePrePersistHook.givenDoesInExecute(it -> {
			((CatalogModel)it).setId("Hook-Id");
			return Optional.of(it);
		});
		final ODataContext context = createContextWithHeader(jsonCatalog("Submitted-Id"), PRE_PERSIST_HOOK, PRE_HOOK_NAME);

		facade.handlePost(context);

		assertModelDoesNotExist(catalogModel("Submitted-Id"));
		assertModelExists(catalogModel("Hook-Id"));
	}

	@Test
	public void testExecutesPostPersistHook()
	{
		final ODataContext context = createContextWithHeader(jsonCatalog("Some-Catalog"), POST_PERSIST_HOOK, POST_HOOK_NAME);

		facade.handlePost(context);

		assertThat(samplePostPersistHook.isExecuted()).isTrue();
	}

	@Test
	public void testPrePersistHookNotFound()
	{
		final ODataContext context = createContextWithHeader(jsonCatalog("MyCatalog"), PRE_PERSIST_HOOK, NON_EXISTING_HOOK_NAME);

		final ODataResponse response = facade.handlePost(context);

		assertBadRequestWithErrorCode("hook_not_found", response);
		assertThat(samplePostPersistHook.isExecuted()).isFalse();
		assertModelDoesNotExist(catalogModel("MyCatalog"));
	}

	@Test
	public void testPostPersistHookNotFound()
	{
		final ODataContext context = createContextWithHeader(jsonCatalog("MyCatalog"), POST_PERSIST_HOOK, NON_EXISTING_HOOK_NAME);

		final ODataResponse response = facade.handlePost(context);

		assertBadRequestWithErrorCode("hook_not_found", response);
		assertModelDoesNotExist(catalogModel("MyCatalog"));
	}

	@Test
	public void testRequestBodyIsReturnedWhenPrePersistHookFiltersTheSubmittedNewItemOut()
	{
		samplePrePersistHook.givenDoesInExecute(it -> Optional.empty());
		final ODataContext context = createContextWithBothHooks(jsonCatalog("Excluded"));

		final ODataResponse response = facade.handlePost(context);

		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue("d.id", "Excluded");
		assertThat(samplePostPersistHook.isExecuted()).isFalse();
		assertModelDoesNotExist(catalogModel("Excluded"));
	}

	@Test
	public void testRequestedLocaleForExistingItemIsReturnedWhenPrePersistHookFiltersTheSubmittedItemOut() throws ImpExException
	{
		final String catalog = "Existing-ReadOnly";
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Catalog; id[unique = true]; name[lang = 'de']",
				"; " + catalog + " ; Katalog");
		samplePrePersistHook.givenDoesInExecute(it -> Optional.empty());
		final ODataContext context = ODataFacadeTestUtils.createContext(postRequest()
				.withHeader(PRE_PERSIST_HOOK, PRE_HOOK_NAME)
				.withAcceptLanguage(Locale.GERMAN)
				.withBody(jsonCatalog(catalog)));

		final ODataResponse response = facade.handlePost(context);

		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue("d.id", catalog)
				.hasPathWithValue("d.name", "Katalog");
	}

	@Test
	public void testNoItemPersistedWhenPrePersistHookThrowsError()
	{
		samplePrePersistHook.givenDoesInExecute(it -> {throw new RuntimeException("Crashing the hook");});
		final ODataContext context = createContextWithBothHooks(jsonCatalog("Crashing"));

		final ODataResponse response = facade.handlePost(context);

		assertBadRequestWithErrorCode("pre_persist_error", response);
		assertModelDoesNotExist(catalogModel("Crashing"));
	}

	@Test
	public void testtNoItemPersistedWhenPostPersistHookThrowsError()
	{
		samplePostPersistHook.givenDoesInExecute(it -> {throw new RuntimeException("Crashing the hook");});
		final ODataContext context = createContextWithHeader(jsonCatalog("Crashing"), POST_PERSIST_HOOK, POST_HOOK_NAME);

		final ODataResponse response = facade.handlePost(context);

		assertBadRequestWithErrorCode("post_persist_error", response);
		assertModelDoesNotExist(catalogModel("Crashing"));
	}

	private String jsonCatalog(final String id)
	{
		return JsonBuilder.json().withId(id).build();
	}

	private ItemModel catalogModel(final String id)
	{
		final CatalogModel model = new CatalogModel();
		model.setId(id);
		return model;
	}

	private ODataContext createContextWithBothHooks(final String content)
	{
		final ODataRequest req = postRequest()
				.withHeader(PRE_PERSIST_HOOK, PRE_HOOK_NAME)
				.withHeader(POST_PERSIST_HOOK, POST_HOOK_NAME)
				.withBody(content)
				.build();
		return ODataFacadeTestUtils.createContext(req);
	}

	private ODataContext createContextWithHeader(final String content, final String headerName, final String headerValue)
	{
		final ODataRequest request = postRequest()
				.withHeader(headerName, headerValue)
				.withBody(content)
				.build();
		return ODataFacadeTestUtils.createContext(request);
	}

	private ODataContext createContext(final String content)
	{
		final ODataRequest request = postRequest().withBody(content).build();
		return ODataFacadeTestUtils.createContext(request);
	}

	private ODataRequestBuilder postRequest()
	{
		return postRequestBuilder(SERVICE_NAME, ENTITY_SET, APPLICATION_JSON_VALUE);
	}
}
