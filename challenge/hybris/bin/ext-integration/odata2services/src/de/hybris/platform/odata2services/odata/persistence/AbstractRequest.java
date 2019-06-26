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

import java.util.Locale;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.atteo.evo.inflector.English;

import com.google.common.base.Preconditions;

/**
 * Encapsulates properties common to all OData requests.
 */
public abstract class AbstractRequest
{
	private EdmEntitySet entitySet;
	private EdmEntityType entityType;
	private Locale acceptLocale;
	private String integrationObjectCode;

	/**
	 * Instantiates this request
	 */
	AbstractRequest()
	{
	}

	/**
	 * Retrieves entity set associated with the request.
	 * @return an entity set associated with the request.
	 */
	public EdmEntitySet getEntitySet()
	{
		return entitySet;
	}

	/**
	 * Retrieves entity type associated with the request entity set. This is the same as calling
	 * {@code getEntitySet().getEntityType()} but this method does not throw {@link EdmException}
	 * @return an entity type associated with the request entity set.
	 */
	public EdmEntityType getEntityType()
	{
		return entityType;
	}

	/**
	 * Retrieves data acceptLocale associated with the request.
	 * @return a "Accept-Language" header locale associated with the request.
	 */
	public Locale getAcceptLocale()
	{
		return acceptLocale;
	}

	public EdmEntitySet getEntitySetReferencedByProperty(final String property) throws EdmException
	{
		return getEntitySet()
				.getEntityContainer()
				.getEntitySet(English.plural(getPropertyTypeName(property)));
	}

	protected String getPropertyTypeName(final String property) throws EdmException
	{
		return getEntityType().getProperty(property).getType().getName();
	}

	/**
	 * Retrieves data integration object code value, for which this request is issued.
	 * @return a value of the integration object code, which also happens to be name of the OData service.
	 */
	public String getIntegrationObjectCode()
	{
		return integrationObjectCode;
	}

	protected void setEntitySet(final EdmEntitySet set)
	{
		entitySet = set;
	}

	protected void setEntityType(final EdmEntityType type)
	{
		entityType = type;
	}

	protected void setAcceptLocale(final Locale loc)
	{
		acceptLocale = loc;
	}

	protected void setIntegrationObjectCode(final String integrationObjectCode)
	{
		this.integrationObjectCode = integrationObjectCode;
	}

	protected static class AbstractRequestBuilder<T extends AbstractRequest.AbstractRequestBuilder, R extends AbstractRequest>
	{
		private final R request;

		protected AbstractRequestBuilder(final R request)
		{
			this.request = request;
		}

		public T withEntitySet(final EdmEntitySet entitySet)
		{
			request.setEntitySet(entitySet);
			return myself();
		}

		public T withAcceptLocale(final Locale locale)
		{
			request.setAcceptLocale(locale);
			return myself();
		}

		public T withIntegrationObject(final String code)
		{
			request.setIntegrationObjectCode(code);
			return myself();
		}

		public T from(final R request)
		{
			withAcceptLocale(request.getAcceptLocale());
			withEntitySet(request.getEntitySet());
			withIntegrationObject(request.getIntegrationObjectCode());

			return myself();
		}

		protected T myself()
		{
			//noinspection unchecked
			return (T) this;
		}

		protected R request()
		{
			return request;
		}

		public final R build() throws EdmException
		{
			assertValidValues();
			request().setEntityType(request().getEntitySet().getEntityType());
			return request();
		}

		/**
		 * Used to assert state of the {@link #request()} being built and to enforce required invariants. If subclasses override
		 * this method they must call {@code super.assertValidValues()}
		 * @throws EdmException if the OData request is invalid
		 * @throws IllegalArgumentException if invariants on this request are not satisfied.
		 */
		protected void assertValidValues() throws EdmException
		{
			Preconditions.checkArgument(request().getEntitySet() != null, "EdmEntitySet cannot be null");
			Preconditions.checkArgument(request().getEntitySet().getEntityType() != null, "EdmEntityType cannot be null");
			Preconditions.checkArgument(request().getAcceptLocale() != null, "Accept Locale must be provided");
		}
	}
}
