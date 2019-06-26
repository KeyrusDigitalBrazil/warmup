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
package de.hybris.platform.odata2services.filter.impl;

import de.hybris.platform.odata2services.filter.MemberExpressionVisitor;
import de.hybris.platform.odata2services.filter.NestedFilterNotSupportedException;
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link MemberExpressionVisitor}
 */
public class DefaultMemberExpressionVisitor implements MemberExpressionVisitor
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMemberExpressionVisitor.class);

	private UriInfo uriInfo;
	private EntitySetNameGenerator entitySetNameGenerator;

	@Override
	public Object visit(final MemberExpression expression, final Object pathResult, final Object propertyResult)
	{
		try
		{
			throwExceptionIfNestedFilter(expression, propertyResult);

			if (isNavigationProperty(pathResult))
			{
				final EdmNavigationProperty navProp = (EdmNavigationProperty) pathResult;
				return uriInfo.getEntityContainer().getEntitySet(getEntitySetNameGenerator().generate(navProp.getToRole()));
			}
		}
		catch (final EdmException e)
		{
			LOG.error("An exception occurred while visiting the MemberExpression", e);
			throw new InternalProcessingException(e);
		}
		return pathResult;
	}

	private boolean isNavigationProperty(final Object result)
	{
		return result instanceof EdmNavigationProperty;
	}

	private void throwExceptionIfNestedFilter(final MemberExpression expression, final Object propertyResult)
	{
		if (isNavigationProperty(propertyResult))
		{
			throw new NestedFilterNotSupportedException(expression.getPath().getUriLiteral() + "/" + expression.getProperty().getUriLiteral());
		}
	}

	protected UriInfo getUriInfo()
	{
		return uriInfo;
	}

	public void setUriInfo(final UriInfo uriInfo)
	{
		this.uriInfo = uriInfo;
	}

	protected EntitySetNameGenerator getEntitySetNameGenerator()
	{
		return entitySetNameGenerator;
	}

	public void setEntitySetNameGenerator(final EntitySetNameGenerator entitySetNameGenerator)
	{
		this.entitySetNameGenerator = entitySetNameGenerator;
	}
}
