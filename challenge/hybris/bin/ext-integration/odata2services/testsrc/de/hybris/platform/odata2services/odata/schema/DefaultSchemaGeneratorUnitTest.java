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

package de.hybris.platform.odata2services.odata.schema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.odata2services.odata.InvalidODataSchemaException;
import de.hybris.platform.odata2services.odata.schema.association.AssociationListGeneratorRegistry;
import de.hybris.platform.odata2services.odata.schema.entity.EntityContainerGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSchemaGeneratorUnitTest
{
	private static final List<EntityType> ENTITY_TYPES = Collections.singletonList(mock(EntityType.class));
	private static final List<Association> ASSOCIATIONS = Collections.singletonList(mock(Association.class));
	private static final List<EntityContainer> ENTITY_CONTAINERS = Collections.singletonList(mock(EntityContainer.class));

	@InjectMocks
	private DefaultSchemaGenerator schemaGenerator;
	@Mock
	private SchemaElementGenerator<List<EntityType>, Collection<IntegrationObjectItemModel>> entityTypeListGenerator;
	@Mock
	private AssociationListGeneratorRegistry associationListGeneratorRegistry;
	@Mock
	private EntityContainerGenerator entityContainerGenerator;

	@Before
	public void stubGenerators()
	{
		doAnswer(call -> ((Collection) call.getArguments()[0]).isEmpty() ? Collections.emptyList() : ENTITY_TYPES)
				.when(entityTypeListGenerator).generate(((anyCollectionOf(IntegrationObjectItemModel.class))));
		doAnswer(call -> ((Collection) call.getArguments()[0]).isEmpty() ? Collections.emptyList() : ASSOCIATIONS)
				.when(associationListGeneratorRegistry).generate(anySetOf(IntegrationObjectItemModel.class));
		doAnswer(call -> ((Collection) call.getArguments()[0]).isEmpty() ? Collections.emptyList() : ENTITY_CONTAINERS)
				.when(entityContainerGenerator).generate(eq(ENTITY_TYPES), eq(ASSOCIATIONS));
	}

	@Test
	public void testGenerateNull()
	{
		assertThatThrownBy(() -> schemaGenerator.generateSchema(null))
				.isInstanceOf(InvalidODataSchemaException.class)
				.hasMessage("The EDMX schema could not be generated. Please make sure that your Integration Object is defined correctly.")
				.hasCauseInstanceOf(IllegalArgumentException.class)
				.hasFieldOrPropertyWithValue("code", "schema_generation_error")
				.hasFieldOrPropertyWithValue("httpStatus", HttpStatusCodes.INTERNAL_SERVER_ERROR);
	}

	@Test
	public void testGenerateWithoutIntegrationObjectItemsPresent()
	{
		final Set<IntegrationObjectItemModel> noModels = Collections.emptySet();

		final Schema schema = schemaGenerator.generateSchema(noModels);

		assertThat(schema).isNotNull();
		assertThat(schema.getEntityTypes()).isEmpty();
		assertThat(schema.getAssociations()).isEmpty();
		assertThat(schema.getEntityContainers()).isEmpty();
	}

	@Test
	public void testGenerate()
	{
		final Set<IntegrationObjectItemModel> models = Collections.singleton(mock(IntegrationObjectItemModel.class));

		final Schema schema = schemaGenerator.generateSchema(models);

		assertThat(schema).isNotNull();
		assertThat(schema.getNamespace()).isEqualTo(SchemaUtils.NAMESPACE);
		assertThat(schema.getEntityTypes()).containsAll(ENTITY_TYPES);
		assertThat(schema.getAssociations()).containsAll(ASSOCIATIONS);
		assertThat(schema.getEntityContainers()).containsAll(ENTITY_CONTAINERS);
	}

	@Test
	public void testAnnotationAttributesContainsSapCommerceNamespace()
	{
		final Set<IntegrationObjectItemModel> noModels = Collections.emptySet();

		final Schema schema = schemaGenerator.generateSchema(noModels);

		assertThat(schema.getAnnotationAttributes())
				.isNotEmpty()
				.hasSize(1);
		final AnnotationAttribute sapAnnotationElement = schema.getAnnotationAttributes().get(0);
		assertThat(sapAnnotationElement.getName()).isEqualTo("schema-version");
		assertThat(sapAnnotationElement.getNamespace()).isEqualTo("http://schemas.sap.com/commerce");
		assertThat(sapAnnotationElement.getPrefix()).isEqualTo("s");
		assertThat(sapAnnotationElement.getText()).isEqualTo("1");
	}
}