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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.model.impl.ItemTypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Collections;
import java.util.Set;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@UnitTest
@RunWith(PowerMockRunner.class)
@PrepareForTest({TypeDescriptor.class, ItemTypeDescriptor.class})
@PowerMockIgnore({ "org.apache.logging.log4j.spi.Provider", "javax.management.*" })
public class PrimitiveEntityTypeGeneratorUnitTest
{
	private static final String JAVA_STRING_CLASS_PATH = "java.lang.String";

	@Mock
	private SchemaElementGenerator<EntityType, String> primitiveCollectionMemberEntityTypeGenerator;
	@Mock
	private IntegrationObjectItemModel integrationObjectItemModel;
	@Mock
	private TypeDescriptor integrationObjectItemTypeDescriptor;
	@Mock
	private TypeAttributeDescriptor typeAttributeDescriptor;
	@Mock
	private TypeDescriptor elementTypeDescriptor;
	@Mock
	private EntityType entityType;
	@InjectMocks
	private PrimitiveEntityTypeGenerator primitiveEntityTypeGenerator;

	@Before
	public void setUp()
	{
		PowerMockito.mockStatic(ItemTypeDescriptor.class);
		PowerMockito.when(ItemTypeDescriptor.create(integrationObjectItemModel)).thenReturn(integrationObjectItemTypeDescriptor);
		when(integrationObjectItemTypeDescriptor.getAttributes()).thenReturn(Collections.singleton(typeAttributeDescriptor));
		when(typeAttributeDescriptor.getAttributeType()).thenReturn(elementTypeDescriptor);
		when(elementTypeDescriptor.getTypeCode()).thenReturn(JAVA_STRING_CLASS_PATH);
		when(primitiveCollectionMemberEntityTypeGenerator.generate(any())).thenReturn(entityType);
	}

	@Test
	public void testGenerate()
	{
		givenIsCollectionReturns(true);
		givenHasPrimitiveElementsReturns(true);

		final Set<EntityType> generatedEntityTypes = primitiveEntityTypeGenerator.generate(integrationObjectItemModel);

		assertThat(generatedEntityTypes).hasSameElementsAs(Collections.singletonList(entityType));
	}

	@Test
	public void testGenerateWhenIsCollectionReturnsFalse()
	{
		givenIsCollectionReturns(false);
		givenHasPrimitiveElementsReturns(true);

		final Set<EntityType> generatedEntityTypes = primitiveEntityTypeGenerator.generate(integrationObjectItemModel);

		assertThat(generatedEntityTypes).isEmpty();
	}

	@Test
	public void testGenerateWhenIsPrimitiveReturnsFalse()
	{
		givenIsCollectionReturns(true);
		givenHasPrimitiveElementsReturns(false);

		final Set<EntityType> generatedEntityTypes = primitiveEntityTypeGenerator.generate(integrationObjectItemModel);

		assertThat(generatedEntityTypes).isEmpty();
	}

	private void givenHasPrimitiveElementsReturns(final boolean b)
	{
		when(elementTypeDescriptor.isPrimitive()).thenReturn(b);
	}

	private void givenIsCollectionReturns(final boolean b)
	{
		when(typeAttributeDescriptor.isCollection()).thenReturn(b);
	}
}
