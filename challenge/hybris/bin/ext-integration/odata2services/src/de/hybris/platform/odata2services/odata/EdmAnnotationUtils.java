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

package de.hybris.platform.odata2services.odata;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ALIAS_ANNOTATION_ATTR_NAME;

import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.edm.EdmAnnotatable;
import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmTyped;

public class EdmAnnotationUtils
{
	private static final String IS_PART_OF = "s:IsPartOf";
	private static final String IS_UNIQUE = "s:IsUnique";
	private static final String IS_AUTO_CREATE = "s:IsAutoCreate";
	private static final String NULLABLE = "Nullable";

	private EdmAnnotationUtils()
	{
		// not instantiable
	}

	public static boolean isPartOf(final EdmAnnotatable property) throws EdmException
	{
		return isAnnotationPresentAndTrue(property, IS_PART_OF);
	}

	public static boolean isAutoCreate(final EdmAnnotatable property) throws EdmException
	{
		return isAnnotationPresentAndTrue(property, IS_AUTO_CREATE);
	}

	public static boolean isKeyProperty(final EdmTyped property) throws EdmException
	{
		return isAnnotationPresentAndTrue((EdmAnnotatable) property, IS_UNIQUE);
	}

	private static boolean isAnnotationPresentAndTrue(final EdmAnnotatable property, final String wantedAnnotation) throws EdmException
	{
		return property.getAnnotations().getAnnotationAttributes().stream()
				.anyMatch(annotation -> wantedAnnotation.equals(annotation.getName()) && "true".equals(annotation.getText()));
	}

	public static boolean isNullable(final EdmAnnotatable property) throws EdmException
	{
		final Optional<EdmAnnotationAttribute> a = property.getAnnotations()
				.getAnnotationAttributes()
				.stream()
				.filter(attr -> NULLABLE.equals(attr.getName()))
				.findFirst();
		return !a.isPresent() || "true".equals(a.get().getText());
	}

	public static String getAliasTextIfPresent(final List<EdmProperty> keyProperties) throws EdmException
	{
		if(!keyProperties.isEmpty() && keyProperties.get(0).isSimple())
		{
			final EdmProperty simpleKeyProperty = keyProperties.get(0);
			return simpleKeyProperty
					.getAnnotations()
					.getAnnotationAttributes()
					.stream()
					.filter(a -> ALIAS_ANNOTATION_ATTR_NAME.equals(a.getName()))
					.map(EdmAnnotationAttribute::getText)
					.findFirst()
					.orElse(StringUtils.EMPTY);
		}
		throw new InvalidDataException("invalid_key_definition", "There is no valid key defined for the current entityType.");
	}
}
