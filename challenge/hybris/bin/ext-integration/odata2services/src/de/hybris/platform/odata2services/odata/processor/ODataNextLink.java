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
package de.hybris.platform.odata2services.odata.processor;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;

import com.google.common.base.Preconditions;

public class ODataNextLink
{
	private static final String SKIPTOKEN = "$skiptoken";
	private String currentLink;
	private int totalCount;
	private int skip;
	private int top;
	private String nextLink;

	private ODataNextLink() {}

	private String getNextLink()
	{
		return !isLastPage() ? getLink() : null;
	}

	private String getLink()
	{
		nextLink = prepareLinkForNextSkipToken();
		nextLink += getNewSkipToken();
		return nextLink;
	}

	private String getNewSkipToken()
	{
		return (nextLink.contains("?") ? "&" : "?") + SKIPTOKEN + "=" + newSkipValue();
	}

	private String newSkipValue()
	{
		return String.valueOf(top + skip);
	}

	private boolean isLastPage()
	{
		return totalCount - top <= skip;
	}

	private String prepareLinkForNextSkipToken()
	{
		return currentLink
				.replaceAll("\\" + SKIPTOKEN + "=.+?(?:&|$)", "")
				.replaceAll("\\$skip=.+?(?:&|$)", "")
				.replaceFirst("(?:\\?|&)$", "");
	}

	private void setCurrentLink(final String currentLink)
	{
		this.currentLink = currentLink;
	}
	
	private void setTotalCount(final int totalCount)
	{
		this.totalCount = totalCount;
	}

	private void setSkip(final int skip)
	{
		this.skip = skip;
	}

	private void setTop(final int top)
	{
		this.top = top;
	}

	public static class Builder
	{
		private ItemLookupRequest lookupRequest;
		private Integer totalCount;

		private Builder()
		{
		}

		public static Builder nextLink()
		{
			return new Builder();
		}

		public Builder withLookupRequest(final ItemLookupRequest lookupRequest)
		{
			this.lookupRequest = lookupRequest;
			return this;
		}

		public Builder withTotalCount(final Integer totalCount)
		{
			this.totalCount = totalCount;
			return this;
		}
		
		public String build()
		{
			assertAllRequiredFieldsAreSet();
			final ODataNextLink oDataNextLink = new ODataNextLink();
			oDataNextLink.setSkip(lookupRequest.getSkip());
			oDataNextLink.setTop(lookupRequest.getTop());
			oDataNextLink.setTotalCount(totalCount);
			oDataNextLink.setCurrentLink(lookupRequest.getRequestUri().toString());
			return oDataNextLink.getNextLink();
		}

		private void assertAllRequiredFieldsAreSet()
		{
			Preconditions.checkArgument(lookupRequest != null, "itemLookupRequest must be provided");
			Preconditions.checkArgument(lookupRequest.getRequestUri() != null, "requestUri must be provided");
			Preconditions.checkArgument(lookupRequest.getSkip() != null, "skip must be provided");
			Preconditions.checkArgument(lookupRequest.getTop() != null, "top must be provided");
			Preconditions.checkArgument(totalCount != null && totalCount >= 0, "totalCount must be provided and have a value of 0 or greater");
		}
	}
}
