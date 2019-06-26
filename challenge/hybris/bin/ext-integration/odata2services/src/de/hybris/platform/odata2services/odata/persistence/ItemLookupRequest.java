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

package de.hybris.platform.odata2services.odata.persistence;

import de.hybris.platform.integrationservices.search.WhereClauseConditions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.NavigationSegment;

import com.google.common.base.Preconditions;

public class ItemLookupRequest extends CrudRequest
{
	private Integer skip;
	private Integer top;
	private boolean count;
	private boolean countOnly;
	private List<ArrayList<NavigationPropertySegment>> expand;
	private List<NavigationSegment> navigationSegments;
	private WhereClauseConditions filter;
	private Pair<String, String> attribute;
	private boolean noFilterResult;

	protected ItemLookupRequest()
	{
		// protected constructor
	}

	static ItemLookupRequestBuilder itemLookupRequestBuilder()
	{
		return new ItemLookupRequestBuilder(new ItemLookupRequest());
	}

	private void setSkip(final Integer skip)
	{
		this.skip = skip;
	}

	public Integer getSkip()
	{
		return skip;
	}

	private void setTop(final Integer top)
	{
		this.top = top;
	}

	public Integer getTop()
	{
		return top;
	}

	public void setNavigationSegments(final List<NavigationSegment> navigationSegments)
	{
		this.navigationSegments = navigationSegments;
	}

	public List<NavigationSegment> getNavigationSegments()
	{
		return navigationSegments;
	}

	public WhereClauseConditions getFilter()
	{
		return filter;
	}

	private void setFilter(final WhereClauseConditions filter)
	{
		this.filter = filter;
	}

	public Pair<String, String> getAttribute()
	{
		return attribute;
	}

	private void setAttribute(final Pair<String, String> attribute)
	{
		this.attribute = attribute;
	}

	public boolean isNoFilterResult()
	{
		return noFilterResult;
	}

	private void setNoFilterResult(final boolean noFilterResult)
	{
		this.noFilterResult = noFilterResult;
	}

	/**
	 * Determines whether total number of items matching this request should be included in the response or not.
	 * @return {@code true}, if the response must include the total number of matching items; {@code false}, if the response
	 * needs to contain item(s) only and does not need total count.
	 */
	public boolean isCount()
	{
		return count;
	}

	private void setCount(final boolean value)
	{
		count = value;
	}

	/**
	 * Determines whether the response should include number of matching items only.
	 * @return {@code true}, if the response should contain only count of the matching items but no matching items themselves;
	 * {@code false}, if the items must be included.
	 */
	public boolean isCountOnly()
	{
		return countOnly;
	}

	private void setCountOnly(final boolean value)
	{
		countOnly = value;
	}

	public List<ArrayList<NavigationPropertySegment>> getExpand()
	{
		return expand;
	}

	private void setExpand(final List<ArrayList<NavigationPropertySegment>> expand)
	{
		this.expand = expand;
	}

	static class ItemLookupRequestBuilder extends DataRequestBuilder<ItemLookupRequestBuilder, ItemLookupRequest>
	{
		ItemLookupRequestBuilder(final ItemLookupRequest itemLookupRequest)
		{
			super(itemLookupRequest);
		}

		ItemLookupRequestBuilder withNavigationSegments(final List<NavigationSegment> navigationSegments)
		{
			request().setNavigationSegments(navigationSegments);
			return myself();
		}

		ItemLookupRequestBuilder withSkip(final Integer skip)
		{
			request().setSkip(skip);
			return myself();
		}

		ItemLookupRequestBuilder withTop(final Integer top)
		{
			request().setTop(top);
			return myself();
		}

		ItemLookupRequestBuilder withCount(final boolean count)
		{
			request().setCount(count);
			return myself();
		}

		ItemLookupRequestBuilder withCountOnly(final boolean count)
		{
			request().setCountOnly(count);
			return myself();
		}

		ItemLookupRequestBuilder withExpand(final List<ArrayList<NavigationPropertySegment>>  expand)
		{
			request().setExpand(expand);
			return myself();
		}

		ItemLookupRequestBuilder withFilter(final WhereClauseConditions filter)
		{
			request().setFilter(filter);
			return this;
		}

		ItemLookupRequestBuilder withHasNoFilterResult(final boolean hasNoFilterResult)
		{
			request().setNoFilterResult(hasNoFilterResult);
			return this;
		}

		ItemLookupRequestBuilder withAttribute(final Pair<String, String> attribute)
		{
			request().setAttribute(attribute);
			return this;
		}

		@Override
		public ItemLookupRequestBuilder from(final ItemLookupRequest request)
		{
			withIntegrationKey(request.getIntegrationKey());
			withNavigationSegments(request.getNavigationSegments());
			withSkip(request.getSkip());
			withTop(request.getTop());
			withCount(request.isCount());
			withCountOnly(request.isCountOnly());
			withExpand(request.getExpand());
			withServiceRoot(request.getServiceRoot());
			withContentType(request.getContentType());
			withRequestUri(request.getRequestUri());
			withFilter(request.getFilter());
			withHasNoFilterResult(request.isNoFilterResult());
			return super.from(request);
		}

		@Override
		protected void assertValidValues() throws EdmException
		{
			super.assertValidValues();
			final Integer top = request().getTop();
			final Integer skip = request().getSkip();
			Preconditions.checkArgument(top == null || top >= 0, "Top cannot be less than 0");
			Preconditions.checkArgument(skip == null || skip >= 0, "Skip cannot be less than 0");
		}
	}
}
