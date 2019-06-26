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
package de.hybris.platform.customerticketingc4cintegration.facade;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerticketingc4cintegration.SitePropsHolder;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.data.Note;
import de.hybris.platform.customerticketingc4cintegration.data.ODataListResponseData;
import de.hybris.platform.customerticketingc4cintegration.data.ODataListResultsData;
import de.hybris.platform.customerticketingc4cintegration.data.ODataSingleResponseData;
import de.hybris.platform.customerticketingc4cintegration.data.ODataSingleResultsData;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingc4cintegration.facade.utils.HttpHeaderUtil;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;


/**
 * Test cases for {@link C4CTicketFacadeImpl}
 */
public class C4CTicketFacadeImplTest extends ServicelayerTest
{
	@InjectMocks
	private C4CTicketFacadeImpl c4CTicketFacade;

	@Mock
	private C4CBaseFacade c4cBaseFacade;

	@Mock
	private RestTemplate restTemplate;
	private SitePropsHolder sitePropsHolder;
	@Mock
	private CustomerFacade customerFacade;
	@Mock
	private StatusData completedStatus;
	@Mock
	private ObjectMapper jacksonObjectMapper;

	@InjectMocks
	private HttpHeaderUtil httpHeaderUtilMock;

	@Mock
	private HttpHeaderUtil httpHeaderUtil;

	@Mock
	private Converter<ServiceRequestData, TicketData> ticketConverter;
	@Mock
	private Converter<TicketData, ServiceRequestData> defaultC4CTicketConverter;
	@Mock
	private Converter<TicketData, Note> updateMessageConverter;

	private static final String COMPLETED = "COMPLETED";
	private static final String SUBJECT = "subject";
	private static final String MESSAGE = "message";
	private static final String RESPONSE_BODY = "response body";
	private static final String CUSTOMER_ID = "customerId";
	private static final String JSON_STRING = "jsonString";
	private static final String TICKET_ID = "ticketId";
	private static final String SITE_ID = "siteId";

	/**
	 * Test setup.
	 */
	@Before
	public void setup()
	{
		sitePropsHolder = Mockito.mock(SitePropsHolder.class);
		Config.setParameter("customerticketingc4cintegration.c4c-url", "http://127.0.0.1"); // NOSONAR
		Config.setParameter("customerticketingc4cintegration.c4c-username", "username");
		Config.setParameter("customerticketingc4cintegration.c4c-password", "password");
	}

