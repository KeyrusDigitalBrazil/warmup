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


import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.ERROR_CODE;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.oDataPostRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.JsonBuilder;
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class ODataCollectionsPersistenceFacadeIntegrationTest extends ServicelayerTest
{
	private static final String CATALOG_ID = "Default";
	private static final String CATALOG_VERSION = "Staged";
	private static final String MEDIA_100 = "media-100";
	private static final String MEDIA_200 = "media-200";
	private static final String[] METADATA_IMPEX = {
			"INSERT_UPDATE Catalog; id[unique = true];defaultCatalog;",
			"; Default       ; true",
			"; Default-other ; true",
			"INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]; active;",
			"; Default       ; Staged ; true",
			"; Default-other ; Staged ; true",
			"INSERT_UPDATE Media; code[unique = true]; catalogVersion(catalog(id), version)",
			"; media-100 ; Default:Staged",
			"; media-200 ; Default:Staged",
			"INSERT_UPDATE IntegrationObject; code[unique = true];",
			"; TestInboundCompany",
			"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
			"; TestInboundCompany ; Company        ; Company",
			"; TestInboundCompany ; Address        ; Address",
			"; TestInboundCompany ; Media          ; Media",
			"; TestInboundCompany ; MediaFolder    ; MediaFolder",
			"; TestInboundCompany ; CatalogVersion ; CatalogVersion",
			"; TestInboundCompany ; Catalog        ; Catalog",
			"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
			"; TestInboundCompany:Company        ; Id             ; Company:Id             ;                                   ; true",
			"; TestInboundCompany:Company        ; uid            ; Company:uid",
			"; TestInboundCompany:Company        ; addresses      ; Company:addresses      ; TestInboundCompany:Address        ;",
			"; TestInboundCompany:Company        ; medias         ; Company:medias         ; TestInboundCompany:Media          ;",
			"; TestInboundCompany:Address        ; publicKey      ; Address:publicKey      ;                                   ; true",
			"; TestInboundCompany:Address        ; firstname      ; Address:firstname",
			"; TestInboundCompany:Address        ; lastname       ; Address:lastname",
			"; TestInboundCompany:Media          ; code           ; Media:code",
			"; TestInboundCompany:Media          ; altText        ; Media:altText",
			"; TestInboundCompany:Media          ; folder         ; Media:folder           ; TestInboundCompany:MediaFolder",
			"; TestInboundCompany:Media          ; catalogVersion ; Media:catalogVersion   ; TestInboundCompany:CatalogVersion",
			"; TestInboundCompany:MediaFolder    ; qualifier      ; MediaFolder:qualifier",
			"; TestInboundCompany:CatalogVersion ; catalog        ; CatalogVersion:catalog ; TestInboundCompany:Catalog",
			"; TestInboundCompany:CatalogVersion ; version        ; CatalogVersion:version",
			"; TestInboundCompany:Catalog        ; id             ; Catalog:id"
	};

	@Resource
	private ModelService modelService;
	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade;
	private String testCompanyId;

	@Before
	public void setUp() throws ImpExException
	{
		testCompanyId = RandomStringUtils.randomAlphabetic(10);
		importImpEx(METADATA_IMPEX);
	}

	@Test
	public void testCreateCompany_WhenPartOfTrue()
	{
		final String content = companyContent(testCompanyId);
		final ODataContext context = createContext(content);

		final ODataResponse oDataResponse = facade.handlePost(context);

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.CREATED);
		assertTestCompanyExists();
	}

	@Test
	public void testAddUpdateToPartOfCollection_WhenPartOfTrue() // addresses is a partOf=true collection in company
	{
		givenExistCompanyWithoutMedia();

		final String content = companyContent(testCompanyId, "Address1", "Address2");
		final ODataContext context = createContext(content);

		final ODataResponse oDataResponse = facade.handlePost(context);

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.CREATED);
		final CompanyModel company = assertTestCompanyExists();
		assertThat(company.getAddresses()).hasSize(2);
		for (final AddressModel address : company.getAddresses())
		{
			assertThat(address.getOwner()).isEqualToComparingFieldByField(company);
		}
	}

	@Test
	public void testAssignExistItemToCollection_ForNewRootItem_WhenPartOfFalse()
	{
		final ODataContext context = createContext(companyWithMedia(MEDIA_100, CATALOG_ID));

		final ODataResponse oDataResponse = facade.handlePost(context);

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.CREATED);

		final CompanyModel company = assertTestCompanyExists();
		assertThat(company.getMedias()).hasSize(1);
		final MediaModel media = (MediaModel) company.getMedias().toArray()[0];
		assertThat(media.getCode()).isEqualTo(MEDIA_100);
		assertThat(media.getCatalogVersion().getCatalog().getId()).isEqualTo(CATALOG_ID);
		assertThat(media.getCatalogVersion().getVersion()).isEqualTo(CATALOG_VERSION);
		assertThat(media.getOwner()).isNull();
	}

	@Test
	public void testAddExistingItemToCollection_ForExistRootItem_WhenPartOfFalse()
	{
		givenExistCompanyWithoutMedia();

		final ODataContext context = createContext(companyWithMedia(MEDIA_200, CATALOG_ID));

		final ODataResponse oDataResponse = facade.handlePost(context);

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.CREATED);

		final CompanyModel company = assertTestCompanyExists();
		assertThat(company.getMedias()).hasSize(1);
		final MediaModel media = (MediaModel) company.getMedias().toArray()[0];
		assertThat(media.getCode()).isEqualTo(MEDIA_200);
		assertThat(media.getCatalogVersion().getCatalog().getId()).isEqualTo(CATALOG_ID);
		assertThat(media.getCatalogVersion().getVersion()).isEqualTo(CATALOG_VERSION);
		assertThat(media.getOwner()).isNull();
	}

	@Test
	public void testNoInsertNewItemToCollection_ForNewRootItem_WhenPartOfFalse()
	{
		final ODataContext context = createContext(companyWithMedia("media-111", CATALOG_ID));

		final ODataResponse oDataResponse = facade.handlePost(context);

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValue(ERROR_CODE, "missing_nav_property");
	}

	@Test
	public void testInsertNewItemToCollection_ForNewRootItem_WhenAutoCreateIsSetToTrue() throws ImpExException
	{
		// set autoCreate to true for previously defined attribute
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; autoCreate",
				"; TestInboundCompany:Company        ; medias         ; true");
		final String mediaCode = "media-111";

		final ODataContext context = createContext(companyWithMedia(mediaCode, CATALOG_ID));
		final ODataResponse oDataResponse = facade.handlePost(context);

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.CREATED);
		final CompanyModel company = assertTestCompanyExists();
		assertThat(company.getMedias())
				.hasSize(1)
				.extracting("code", "catalogVersion.catalog.id", "catalogVersion.version")
				.containsExactly(tuple(mediaCode, CATALOG_ID, CATALOG_VERSION));
	}

	@Test
	public void testAddNewItemToItemCollection_ForExistRootItem_WhenPartOfFalse()
	{
		givenExistCompany(givenExistMedia());

		final String content = companyWithMedia("media-2", "Default-other");

		final ODataContext context = createContext(content);

		final ODataResponse oDataResponse = facade.handlePost(context);

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValue(ERROR_CODE, "missing_nav_property");

		final CompanyModel company = assertTestCompanyExists();
		assertThat(company.getMedias()).hasSize(1);
		final MediaModel media = (MediaModel) company.getMedias().toArray()[0];
		assertThat(media.getCatalogVersion().getCatalog().getId()).isEqualTo(CATALOG_ID);
		assertThat(media.getCatalogVersion().getVersion()).isEqualTo(CATALOG_VERSION);
	}

	@Test
	public void testCreatesNewDeeplyNestedObjects_WhenAutoCreateIsSetToTrue() throws ImpExException
	{
		// set autoCreate to true for previously defined attribute
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; autoCreate",
				"; TestInboundCompany:Company ; medias ; true",
				"; TestInboundCompany:Media   ; folder ; true");

		final String mediaCode = "nestedTestMedia";
		final String folder = "autoCreateFolder";
		final String content = companyWithMedia(mediaContent(mediaCode, CATALOG_ID)
				.withField("folder", JsonBuilder.json().withField("qualifier", folder)));
		final ODataContext context = createContext(content);
		final ODataResponse oDataResponse = facade.handlePost(context);

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.CREATED);
		final CompanyModel company = assertTestCompanyExists();
		assertThat(company.getMedias())
				.hasSize(1)
				.extracting("code", "folder.qualifier")
				.containsExactly(tuple(mediaCode, folder));
	}

	@Test
	public void testUpdateExistingItemOfCollectionItemAttributes_ForNewRootItem_WhenPartOfFalse()
	{
		final JsonBuilder mediaContent = mediaContent(MEDIA_100, CATALOG_ID, "updated-100");
		final String content = companyWithMedia(mediaContent);

		final ODataContext context = createContext(content);
		final ODataResponse oDataResponse = facade.handlePost(context);

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.CREATED);

		final CompanyModel company = assertTestCompanyExists();
		assertThat(company.getMedias()).hasSize(1);
		final MediaModel media = (MediaModel) company.getMedias().toArray()[0];
		assertThat(media.getCode()).isEqualTo(MEDIA_100);
		assertThat(media.getCatalogVersion().getCatalog().getId()).isEqualTo(CATALOG_ID);
		assertThat(media.getCatalogVersion().getVersion()).isEqualTo(CATALOG_VERSION);
		assertThat(media.getAltText()).isEqualTo("updated-100");
	}

	@Test
	public void testUpdateExistingItemOfCollectionItemAttributes_ForExistRootItem_WhenPartOfFalse()
	{
		givenExistCompanyWithoutMedia();
		final JsonBuilder mediaContent = mediaContent(MEDIA_200, CATALOG_ID, "updated-200");
		final String content = companyWithMedia(mediaContent);

		final ODataContext context = createContext(content);
		final ODataResponse oDataResponse = facade.handlePost(context);

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.CREATED);

		final CompanyModel company = assertTestCompanyExists();
		assertThat(company.getMedias()).hasSize(1);
		final MediaModel media = (MediaModel) company.getMedias().toArray()[0];
		assertThat(media.getCode()).isEqualTo(MEDIA_200);
		assertThat(media.getCatalogVersion().getCatalog().getId()).isEqualTo(CATALOG_ID);
		assertThat(media.getCatalogVersion().getVersion()).isEqualTo(CATALOG_VERSION);
		assertThat(media.getAltText()).isEqualTo("updated-200");
	}

	@Test
	public void testPersistEntity__Collection_AttributeNotOfCollectionTypeModel() throws ImpExException
	{
		importImpEx("INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; ProductWithKeywords",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; ProductWithKeywords ; Product        ; Product",
				"; ProductWithKeywords ; Catalog        ; Catalog",
				"; ProductWithKeywords ; CatalogVersion ; CatalogVersion",
				"; ProductWithKeywords ; Keyword        ; Keyword",
				"; ProductWithKeywords ; Language       ; Language",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; ProductWithKeywords:Product        ; code            ; Product:code            ;",
				"; ProductWithKeywords:Product        ; catalogVersion  ; Product:catalogVersion  ; ProductWithKeywords:CatalogVersion ;",
				"; ProductWithKeywords:Product        ; name            ; Product:name            ;",
				"; ProductWithKeywords:Product        ; keywords        ; Product:keywords        ; ProductWithKeywords:Keyword        ;",

				"; ProductWithKeywords:Catalog        ; id              ; Catalog:id              ;",

				"; ProductWithKeywords:CatalogVersion ; catalog         ; CatalogVersion:catalog  ; ProductWithKeywords:Catalog        ;",
				"; ProductWithKeywords:CatalogVersion ; version         ; CatalogVersion:version  ;",
				"; ProductWithKeywords:CatalogVersion ; active          ; CatalogVersion:active   ;",

				"; ProductWithKeywords:Keyword        ; keyword         ; Keyword:keyword         ;",
				"; ProductWithKeywords:Keyword        ; language        ; Keyword:language        ; ProductWithKeywords:Language       ;",

				"; ProductWithKeywords:Language       ; isocode         ; Language:isocode        ;"
		);

		importImpEx(
				"$catalogVersion = Default:Staged",
				"INSERT_UPDATE Catalog; id[unique = true]; name[lang = en]; defaultCatalog;",
				"; Default ; Default ; true",
				"INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]; active;",
				"; Default ; Staged ; true",
				"INSERT_UPDATE Language; isocode[unique = true]",
				"; en",
				"INSERT_UPDATE Keyword; keyword[unique = true]; language(isocode)",
				"; Test_Keyword ; en");

		final String content = JsonBuilder.json()
				.withCode("test_product_1")
				.withField("name", "product 1 name")
				.withField("catalogVersion", JsonBuilder.json()
						.withField("catalog", JsonBuilder.json().withId("Default"))
						.withField("version", "Staged"))
				.withFieldValues("keywords", JsonBuilder.json()
						.withField("language", JsonBuilder.json().withField("isocode", "en"))
						.withField("keyword", "Test_Keyword"))
				.build();

		final ODataRequest request = oDataPostRequest("ProductWithKeywords", "Products", content, Locale.ENGLISH, APPLICATION_JSON_VALUE);
		final ODataResponse oDataResponse = facade.handlePost(ODataFacadeTestUtils.createContext(request));

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.CREATED);
	}

	private MediaModel givenExistMedia()
	{
		final MediaModel media = modelService.create(MediaModel.class);
		media.setCode(("media-1"));
		media.setCatalogVersion(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION));
		return media;
	}

	private ODataContext createContext(final String content)
	{
		final ODataRequest request = oDataPostRequest("TestInboundCompany", "Companies", content, Locale.ENGLISH, APPLICATION_JSON_VALUE);
		return ODataFacadeTestUtils.createContext(request);
	}

	private void givenExistCompanyWithoutMedia()
	{
		givenExistCompany();
	}

	private void givenExistCompany(final MediaModel... medias)
	{
		final CompanyModel companyModel = modelService.create(CompanyModel.class);
		companyModel.setId(testCompanyId);
		companyModel.setUid(testCompanyId);
		companyModel.setMedias(Arrays.asList(medias));

		modelService.save(companyModel);
	}

	private String companyContent(final String id, final String... addresses)
	{
		final Collection<JsonBuilder> addressesContent = Stream.of(addresses)
				.map(addr -> JsonBuilder.json().withField("publicKey", addr))
				.collect(Collectors.toSet());
		final JsonBuilder companyContent = JsonBuilder.json()
				.withField("Id", id)
				.withField("uid", id);
		return addresses.length > 0
				? companyContent.withField("addresses", addressesContent).build()
				: companyContent.build();
	}

	private String companyWithMedia(final String mediaCode, final String catalogId)
	{
		return companyWithMedia(mediaContent(mediaCode, catalogId));
	}

	private String companyWithMedia(final JsonBuilder media)
	{
		return JsonBuilder.json()
				.withField("Id", testCompanyId)
				.withField("uid", testCompanyId)
				.withFieldValues("medias", media)
				.build();
	}

	private JsonBuilder mediaContent(final String code, final String catalogId)
	{
		return JsonBuilder.json()
				.withCode(code)
				.withField("catalogVersion", catalogVersionContent(catalogId));
	}

	private JsonBuilder mediaContent(final String code, final String catalogId, final String altText)
	{
		return mediaContent(code, catalogId).withField("altText", altText);
	}

	private JsonBuilder catalogVersionContent(final String catalogId)
	{
		return JsonBuilder.json()
				.withField("catalog", JsonBuilder.json().withId(catalogId))
				.withField("version", CATALOG_VERSION);
	}

	private CompanyModel assertTestCompanyExists()
	{
		final CompanyModel companyModel = new CompanyModel();
		companyModel.setId(testCompanyId);
		final CompanyModel persistedModel = assertModelExists(companyModel);
		assertThat(persistedModel.getId()).isEqualTo(testCompanyId);
		return persistedModel;
	}
}
