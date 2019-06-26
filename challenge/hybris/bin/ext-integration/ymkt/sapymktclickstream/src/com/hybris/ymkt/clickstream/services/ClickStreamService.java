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
package com.hybris.ymkt.clickstream.services;

import de.hybris.eventtracking.model.events.AbstractProductAndCartAwareTrackingEvent;
import de.hybris.eventtracking.model.events.AbstractProductAwareTrackingEvent;
import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.eventtracking.model.events.AddToCartEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.common.product.ProductURLService;


/**
 * Send {@link AbstractTrackingEvent} to CUAN_IMPORT_SRV
 */
public class ClickStreamService
{
	protected static final String IMPORT_HEADERS = "ImportHeaders";

	private static final Logger LOG = LoggerFactory.getLogger(ClickStreamService.class);

	protected static final String SAP_MERCH_SHOP = "SAP_MERCH_SHOP";

	protected static final Short SHORT_ONE = Short.valueOf((short) 1);
	protected static final Short SHORT_ZERO = Short.valueOf((short) 0);

	protected static final String SOURCE_OBJECT_ID = "SourceObjectId";
	protected static final String SOURCE_OBJECT_TYPE = "SourceObjectType";
	protected static final String SOURCE_SYSTEM_ID = "SourceSystemId";
	protected static final String SOURCE_SYSTEM_TYPE = "SourceSystemType";

	protected static final Predicate<String> STRING_IS_EMPTY = String::isEmpty;
	protected static final Predicate<String> STRING_IS_EMPTY_NOT = STRING_IS_EMPTY.negate();

	protected static final String URL_PK_CAMPAIGN = "pk_campaign=";
	protected static final String URL_SAP_OUTBOUND_ID = "sap-outbound-id=";

	protected boolean linkAnonymousAndLoggedInUsers;
	protected final Map<String, String> interactionTypes = new HashMap<>();
	protected ODataService oDataService;
	protected ProductURLService productURLService;

