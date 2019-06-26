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

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

import com.google.common.base.Preconditions;

public class ExpandedEntity
{
	private EdmEntitySet edmEntitySet;
	private ODataEntry oDataEntry;
	private ODataFeed oDataFeed;

	protected ExpandedEntity()
	{
		// protected constructor
	}

	public static ExpandedEntity.ExpandedEntityBuilder expandedEntityBuilder()
	{
		return new ExpandedEntity.ExpandedEntityBuilder(new ExpandedEntity());
	}

	public EdmEntitySet getEdmEntitySet()
	{
		return edmEntitySet;
	}

	public void setEdmEntitySet(final EdmEntitySet edmEntitySet)
	{
		this.edmEntitySet = edmEntitySet;
	}

	public ODataEntry getODataEntry()
	{
		return oDataEntry;
	}

	public void setODataEntry(final ODataEntry oDataEntry)
	{
		this.oDataEntry = oDataEntry;
	}

	public ODataFeed getODataFeed()
	{
		return oDataFeed;
	}

	public void setODataFeed(final ODataFeed oDataFeed)
	{
		this.oDataFeed = oDataFeed;
	}

	public static class ExpandedEntityBuilder<T extends ExpandedEntity.ExpandedEntityBuilder, R extends ExpandedEntity>
	{
		private ExpandedEntity expandedEntity;

		ExpandedEntityBuilder(final ExpandedEntity expandedEntity)
		{
			this.expandedEntity = expandedEntity;
		}

		public T withEdmEntitySet(final EdmEntitySet edmEntitySet)
		{
			this.expandedEntity.setEdmEntitySet(edmEntitySet);
			return (T) this;
		}

		public T withODataEntry(final ODataEntry oDataEntry)
		{
			this.expandedEntity.setODataEntry(oDataEntry);
			return (T) this;
		}

		public T withODataFeed(final ODataFeed oDataFeed)
		{
			this.expandedEntity.setODataFeed(oDataFeed);
			return (T) this;
		}

		public T from(final ExpandedEntity expandedEntity)
		{
			withEdmEntitySet(expandedEntity.getEdmEntitySet());
			withODataEntry(expandedEntity.getODataEntry());
			withODataFeed(expandedEntity.getODataFeed());

			return (T) this;
		}

		public R build()
		{
			Preconditions.checkArgument(this.expandedEntity.getEdmEntitySet() != null, "EdmEntitySet cannot be null");

			return (R) this.expandedEntity;
		}
	}
}
