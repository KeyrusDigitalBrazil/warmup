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
package com.hybris.ymkt.segmentation.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.http.HttpURLConnectionResponse;
import com.hybris.ymkt.common.odata.ODataFilterBuilder.ODataFilterPredicate;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.segmentation.dto.SAPInitiative;
import com.hybris.ymkt.segmentation.services.InitiativeService.InitiativeQuery.TileFilterCategory;


/**
 * Service exposing initiatives from CUAN_INITIATIVE_SRV.
 */
public class InitiativeService
{
	public static class InitiativeQuery
	{
		public static class Builder
		{
			private String[] contactOrigins = ZERO_STRINGS;
			private String[] contacts = ZERO_STRINGS;
			private boolean isFilterByUserContext;
			private String id = "";
			private String[] searchTerms = ZERO_STRINGS;
			private TileFilterCategory[] tileFilterCategories = ZERO_TILE_FILTER_CATEGORIES;

			public InitiativeQuery build()
			{
				return new InitiativeQuery(this);
			}

			public Builder setContactOrigins(final String... contactOrigins)
			{
				this.contactOrigins = contactOrigins;
				return this;
			}

			public Builder setContacts(final String... contacts)
			{
				this.contacts = contacts;
				return this;
			}

			public Builder filterByUserContext(final boolean isFilterByUserContext)
			{
				this.isFilterByUserContext = isFilterByUserContext;
				return this;
			}

			public Builder setId(final String id)
			{
				this.id = id;
				return this;
			}

			public Builder setSearchTerms(final String... searchTerms)
			{
				this.searchTerms = searchTerms;
				return this;
			}

			public Builder setTileFilterCategories(final TileFilterCategory... tileFilterCategories)
			{
				this.tileFilterCategories = tileFilterCategories;
				return this;
			}
		}

		public enum TileFilterCategory
		{
			ACTIVE("1"), PLANNED("2");

			private final String oDataValue;

			private TileFilterCategory(final String oDataValue)
			{
				this.oDataValue = oDataValue;
			}
		}

		private final String[] contactOrigins;
		private final String[] contacts;
		private final boolean filterByUserContext;
		private final String id;
		private final String[] searchTerms;
		private final TileFilterCategory[] tileFilterCategories;

		private InitiativeQuery(final Builder builder)
		{
			contactOrigins = builder.contactOrigins;
			contacts = builder.contacts;
			filterByUserContext = builder.isFilterByUserContext;
			id = builder.id;
			searchTerms = builder.searchTerms;
			tileFilterCategories = builder.tileFilterCategories;
		}

		@Override
		public String toString()
		{
			final StringBuilder builder = new StringBuilder();
			builder.append("InitiativeQuery [filterByUserContext=");
			builder.append(filterByUserContext);
			builder.append(", id=");
			builder.append(id);
			builder.append(", searchTerms=");
			builder.append(Arrays.toString(searchTerms));
			builder.append(", tileFilterCategories=");
			builder.append(Arrays.toString(tileFilterCategories));
			builder.append(", contacts=");
			builder.append(Arrays.toString(contacts));
			builder.append(", contactOrigins=");
			builder.append(Arrays.toString(contactOrigins));
			builder.append("]");
			return builder.toString();
		}
	}

	protected static final String APPLICATION_JSON = "application/json";

	protected static final String INITIATIVES = "Initiatives";

	private static final Logger LOG = LoggerFactory.getLogger(InitiativeService.class);

	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();

	protected static final String SELECT_FIELDS = "InitiativeId,Name,TargetGroup/CustomerMemberCount";
	protected static final String TARGET_GROUP = "TargetGroup";

	protected static final String[] ZERO_STRINGS = new String[0];
	protected static final TileFilterCategory[] ZERO_TILE_FILTER_CATEGORIES = new TileFilterCategory[0];

	protected String campaignCategoryId;
	protected String campaignOrderBy;

	protected ODataService oDataService;
	protected UserContextService userContextService;

	protected String buildFilterOption(InitiativeQuery query) throws EdmException, IOException
	{

		if (!query.id.isEmpty())
		{
			return this.oDataService.filter(INITIATIVES).on("InitiativeId").eq(query.id).toExpression();
		}

		ODataFilterPredicate predicate = this.oDataService.filter(INITIATIVES).on("Category/CategoryCode").eq(campaignCategoryId);

		predicate = predicate.and("Search/SearchTerm").eq(query.searchTerms);

		final Object[] categories = Stream.of(query.tileFilterCategories).map(t -> t.oDataValue).toArray();
		predicate = predicate.and("Search/TileFilterCategory").eq(categories);

		final Set<String> contacts = new HashSet<>(Arrays.asList(query.contacts));
		final Set<String> contactOrigins = new HashSet<>(Arrays.asList(query.contactOrigins));

		if (query.filterByUserContext)
		{
			contacts.add(getInteractionContactId());
			contactOrigins.add(userContextService.getUserOrigin());
		}

		predicate = predicate.and("Filter/InteractionContactId").eq(contacts);

		predicate = predicate.and("Filter/InteractionContactIdOrigin").eq(contactOrigins);

		return predicate.toExpression();
	}

