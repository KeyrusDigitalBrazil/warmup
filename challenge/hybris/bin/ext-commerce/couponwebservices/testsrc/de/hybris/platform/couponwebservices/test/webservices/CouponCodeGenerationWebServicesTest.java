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
package de.hybris.platform.couponwebservices.test.webservices;

import static java.util.stream.Collectors.toList;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponwebservices.constants.CouponwebservicesConstants;
import de.hybris.platform.couponwebservices.dto.CouponGeneratedCodeWsDTO;
import de.hybris.platform.couponwebservices.dto.MultiCodeCouponWsDTO;
import de.hybris.platform.couponwebservices.dto.ws.CodeGenerationConfigurationsSearchPageWsDTO;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;
import de.hybris.platform.webservicescommons.dto.SortWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;


@NeedsEmbeddedServer(webExtensions =
{ CouponwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CouponCodeGenerationWebServicesTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "coupon_user";
	public static final String OAUTH_CLIENT_PASS = "secret";

	private static final String BASE_URI = "couponservices/v2";
	private static final String MULTICODECOUPON_URI = BASE_URI + "/multicodecoupon";

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		importCsv("/couponwebservices/test/ws-user.impex", "utf-8");
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
	}

	@Test
	public void testPutGenerateCodesWithoutAuthorization()
	{
		final Response result = createWsRequestBuilder()//
				.path(MULTICODECOUPON_URI)//
				.path("generate")//
				.path("COUPON1")//
				.path("10")//
				.build()//
				.put(Entity.text(""));
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
	}

	@Test
	public void testPutGenerateCodes()
	{
		final Response result = createWsSecuredRequestBuilder()//
				.path(MULTICODECOUPON_URI)//
				.path("generate")//
				.path("COUPON2")//
				.path("1")//
				.build()//
				.put(Entity.text(""));
		result.bufferEntity();
		final MultivaluedMap<String, Object> headers = result.getHeaders();
		WebservicesAssert.assertResponse(Status.CREATED, Optional.empty(), result);
		assertThat(headers).isNotEmpty();
		assertThat(headers.get("Location")).isNotNull();
		assertThat(headers.get("Location")).hasSize(1);
		assertThat(headers.getFirst("Location").toString()).containsIgnoringCase("/couponcodes/COUPON2/1+COUPON2");
	}

	@Test
	public void testGetGeneratedCouponCodesUnauthorized() throws Exception
	{
		final Response result = createWsSecuredRequestBuilder()//
				.path(MULTICODECOUPON_URI)//
				.path("generate")//
				.path("COUPON2")//
				.path("1")//
				.build()//
				.put(Entity.text(""));
		result.bufferEntity();
		final MultivaluedMap<String, Object> headers = result.getHeaders();
		final String locationUrl = extractQueryPath((String) headers.getFirst("Location"));

		final Response couponCodesResult = createWsRequestBuilder()//
				.path(locationUrl)//
				.build()//
				.get();
		couponCodesResult.bufferEntity();
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), couponCodesResult);
	}

	@Test
	public void testGetGeneratedCouponCodes() throws Exception
	{
		final Response result = createWsSecuredRequestBuilder()//
				.path(MULTICODECOUPON_URI)//
				.path("generate")//
				.path("COUPON2")//
				.path("1")//
				.build()//
				.put(Entity.text(""));
		result.bufferEntity();
		final MultivaluedMap<String, Object> headers = result.getHeaders();
		final String locationUrl = extractQueryPath((String) headers.getFirst("Location"));

		final Response couponCodesResult = createWsSecuredRequestBuilder()//
				.path(locationUrl)//
				.build()//
				.get();
		couponCodesResult.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), couponCodesResult);
		final String couponCodes = couponCodesResult.readEntity(String.class);
		assertThat(couponCodes).isNotNull();

		assertThat(couponCodes).isNotEmpty().startsWith("COUPON2");
	}

	@Test
	public void testGetMultiCodeCouponWithGeneratedCodes() throws Exception
	{
		Response result = createWsSecuredRequestBuilder()//
				.path(MULTICODECOUPON_URI)//
				.path("generate")//
				.path("COUPON1")//
				.path("1")//
				.build()//
				.put(Entity.text(""));
		result.bufferEntity();
		MultivaluedMap<String, Object> headers = result.getHeaders();
		final String batch1Location = (String) headers.getFirst("Location");

		Thread.sleep(1000);

		result = createWsSecuredRequestBuilder()//
				.path(MULTICODECOUPON_URI)//
				.path("generate")//
				.path("COUPON1")//
				.path("3")//
				.build()//
				.put(Entity.text(""));
		result.bufferEntity();
		headers = result.getHeaders();
		final String batch2Location = (String) headers.getFirst("Location");

		final Response multiCodeCouponWsResponse = createWsSecuredRequestBuilder()//
				.path(MULTICODECOUPON_URI)//
				.path("get")//
				.path("COUPON1")//
				.build()//
				.get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), multiCodeCouponWsResponse);
		final MultiCodeCouponWsDTO respMultiCodeCouponWsDTO = multiCodeCouponWsResponse.readEntity(MultiCodeCouponWsDTO.class);
		final List<CouponGeneratedCodeWsDTO> generatedCodes = respMultiCodeCouponWsDTO.getGeneratedCodes();
		assertThat(generatedCodes).isNotEmpty();
		final List<String> linksToGeneratedCodes = generatedCodes.stream().map(CouponGeneratedCodeWsDTO::getLink).collect(toList());
		assertThat(linksToGeneratedCodes).contains(batch1Location, batch2Location);
	}

	private WsSecuredRequestBuilder createWsSecuredRequestBuilder()
	{
		return new WsSecuredRequestBuilder()//
				.extensionName(CouponwebservicesConstants.EXTENSIONNAME)//
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)//
				.grantClientCredentials();
	}

	private WsRequestBuilder createWsRequestBuilder()
	{
		return new WsRequestBuilder()//
				.extensionName(CouponwebservicesConstants.EXTENSIONNAME);
	}

	private String extractQueryPath(final String path) throws MalformedURLException
	{
		String queryPath = new String(path);
		final java.net.URL uri = new java.net.URL(path);
		final String appPath = Joiner.on("").join(uri.getProtocol(), "://", uri.getAuthority(), "/");
		queryPath = queryPath.replace(appPath, "");
		return queryPath.substring(queryPath.indexOf("/"));
	}

	@Test
	public void shouldProvideCodeGenerationConfiguration()
	{
		final Response result = createWsSecuredRequestBuilder()//
				.path(BASE_URI)//
				.path("codegenerationconfiguration")//
				.path("list")//
				.build()//
				.get();
		//when
		final CodeGenerationConfigurationsSearchPageWsDTO codeGenerationConfigurations = result.readEntity(CodeGenerationConfigurationsSearchPageWsDTO.class);
		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		assertThat(codeGenerationConfigurations.getResults()).hasSize(2);
	}

	@Test
	public void shouldProvideCodeGenerationConfigurationSortedByNameWithDesc()
	{
		final Response result = createWsSecuredRequestBuilder()//
				.path(BASE_URI)//
				.path("codegenerationconfiguration")//
				.path("list")//
				.queryParam("sort","name:desc")
				.build()//
				.get();
		//when
		final CodeGenerationConfigurationsSearchPageWsDTO codeGenerationConfigurations = result.readEntity(CodeGenerationConfigurationsSearchPageWsDTO.class);
		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final SortWsDTO sort = codeGenerationConfigurations.getSorts().iterator().next();
		assertThat(sort.getCode()).isEqualTo("name");
		assertThat(sort.isAsc()).isEqualTo(false);
	}

	@Test
	public void shouldProvideCodeGenerationConfigurationPaged()
	{
		final Response result = createWsSecuredRequestBuilder()//
				.path(BASE_URI)//
				.path("codegenerationconfiguration")//
				.path("list")//
				.queryParam("currentPage",1).queryParam("pageSize",1)
				.build()//
				.get();
		//when
		final CodeGenerationConfigurationsSearchPageWsDTO codeGenerationConfigurations = result.readEntity(CodeGenerationConfigurationsSearchPageWsDTO.class);
		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final PaginationWsDTO pagination = codeGenerationConfigurations.getPagination();
		assertThat(pagination.getPage()).isEqualTo(1);
		assertThat(pagination.getCount()).isEqualTo(1);
	}
}
