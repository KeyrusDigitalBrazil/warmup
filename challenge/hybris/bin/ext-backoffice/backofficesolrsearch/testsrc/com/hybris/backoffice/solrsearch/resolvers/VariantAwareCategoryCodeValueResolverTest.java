/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.solrsearch.resolvers;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.solrsearch.providers.ProductCategoryAssignmentResolver;

@RunWith(MockitoJUnitRunner.class)
public class VariantAwareCategoryCodeValueResolverTest
{
	@Spy
	private VariantAwareCategoryCodeValueResolver resolver;

	@Mock
	private ProductCategoryAssignmentResolver valueProvider;

	@Test
	public void shouldAddCodesToDocument() throws FieldValueProviderException
	{
		//given
		resolver.setCategoryAttributeValueProvider(valueProvider);
		final ProductModel product = mock(ProductModel.class);
		final Set categories = new LinkedHashSet();
		final CategoryModel classA = mock(CategoryModel.class);
		categories.add(classA);
		when(classA.getCode()).thenReturn("a");
		final CategoryModel classB = mock(CategoryModel.class);
		categories.add(classB);
		when(classB.getCode()).thenReturn("b");
		doReturn(categories).when(valueProvider).getIndexedCategories(product);

		//when
		final IndexedProperty property = mock(IndexedProperty.class);
		final InputDocument document = mock(InputDocument.class);
		resolver.addFieldValues(document, null, property, product, null);

		//then
		verify(document).addField(property, "a");
		verify(document).addField(property, "b");

	}
}
