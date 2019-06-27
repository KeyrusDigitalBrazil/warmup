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
package de.hybris.platform.odata2services.odata.schema.navigation;

import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.collectionAttributeBuilder;
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.oneToOneRelationAttributeBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.MockCollectionDescriptorModelBuilder;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.association.AssociationGeneratorRegistry;
import de.hybris.platform.odata2services.odata.schema.association.CollectionAssociationGenerator;
import de.hybris.platform.odata2services.odata.schema.association.OneToOneAssociationGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NavigationPropertyGeneratorUnitTest
{
	private static final String PRODUCT = "Product";
	private static final String UNIT = "Unit";
	private static final String CATEGORY = "Category";
	private static final String PRODUCT_UNIT = "Product_Unit";
	private static final String PRODUCT_CATEGORY = "Product_Category";
	private static final String IS_UNIQUE = "s:IsUnique";

	@InjectMocks
	private NavigationPropertyGenerator generator;

	@Mock
	private AssociationGeneratorRegistry associationGeneratorRegistry;
	@Mock
	private OneToOneAssociationGenerator associationGenerator;
	@Mock
	private SchemaElementGenerator<List<AnnotationAttribute>, IntegrationObjectItemAttributeModel> attributeListGenerator;

	private final AnnotationAttribute annotationAttribute = new AnnotationAttribute();

	@Before
	public void setup()
	{
		when(associationGenerator.getAssociationName(any(IntegrationObjectItemAttributeModel.class))).thenReturn(PRODUCT_UNIT);
		when(associationGenerator.getSourceRole(any(IntegrationObjectItemAttributeModel.class))).thenReturn(PRODUCT);
		when(associationGenerator.getTargetRole(any(IntegrationObjectItemAttributeModel.class))).thenReturn(UNIT);

		when(attributeListGenerator.generate(any(IntegrationObjectItemAttributeModel.class))).thenReturn(Collections.singletonList(annotationAttribute));
	}

	@Test
	public void testGenerateForAssociation()
	{
		final IntegrationObjectItemAttributeModel mockAttributeDefinitionModel =
				oneToOneRelationAttributeBuilder()
					.withSource(PRODUCT)
					.withTarget(UNIT)
					.withName(UNIT)
					.build();
		when(associationGeneratorRegistry.getAssociationGenerator(mockAttributeDefinitionModel)).thenReturn(Optional.of(associationGenerator));
		final Optional<NavigationProperty> navigationPropertyOptional = generator.generate(mockAttributeDefinitionModel);
		assertThat(navigationPropertyOptional).isPresent();
		assertThat(navigationPropertyOptional.get()).isEqualToComparingFieldByField(expectedNavigationProperty());
	}

	@Test
	public void testGenerateForAssociation_collection()
	{
		when(attributeListGenerator.generate(any(IntegrationObjectItemAttributeModel.class)))
				.thenReturn(Collections.singletonList(new AnnotationAttribute().setName(IS_UNIQUE).setText("true")));

		final IntegrationObjectItemAttributeModel mockAttributeDefinitionModel =
				collectionAttributeBuilder()
						.withSource(PRODUCT)
						.withTarget(CATEGORY)
						.withName("supercategories")
						.withAttributeDescriptor(MockCollectionDescriptorModelBuilder.collectionDescriptor().withTarget(CATEGORY))
						.build();

		final CollectionAssociationGenerator associationGeneratorForCollections = mock(CollectionAssociationGenerator.class);
		when(associationGeneratorForCollections.getAssociationName(any(IntegrationObjectItemAttributeModel.class))).thenReturn(PRODUCT_CATEGORY);
		when(associationGeneratorForCollections.getSourceRole(any(IntegrationObjectItemAttributeModel.class))).thenReturn(PRODUCT);
		when(associationGeneratorForCollections.getTargetRole(any(IntegrationObjectItemAttributeModel.class))).thenReturn(CATEGORY);

		when(associationGeneratorRegistry.getAssociationGenerator(mockAttributeDefinitionModel)).thenReturn(Optional.of(associationGeneratorForCollections));

		assertThatThrownBy(() -> generator.generate(mockAttributeDefinitionModel))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot generate unique navigation property for collections [Product.supercategories]");
	}

	@Test
	public void testGenerateForSimpleProperty()
	{
		final IntegrationObjectItemAttributeModel simpleAttributeDefinition = mock(IntegrationObjectItemAttributeModel.class);
		when(associationGeneratorRegistry.getAssociationGenerator(simpleAttributeDefinition)).thenReturn(Optional.empty());
		final Optional<NavigationProperty> navigationPropertyOptional = generator.generate(simpleAttributeDefinition);
		assertThat(navigationPropertyOptional).isNotPresent();
	}

	@Test
	public void testGenerateForNullProperty()
	{
		assertThatThrownBy(() -> generator.generate(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private NavigationProperty expectedNavigationProperty()
	{
		return new NavigationProperty()
				.setName(UNIT)
				.setRelationship(new FullQualifiedName(SchemaUtils.NAMESPACE, PRODUCT_UNIT))
				.setFromRole(PRODUCT)
				.setToRole(UNIT)
				.setAnnotationAttributes(Collections.singletonList(annotationAttribute));
	}
}