	protected byte[] compressGZIP(final byte[] payload) throws IOException
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (final GZIPOutputStream gzos = new GZIPOutputStream(baos))
		{
			gzos.write(payload);
		}
		return baos.toByteArray();
	}

	protected Map<String, Object> createImportHeader()
	{
		final Map<String, Object> map = new LinkedHashMap<>();
		map.put("Id", "");
		map.put(SOURCE_SYSTEM_TYPE, "COM");
		map.put(SOURCE_SYSTEM_ID, SAP_MERCH_SHOP);
		return map;
	}

	protected Map<String, Object> createInteraction(final AbstractTrackingEvent event)
	{
		final Map<String, Object> interaction = new HashMap<>();

		this.populateInteraction(interaction, event);

		if (event instanceof AbstractProductAwareTrackingEvent)
		{
			Optional.of(event) //
					.map(AbstractProductAwareTrackingEvent.class::cast) //
					.map(AbstractProductAwareTrackingEvent::getProductName) //
					.filter(STRING_IS_EMPTY_NOT) //
					.ifPresent(name -> interaction.put("ContentTitle", name));

			interaction.put("Products", this.createInteractionProduct((AbstractProductAwareTrackingEvent) event));
		}

		return interaction;
	}

	protected Map<String, Object> createInteractionProduct(final AbstractProductAwareTrackingEvent event)
	{
		final Map<String, Object> product = new HashMap<>();

		product.put("ItemId", event.getProductId());
		product.put("ItemType", "SAP_HYBRIS_PRODUCT");

		Optional.ofNullable(event.getProductName()) //
				.filter(STRING_IS_EMPTY_NOT) //
				.ifPresent(name -> product.put("Name", name));

		product.put("NavigationURL", this.productURLService.getProductURL(event.getProductId()));

		if (event instanceof AbstractProductAndCartAwareTrackingEvent)
		{
			product.put("Key", ((AbstractProductAndCartAwareTrackingEvent) event).getCartId());
		}

		if (event instanceof AddToCartEvent)
		{
			product.put("Quantity", ((AddToCartEvent) event).getQuantity());
		}

		product.put(SOURCE_SYSTEM_ID, SAP_MERCH_SHOP);

		return product;
	}

	protected String extractInitiativeId(final String url1, final String url2)
	{
		final String param1 = this.extractURLParameter(URL_PK_CAMPAIGN, url1);
		final String param2 = this.extractURLParameter(URL_PK_CAMPAIGN, url2);
		return Optional.ofNullable(param1).orElse(param2);
	}

	protected String extractSAPTrackingId(final String url1, final String url2)
	{
		final String param1 = this.extractURLParameter(URL_SAP_OUTBOUND_ID, url1);
		final String param2 = this.extractURLParameter(URL_SAP_OUTBOUND_ID, url2);
		return Optional.ofNullable(param1).orElse(param2);
	}

	protected String extractURLParameter(final String parameterName, final String url)
	{
		if (url == null)
		{
			return null;
		}
		final int beginIndex = url.indexOf(parameterName) + parameterName.length();
		if (beginIndex == parameterName.length() - 1)
		{
			return null;
		}
		final int endIndex = url.indexOf('&', beginIndex);
		return endIndex == -1 ? url.substring(beginIndex) : url.substring(beginIndex, endIndex);
	}

	protected int getReadTimeout()
	{
		return 300000; // 5 Minutes
	}

	protected void populateInteraction(final Map<String, Object> interaction, final AbstractTrackingEvent event)
	{
		final String sapTrackingId = this.extractSAPTrackingId(event.getPageUrl(), event.getRefUrl());
		final boolean isTrackingId = sapTrackingId != null && !sapTrackingId.isEmpty();

		final String contactId = isTrackingId ? sapTrackingId : event.getYmktContactId();
		final String contactIdOrigin = isTrackingId ? "SAP_TRACKING_ID" : event.getYmktContactIdOrigin();

		// No anonymous tracking
		final boolean isAnonymous = contactId == null && contactIdOrigin == null;

		interaction.put("Key", Integer.toHexString(event.hashCode()));
		interaction.put("CommunicationMedium", "ONLINE_SHOP");

		if (!isAnonymous)
		{
			interaction.put("ContactId", contactId);
			interaction.put("ContactIdOrigin", contactIdOrigin);
		}

		Optional.ofNullable(this.extractInitiativeId(event.getPageUrl(), event.getRefUrl())) //
				.filter(STRING_IS_EMPTY_NOT) //
				.ifPresent(initiativeId -> interaction.put("InitiativeId", initiativeId));

		interaction.put("InteractionType", this.interactionTypes.get(event.getEventType()));
		interaction.put("IsAnonymous", Boolean.valueOf(isAnonymous));
		interaction.put("Quantifier", SHORT_ONE);
		interaction.put("SourceDataUrl", event.getPageUrl());

		interaction.put(SOURCE_OBJECT_ID, event.getSessionId());
		interaction.put(SOURCE_OBJECT_TYPE, "WEB_SESSION");
		interaction.put(SOURCE_SYSTEM_ID, SAP_MERCH_SHOP);
		interaction.put(SOURCE_SYSTEM_TYPE, "COM");
		interaction.put("Timestamp", Long.parseLong(event.getInteractionTimestamp()) * 1000);
		interaction.put("Valuation", SHORT_ZERO);

		if (isTrackingId)
		{
			final Map<String, Object> reference = new HashMap<>();
			reference.put("ObjectId", sapTrackingId);
			reference.put("ObjectType", "CUAN_CAMPAIGN_OUTBOUND");
			interaction.put("AdditionalObjectReferences", reference);
		}
	}

	/**
	 * Transform and send the {@link AbstractTrackingEvent}s to yMKT.
	 *
	 * @param events
	 *           {@link List} of {@link AbstractTrackingEvent} to send to yMKT.
	 * @return true if the transfer was successful. false otherwise.
	 */
	public boolean sendEvents(final List<? extends AbstractTrackingEvent> events)
	{
		try
		{
			final URL url = this.oDataService.createURL(IMPORT_HEADERS);
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("POST", url);
			request.getRequestProperties().put("Accept", MediaType.APPLICATION_JSON);
			request.getRequestProperties().put("Content-Type", MediaType.APPLICATION_JSON);
			request.setReadTimeout(this.getReadTimeout());

			final Map<String, Object> data = this.createImportHeader();

			final List<Map<String, Object>> interactions = events.stream().map(this::createInteraction).collect(Collectors.toList());

			data.put("Interactions", interactions);

			final byte[] payload = this.oDataService.convertMapToJSONPayload(IMPORT_HEADERS, data);
			final byte[] payloadGZIP = this.compressGZIP(payload);
			request.getRequestProperties().put("Content-Encoding", "gzip");
			request.setPayload(payloadGZIP);
			this.oDataService.executeWithRetry(request);
			return true;
		}
		catch (final IOException e)
		{
			LOG.error("Error sending '{}' events to YMKT", events.size(), e);
			return false;
		}
	}

	/**
	 *
	 * Links the anonymous and logged-in users by making the anonymous user a facet of the logged-in user.<br>
	 * In SAP Marketing, the anonymous & logged-in events are linked to a single contact golden record.
	 *
	 * @param anonymousUserId
	 *           User ID before login or register.
	 * @param anonymousUserOrigin
	 *           yMKT Origin ID.
	 * @param loggedInUserId
	 *           User ID after login or register.
	 * @param loggedInUserOrigin
	 *           yMKT Origin ID.
	 *
	 * @return <code>true</code> if the transfer was successful. <code>false</code> otherwise.
	 */
	public boolean linkAnonymousAndLoggedInUsers(final String anonymousUserId, final String anonymousUserOrigin,
			final String loggedInUserId, final String loggedInUserOrigin)
	{
		if (!this.linkAnonymousAndLoggedInUsers)
		{
			return false;
		}

		try
		{
			final URL url = this.oDataService.createURL(IMPORT_HEADERS);
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("POST", url);
			request.getRequestProperties().put("Accept", MediaType.APPLICATION_JSON);
			request.getRequestProperties().put("Content-Type", MediaType.APPLICATION_JSON);
			request.setReadTimeout(this.getReadTimeout());

			final Map<String, Object> facet = new HashMap<>();
			facet.put("Id", loggedInUserId);
			facet.put("IdOrigin", loggedInUserOrigin);

			final Map<String, Object> contact = new HashMap<>();
			contact.put("Id", anonymousUserId);
			contact.put("IdOrigin", anonymousUserOrigin);
			contact.put("Timestamp", new Date());
			contact.put("Facets", facet);

			final Map<String, Object> importHeader = this.createImportHeader();
			importHeader.put("Contacts", contact);

			final byte[] payload = this.oDataService.convertMapToJSONPayload(IMPORT_HEADERS, importHeader);
			final byte[] payloadGZIP = this.compressGZIP(payload);
			request.getRequestProperties().put("Content-Encoding", "gzip");
			request.setPayload(payloadGZIP);
			this.oDataService.executeWithRetry(request);
			return true;
		}
		catch (final IOException e)
		{
			LOG.error("Error sending anonymous facet to YMKT", e);
			return false;
		}
	}

	/**
	 * Build a map to link commerce event to yMKT event type.<br>
	 * SPRO - SAP Customizing Implementation Guide - SAP Marketing - Contacts and Profiles - Interactions - Define
	 * Interaction Types : <br>
	 * <ul>
	 * <li>PROD_REVIEW_VIEW - Product Review Read</li>
	 * <li>SHOP_CART_ABANDONED - Shopping Cart Abandoned</li>
	 * <li>SHOP_CART_VIEW - View Shopping Cart</li>
	 * <li><strike>SHOP_CHECKOUT_ABNDND - Checkout Abandoned</strike>. No matching commerce event.</li>
	 * <li>SHOP_CHECKOUT_START - Proceeded to Checkout</li>
	 * <li>SHOP_CHECKOUT_SUCCES - Checkout Successful</li>
	 * <li>SHOP_ITEM_ADD - Product Added to Shopping Cart</li>
	 * <li>SHOP_ITEM_REMOVE - Product Removed from Shopping Cart</li>
	 * <li>SHOP_ITEM_VIEW - Product Viewed</li>
	 * </ul>
	 *
	 * @param interactionTypeMapping
	 *           Mapping from ECP event type to yMKT interaction type.
	 */
	@Required
	public void setInteractionTypeMapping(final Map<String, String> interactionTypeMapping)
	{
		LOG.debug("interactionTypeMapping={}", interactionTypeMapping);
		this.interactionTypes.clear();
		interactionTypeMapping.forEach((k, v) -> this.interactionTypes.put(k.intern(), v.intern()));
	}

	@Required
	public void setODataService(final ODataService oDataService)
	{
		this.oDataService = oDataService;
	}

	@Required
	public void setProductURLService(final ProductURLService productURLService)
	{
		this.productURLService = productURLService;
	}

	@Required
	public void setLinkAnonymousAndLoggedInUsers(final boolean linkAnonymousAndLoggedInUsers)
	{
		LOG.debug("linkAnonymousAndLoggedInUsers={}", linkAnonymousAndLoggedInUsers);
		this.linkAnonymousAndLoggedInUsers = linkAnonymousAndLoggedInUsers;
	}
}