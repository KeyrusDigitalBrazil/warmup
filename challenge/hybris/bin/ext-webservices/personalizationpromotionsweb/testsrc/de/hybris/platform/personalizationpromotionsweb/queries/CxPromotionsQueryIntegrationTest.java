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
/**
 *
 */
package de.hybris.platform.personalizationpromotionsweb.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationpromotionsweb.data.PromotionRuleListWsDTO;
import de.hybris.platform.personalizationpromotionsweb.data.PromotionRuleWsDTO;
import de.hybris.platform.personalizationwebservices.BaseWebServiceTest;
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
import de.hybris.platform.personalizationwebservices.data.QueryParamsWsDTO;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.ruleengineservices.RuleEngineServiceException;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.maintenance.RuleMaintenanceService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
{ PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public class CxPromotionsQueryIntegrationTest extends BaseWebServiceTest
{
	private static final String PROMOTIONS_PATH = "v1/query/cxpromotionsforcatalog";

	private static final String CATALOG = "catalog";
	private static final String CATALOG_VERSION = "version";

	private static final String PUBLISHED_EMPTY_PROMOTION = "promotionRule1";
	private static final String PUBLISHED_AWARE_PROMOTION = "promotionRule2";
	private static final String PUBLISHED_COMPLEX_PROMOTION = "promotionRule3";
	private static final String PUBLISHED_COMPLEX_AWARE_PROMOTION = "promotionRule4";

	@Resource
	ConfigurationService configurationService;

	@Resource
	FlexibleSearchService flexibleSearchService;
	@Resource
	RuleMaintenanceService ruleMaintenanceService;
	@Resource
	ModelService modelService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		importData(new ClasspathImpExResource("/personalizationpromotionsweb/test/webcontext_testdata.impex", "UTF-8"));
		//		importCsv("/personalizationpromotionsweb/test/querycontext_testdata.impex", "utf-8");
		publishPromotions(PUBLISHED_EMPTY_PROMOTION, PUBLISHED_AWARE_PROMOTION, PUBLISHED_COMPLEX_PROMOTION,
				PUBLISHED_COMPLEX_AWARE_PROMOTION);
	}

	protected void publishPromotions(final String... codes) throws RuleEngineServiceException
	{
		final List<PromotionSourceRuleModel> rules = new ArrayList<>();

		for (final String code : codes)
		{
			rules.add(getPromotionRule(code));
		}
		ruleMaintenanceService.compileAndPublishRulesWithBlocking(rules, "promotions-module", false);

	}

	protected PromotionSourceRuleModel getPromotionRule(final String code)
	{
		final PromotionSourceRuleModel sample = new PromotionSourceRuleModel();
		sample.setCode(code);
		final PromotionSourceRuleModel model = flexibleSearchService.getModelByExample(sample);
		modelService.refresh(model);
		return model;
	}

	@Test
	public void getWithInvalidRequest() throws IOException, JAXBException
	{
		//given
		final Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put(CATALOG, "testCatalog");
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(paramsMap);

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));

		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO errorWsDTO = errors.getErrors().get(0);
		assertEquals("ValidationError", errorWsDTO.getType());
		assertEquals(CATALOG_VERSION, errorWsDTO.getSubject());
	}


	@Test
	public void getWithValidCatalogVersion() throws IOException, JAXBException
	{
		//given
		final Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put(CATALOG, "testCatalog");
		paramsMap.put(CATALOG_VERSION, "Online");
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(paramsMap);

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));

		WebservicesAssert.assertResponse(Status.OK, response);
		final PromotionRuleListWsDTO promotionList = response.readEntity(PromotionRuleListWsDTO.class);
		assertNotNull(promotionList.getPromotions());
		assertEquals(2, promotionList.getPromotions().size());

		final PromotionRuleWsDTO promotionRule1 = promotionList.getPromotions().get(0);
		assertPromotionRule(promotionRule1, "promotionRule2", "aware", null);

		final PromotionRuleWsDTO promotionRule2 = promotionList.getPromotions().get(1);
		assertPromotionRule(promotionRule2, "promotionRule4", "complex_aware", null);
	}

	@Test
	public void getPromotionsPermissionsCheck() throws JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK, getPromotionsRESTRequest(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getPromotionsRESTRequest(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getPromotionsRESTRequest(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getPromotionsRESTRequest(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getPromotionsRESTRequest(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	@Test
	public void getWithInvalidCatalogVersion() throws IOException, JAXBException
	{
		//given
		final Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put(CATALOG, "testCatalog");
		paramsMap.put(CATALOG_VERSION, "invalid");
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(paramsMap);

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));

		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO errorWsDTO = errors.getErrors().get(0);
		assertEquals("ValidationError", errorWsDTO.getType());
	}

	@Test
	public void getWithInvalidCatalog() throws IOException, JAXBException
	{
		//given
		final Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put(CATALOG, "testCatalogInvalid");
		paramsMap.put(CATALOG_VERSION, "Online");
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(paramsMap);

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));

		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO errorWsDTO = errors.getErrors().get(0);
		assertEquals("ValidationError", errorWsDTO.getType());
	}

	private void assertPromotionRule(final PromotionRuleWsDTO promotionRule, final String code, final String name,
			final String description)
	{
		Assert.assertNotNull("Not found ", promotionRule);
		Assert.assertEquals(code, promotionRule.getCode());
		Assert.assertEquals(name, promotionRule.getName());
		Assert.assertEquals(description, promotionRule.getDescription());
		Assert.assertEquals(RuleStatus.PUBLISHED.toString(), promotionRule.getStatus());
	}

	protected Response getPromotionsRESTRequest(final String user, final String pwd) throws JAXBException
	{
		final Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put(CATALOG, "testCatalog");
		paramsMap.put(CATALOG_VERSION, "Online");
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(paramsMap);

		//when
		final Response response = getWsSecuredRequestBuilder(user, pwd)//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));
		return response;
	}


}
