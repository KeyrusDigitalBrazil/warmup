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

import static de.hybris.platform.odata2services.odata.content.ODataBatchBuilder.batchBuilder;
import static de.hybris.platform.odata2services.odata.content.ODataChangeSetBuilder.changeSetBuilder;
import static de.hybris.platform.odata2services.odata.content.ODataJsonProductBuilder.product;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.PRODUCTS_ENTITYSET;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.SERVICE_NAME;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.batchODataPostRequest;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.handleRequest;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.oDataPostRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_ATOM_XML_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.inboundservices.model.InboundRequestModel;
import de.hybris.platform.inboundservices.util.InboundMonitoringRule;
import de.hybris.platform.inboundservices.util.InboundRequestPersistenceContext;
import de.hybris.platform.integrationservices.enums.IntegrationRequestStatus;
import de.hybris.platform.integrationservices.model.IntegrationApiMediaModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.odata2services.config.ODataServicesConfiguration;
import de.hybris.platform.odata2services.odata.content.ODataAtomProductBuilder;
import de.hybris.platform.odata2services.odata.content.ODataJsonProductBuilder;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for verifying the feature of persisting the incoming POST request and its body.
 */
@IntegrationTest
public class ODataFacadeInboundMonitoringIntegrationTest extends ServicelayerTest
{
	private static final String[] metadata = {
			"INSERT_UPDATE IntegrationObject; code[unique = true];",
			"; MyProduct",
			"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique=true]; code[unique = true]; type(code)",
			"; MyProduct ; Product        ; Product",
			"; MyProduct ; Catalog        ; Catalog",
			"; MyProduct ; CatalogVersion ; CatalogVersion",
			"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
			"; MyProduct:Product        ; code             ; Product:code             ;",
			"; MyProduct:Product        ; name             ; Product:name             ;",
			"; MyProduct:Product        ; catalogVersion   ; Product:catalogVersion   ; MyProduct:CatalogVersion",
			"; MyProduct:Catalog        ; id               ; Catalog:id               ;",
			"; MyProduct:CatalogVersion ; catalog          ; CatalogVersion:catalog   ; MyProduct:Catalog",
			"; MyProduct:CatalogVersion ; version          ; CatalogVersion:version   ;",
			"INSERT_UPDATE Catalog;id[unique=true];name[lang=en];defaultCatalog;",
			";Default;Default;true",
			"INSERT_UPDATE CatalogVersion; catalog(id)[unique=true]; version[unique=true];active;",
			";Default;Staged;true",
			"INSERT_UPDATE Language;isocode[unique=true];name[lang=de];name[lang=en]",
			";de;Deutsch;German"
	};
	private static final String STATUS = "status";

	@Rule
	public InboundRequestPersistenceContext requestPersistenceContext = InboundRequestPersistenceContext.create();
	@Rule
	public InboundMonitoringRule monitoring = InboundMonitoringRule.enabled();

	@Resource(name = "oDataServicesConfiguration")
	private ODataServicesConfiguration configuration;
	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade;

	@Resource
	private ConfigurationService configurationService;

	@Before
	public void setUp() throws ImpExException
	{
		// create product metadata
		IntegrationTestUtil.importImpEx(metadata);
	}

	@Test
	public void testResponseHasErrors() throws IOException
	{
		final String content = "<invalid_content />";

		final ODataResponse response = handleRequest(facade, oDataPostRequest(SERVICE_NAME, PRODUCTS_ENTITYSET, content, APPLICATION_JSON_VALUE));

		assertThat(response).hasFieldOrPropertyWithValue(STATUS, HttpStatusCodes.BAD_REQUEST);
		final InboundRequestModel request = assertThatInboundRequestIsPresent(IntegrationRequestStatus.ERROR);
		assertThat(getMediaContent(request.getPayload())).isEqualTo(content);
		assertThat(request.getErrors()).hasSize(1);
	}

	@Test
	public void testResponseIsSuccessful_successRetentionTrue() throws IOException
	{
		configurationService.getConfiguration().setProperty("inboundservices.monitoring.success.payload.retention", "true");
		final String content = product().build();

		final ODataResponse response = handleRequest(facade, oDataPostRequest(SERVICE_NAME, PRODUCTS_ENTITYSET, content, APPLICATION_JSON_VALUE));

		assertThat(response).hasFieldOrPropertyWithValue(STATUS, HttpStatusCodes.CREATED);
		final InboundRequestModel request = assertThatInboundRequestIsPresent(IntegrationRequestStatus.SUCCESS);
		assertThat(getMediaContent(request.getPayload())).isEqualTo(content);
		assertThat(request.getErrors()).isEmpty();
	}