	/**
	 * Test {@link C4CTicketFacadeImpl#createTicket(TicketData)} Should create a ticket.
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldCreateATicket() throws IOException
	{
		MockitoAnnotations.initMocks(this);

		final TicketData ticketData = new TicketData();
		ticketData.setSubject(SUBJECT);
		ticketData.setMessage(MESSAGE);

		final ServiceRequestData serviceRequestData = new ServiceRequestData();
		final TicketData convertedTicketData = new TicketData();
		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);
		Mockito.when(ticketConverter.convert(serviceRequestData)).thenReturn(convertedTicketData);
		Mockito.when(defaultC4CTicketConverter.convert(ticketData)).thenReturn(serviceRequestData);


		final ResponseEntity<String> mockPostResponseResult = Mockito.mock(ResponseEntity.class);
		Mockito.when(mockPostResponseResult.getStatusCode()).thenReturn(HttpStatus.OK);
		Mockito.when(mockPostResponseResult.getBody()).thenReturn(RESPONSE_BODY);
		Mockito.when(mockPostResponseResult.getHeaders()).thenReturn(new HttpHeaders());

		Mockito.when(jacksonObjectMapper.writeValueAsString(serviceRequestData)).thenReturn(JSON_STRING);
		Mockito.when(
				restTemplate.postForEntity(
						Mockito.eq(Customerticketingc4cintegrationConstants.URL
								+ Customerticketingc4cintegrationConstants.TICKETING_SUFFIX), Mockito.any(), Mockito.eq(String.class)))
				.thenReturn(mockPostResponseResult);

		final ODataSingleResponseData responseData = new ODataSingleResponseData();
		final ODataSingleResultsData dData = new ODataSingleResultsData();
		dData.setResults(serviceRequestData);
		responseData.setD(dData);
		Mockito.when(jacksonObjectMapper.readValue(RESPONSE_BODY, ODataSingleResponseData.class)).thenReturn(responseData);


		final TicketData resultTicketData = c4CTicketFacade.createTicket(ticketData);

		Mockito.verify(restTemplate).postForEntity(
				Mockito.eq(Customerticketingc4cintegrationConstants.URL + Customerticketingc4cintegrationConstants.TICKETING_SUFFIX),
				Mockito.any(), Mockito.eq(String.class));
		Assert.assertEquals(convertedTicketData, resultTicketData);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#createTicket(TicketData)} throws runtime exp. when RestClientException happens
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldReturnRuntimeExceptionWhenThrowRestClientException() throws IOException
	{
		MockitoAnnotations.initMocks(this);

		final TicketData ticketData = new TicketData();
		ticketData.setSubject(SUBJECT);
		ticketData.setMessage(MESSAGE);

		final ServiceRequestData serviceRequestData = new ServiceRequestData();
		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);

		Mockito.when(jacksonObjectMapper.writeValueAsString(serviceRequestData)).thenReturn(JSON_STRING);
		Mockito.when(
				restTemplate.postForEntity(
						Mockito.eq(Customerticketingc4cintegrationConstants.URL
								+ Customerticketingc4cintegrationConstants.TICKETING_SUFFIX), Mockito.any(), Mockito.eq(String.class)))
				.thenThrow(RestClientException.class);

		final ODataSingleResponseData responseData = new ODataSingleResponseData();
		final ODataSingleResultsData dData = new ODataSingleResultsData();
		dData.setResults(serviceRequestData);
		responseData.setD(dData);
		Mockito.when(jacksonObjectMapper.readValue(RESPONSE_BODY, ODataSingleResponseData.class)).thenReturn(responseData);

		try
		{
			c4CTicketFacade.createTicket(ticketData);
			Assert.fail("RuntimeException is expected before this point!");
		}
		catch (final RuntimeException expectedException)
		{
			Assert.assertEquals("Can't send request", expectedException.getMessage());
		}
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#createTicket(TicketData)} Should return runtime exception when something
	 * happens
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldReturnCustomerSupportTicketExceptionWhenThrowIOException() throws IOException
	{
		MockitoAnnotations.initMocks(this);

		final TicketData ticketData = new TicketData();
		ticketData.setSubject(SUBJECT);
		ticketData.setMessage(MESSAGE);

		final ServiceRequestData serviceRequestData = new ServiceRequestData();
		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);


		final ResponseEntity<String> mockPostResponseResult = Mockito.mock(ResponseEntity.class);
		Mockito.when(mockPostResponseResult.getStatusCode()).thenReturn(HttpStatus.OK);
		Mockito.when(mockPostResponseResult.getBody()).thenReturn(RESPONSE_BODY);
		Mockito.when(mockPostResponseResult.getHeaders()).thenReturn(new HttpHeaders());

		Mockito.when(jacksonObjectMapper.writeValueAsString(serviceRequestData)).thenReturn(JSON_STRING);
		Mockito.when(
				restTemplate.postForEntity(
						Mockito.eq(Customerticketingc4cintegrationConstants.URL
								+ Customerticketingc4cintegrationConstants.TICKETING_SUFFIX), Mockito.any(), Mockito.eq(String.class)))
				.thenReturn(mockPostResponseResult);
		Mockito.when(jacksonObjectMapper.readValue(RESPONSE_BODY, ODataSingleResponseData.class)).thenThrow(IOException.class);

		try
		{
			c4CTicketFacade.createTicket(ticketData);
			Assert.fail("RuntimeException is expected before this point!");
		}
		catch (final RuntimeException expectedException)
		{
			Assert.assertEquals("Can't read ticketData from response", expectedException.getMessage());
		}
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTicket(String)} should get a ticket DTO by given ticket id.
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldGetTicket() throws IOException
	{
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);
		MockitoAnnotations.initMocks(this);
		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);

		Mockito.when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);

		final ResponseEntity<String> mockPostResponseResult = Mockito.mock(ResponseEntity.class);
		Mockito.when(mockPostResponseResult.getStatusCode()).thenReturn(HttpStatus.OK);
		Mockito.when(mockPostResponseResult.getBody()).thenReturn(RESPONSE_BODY);
		Mockito.when(mockPostResponseResult.getHeaders()).thenReturn(new HttpHeaders());

		final HttpEntity<String> entity = new HttpEntity<>(mockHttpHeaders);

		Mockito.when(
				restTemplate.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET), Mockito.eq(entity),
						Mockito.eq(String.class))).thenReturn(mockPostResponseResult);

		final ODataListResponseData responseData = new ODataListResponseData();
		final ODataListResultsData resultsData = new ODataListResultsData();
		final ServiceRequestData serviceRequestData = new ServiceRequestData();
		resultsData.setResults(Lists.newArrayList(serviceRequestData));
		responseData.setD(resultsData);
		Mockito.when(jacksonObjectMapper.readValue(RESPONSE_BODY, ODataListResponseData.class)).thenReturn(responseData);

		final TicketData convertedTicketData = new TicketData();
		Mockito.when(ticketConverter.convert(Mockito.any())).thenReturn(convertedTicketData);


		final TicketData ticketData = c4CTicketFacade.getTicket(TICKET_ID);
		Mockito.verify(jacksonObjectMapper).readValue(RESPONSE_BODY, ODataListResponseData.class);
		Mockito.verify(restTemplate).exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET), Mockito.eq(entity),
				Mockito.eq(String.class));
		Assert.assertEquals(convertedTicketData, ticketData);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTicket(String)} Should return runtime exception when something goes wrong
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldReturnRuntimeExceptionWhenGetTicketThrowIOException() throws IOException
	{
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);
		MockitoAnnotations.initMocks(this);
		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);

		Mockito.when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);

		final ResponseEntity<String> mockPostResponseResult = Mockito.mock(ResponseEntity.class);
		Mockito.when(mockPostResponseResult.getStatusCode()).thenReturn(HttpStatus.OK);
		Mockito.when(mockPostResponseResult.getBody()).thenReturn(RESPONSE_BODY);
		Mockito.when(mockPostResponseResult.getHeaders()).thenReturn(new HttpHeaders());

		final HttpEntity<String> entity = new HttpEntity<>(mockHttpHeaders);

		Mockito.when(
				restTemplate.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET), Mockito.eq(entity),
						Mockito.eq(String.class))).thenReturn(mockPostResponseResult);

		Mockito.when(jacksonObjectMapper.readValue(RESPONSE_BODY, ODataListResponseData.class)).thenThrow(IOException.class);

		final TicketData convertedTicketData = new TicketData();
		Mockito.when(ticketConverter.convert(Mockito.any())).thenReturn(convertedTicketData);

		try
		{
			c4CTicketFacade.getTicket(TICKET_ID);
			Assert.fail("RuntimeException is expected before this point!");
		}
		catch (final RuntimeException expectedException)
		{
			Assert.assertEquals("Can't convert ticketData", expectedException.getMessage());
		}
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTicket(String)} Should return runtime exception when something goes wrong
	 *
	 * @throws IOException
	 * @throws RuntimeException
	 */
	@Test
	public void shouldReturnRuntimeExceptionWhenGetTicketThrowRestClientException() throws IOException
	{
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);
		MockitoAnnotations.initMocks(this);
		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);

		Mockito.when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);

		final HttpEntity<String> entity = new HttpEntity<>(mockHttpHeaders);

		Mockito.when(
				restTemplate.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET), Mockito.eq(entity),
						Mockito.eq(String.class))).thenThrow(RestClientException.class);

		final TicketData convertedTicketData = new TicketData();
		Mockito.when(ticketConverter.convert(Mockito.any())).thenReturn(convertedTicketData);

		try
		{
			c4CTicketFacade.getTicket(TICKET_ID);
			Assert.fail("RuntimeException is expected before this point!");
		}
		catch (final RuntimeException expectedException)
		{
			Assert.assertEquals("Can't send request", expectedException.getMessage());
		}
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTickets(PageableData)} should return a SearchPageData<TicketData> result.
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldGetTickets() throws IOException
	{
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);
		final SearchPageData mockedResults = Mockito.mock(SearchPageData.class);
		MockitoAnnotations.initMocks(this);
		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);

		Mockito.when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);

		final ResponseEntity<String> mockPostResponseResult = Mockito.mock(ResponseEntity.class);
		Mockito.when(mockPostResponseResult.getStatusCode()).thenReturn(HttpStatus.OK);
		Mockito.when(mockPostResponseResult.getBody()).thenReturn(RESPONSE_BODY);
		Mockito.when(mockPostResponseResult.getHeaders()).thenReturn(new HttpHeaders());

		final HttpEntity<String> entity = new HttpEntity<>(mockHttpHeaders);

		Mockito.when(
				restTemplate.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET), Mockito.eq(entity),
						Mockito.eq(String.class))).thenReturn(mockPostResponseResult);

		final ODataListResponseData responseData = new ODataListResponseData();
		final ODataListResultsData resultsData = new ODataListResultsData();
		final ServiceRequestData serviceRequestData = new ServiceRequestData();
		resultsData.setResults(Lists.newArrayList(serviceRequestData));
		responseData.setD(resultsData);
		resultsData.set__count("1");
		Mockito.when(jacksonObjectMapper.readValue(RESPONSE_BODY, ODataListResponseData.class)).thenReturn(responseData);

		final TicketData convertedTicketData = new TicketData();
		Mockito.when(ticketConverter.convert(Mockito.any())).thenReturn(convertedTicketData);

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(1);
		pageableData.setCurrentPage(1);
		pageableData.setSort(StringUtils.EMPTY);

		Mockito.when(c4cBaseFacade.convertPageData(Lists.newArrayList(serviceRequestData), ticketConverter, pageableData, 1))
				.thenReturn(mockedResults);
		final SearchPageData<TicketData> tickets = c4CTicketFacade.getTickets(pageableData);

		Mockito.verify(jacksonObjectMapper).readValue(RESPONSE_BODY, ODataListResponseData.class);
		Mockito.verify(restTemplate).exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET), Mockito.eq(entity),
				Mockito.eq(String.class));
		Assert.assertEquals(mockedResults, tickets);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTickets(PageableData)} should return an empty result list if throw
	 * IOException.
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldReturnEmptySearchResultWhenGetTicketsThrowIOException() throws IOException
	{
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);
		final SearchPageData mockedEmptyResults = Mockito.mock(SearchPageData.class);
		MockitoAnnotations.initMocks(this);
		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);

		Mockito.when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);

		final ResponseEntity<String> mockPostResponseResult = Mockito.mock(ResponseEntity.class);
		Mockito.when(mockPostResponseResult.getStatusCode()).thenReturn(HttpStatus.OK);
		Mockito.when(mockPostResponseResult.getBody()).thenReturn(RESPONSE_BODY);
		Mockito.when(mockPostResponseResult.getHeaders()).thenReturn(new HttpHeaders());

		final HttpEntity<String> entity = new HttpEntity<>(mockHttpHeaders);

		Mockito.when(
				restTemplate.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET), Mockito.eq(entity),
						Mockito.eq(String.class))).thenReturn(mockPostResponseResult);

		Mockito.when(jacksonObjectMapper.readValue(RESPONSE_BODY, ODataListResponseData.class)).thenThrow(IOException.class);

		final TicketData convertedTicketData = new TicketData();
		Mockito.when(ticketConverter.convert(Mockito.any())).thenReturn(convertedTicketData);

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(1);
		pageableData.setCurrentPage(1);
		pageableData.setSort(StringUtils.EMPTY);

		Mockito.when(c4cBaseFacade.convertPageData(Collections.emptyList(), ticketConverter, pageableData, 0)).thenReturn(
				mockedEmptyResults);

		TestUtils.disableFileAnalyzer("Expect to have here an error message.", 20); //NOSONAR
		final SearchPageData<TicketData> tickets = c4CTicketFacade.getTickets(pageableData);

		Mockito.verify(jacksonObjectMapper).readValue(RESPONSE_BODY, ODataListResponseData.class);
		Mockito.verify(restTemplate).exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET), Mockito.eq(entity),
				Mockito.eq(String.class));
		Assert.assertEquals(mockedEmptyResults, tickets);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTickets(PageableData)} should return an empty result list if throw
	 * RestClientException.
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldReturnEmptySearchResultWhenGetTicketsThrowRestClientException() throws IOException
	{
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);
		final SearchPageData mockedEmptyResults = Mockito.mock(SearchPageData.class);
		MockitoAnnotations.initMocks(this);

		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);

		Mockito.when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);

		final HttpEntity<String> entity = new HttpEntity<>(mockHttpHeaders);

		Mockito.when(
				restTemplate.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET), Mockito.eq(entity),
						Mockito.eq(String.class))).thenThrow(RestClientException.class);

		final TicketData convertedTicketData = new TicketData();
		Mockito.when(ticketConverter.convert(Mockito.any())).thenReturn(convertedTicketData);

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(1);
		pageableData.setCurrentPage(1);
		pageableData.setSort(StringUtils.EMPTY);

		Mockito.when(c4cBaseFacade.convertPageData(Collections.emptyList(), ticketConverter, pageableData, 0)).thenReturn(
				mockedEmptyResults);

		TestUtils.disableFileAnalyzer("Expect to have here an error message.", 20);
		final SearchPageData<TicketData> tickets = c4CTicketFacade.getTickets(pageableData);

		Mockito.verify(restTemplate).exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET), Mockito.eq(entity),
				Mockito.eq(String.class));
		Assert.assertEquals(mockedEmptyResults, tickets);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#createBasicAuthHeader(String, String)} Should create a correct basic auth
	 * header.
	 */
	@Test
	public void shouldCreateBasicAuthHeader()
	{
		httpHeaderUtil = new HttpHeaderUtil();
		final String basicAuthHeader = httpHeaderUtil.createBasicAuthHeader("username", "password");
		Assert.assertEquals("Basic dXNlcm5hbWU6cGFzc3dvcmQ=", basicAuthHeader);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getDefaultHeaders(String)} should get a correct default header by given site
	 * id.
	 */
	@Test
	public void shouldGetDefaultHeaders()
	{
		httpHeaderUtil = new HttpHeaderUtil();
		c4CTicketFacade = new C4CTicketFacadeImpl();
		final HttpHeaders headers = httpHeaderUtil.getDefaultHeaders(SITE_ID);

		Assert.assertEquals(Arrays.asList(SITE_ID), headers.get(Customerticketingc4cintegrationConstants.SITE_HEADER));
		Assert.assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
		Assert.assertEquals(Arrays.asList(Customerticketingc4cintegrationConstants.ACCEPT), headers.get(HttpHeaders.ACCEPT));
		Assert.assertEquals(Arrays.asList("Basic dXNlcm5hbWU6cGFzc3dvcmQ="), headers.get(HttpHeaders.AUTHORIZATION));
		Assert.assertEquals(Arrays.asList(Customerticketingc4cintegrationConstants.TOKEN_EMPTY),
				headers.get(Customerticketingc4cintegrationConstants.TOKEN_NAMING));
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getEnrichedHeaders()} should return an enriched header.
	 */
	@Test
	public void shouldGetEnrichedHeaders()
	{

		final HttpHeaders mockHeaders = Mockito.mock(HttpHeaders.class);
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);
		httpHeaderUtilMock = new HttpHeaderUtil()
		{
			@Override
			public HttpHeaders enrichHeaders(final HttpHeaders headers, final String siteId)
			{
				return mockHeaders;
			}

			@Override
			public HttpHeaders getDefaultHeaders(final String siteId)
			{
				return mockHttpHeaders;
			}
		};

		MockitoAnnotations.initMocks(this);

		Mockito.when(sitePropsHolder.getSiteId()).thenReturn(SITE_ID);
		final HttpHeaders enrichedHeaders = httpHeaderUtilMock.getEnrichedHeaders();

		Mockito.verify(sitePropsHolder).getSiteId();
		Assert.assertEquals(mockHeaders, enrichedHeaders);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getAssociatedToObjects()} Should throw UnsupportedOperationException.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void shouldGetAssociatedToObjectsThrowUnsupportedOperationException()
	{
		c4CTicketFacade = new C4CTicketFacadeImpl();
		c4CTicketFacade.getAssociatedToObjects();
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTicketCategories()} Should throw UnsupportedOperationException.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void shouldGetTicketCategoriesThrowUnsupportedOperationException()
	{
		c4CTicketFacade = new C4CTicketFacadeImpl();
		c4CTicketFacade.getTicketCategories();
	}

	/**
	 * Test o f{@link C4CTicketFacadeImpl#addBatchHeaders(String)} should return http headers which populated correctly
	 * data.
	 */
	@Test
	public void shouldAddBatchHeaders()
	{
		c4CTicketFacade = new C4CTicketFacadeImpl();
		httpHeaderUtil = new HttpHeaderUtil();
		final HttpHeaders headers = httpHeaderUtil.addBatchHeaders("test-url");

		Assert.assertEquals(Arrays.asList("test-url"), headers.get(""));
		Assert.assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
		Assert.assertTrue(CollectionUtils.isNotEmpty(headers.get(Customerticketingc4cintegrationConstants.CONTENT_ID)));
		Assert.assertTrue(headers.get(Customerticketingc4cintegrationConstants.CONTENT_ID).get(0)
				.startsWith(Customerticketingc4cintegrationConstants.CONTENT_ID_VALUE_PREFIX));
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#enrichHeaders(HttpHeaders, String)} should return the enriched http headers
	 * with required data.
	 */
	@Test
	public void shouldEnrichHeaders()
	{
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);
		httpHeaderUtilMock = new HttpHeaderUtil()
		{
			@Override
			public HttpHeaders getDefaultHeaders(final String siteId)
			{
				return mockHttpHeaders;
			}
		};
		MockitoAnnotations.initMocks(this);

		final ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);
		final HttpEntity<String> entity = new HttpEntity<>(mockHttpHeaders);
		Mockito.when(
				restTemplate.exchange(Customerticketingc4cintegrationConstants.URL
						+ Customerticketingc4cintegrationConstants.TICKETING_SUFFIX
						+ Customerticketingc4cintegrationConstants.TOKEN_URL_SUFFIX, HttpMethod.GET, entity, String.class)).thenReturn(
				responseEntity);

		final HttpHeaders returnedHttpHeader = Mockito.mock(HttpHeaders.class);
		Mockito.when(responseEntity.getHeaders()).thenReturn(returnedHttpHeader);
		Mockito.when(returnedHttpHeader.get(Customerticketingc4cintegrationConstants.RESPONSE_COOKIE_NAME)).thenReturn(
				Arrays.asList("response-cookie-name"));
		Mockito.when(Boolean.valueOf(returnedHttpHeader.containsKey(Customerticketingc4cintegrationConstants.TOKEN_NAMING)))
				.thenReturn(Boolean.TRUE);
		Mockito.when(returnedHttpHeader.get(Customerticketingc4cintegrationConstants.TOKEN_NAMING)).thenReturn(
				Arrays.asList("token-naming"));

		final HttpHeaders headers = new HttpHeaders();
		final HttpHeaders enrichedHeaders = httpHeaderUtilMock.enrichHeaders(headers, SITE_ID);

		Mockito.verify(restTemplate).exchange(
				Customerticketingc4cintegrationConstants.URL + Customerticketingc4cintegrationConstants.TICKETING_SUFFIX
						+ Customerticketingc4cintegrationConstants.TOKEN_URL_SUFFIX, HttpMethod.GET, entity, String.class);

		Assert.assertEquals("response-cookie-name", enrichedHeaders.get(HttpHeaders.COOKIE).get(0));
		Assert.assertEquals("token-naming", enrichedHeaders.get(Customerticketingc4cintegrationConstants.TOKEN_NAMING).get(0));
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#updateTicket(TicketData)} should throw IllegalArgumentException when ticket is
	 * completed status.
	 *
	 */
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrownIllegalArgumentExceptionWhenUpdateTicketWithCompletedStatus()
	{
		final TicketData mockedTicketData = Mockito.mock(TicketData.class);
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);

		httpHeaderUtil = new HttpHeaderUtil();
		MockitoAnnotations.initMocks(this);

		Mockito.when(completedStatus.getId()).thenReturn(COMPLETED);

		Mockito.when(mockedTicketData.getStatus()).thenReturn(completedStatus);
		Mockito.when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);

		final TicketData ticketData = new TicketData();
		ticketData.setMessage(MESSAGE);
		final StatusData status = new StatusData();
		status.setId(COMPLETED);
		ticketData.setStatus(status);
		c4CTicketFacade.updateTicket(ticketData);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#updateTicket(TicketData)} should update the ticket when the status is open.
	 *
	 * @throws JsonProcessingException
	 */
	@Test
	public void shouldUpdateTicketWithOpenStatus() throws JsonProcessingException
	{
		final TicketData mockedTicketData = Mockito.mock(TicketData.class);
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);
		c4CTicketFacade = new C4CTicketFacadeImpl() // NOSONAR
		{
			@Override
			public TicketData getTicket(final String ticketId)
			{
				return mockedTicketData;
			}
		};
		MockitoAnnotations.initMocks(this);

		Mockito.when(completedStatus.getId()).thenReturn(COMPLETED);

		Mockito.when(mockedTicketData.getStatus()).thenReturn(completedStatus);

		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);

		final String someJsonString = "some-json-string";
		Mockito.when(jacksonObjectMapper.writeValueAsString(Mockito.any())).thenReturn(someJsonString);
		Mockito.when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);
		final HttpHeaders statusUpdateHeaders = httpHeaderUtil
				.addBatchHeaders("PATCH ServiceTicketCollection('ticket-id') HTTP/1.1");
		final HttpHeaders messageUpdateHeaders = httpHeaderUtil
				.addBatchHeaders("POST ServiceTicketCollection('ticket-id')/Notes HTTP/1.1");

		final HttpEntity<String> statusEntity = new HttpEntity<>(someJsonString, statusUpdateHeaders);
		final HttpEntity<String> messageEntity = new HttpEntity<>(someJsonString, messageUpdateHeaders);

		final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
		final MultiValueMap<String, Object> messagePart = new LinkedMultiValueMap<>();
		parts.add("status", statusEntity);
		messagePart.add(MESSAGE, messageEntity);

		final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, mockHttpHeaders);
		final HttpEntity<MultiValueMap<String, Object>> messageRequestEntity = new HttpEntity<>(messagePart, mockHttpHeaders);

		final ResponseEntity<MultiValueMap> mockResponseEntity = Mockito.mock(ResponseEntity.class);
		Mockito.when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		Mockito.when(mockResponseEntity.getHeaders()).thenReturn(mockHttpHeaders);
		final MultiValueMap<String, String> mockedResponseEntityBody = new LinkedMultiValueMap<>();
		mockedResponseEntityBody.add("anything mocked value", Integer.toString(1));

		Mockito.when(mockResponseEntity.getBody()).thenReturn(mockedResponseEntityBody);
		Mockito.when(
				restTemplate.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.POST), Mockito.eq(requestEntity),
						Mockito.eq(MultiValueMap.class))).thenReturn(mockResponseEntity);
		Mockito.when(
				restTemplate.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.POST), Mockito.eq(messageRequestEntity),
						Mockito.eq(MultiValueMap.class))).thenReturn(mockResponseEntity);


		final TicketData ticketData = new TicketData();
		ticketData.setId("ticket-id");
		ticketData.setMessage(MESSAGE);
		final StatusData status = new StatusData();
		status.setId("OPEN");
		ticketData.setStatus(status);

		final TicketData updatedTicket = c4CTicketFacade.updateTicket(ticketData);
		Mockito.verify(restTemplate).exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.POST), Mockito.eq(requestEntity),
				Mockito.eq(MultiValueMap.class));
		Assert.assertEquals(mockedTicketData, updatedTicket);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#updateTicket(TicketData)} should return null if update ticket with open status
	 * bu the response contains errors.
	 *
	 * @throws JsonProcessingException
	 */
	@Test
	public void shouldReturnNullIfUpdateTicketWithOpenStatusResponseContainsError() throws JsonProcessingException
	{
		final TicketData mockedTicketData = Mockito.mock(TicketData.class);
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);

		MockitoAnnotations.initMocks(this);

		Mockito.when(completedStatus.getId()).thenReturn(COMPLETED);

		Mockito.when(mockedTicketData.getStatus()).thenReturn(completedStatus);

		final CustomerData customer = Mockito.mock(CustomerData.class);
		Mockito.when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		Mockito.when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);

		final String someJsonString = "some-json-string";

		Mockito.when(jacksonObjectMapper.writeValueAsString(Mockito.any())).thenReturn(someJsonString);

		final HttpHeaders statusUpdateHeaders = httpHeaderUtil
				.addBatchHeaders("PATCH ServiceTicketCollection('ticket-id') HTTP/1.1");
		final HttpHeaders messageUpdateHeaders = httpHeaderUtil
				.addBatchHeaders("POST ServiceTicketCollection('ticket-id')/Notes HTTP/1.1");

		final HttpEntity<String> statusEntity = new HttpEntity<>(someJsonString, statusUpdateHeaders);
		final HttpEntity<String> messageEntity = new HttpEntity<>(someJsonString, messageUpdateHeaders);

		final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
		final MultiValueMap<String, Object> messagePart = new LinkedMultiValueMap<>();
		parts.add("status", statusEntity);
		messagePart.add(MESSAGE, messageEntity);

		final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, mockHttpHeaders);
		final HttpEntity<MultiValueMap<String, Object>> messageRequestEntity = new HttpEntity<>(parts, mockHttpHeaders);

		final ResponseEntity<MultiValueMap> mockResponseEntity = Mockito.mock(ResponseEntity.class);
		Mockito.when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
		Mockito.when(mockResponseEntity.getHeaders()).thenReturn(mockHttpHeaders);
		final MultiValueMap<String, String> mockedResponseEntityBody = new LinkedMultiValueMap<>();
		mockedResponseEntityBody.add(Customerticketingc4cintegrationConstants.MULTIPART_HAS_ERROR, "Some mocked errors");

		Mockito.when(mockResponseEntity.getBody()).thenReturn(mockedResponseEntityBody);
		Mockito.when(
				restTemplate.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.POST), Mockito.eq(requestEntity),
						Mockito.eq(MultiValueMap.class))).thenReturn(mockResponseEntity);
		Mockito.when(
				restTemplate.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.POST), Mockito.eq(messageRequestEntity),
						Mockito.eq(MultiValueMap.class))).thenReturn(mockResponseEntity);
		Mockito.when(httpHeaderUtil.getEnrichedHeaders()).thenReturn(mockHttpHeaders);

		final TicketData ticketData = new TicketData();
		ticketData.setId("ticket-id");
		ticketData.setMessage(MESSAGE);
		final StatusData status = new StatusData();
		status.setId("OPEN");
		ticketData.setStatus(status);
		TestUtils.disableFileAnalyzer("Expect to have here an error message.", 20);
		final TicketData updatedTicket = c4CTicketFacade.updateTicket(ticketData);
		Mockito.verify(restTemplate).exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.POST), Mockito.eq(requestEntity),
				Mockito.eq(MultiValueMap.class));
		Assert.assertNull(updatedTicket);
	}

}
