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
package de.hybris.platform.odata2services.odata.schema.property;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.odata2services.odata.schema.attribute.AliasAnnotationGenerator;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IntegrationKeyPropertyGeneratorUnitTest
{
	@Mock
	private AliasAnnotationGenerator aliasGenerator;

	@InjectMocks
	private IntegrationKeyPropertyGenerator generator;

	@Test
	public void testGenerateSuccessful()
	{
		final AnnotationAttribute aliasAnnotation = new AnnotationAttribute().setName("s:Alias").setText("MyAlias");
		final AnnotationAttribute nullableAnnotation = new AnnotationAttribute().setName("Nullable").setText("false");
		when(aliasGenerator.generate(any(IntegrationObjectItemModel.class))).thenReturn(aliasAnnotation);

		final SimpleProperty integrationKeyProperty = (SimpleProperty) generator.generate(mock(IntegrationObjectItemModel.class)).get();

		assertThat(integrationKeyProperty)
				.hasFieldOrPropertyWithValue("name", "integrationKey")
				.hasFieldOrPropertyWithValue("type", EdmSimpleTypeKind.String);
		assertThat(integrationKeyProperty.getAnnotationAttributes())
				.usingElementComparatorOnFields("name", "text")
				.containsExactlyInAnyOrder(nullableAnnotation, aliasAnnotation);
	}

	@Test
	public void testGenerateReturnsNullWhenNoKeyFound()
	{
		when(aliasGenerator.generate(any(IntegrationObjectItemModel.class))).thenReturn(null);

		assertThat(generator.generate(mock(IntegrationObjectItemModel.class))).isNotPresent();
	}
	
	@Test
	public void testGenerateThrowsExceptionWhenNullIntegrationObjectItemModel()
	{
		assertThatThrownBy(() -> generator.generate(null)).isInstanceOf(IllegalArgumentException.class);
	}
}