	@Test
	public void testResponseIsSuccessful_successRetentionTrue_AtomRequest() throws IOException
	{
		configurationService.getConfiguration().setProperty("inboundservices.monitoring.success.payload.retention", "true");
		final String content = ODataAtomProductBuilder.product().build();

		final ODataResponse response = handleRequest(facade, oDataPostRequest(SERVICE_NAME, PRODUCTS_ENTITYSET, content, APPLICATION_ATOM_XML_VALUE));

		assertThat(response).hasFieldOrPropertyWithValue(STATUS, HttpStatusCodes.CREATED);
		final InboundRequestModel request = assertThatInboundRequestIsPresent(IntegrationRequestStatus.SUCCESS);
		assertThat(getMediaContent(request.getPayload())).isEqualTo(content);
		assertThat(request.getErrors()).isEmpty();
	}

	@Test
	public void testResponseIsSuccessful_successRetentionFalse()
	{
		configurationService.getConfiguration().setProperty("inboundservices.monitoring.success.payload.retention", "false");
		final String content = product().build();

		final ODataResponse response = handleRequest(facade, oDataPostRequest(SERVICE_NAME, PRODUCTS_ENTITYSET, content, APPLICATION_JSON_VALUE));

		assertThat(response).hasFieldOrPropertyWithValue(STATUS, HttpStatusCodes.CREATED);
		final InboundRequestModel request = assertThatInboundRequestIsPresent(IntegrationRequestStatus.SUCCESS);
		assertThat(request.getPayload()).isNull();
		assertThat(request.getErrors()).isEmpty();
	}

	@Test
	public void testPayloadAndInboundRequestAndErrorsNotPersistedWhenMonitoringIsTurnedOff()
	{
		requestPersistenceContext.turnMonitoringOff();

		final String content = "{ \"code\": \"InvalidProduct\" }";
		handleRequest(facade, oDataPostRequest(SERVICE_NAME, PRODUCTS_ENTITYSET, content, APPLICATION_JSON_VALUE));

		assertThat(requestPersistenceContext.getAllMedia()).isEmpty();
		assertThat(requestPersistenceContext.searchAllInboundRequest()).isEmpty();
		assertThat(requestPersistenceContext.searchAllInboundRequestErrors()).isEmpty();
	}

	@Test
	public void testCreatesInboundRequestForBatchWithMultipleChangeSets_successRetentionTrue() throws IOException
	{
		configurationService.getConfiguration().setProperty("inboundservices.monitoring.success.payload.retention", "true");

		final ODataJsonProductBuilder englishProduct = product().withName("a product");
		final ODataJsonProductBuilder germanProduct = product().withName("ein Produkt");

		final String content = batchBuilder()
				.withChangeSet(changeSetBuilder()
						.withUri("Products")
						.withPart(Locale.ENGLISH, englishProduct)
						.withPart(Locale.GERMAN, germanProduct))
				.build();

		handleRequest(facade, batchODataPostRequest(SERVICE_NAME, content));

		final InboundRequestModel request = assertThatInboundRequestIsPresent(IntegrationRequestStatus.SUCCESS);
		assertThat(getMediaContent(request.getPayload())).isEqualToIgnoringWhitespace(englishProduct.build() + germanProduct.build());
		assertThat(request.getErrors()).isEmpty();
	}

	@Test
	public void testCreatesInboundRequestForBatchWithMultipleChangeSets_successRetentionFalse()
	{
		configurationService.getConfiguration().setProperty("inboundservices.monitoring.success.payload.retention", "false");

		final ODataJsonProductBuilder englishProduct = product().withName("a product");
		final ODataJsonProductBuilder germanProduct = product().withName("ein Produkt");

		final String content = batchBuilder()
				.withChangeSet(changeSetBuilder()
						.withUri("Products")
						.withPart(Locale.ENGLISH, englishProduct)
						.withPart(Locale.GERMAN, germanProduct))
				.build();

		handleRequest(facade, batchODataPostRequest(SERVICE_NAME, content));

		final InboundRequestModel request = assertThatInboundRequestIsPresent(IntegrationRequestStatus.SUCCESS);
		assertThat(request.getPayload()).isNull();
		assertThat(request.getErrors()).isEmpty();
	}

