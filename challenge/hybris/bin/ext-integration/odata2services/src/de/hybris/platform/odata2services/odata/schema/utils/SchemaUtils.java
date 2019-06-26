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
package de.hybris.platform.odata2services.odata.schema.utils;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LOCALIZED_ENTITY_TYPE_PREFIX;
import static org.apache.commons.lang3.ClassUtils.getShortClassName;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.impl.DefaultTypeAttributeDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;

import com.google.common.base.Preconditions;

public final class SchemaUtils
{

	public static final String NAMESPACE = "HybrisCommerceOData";
	public static final String CONTAINER_NAME = "Container";


	private SchemaUtils()
	{
	}

	/**
	 * Prepends schema name to the specified name.
	 *
	 * @param name name of a schema element, e.g. EnityType, to be presented in a fully qualified format.
	 * @return string presentation of the fully qualified name.
	 * @throws IllegalArgumentException if the specified name is {@code null}, empty or blank.
	 */
	public static String fullyQualified(final String name)
	{
		return toFullQualifiedName(name).toString();
	}

	/**
	 * Converts specified simple schema element name to the fully qualified name.
	 *
	 * @param name name of a schema element, e.g. EntityType, to be converted.
	 * @return fully qualified name.
	 * @throws IllegalArgumentException if the specified name is {@code null}, empty or blank.
	 */
	public static FullQualifiedName toFullQualifiedName(final String name)
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(name));
		return new FullQualifiedName(NAMESPACE, getShortClassName(name));
	}

	public static String buildAssociationName(final String sourceTypeCode, final String targetTypeCode)
	{
		return String.format("FK_%s_%s",sourceTypeCode, targetTypeCode);
	}

	/**
	 * Returns the localized entity name
	 *
	 * @param typeCode type of the integration object item
	 * @return localized entity name
	 */
	public static String localizedEntityName(final String typeCode)
	{
		return LOCALIZED_ENTITY_TYPE_PREFIX + typeCode;
	}

	/**
	 * Removes duplicates from the given {@link List}
	 *
	 * An example usage would be:
	 * Given a <code>List<String> list = Arrays.asList("a", "b", "a");</code>
	 * To remove replicates, call <code>removeDuplicates(list, e -> e);</code>,
	 * where the idFunction is the element e itself. The resulting list would be <code>["a", "b"]</code>.
	 *
	 * @param list List to remove the duplicates from
	 * @param idFunction Function that returns the identifier that uniquely identifies object of type T
	 * @param <T> Type of the elements in the {@link List}
	 * @param <U> Type of the unique identifier of object T
	 * @return List of type T with duplicates removed
	 */
	public static <T, U> List<T> removeDuplicates(final List<T> list, final Function<T,U> idFunction)
	{
		return new ArrayList<>(list.stream().collect(Collectors.toMap(idFunction, Function.identity(), (a, b) -> b)).values());
	}

	/**
	 * Finds the first localized attribute from the {@link Collection} of attributes
	 *
	 * @param attributeModels Collection of attributes
	 * @return An {@link Optional} containing the {@link TypeAttributeDescriptor} if found, otherwise empty
	 */
	public static Optional<TypeAttributeDescriptor> findFirstLocalizedAttribute(final Collection<IntegrationObjectItemAttributeModel> attributeModels)
	{
		return attributeModels.stream().map(SchemaUtils::asDescriptor).filter(TypeAttributeDescriptor::isLocalized).findFirst();
	}

	private static TypeAttributeDescriptor asDescriptor(final IntegrationObjectItemAttributeModel attributeModel)
	{
		return DefaultTypeAttributeDescriptor.create(attributeModel);
	}

	/**
	 * Defines new annotation attributes for things like namespaces
	 *
	 * @return the newly defined annotation attributes
	 */
	public static List<AnnotationAttribute> createNamespaceAnnotations()
	{
		return Collections.singletonList(sapCommerceNamespace());
	}

	private static AnnotationAttribute sapCommerceNamespace()
	{
		return new AnnotationAttribute()
				.setNamespace("http://schemas.sap.com/commerce")
				.setPrefix("s")
				.setName("schema-version")
				.setText("1");
	}
}
