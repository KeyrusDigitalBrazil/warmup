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
package de.hybris.platform.odata2services.filter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.uri.UriInfo;

/**
 * A parameter object used for passing data to the {@link ExpressionVisitorFactory}
 */
public class ExpressionVisitorParameters
{
	private ODataContext context;
	private UriInfo uriInfo;

	private ExpressionVisitorParameters()
	{
	}

	public static ExpressionVisitorParametersBuilder parametersBuilder()
	{
		return new ExpressionVisitorParametersBuilder();
	}

	public ODataContext getContext()
	{
		return context;
	}

	private void setContext(final ODataContext context)
	{
		this.context = context;
	}

	public UriInfo getUriInfo()
	{
		return uriInfo;
	}

	private void setUriInfo(final UriInfo uriInfo)
	{
		this.uriInfo = uriInfo;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final ExpressionVisitorParameters that = (ExpressionVisitorParameters) o;

		return new EqualsBuilder()
				.append(context, that.context)
				.append(uriInfo, that.uriInfo)
				.isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37)
				.append(context)
				.append(uriInfo)
				.toHashCode();
	}

	@Override
	public String toString()
	{
		return "ExpressionVisitorParameters{" +
				"context=" + context +
				", uriInfo=" + uriInfo +
				'}';
	}

	public static class ExpressionVisitorParametersBuilder
	{
		private ExpressionVisitorParameters parameters;

		private ExpressionVisitorParametersBuilder()
		{
			parameters = new ExpressionVisitorParameters();
		}

		public ExpressionVisitorParametersBuilder withODataContext(final ODataContext context)
		{
			parameters.setContext(context);
			return this;
		}

		public ExpressionVisitorParametersBuilder withUriInfo(final UriInfo uriInfo)
		{
			parameters.setUriInfo(uriInfo);
			return this;
		}

		public ExpressionVisitorParameters build()
		{
			return parameters;
		}
	}
}