	@Test
	public void testCreatesInboundRequestsForMultipleBatches_payloadRetention() throws IOException
	{
		configurationService.getConfiguration().setProperty("inboundservices.monitoring.success.payload.retention", "true");
		configurationService.getConfiguration().setProperty("inboundservices.monitoring.error.payload.retention", "true");

		final String content = batchBuilder()
				.withChangeSet(changeSetBuilder()
						.withUri("Products")
						.withPart(Locale.GERMAN, product().withCode("Prod-1").withName("ein gutes Produkt"))
						.withPart(Locale.ENGLISH, product().withCode("Prod-1").withName("invalid product").withCatalog(null)))
				.withChangeSet(changeSetBuilder()
						.withUri("Products")
						.withPart(Locale.ENGLISH, product().withCode("Prod-2").withName("a product")))
				.build();

		handleRequest(facade, batchODataPostRequest(SERVICE_NAME, content));

		final Collection<InboundRequestModel> requests = requestPersistenceContext.searchAllInboundRequest();
		assertThat(requests).hasSize(2);

		final InboundRequestModel failedRequest = findInboundRequestWithStatus(requests, IntegrationRequestStatus.ERROR);
		assertThat(failedRequest.getErrors()).isNotEmpty();
		assertThat(getMediaContent(failedRequest.getPayload()))
				.describedAs("The payload was not split by batches correctly")
				.contains("Prod-1")
				.contains("invalid product")
				.contains("ein gutes Produkt")
				.doesNotContain("Prod-2")
				.doesNotContain("a product");

		final InboundRequestModel successRequest = findInboundRequestWithStatus(requests, IntegrationRequestStatus.SUCCESS);
		assertThat(successRequest.getIntegrationKey()).isEqualTo("Staged|Default|Prod-2");
		assertThat(successRequest.getErrors()).isEmpty();
		assertThat(getMediaContent(successRequest.getPayload()))
				.describedAs("The payload was not split by batches correctly")
				.contains("Prod-2")
				.contains("a product")
				.doesNotContain("Prod-1")
				.doesNotContain("ein gutes Produkt")
				.doesNotContain("invalid product");
	}

	@Test
	public void testCreatesInboundRequestsForMultipleBatches_noPayloadRetention()
	{
		configurationService.getConfiguration().setProperty("inboundservices.monitoring.success.payload.retention", "false");
		configurationService.getConfiguration().setProperty("inboundservices.monitoring.error.payload.retention", "false");

		final String content = batchBuilder()
				.withChangeSet(changeSetBuilder()
						.withUri("Products")
						.withPart(Locale.GERMAN, product().withCode("Prod-1").withName("ein gutes Produkt"))
						.withPart(Locale.ENGLISH, product().withCode("Prod-1").withName("invalid product").withCatalog(null)))
				.withChangeSet(changeSetBuilder()
						.withUri("Products")
						.withPart(Locale.ENGLISH, product().withCode("Prod-2").withName("a product")))
				.build();

		handleRequest(facade, batchODataPostRequest(SERVICE_NAME, content));

		final Collection<InboundRequestModel> requests = requestPersistenceContext.searchAllInboundRequest();
		assertThat(requests).hasSize(2);

		final InboundRequestModel failedRequest = findInboundRequestWithStatus(requests, IntegrationRequestStatus.ERROR);
		assertThat(failedRequest.getErrors()).isNotEmpty();
		assertThat(failedRequest.getPayload()).isNull();

		final InboundRequestModel successRequest = findInboundRequestWithStatus(requests, IntegrationRequestStatus.SUCCESS);
		assertThat(successRequest.getIntegrationKey()).isEqualTo("Staged|Default|Prod-2");
		assertThat(successRequest.getErrors()).isEmpty();
		assertThat(successRequest.getPayload()).isNull();
	}

	@Test
	public void testNumberOfBatchesExceedsTheChangeSetLimit()
	{
		configuration.setBatchLimit(4);
		final String content = batchBuilder()
				.withChangeSet(changeSetBuilder().withUri("Products").withPart(Locale.ENGLISH, product()))
				.withChangeSet(changeSetBuilder().withUri("Products").withPart(Locale.ENGLISH, product()))
				.withChangeSet(changeSetBuilder().withUri("Products").withPart(Locale.ENGLISH, product()))
				.withChangeSet(changeSetBuilder().withUri("Products").withPart(Locale.ENGLISH, product()))
				.withChangeSet(changeSetBuilder().withUri("Products").withPart(Locale.ENGLISH, product()))
				.build();

		handleRequest(facade, batchODataPostRequest(SERVICE_NAME, content));

		final Collection<InboundRequestModel> requests = requestPersistenceContext.searchAllInboundRequest();
		assertThat(requests).hasSize(1);

		final InboundRequestModel request = findInboundRequestWithStatus(requests, IntegrationRequestStatus.ERROR);
		assertThat(request.getErrors()).isNotEmpty();
		assertThat(request.getPayload()).isNull();
	}

	private String getMediaContent(final IntegrationApiMediaModel payload) throws IOException
	{
		return requestPersistenceContext.getMediaContentAsString(payload);
	}

	private InboundRequestModel assertThatInboundRequestIsPresent(final IntegrationRequestStatus status)
	{
		final Collection<?> inboundRequestModels = requestPersistenceContext.searchAllInboundRequest();
		assertThat(inboundRequestModels).isNotNull().hasSize(1);
		final InboundRequestModel request = (InboundRequestModel) inboundRequestModels.iterator().next();
		assertThat(request)
				.isNotNull()
				.hasFieldOrPropertyWithValue(STATUS, status);
		return request;
	}

	private InboundRequestModel findInboundRequestWithStatus(final Collection<InboundRequestModel> requests, final IntegrationRequestStatus status)
	{
		return requests.stream()
				.filter(r -> r.getStatus().equals(status))
				.findAny()
				.orElse(null);
	}
}
