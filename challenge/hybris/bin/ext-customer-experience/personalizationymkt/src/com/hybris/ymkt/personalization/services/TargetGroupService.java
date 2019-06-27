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
/**
 *
 */
package com.hybris.ymkt.personalization.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.http.HttpURLConnectionResponse;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.personalization.dto.MKTTargetGroup;


/**
 * Service exposing target groups from API_MKT_TARGET_GROUP_SRV.
 */
public class TargetGroupService
{
	protected static final int MAX = 100;
	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();
	protected static final String TARGET_GROUP_INTERACTION_CONTACTS_ENTITY_SET = "TargetGroupInteractionContacts";
	protected static final String TARGET_GROUP_UUID = "TargetGroupUUID";
	protected static final String TARGET_GROUPS_ENTITY_SET = "TargetGroups";

	protected final Map<UUID, String> guidIdCache = new ConcurrentHashMap<>();
	protected ODataService oDataService;

	@Nonnull
	protected static <T> List<List<T>> partition(final List<T> list, final int size)
	{
		final List<List<T>> lists = new ArrayList<>(list.size() / size + 1);
		for (int index = 0; index < list.size(); index += size)
		{
			final int min = Math.min(list.size() - index, size);
			final List<T> subList = list.subList(index, index + min);
			lists.add(subList);
		}
		return lists;
	}

	protected void addTargetGroupToCache(final MKTTargetGroup tg)
	{
		this.guidIdCache.put(tg.getTargetGroupUUID(), tg.getTargetGroup());
	}

	protected MKTTargetGroup createMKTTargetGroup(final ODataEntry entry)
	{
		final MKTTargetGroup sapTargetGroup = new MKTTargetGroup();
		final Map<String, Object> entryMap = entry.getProperties();

		sapTargetGroup.setTargetGroupUUID((UUID) entryMap.get(TARGET_GROUP_UUID));
		sapTargetGroup.setTargetGroup(entryMap.get("TargetGroup").toString());
		sapTargetGroup.setTargetGroupName(entryMap.get("TargetGroupName").toString());
		sapTargetGroup.setTargetGroupDescription(entryMap.get("TargetGroupDescription").toString());
		sapTargetGroup.setTargetGroupMemberCount((int) entryMap.get("TargetGroupMemberCount"));
		sapTargetGroup.setMarketingArea(entryMap.get("MarketingArea").toString());
		return sapTargetGroup;
	}

	/**
	 * Get all the Target Group objects containing more descriptive information from the GUIDs retrieved
	 *
	 * @param guids
	 *           Target Group GUIDs.
	 * @return {@link List} of {@link String} Target Group IDs.
	 * @throws IOException
	 *            if any error occurs.
	 */
	@Nonnull
	public List<String> getCustomerTargetGroupIds(final List<UUID> guids) throws IOException
	{
		final Predicate<UUID> cached = this.guidIdCache::containsKey;
		final Predicate<UUID> notCached = cached.negate();

		final List<UUID> unknownGuids = guids.stream().filter(notCached).collect(Collectors.toList());

		this.getCustomerTargetGroups(unknownGuids).forEach(this::addTargetGroupToCache);

		return guids.stream().map(this.guidIdCache::get).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * Get all the Target Group objects containing more descriptive information from the GUIDs retrieved
	 *
	 * @param guids
	 *           Target Group GUIDs.
	 * @return {@link List} of {@link MKTTargetGroup}
	 * @throws IOException
	 *            if any error occurs.
	 */
	@Nonnull
	public List<MKTTargetGroup> getCustomerTargetGroups(final List<UUID> guids) throws IOException
	{
		final List<MKTTargetGroup> completeList = new ArrayList<>(guids.size());

		for (final List<UUID> list : TargetGroupService.partition(guids, MAX))
		{
			completeList.addAll(this.getCustomerTargetGroups(list, MAX));
		}

		return completeList;
	}

	@Nonnull
	protected List<MKTTargetGroup> getCustomerTargetGroups(final List<UUID> guids, final int max) throws IOException
	{
		try
		{
			final String filter = this.oDataService.filter(TARGET_GROUPS_ENTITY_SET).on(TARGET_GROUP_UUID).eq(guids).toExpression();
			final URL url = this.oDataService.createURL(TARGET_GROUPS_ENTITY_SET, //
					"$filter", filter, //
					"$top", Integer.toString(max));

			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);

			request.getRequestProperties().put("Accept", MediaType.APPLICATION_JSON);
			request.setReadTimeout(300000);

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEntitySet(TARGET_GROUPS_ENTITY_SET);
			final InputStream content = new ByteArrayInputStream(response.getPayload());
			final ODataFeed feed = EntityProvider.readFeed(MediaType.APPLICATION_JSON, entitySet, content, NO_READ_PROPERTIES);
			return feed.getEntries().stream() //
					.map(this::createMKTTargetGroup) //
					.collect(Collectors.toList());
		}
		catch (final ODataException e)
		{
			throw new IOException("Error using/parsing entitySet TargetGroupInteractionContacts.", e);
		}
	}

	/**
	 * Retrieves all the Target Group GUIDs from that specific interaction contact origin Id
	 *
	 * @param contactId
	 *           Contact Id value.
	 * @param contactOrigin
	 *           COOKIE_ID or SAP_HYBRIS_CONSUMER
	 * @return {@link List} of {@link MKTTargetGroup}
	 * @throws IOException
	 *            if any error occurs.
	 */
	@Nonnull
	public List<UUID> getCustomerTargetGroupsGUIDs(final String contactId, final String contactOrigin) throws IOException
	{
		try
		{
			final String filter = this.oDataService.filter(TARGET_GROUP_INTERACTION_CONTACTS_ENTITY_SET) //
					.on("InteractionContactOrigin").eq(contactOrigin) //
					.and("InteractionContactId").eq(contactId) //
					.toExpression();
			final URL url = this.oDataService.createURL(TARGET_GROUP_INTERACTION_CONTACTS_ENTITY_SET, //
					"$filter", filter, //
					"$top", "1000");

			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);

			request.getRequestProperties().put("Accept", MediaType.APPLICATION_JSON);

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEntitySet(TARGET_GROUP_INTERACTION_CONTACTS_ENTITY_SET);
			final InputStream content = new ByteArrayInputStream(response.getPayload());
			final ODataFeed feed = EntityProvider.readFeed(MediaType.APPLICATION_JSON, entitySet, content, NO_READ_PROPERTIES);
			return feed.getEntries().stream() //
					.map(ODataEntry::getProperties) //
					.map(e -> e.get(TARGET_GROUP_UUID)) //
					.map(UUID.class::cast) //
					.distinct() // service returns duplicates
					.collect(Collectors.toList());
		}
		catch (final ODataException e)
		{
			throw new IOException("Error using/parsing entitySet TargetGroupInteractionContacts.", e);
		}
	}

	@Required
	public void setODataService(final ODataService oDataService)
	{
		this.oDataService = oDataService;
	}

}