	/**
	 * Provide the {@link SAPInitiative} by id if found.
	 * 
	 * @param id
	 *           Initiative ID
	 * @return {@link Optional} of {@link SAPInitiative}
	 * @throws IOException
	 *            If any communication errors.
	 */
	public Optional<SAPInitiative> getInitiative(String id) throws IOException
	{
		final InitiativeQuery query = new InitiativeQuery.Builder().setId(id).build();
		return this.getInitiatives(query).stream().findFirst();
	}

	/**
	 * Provide {@link SAPInitiative}s according to {@link InitiativeQuery}.
	 * 
	 * @param query
	 *           {@link InitiativeQuery}
	 * @return {@link List} of {@link SAPInitiative}
	 * @throws IOException
	 *            If any communication errors.
	 */
	public List<SAPInitiative> getInitiatives(InitiativeQuery query) throws IOException
	{
		try
		{
			final URL url = this.oDataService.createURL(INITIATIVES, //
					"$select", SELECT_FIELDS, //
					"$expand", TARGET_GROUP, //
					"$orderby", campaignOrderBy, //
					"$filter", buildFilterOption(query));

			return this.makeODataCall(url);
		}
		catch (final IOException | EdmException | EntityProviderException e)
		{
			throw new IOException("Error reading initiatives using query " + query, e);
		}
	}

	/**
	 * Provide paged {@link SAPInitiative}s according to {@link InitiativeQuery}.
	 * 
	 * @param query
	 *           {@link InitiativeQuery}
	 * @param skip
	 *           number of initiatives to exclude
	 * @param top
	 *           number of initiatives to return
	 * @return {@link List} of {@link SAPInitiative}
	 * @throws IOException
	 *            If any communication errors.
	 */
	public List<SAPInitiative> getInitiatives(final InitiativeQuery query, final String skip, final String top) throws IOException
	{
		try
		{
			final URL url = this.oDataService.createURL(INITIATIVES, //
					"$select", SELECT_FIELDS, //
					"$expand", TARGET_GROUP, //
					"$filter", buildFilterOption(query), //
					"$orderby", campaignOrderBy, //
					"$top", top, //
					"$skip", skip);

			return this.makeODataCall(url);
		}
		catch (final IOException | EdmException | EntityProviderException e)
		{
			throw new IOException("Error reading initiatives using query " + query, e);
		}
	}

	public String getInteractionContactId()
	{
		return userContextService.getUserId();
	}

	private List<SAPInitiative> makeODataCall(final URL url) throws IOException, EntityProviderException
	{
		final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);
		request.getRequestProperties().put("Accept", APPLICATION_JSON);

		final HttpURLConnectionResponse response = oDataService.executeWithRetry(request);

		final EdmEntitySet edmEntitySet = this.oDataService.getEntitySet(INITIATIVES);
		final InputStream content = new ByteArrayInputStream(response.getPayload());
		final ODataFeed feed = EntityProvider.readFeed(APPLICATION_JSON, edmEntitySet, content, NO_READ_PROPERTIES);
		return feed.getEntries().stream() //
				.map(this::mapODataEntryToSAPInitiative) //
				.sorted() //
				.collect(Collectors.toList());
	}

	protected SAPInitiative mapODataEntryToSAPInitiative(ODataEntry entry)
	{
		final Map<String, Object> properties = entry.getProperties();

		final SAPInitiative initiative = new SAPInitiative();
		initiative.setName((String) properties.get("Name"));
		initiative.setId((String) properties.get("InitiativeId"));

		initiative.setMemberCount(Optional.ofNullable(properties.get(TARGET_GROUP)) //
				.map(ODataEntry.class::cast) //
				.map(ODataEntry::getProperties) //
				.map(targetGroup -> targetGroup.get("CustomerMemberCount")) //
				.map(Object::toString) //
				.orElse(""));

		return initiative;
	}

	@Required
	public void setCampaignCategoryId(final String campaignCategoryId)
	{
		LOG.debug("sapymktsegmentation.campaign_category_id={}", campaignCategoryId);
		this.campaignCategoryId = campaignCategoryId;
	}

	@Required
	public void setCampaignOrderBy(final String campaignOrderBy)
	{
		LOG.debug("sapymktsegmentation.campaign_order_by={}", campaignOrderBy);
		this.campaignOrderBy = campaignOrderBy;
	}

	@Required
	public void setODataService(final ODataService oDataService)
	{
		this.oDataService = oDataService;
	}

	@Required
	public void setUserContextService(final UserContextService userContextService)
	{
		this.userContextService = userContextService;
	}

}
