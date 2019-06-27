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
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.ERROR_CODE;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.createContext;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.oDataPostRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.impex.model.cronjob.ImpExImportCronJobModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.JsonBuilder;
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class ODataAdvancedPersistenceFacadeIntegrationTest extends ServicelayerTest
{
	private static final String SERVICE = "TestImpExImportCronJob";
	private static final String SERVICE_IMPEXIMPORTCRONJOBS = "ImpExImportCronJobs";
	private static final String SERVICE_IMPEXMEDIAS = "ImpExMedias";
	private static final String CATALOG_ID = "Default";
	private static final String CATALOG_VERSION = "Staged";
	private static final String CRON_EXPRESSION = "2 2 2 1/1 * ? *";
	private static final String ATTR_WORK_MEDIA = "workMedia";
	private static final String PATH_CODE = "d.code";

	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade;

	@Before
	public void setUp() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"$testCronExpressKey = " + CRON_EXPRESSION,
				"INSERT_UPDATE Catalog; id[unique = true]; defaultCatalog;",
				"; " + CATALOG_ID + " ; true",

				"INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]; active;",
				"; Default ; Staged ; true",

				"INSERT_UPDATE BatchJob; code[unique = true];",
				"; test-BatchJob",

				"INSERT_UPDATE CSVExportStep; code[unique = true]; sequenceNumber; batchJob(code)",
				"; test-CSVExportStep ; 1 ; test-BatchJob",

				"INSERT_UPDATE ImpExImportJob; code[unique = true]",
				"; test-ImpExImportJob        ;",
				"; test-ImpExImportJob-update ;",

				"INSERT_UPDATE ImpExImportCronJob; code[unique = true]; job(code)",
				"; Test-ImpExImportCronJob-transaction ; test-ImpExImportJob",

				"INSERT_UPDATE Trigger; cronExpression[unique = true]; cronJob(code)[unique = true]; day[unique = true];",
				"; $testCronExpressKey ; Test-ImpExImportCronJob-transaction-update ; 1",

				"INSERT_UPDATE ImpExImportCronJob; code[unique = true]; job(code); triggers(cronExpression)",
				"; Test-ImpExImportCronJob-transaction-update ; test-ImpExImportJob-update ; $testCronExpressKey",

				"INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)",
				"; TestImpExImportCronJob ; INBOUND",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; TestImpExImportCronJob ; ImpExImportCronJob ; ImpExImportCronJob",
				"; TestImpExImportCronJob ; ImpExMedia         ; ImpExMedia",
				"; TestImpExImportCronJob ; MediaFolder        ; MediaFolder",
				"; TestImpExImportCronJob ; Catalog            ; Catalog",
				"; TestImpExImportCronJob ; CatalogVersion     ; CatalogVersion",
				"; TestImpExImportCronJob ; Step               ; Step",
				"; TestImpExImportCronJob ; Trigger            ; Trigger",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code)",
				"; TestImpExImportCronJob:ImpExImportCronJob ; code           ; ImpExImportCronJob:code",
				"; TestImpExImportCronJob:ImpExImportCronJob ; workMedia      ; ImpExImportCronJob:workMedia   ; TestImpExImportCronJob:ImpExMedia",
				"; TestImpExImportCronJob:ImpExImportCronJob ; currentStep    ; ImpExImportCronJob:currentStep ; TestImpExImportCronJob:Step",
				"; TestImpExImportCronJob:ImpExImportCronJob ; triggers       ; ImpExImportCronJob:triggers    ; TestImpExImportCronJob:Trigger",
				"; TestImpExImportCronJob:ImpExMedia         ; code           ; ImpExMedia:code",
				"; TestImpExImportCronJob:ImpExMedia         ; folder         ; ImpExMedia:folder              ; TestImpExImportCronJob:MediaFolder",
				"; TestImpExImportCronJob:ImpExMedia         ; catalogVersion ; ImpExMedia:catalogVersion      ; TestImpExImportCronJob:CatalogVersion",
				"; TestImpExImportCronJob:MediaFolder        ; qualifier      ; MediaFolder:qualifier",
				"; TestImpExImportCronJob:Step               ; code           ; Step:code",
				"; TestImpExImportCronJob:Catalog            ; id             ; Catalog:id",
				"; TestImpExImportCronJob:CatalogVersion     ; catalog        ; CatalogVersion:catalog         ; TestImpExImportCronJob:Catalog",
				"; TestImpExImportCronJob:CatalogVersion     ; version        ; CatalogVersion:version",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique",
				"; TestImpExImportCronJob:Trigger ; cronExpression ; Trigger:cronExpression ;                                           ; true",
				"; TestImpExImportCronJob:Trigger ; cronJob        ; Trigger:cronJob        ; TestImpExImportCronJob:ImpExImportCronJob ; true",
				"; TestImpExImportCronJob:Trigger ; day            ; Trigger:day            ;  											; false");
	}

	@Test
	public void testPersistEntity_ImpExImportCronJobModel_NewEntryEntityShouldCreate()
	{
		final String testModelCode = "testCode";
		final String testWorkMediaCode = "testWorkMediaCode";
		final ImpExImportCronJobModel cronJobModel = cronJob(testModelCode);
		assertModelDoesNotExist(cronJobModel);

		final String content = JsonBuilder.json()
				.withCode(testModelCode)
				.withField(ATTR_WORK_MEDIA, impExMediaContent(testWorkMediaCode, CATALOG_ID, CATALOG_VERSION))
				.build();

		final ODataRequest request = oDataPostRequest(SERVICE, SERVICE_IMPEXIMPORTCRONJOBS, content, Locale.ENGLISH, APPLICATION_JSON_VALUE);
		final ODataResponse oDataResponse = facade.handlePost(createContext(request));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue(PATH_CODE, testModelCode);

		final ImpExImportCronJobModel persistedModel = assertModelExists(cronJobModel);

		assertThat(persistedModel.getCode()).isEqualTo(testModelCode);
		assertThat(persistedModel.getWorkMedia().getCode()).isEqualTo(testWorkMediaCode);
		assertThat(persistedModel.getWorkMedia().getCatalogVersion().getVersion()).isEqualTo(CATALOG_VERSION);
		assertThat(persistedModel.getWorkMedia().getCatalogVersion().getCatalog().getId()).isEqualTo(CATALOG_ID);
		assertThat(persistedModel.getWorkMedia().getOwner()).isEqualToComparingFieldByField(persistedModel);
	}

	@Test
	public void testPersistEntity_ImpExImportCronJobModel_collectionEntryShouldUpdated()
	{
		final String testCronJobCode = "Test-ImpExImportCronJob-transaction-update";

		final String content = JsonBuilder.json()
				.withCode(testCronJobCode)
				.withFieldValues("triggers", JsonBuilder.json()
						.withField("cronExpression", CRON_EXPRESSION)
						.withField("cronJob", JsonBuilder.json().withCode(testCronJobCode))
						.withField("day", 2))
				.build();

		final ODataRequest request = oDataPostRequest(SERVICE, SERVICE_IMPEXIMPORTCRONJOBS, content, Locale.ENGLISH, APPLICATION_JSON_VALUE);
		final ODataResponse oDataResponse = facade.handlePost(createContext(request));

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.CREATED);

		final TriggerModel testTriggerModel = new TriggerModel();
		testTriggerModel.setCronExpression(CRON_EXPRESSION);
		final TriggerModel trigger = assertModelExists(testTriggerModel);
		assertThat(trigger.getCronExpression()).isEqualTo(CRON_EXPRESSION);
		assertThat(trigger.getDay()).isEqualTo(2);
	}

	@Test
	public void testPersistEntity_ImpExMediaModel_WhenIsPartOfFalse_NoNewEntryEntityShouldCreate()
	{
		final ImpExMediaModel impExMediaModel = impExMediaModel("test-workMedia");

		assertModelDoesNotExist(impExMediaModel);

		final String content = impExMediaContent("test-workMedia", "default-new", "per-staged").build();

		final ODataRequest request = oDataPostRequest(SERVICE, SERVICE_IMPEXMEDIAS, content, Locale.ENGLISH, APPLICATION_JSON_VALUE);
		final ODataResponse oDataResponse = facade.handlePost(createContext(request));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValue(ERROR_CODE, "missing_nav_property");

		assertModelDoesNotExist(impExMediaModel);
		assertModelDoesNotExist(catalogVersion("per-staged"));
	}

	@Test
	public void testPersistEntity_ImpExMediaModel_WhenAutoCreateIsSetToTrue_NewEntryShouldCreate() throws ImpExException
	{
		final String mediaCode = "mediaWithCatalogVersionPartOfTrue";
		// override previous attribute definition with autoCreate set to 'true'
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; autoCreate",
				"; TestImpExImportCronJob:ImpExMedia         ; catalogVersion ; true");

		final String content = impExMediaContent(mediaCode, CATALOG_ID, "NewVersion").build();
		final ODataRequest request = oDataPostRequest(SERVICE, SERVICE_IMPEXMEDIAS, content, Locale.ENGLISH, APPLICATION_JSON_VALUE);
		final ODataResponse oDataResponse = facade.handlePost(createContext(request));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue(PATH_CODE, mediaCode)
				.hasPath("d.catalogVersion.__deferred");

		assertModelExists(impExMediaModel(mediaCode));
		assertModelExists(catalogVersion("NewVersion"));
	}

	@Test
	public void testPersistEntity_WhenAutoCreateIsSetToTrue_NewEntryShouldCreate_EvenDeeplyNested() throws ImpExException
	{
		final String jobCode = "testImpExJob";
		final String mediaCode = "mediaWithFolderPartOfTrue";
		final String folder = "MyPartOfFolder";
		// set previous attribute definition with autoCreate set to 'true'
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; autoCreate",
				"; TestImpExImportCronJob:ImpExImportCronJob ; workMedia ; true",
				"; TestImpExImportCronJob:ImpExMedia         ; folder    ; true");

		final String content = JsonBuilder.json()
				.withCode(jobCode)
				.withField(ATTR_WORK_MEDIA, impExMediaContent(mediaCode, CATALOG_ID, CATALOG_VERSION)
						.withField("folder", JsonBuilder.json().withField("qualifier", folder)))
				.build();
		final ODataRequest request = oDataPostRequest(SERVICE, SERVICE_IMPEXIMPORTCRONJOBS, content, Locale.ENGLISH, APPLICATION_JSON_VALUE);
		final ODataResponse oDataResponse = facade.handlePost(createContext(request));

		ODataAssertions.assertThat(oDataResponse)
				.hasStatus(HttpStatusCodes.CREATED)
				.jsonBody()
				.hasPathWithValue(PATH_CODE, jobCode)
				.hasPath("d.workMedia.__deferred");

		assertModelExists(cronJob(jobCode));
		assertModelExists(impExMediaModel(mediaCode));
		assertModelExists(mediaFolder(folder));
	}

	private MediaFolderModel mediaFolder(final String folder)
	{
		final MediaFolderModel model = new MediaFolderModel();
		model.setQualifier(folder);
		return model;
	}

	private ImpExMediaModel impExMediaModel(final String s)
	{
		final ImpExMediaModel impExMediaModel = new ImpExMediaModel();
		impExMediaModel.setCode(s);
		return impExMediaModel;
	}

	private CatalogVersionModel catalogVersion(final String version)
	{
		final CatalogVersionModel model = new CatalogVersionModel();
		model.setVersion(version);
		return model;
	}

	@Test
	public void testPersistEntity_MissingCodeValue_transactional_WhenErrorOccursShouldRollback()
	{
		final String testWorkMediaCode = "shouldNotCreatedWorkMedia";

		final ImpExMediaModel testWorkMediaModel = impExMediaModel(testWorkMediaCode);
		assertModelDoesNotExist(testWorkMediaModel);

		final String content = JsonBuilder.json()
				// missing code
				.withField("currentStep", JsonBuilder.json().withCode("test-CSVExportStep"))
				.withField(ATTR_WORK_MEDIA, impExMediaContent(testWorkMediaCode, CATALOG_ID, CATALOG_VERSION))
				.build();

		final ODataRequest request = oDataPostRequest(SERVICE, SERVICE_IMPEXIMPORTCRONJOBS, content, Locale.ENGLISH, APPLICATION_JSON_VALUE);
		final ODataResponse oDataResponse = facade.handlePost(createContext(request));

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.BAD_REQUEST);

		assertModelDoesNotExist(testWorkMediaModel);
	}

	@Test
	public void testPersistEntity_MissingCodeValue_collection_transactional_WhenErrorOccursShouldRollback()
	{
		final String testCronJobCode = "Test-ImpExImportCronJob-transaction";
		final String testTriggerCronExpression = "some cron expression";

		final TriggerModel testTriggerModel = new TriggerModel();
		testTriggerModel.setCronExpression(testTriggerCronExpression);
		assertModelDoesNotExist(testTriggerModel);

		final String content = JsonBuilder.json()
				// missing code
				.withFieldValues("triggers", JsonBuilder.json()
						.withField("cronExpression", CRON_EXPRESSION)
						.withField("cronJob", JsonBuilder.json().withCode(testCronJobCode)))
				.withField("currentStep", JsonBuilder.json().withCode("test-CSVExportStep"))
				.build();

		final ODataRequest request = oDataPostRequest(SERVICE, SERVICE_IMPEXIMPORTCRONJOBS, content, Locale.ENGLISH, APPLICATION_JSON_VALUE);
		final ODataResponse oDataResponse = facade.handlePost(createContext(request));

		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.BAD_REQUEST);
		assertModelDoesNotExist(testTriggerModel);
	}

	private JsonBuilder impExMediaContent(final String code, final String catalogId, final String catalogVersion)
	{
		return JsonBuilder.json()
				.withCode(code)
				.withField("catalogVersion", JsonBuilder.json()
						.withField("catalog", JsonBuilder.json().withId(catalogId))
						.withField("version", catalogVersion));
	}

	private ImpExImportCronJobModel cronJob(final String testModelCode)
	{
		final ImpExImportCronJobModel cronJobModel = new ImpExImportCronJobModel();
		cronJobModel.setCode(testModelCode);
		return cronJobModel;
	}
}
