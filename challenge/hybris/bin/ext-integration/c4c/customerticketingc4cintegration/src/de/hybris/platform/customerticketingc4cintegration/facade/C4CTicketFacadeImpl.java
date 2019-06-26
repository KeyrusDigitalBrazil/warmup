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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerticketingc4cintegration.SitePropsHolder;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.data.Note;
import de.hybris.platform.customerticketingc4cintegration.data.ODataListResponseData;
import de.hybris.platform.customerticketingc4cintegration.data.ODataListResultsData;
import de.hybris.platform.customerticketingc4cintegration.data.ODataSingleResponseData;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingc4cintegration.facade.utils.HttpHeaderUtil;
import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * TicketFacade for c4c integration
 */
public class C4CTicketFacadeImpl implements TicketFacade
{
	private final static Logger LOGGER = Logger.getLogger(C4CTicketFacadeImpl.class);

	private ObjectMapper jacksonObjectMapper;
	private Converter<ServiceRequestData, TicketData> ticketConverter;
	private Converter<TicketData, ServiceRequestData> defaultC4CTicketConverter;
	private Converter<TicketData, Note> updateMessageConverter;
	private RestTemplate restTemplate;
	private SitePropsHolder sitePropsHolder;
	private CustomerFacade customerFacade;
	private StatusData completedStatus;
	private C4CBaseFacade c4cBaseFacade;
	private HttpHeaderUtil httpHeaderUtil;

	@Override
	public TicketData createTicket(final TicketData ticket)
	{
		Assert.isTrue(StringUtils.isNotBlank(ticket.getSubject()), Customerticketingc4cintegrationConstants.EMPTY_SUBJECT);
		Assert.isTrue(ticket.getSubject().length() <= 255, Customerticketingc4cintegrationConstants.SUBJECT_EXCEEDS_255_CHARS);
		Assert.isTrue(StringUtils.isNotBlank(ticket.getMessage()), Customerticketingc4cintegrationConstants.EMPTY_MESSAGE);

		LOGGER.debug("Sending request to: " + Customerticketingc4cintegrationConstants.URL
				+ Customerticketingc4cintegrationConstants.TICKETING_SUFFIX);

		try
		{
			//setting customerId explicitly and override the customerUid set from the addon
			ticket.setCustomerId(getCustomerFacade().getCurrentCustomer().getCustomerId());

			final HttpHeaders headers = getHttpHeaderUtil().getEnrichedHeaders();
			final HttpEntity<String> entity = new HttpEntity<>(getJacksonObjectMapper().writeValueAsString(
					getDefaultC4CTicketConverter().convert(ticket)), headers);

			final ResponseEntity<String> result = getRestTemplate().postForEntity(
					Customerticketingc4cintegrationConstants.URL + Customerticketingc4cintegrationConstants.TICKETING_SUFFIX, entity,
					String.class);

			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Response status: " + result.getStatusCode() + "\nResponse headers: " + result.getHeaders()
						+ "\nResponse body: " + result.getBody());
			}

			final ODataSingleResponseData responseData = getJacksonObjectMapper().readValue(result.getBody(),
					ODataSingleResponseData.class);

