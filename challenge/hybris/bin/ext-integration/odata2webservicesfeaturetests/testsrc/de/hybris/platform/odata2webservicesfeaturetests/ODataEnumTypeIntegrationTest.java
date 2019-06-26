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
package de.hybris.platform.odata2webservicesfeaturetests;

import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.createContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.odata2webservices.odata.ODataFacade;
import de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class ODataEnumTypeIntegrationTest extends ServicelayerTest
{
	@Resource(name = "oDataWebMonitoringFacade")
	private ODataFacade facade;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/classattributeassignment-odata2webservicesfeaturetests.impex", "UTF-8");
	}

	@Test
	public void shouldSuccessPersist_ClassAttributeTypeEnum_WhenThereAreMoreThanOneEnumTypes()
	{
		final String content = "{"
				+ "  \"@odata.context\": \"$metadata#ClassAttributeAssignments/$entity\","
				+ "  \"range\": false,"
				+ "  \"multiValued\":false,"
				+ "  \"classificationClass\":{"
				+ "    \"code\": \"WEC_CDRAGON_CAR\","
				+ "    \"catalogVersion\": {"
				+ "      \"catalog\": {"
				+ "        \"id\": \"ERP_CLASSIFICATION_3000\""
				+ "      },"
				+ "      \"version\": \"ERP_IMPORT\""
				+ "    }"
				+ "  },"
				+ "  \"classificationAttribute\":{"
				+ "    \"code\": \"WEC_DC_COLOR\","
				+ "    \"systemVersion\": {"
				+ "      \"catalog\": {"
				+ "        \"id\": \"ERP_CLASSIFICATION_3000\""
				+ "      },"
				+ "      \"version\": \"ERP_IMPORT\""
				+ "    }"
				+ "  },"
				+ "  \"attributeType\":{"
				+ "    \"code\":\"string\""
				+ "  },"
				+ "  \"unit\":{"
				+ "    \"code\":\"WTT\","
				+ "    \"unitType\":\"SAP-POWER\","
				+ "    "
				+ "    \"systemVersion\":{"
				+ "      \"version\":\"ERP_IMPORT\","
				+ "      \"catalog\":{"
				+ "        \"id\":\"ERP_CLASSIFICATION_3000\""
				+ "      }"
				+ "    }"
				+ "  }"
				+ "}";

		final ODataRequest request = ODataFacadeTestUtils.oDataPostRequest("ClassAttributeAssignment", "ClassAttributeAssignments", content, APPLICATION_JSON_VALUE);
		final ODataResponse oDataResponse = facade.handlePost(createContext(request));
		assertThat(oDataResponse).hasFieldOrPropertyWithValue("status", HttpStatusCodes.CREATED);

		final ClassAttributeAssignmentModel model = new ClassAttributeAssignmentModel();
		model.setAttributeType(ClassificationAttributeTypeEnum.STRING);

		final ClassAttributeAssignmentModel persistedModel = flexibleSearchService.getModelByExample(model);
		assertThat(persistedModel.getClassificationClass().getCode()).isEqualTo("WEC_CDRAGON_CAR");
		assertThat(persistedModel.getAttributeType()).isEqualTo(ClassificationAttributeTypeEnum.STRING);
		assertThat(persistedModel.getClassificationAttribute().getCode()).isEqualTo("WEC_DC_COLOR");
		assertThat(persistedModel.getUnit().getCode()).isEqualTo("WTT");
		assertThat(persistedModel.getUnit().getUnitType()).isEqualTo("SAP-POWER");

		assertHasMoreThanOneStringTypClassificationAttributeTypeEnum();
	}

	private void assertHasMoreThanOneStringTypClassificationAttributeTypeEnum()
	{
		final String searchQuery = "SELECT {pk} FROM {ClassificationAttributeTypeEnum*} WHERE {code} = 'string'";
		final SearchResult<Object> search = flexibleSearchService.search(searchQuery);

		assertThat(search.getCount()).isGreaterThan(1);
	}
}
