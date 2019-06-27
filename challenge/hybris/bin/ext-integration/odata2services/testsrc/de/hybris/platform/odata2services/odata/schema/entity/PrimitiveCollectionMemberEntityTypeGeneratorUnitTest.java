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

package de.hybris.platform.odata2services.odata.schema.entity;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.junit.Test;

@UnitTest
public class PrimitiveCollectionMemberEntityTypeGeneratorUnitTest
{
	private final PrimitiveCollectionMemberEntityTypeGenerator generator = new PrimitiveCollectionMemberEntityTypeGenerator();

	@Test
	public void testGenerateString()
	{
		final EntityType entityType = generator.generate("java.lang.String");

		assertThat(entityType.getName()).isEqualTo("String");
		assertValuePropertyGenerated(entityType, EdmSimpleTypeKind.String);
	}

	@Test
	public void testGenerateInteger()
	{
		final EntityType entityType = generator.generate("java.lang.Integer");

		assertThat(entityType.getName()).isEqualTo("Integer");
		assertValuePropertyGenerated(entityType, EdmSimpleTypeKind.Int32);
	}

	@Test
	public void testGenerateDate()
	{
		final EntityType entityType = generator.generate("java.util.Date");

		assertThat(entityType.getName()).isEqualTo("Date");
		assertValuePropertyGenerated(entityType, EdmSimpleTypeKind.DateTime);
	}

	@Test
	public void testGenerateDouble()
	{
		final EntityType entityType = generator.generate("java.lang.Double");

		assertThat(entityType.getName()).isEqualTo("Double");
		assertValuePropertyGenerated(entityType, EdmSimpleTypeKind.Double);
	}

	@Test
	public void testGenerateBigDecimal()
	{
		final EntityType entityType = generator.generate("java.math.BigDecimal");

		assertThat(entityType.getName()).isEqualTo("BigDecimal");
		assertValuePropertyGenerated(entityType, EdmSimpleTypeKind.Decimal);
	}

	@Test
	public void testGenerateLong()
	{
		final EntityType entityType = generator.generate("java.lang.Long");

		assertThat(entityType.getName()).isEqualTo("Long");
		assertValuePropertyGenerated(entityType, EdmSimpleTypeKind.Int64);
	}

	@Test
	public void testGenerateBoolean()
	{
		final EntityType entityType = generator.generate("java.lang.Boolean");

		assertThat(entityType.getName()).isEqualTo("Boolean");
		assertValuePropertyGenerated(entityType, EdmSimpleTypeKind.Boolean);
	}

	private void assertNullableAnnotationGenerated(final Property annotationProp)
	{
		assertThat(annotationProp.getAnnotationAttributes()).first().hasFieldOrPropertyWithValue("name","Nullable");
		assertThat(annotationProp.getAnnotationAttributes()).first().hasFieldOrPropertyWithValue("text","false");
	}

	private void assertKeyIsValue(final EntityType entityType)
	{
		assertThat(entityType.getKey().getKeys()).hasSize(1).first().hasFieldOrPropertyWithValue("name", "value");
	}

	private void assertValuePropertyGenerated(final EntityType entityType, final EdmSimpleTypeKind edmType)
	{
		assertKeyIsValue(entityType);
		assertThat(entityType.getProperties()).hasSize(1);

		final Property property = entityType.getProperties().get(0);
		assertThat(property).hasFieldOrPropertyWithValue("type", edmType);
		assertThat(property).hasFieldOrPropertyWithValue("name","value");

		assertNullableAnnotationGenerated(property);
	}
}