			return getTicketConverter().convert(responseData.getD().getResults());
		}
		catch (final JsonProcessingException e)
		{
			throw new RuntimeException("Can't write ticketData to request", e);
		}
		catch (final IOException e)
		{
			throw new RuntimeException("Can't read ticketData from response", e);
		}
		catch (final RestClientException e)
		{
			throw new RuntimeException("Can't send request", e);
		}
	}

	@Override
	public TicketData updateTicket(final TicketData ticket)
	{
		Assert.isTrue(StringUtils.isNotBlank(ticket.getMessage()), "Message can't be empty");
		try
		{
			//setting customerId explicitly and override the customerUid set from the addon
			ticket.setCustomerId(getCustomerFacade().getCurrentCustomer().getCustomerId());

			final HttpHeaders updateTicketHeaders = getHttpHeaderUtil().getEnrichedHeaders();

			updateTicketHeaders.set(HttpHeaders.CONTENT_TYPE, Customerticketingc4cintegrationConstants.MULTIPART_MIXED_MODE);

			final HttpHeaders statusUpdateHeaders = getHttpHeaderUtil().addBatchHeaders(
					"PATCH ServiceTicketCollection('" + ticket.getId() + "') HTTP/1.1");

			final HttpHeaders messageUpdateHeaders = getHttpHeaderUtil().addBatchHeaders(
					"POST ServiceTicketCollection('" + ticket.getId() + "')/Notes HTTP/1.1");


			final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
			final MultiValueMap<String, Object> messagePart = new LinkedMultiValueMap<String, Object>();

			final HttpEntity<String> statusEntity = new HttpEntity<>(getJacksonObjectMapper().writeValueAsString(
					getDefaultC4CTicketConverter().convert(ticket)), statusUpdateHeaders);

			final HttpEntity<String> messageEntity = new HttpEntity<>(getJacksonObjectMapper().writeValueAsString(
					getUpdateMessageConverter().convert(ticket)), messageUpdateHeaders);

			if (getCompletedStatus().getId().equalsIgnoreCase(ticket.getStatus().getId())
					|| "CLOSED".equalsIgnoreCase(ticket.getStatus().getId()))
			{
				if (getTicket(ticket.getId()).getStatus().getId().equals(ticket.getStatus().getId())) // so status doesn't changed
				{
					throw new IllegalArgumentException("You can not add a message to a completed ticket. Please, reopen the ticket");
				}

				parts.add("message", messageEntity);
				parts.add("status", statusEntity);
			}
			else
			{
				parts.add("status", statusEntity);
				messagePart.add("message", messageEntity); //no batch call in case ticket is closed
			}

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(parts, updateTicketHeaders);


			final URI uri = UriComponentsBuilder
					.fromHttpUrl(Customerticketingc4cintegrationConstants.URL + Customerticketingc4cintegrationConstants.BATCH_SUFFIX)
					.build().encode().toUri();

			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Result uri for status update: " + uri);
				LOGGER.debug("Request headers for status update: " + requestEntity.getHeaders());
				LOGGER.debug("Request body for status update: " + requestEntity.getBody());
			}

			ResponseEntity<MultiValueMap> result = getRestTemplate().exchange(uri, HttpMethod.POST, requestEntity,
					MultiValueMap.class);

			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Response status: " + result.getStatusCode() + "\nResponse headers: " + result.getHeaders()
						+ "\nResponse body: " + result.getBody());
			}

			if (result.getBody().containsKey(Customerticketingc4cintegrationConstants.MULTIPART_HAS_ERROR))
			{
				LOGGER.error("Error happend during update for ticket (" + ticket.getId() + ")");
				if (null != result.getBody().get(Customerticketingc4cintegrationConstants.MULTIPART_ERROR_MESSAGE))
				{
					LOGGER.error(result.getBody().get(Customerticketingc4cintegrationConstants.MULTIPART_ERROR_MESSAGE));
				}

				return null;
			}
			if (!(getCompletedStatus().getId().equalsIgnoreCase(ticket.getStatus().getId()) || "CLOSED".equalsIgnoreCase(ticket
					.getStatus().getId())))
			{
				//second call in case ticket is closed
				requestEntity = new HttpEntity(messagePart, updateTicketHeaders);

				if (LOGGER.isDebugEnabled())
				{
					LOGGER.debug("Result uri for status update: " + uri);
					LOGGER.debug("Request headers for status update: " + requestEntity.getHeaders());
					LOGGER.debug("Request body for status update: " + requestEntity.getBody());
				}

				result = getRestTemplate().exchange(uri, HttpMethod.POST, requestEntity, MultiValueMap.class);

				if (LOGGER.isDebugEnabled())
				{
					LOGGER.debug("Response status: " + result.getStatusCode() + "\nResponse headers: " + result.getHeaders()
							+ "\nResponse body: " + result.getBody());
				}

				if (result.getBody().containsKey(Customerticketingc4cintegrationConstants.MULTIPART_HAS_ERROR))
				{
					LOGGER.error("Error happend during update for ticket (" + ticket.getId() + ")");
					if (null != result.getBody().get(Customerticketingc4cintegrationConstants.MULTIPART_ERROR_MESSAGE))
					{
						LOGGER.error(result.getBody().get(Customerticketingc4cintegrationConstants.MULTIPART_ERROR_MESSAGE));
					}

					return null;
				}
			}

			return getTicket(ticket.getId());

		}
		catch (final JsonProcessingException e)
		{
			throw new RuntimeException("Can't write ticketData to request", e);
		}

		catch (final RestClientException e)
		{
			throw new RuntimeException("Can't send request", e);
		}
	}

	@Override
	public TicketData getTicket(final String ticketId)
	{
		validateParameterNotNullStandardMessage("ticketId", ticketId);

		final UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(Customerticketingc4cintegrationConstants.URL + Customerticketingc4cintegrationConstants.TICKETING_SUFFIX)
				.queryParam(
						Customerticketingc4cintegrationConstants.FILETR_SUFFIX,
						(getSitePropsHolder().isB2C() ? String.format("ExternalCustomerID eq '%s'", getCustomerFacade()
								.getCurrentCustomer().getCustomerId()) : String.format("ExternalContactID eq '%s'", getCustomerFacade()
								.getCurrentCustomer().getCustomerId()))
								+ String.format("and ObjectID eq '%s'", ticketId))
				.query(Customerticketingc4cintegrationConstants.EXPAND_SUFFIX);

		LOGGER.debug("Result uri: " + builder.build().encode().toUri());

		try
		{
			final HttpHeaders headers = getHttpHeaderUtil().getEnrichedHeaders();
			final HttpEntity<String> entity = new HttpEntity<>(headers);

			final ResponseEntity<String> result = getRestTemplate().exchange(builder.build().encode().toUri(), HttpMethod.GET,
					entity, String.class);

			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Response status: " + result.getStatusCode() + "\nResponse headers: " + result.getHeaders()
						+ "\nResponse body: " + result.getBody());
			}

			final List<TicketData> dataList = getJacksonObjectMapper().readValue(result.getBody(), ODataListResponseData.class)
					.getD().getResults().stream().map(getTicketConverter()::convert).collect(Collectors.toList());

			LOGGER.debug(dataList);

			return dataList.isEmpty() ? null : dataList.get(0);
		}
		catch (final IOException e)
		{
			throw new RuntimeException("Can't convert ticketData", e);
		}
		catch (final RestClientException e)
		{
			throw new RuntimeException("Can't send request", e);
		}
	}

	@Override
	public SearchPageData<TicketData> getTickets(final PageableData pageableData)
	{
		final int skip = pageableData.getPageSize() * pageableData.getCurrentPage();
		final int top = pageableData.getPageSize();
		final String sorting = StringUtils.isNotBlank(pageableData.getSort()) ? pageableData.getSort()
				: Customerticketingc4cintegrationConstants.ORDER_DEFAULT_VALUE;
		LOGGER.debug("Sorting: " + sorting);
		final UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(Customerticketingc4cintegrationConstants.URL + Customerticketingc4cintegrationConstants.TICKETING_SUFFIX)
				.queryParam(
						Customerticketingc4cintegrationConstants.FILETR_SUFFIX,
						getSitePropsHolder().isB2C() ? String.format("ExternalCustomerID eq '%s'", getCustomerFacade()
								.getCurrentCustomer().getCustomerId()) : String.format("ExternalContactID eq '%s'", getCustomerFacade()
								.getCurrentCustomer().getCustomerId()))
				.query(Customerticketingc4cintegrationConstants.ORDER_BY_SUFFIX + sorting + " desc")
				.queryParam(Customerticketingc4cintegrationConstants.PAGING_SKIP_SUFFIX, Integer.valueOf(skip))
				.queryParam(Customerticketingc4cintegrationConstants.PAGING_TOP_SUFFIX, Integer.valueOf(top))
				.query(Customerticketingc4cintegrationConstants.PAGING_COUNT_SUFFIX)
				.query(Customerticketingc4cintegrationConstants.EXPAND_SUFFIX);
		LOGGER.debug("Result uri: " + builder.build().encode().toUri());
		try
		{
			final HttpHeaders headers = getHttpHeaderUtil().getEnrichedHeaders();
			final HttpEntity<String> entity = new HttpEntity<>(headers);

			final ResponseEntity<String> result = getRestTemplate().exchange(builder.build().encode().toUri(), HttpMethod.GET,
					entity, String.class);

			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Response status: " + result.getStatusCode() + "\nResponse headers: " + result.getHeaders()
						+ "\nResponse body: " + result.getBody());
			}

			final ODataListResultsData oDataListResultsData = getJacksonObjectMapper().readValue(result.getBody(),
					ODataListResponseData.class).getD();

			LOGGER.debug(oDataListResultsData.get__count());
			return getC4cBaseFacade().convertPageData(oDataListResultsData.getResults(), getTicketConverter(), pageableData,
					Integer.parseInt(oDataListResultsData.get__count()));
		}
		catch (final IOException e)
		{
			LOGGER.error("Can't convert ticketData" + e);
		}
		catch (final RestClientException e)
		{
			LOGGER.error("Can't send request" + e);
		}

		return getC4cBaseFacade().convertPageData(Collections.emptyList(), getTicketConverter(), pageableData, 0);
	}



	public RestTemplate getRestTemplate()
	{
		return restTemplate;
	}

	public void setRestTemplate(final RestTemplate restTemplate)
	{
		this.restTemplate = restTemplate;
	}

	public Converter<ServiceRequestData, TicketData> getTicketConverter()
	{
		return ticketConverter;
	}

	public void setTicketConverter(final Converter<ServiceRequestData, TicketData> ticketConverter)
	{
		this.ticketConverter = ticketConverter;
	}

	public ObjectMapper getJacksonObjectMapper()
	{
		return jacksonObjectMapper;
	}

	public void setJacksonObjectMapper(final ObjectMapper jacksonObjectMapper)
	{
		this.jacksonObjectMapper = jacksonObjectMapper;
	}

	public Converter<TicketData, ServiceRequestData> getDefaultC4CTicketConverter()
	{
		return defaultC4CTicketConverter;
	}

	public void setDefaultC4CTicketConverter(final Converter<TicketData, ServiceRequestData> defaultC4CTicketConverter)
	{
		this.defaultC4CTicketConverter = defaultC4CTicketConverter;
	}

	public Converter<TicketData, Note> getUpdateMessageConverter()
	{
		return updateMessageConverter;
	}

	public void setUpdateMessageConverter(final Converter<TicketData, Note> updateMessageConverter)
	{
		this.updateMessageConverter = updateMessageConverter;
	}

	@Override
	public Map<String, List<TicketAssociatedData>> getAssociatedToObjects()
	{
		throw new UnsupportedOperationException("It has not been implemeted for C4C yet.....");
	}

	@Override
	public List<TicketCategory> getTicketCategories()
	{
		throw new UnsupportedOperationException("It has not been implemeted for C4C yet.....");
	}

	protected SitePropsHolder getSitePropsHolder()
	{
		return sitePropsHolder;
	}

	@Required
	public void setSitePropsHolder(final SitePropsHolder sitePropsHolder)
	{
		this.sitePropsHolder = sitePropsHolder;
	}

	protected CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

	@Required
	public void setCustomerFacade(final CustomerFacade customerFacade)
	{
		this.customerFacade = customerFacade;
	}

	protected StatusData getCompletedStatus()
	{
		return completedStatus;
	}

	@Required
	public void setCompletedStatus(final StatusData completedStatus)
	{
		this.completedStatus = completedStatus;
	}

	protected C4CBaseFacade getC4cBaseFacade()
	{
		return c4cBaseFacade;
	}

	@Required
	public void setC4cBaseFacade(final C4CBaseFacade c4cBaseFacade)
	{
		this.c4cBaseFacade = c4cBaseFacade;
	}

	protected HttpHeaderUtil getHttpHeaderUtil()
	{
		return httpHeaderUtil;
	}

	@Required
	public void setHttpHeaderUtil(final HttpHeaderUtil httpHeaderUtil)
	{
		this.httpHeaderUtil = httpHeaderUtil;
	}
}