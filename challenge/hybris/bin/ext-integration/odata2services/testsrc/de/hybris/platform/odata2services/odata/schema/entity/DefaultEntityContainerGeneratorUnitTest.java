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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;
import org.apache.olingo.odata2.api.edm.provider.AssociationSet;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.PropertyRef;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultEntityContainerGeneratorUnitTest
{
	private static final String COMPOSED_TYPE = "Product";
	private static final String PRIMITIVE_TYPE = "Double";
	private static final String LOCALIZED_TYPE = "Localized^Product";
	private static final AssociationSet GENERATED_ASSOCIATION = new AssociationSet();

	@Mock
	private SchemaElementGenerator<EntitySet, EntityType> entitySetGenerator;
	@Mock
	private SchemaElementGenerator<AssociationSet, Association> associationSetGenerator;
	@InjectMocks
	private DefaultEntityContainerGenerator containerGenerator;

	@Test
	public void testGenerateWhenNoEntitiesAndNoAssociationsPresent()
	{
		final List<EntityType> no_types = Collections.emptyList();
		final List<Association> no_associations = Collections.emptyList();

		final List<EntityContainer> containers = containerGenerator.generate(no_types, no_associations);

		assertThat(containers).isEmpty();
	}

	@Test
	public void testGenerateWhenEntitiesAndAssociationsPresent()
	{
		final Collection<EntityType> someEntityTypes = Collections.singleton(composedEntityType());
		final Collection<Association> someAssociations = Collections.singleton(composedAssociation());

		final EntitySet generatedEntitySet = new EntitySet().setEntityType(new FullQualifiedName("TEST", COMPOSED_TYPE));
		stubGeneratedEntityAndAssociationSets(generatedEntitySet);

		final List<EntityContainer> containers = containerGenerator.generate(someEntityTypes, someAssociations);

		assertThat(containers).hasSize(1);
		final EntityContainer container = containers.get(0);
		assertThat(container)
				.hasFieldOrPropertyWithValue("defaultEntityContainer", true)
				.hasFieldOrPropertyWithValue("name", "Container");
		assertThat(container.getEntitySets()).containsExactly(generatedEntitySet);
		assertThat(container.getAssociationSets()).containsExactly(GENERATED_ASSOCIATION);
	}

	@Test
	public void testGenerateNullAssociations()
	{
		final Collection<EntityType> someEntityTypes = Collections.singleton(new EntityType());

		assertThatThrownBy(() -> containerGenerator.generate(someEntityTypes, null))
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void testGenerateNullEntityTypes()
	{
		final Collection<Association> someAssociations = Collections.singleton(new Association());

		assertThatThrownBy(() -> containerGenerator.generate(null, someAssociations))
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void testPrimitiveTypesIncludedInEntitySets()
	{
		final Collection<EntityType> someEntityTypes = Collections.singleton(primitiveEntityType());
		final Collection<Association> someAssociations = Collections.singleton(new Association());

		final List<EntityContainer> containers = containerGenerator.generate(someEntityTypes, someAssociations);

		final EntityContainer container = containers.get(0);

		assertThat(container.getEntitySets()).hasSize(1);
	}

	@Test
	public void testPrimitiveTypesIncludedInEntitySetsWhenKeyIsNull()
	{
		final Collection<EntityType> someEntityTypes = Collections.singleton(new EntityType());
		final Collection<Association> someAssociations = Collections.singleton(new Association());

		final List<EntityContainer> containers = containerGenerator.generate(someEntityTypes, someAssociations);

		final EntityContainer container = containers.get(0);

		assertThat(container.getEntitySets()).hasSize(1);
	}

	@Test
	public void testPrimitiveTypesIncludedInAssociationSets()
	{
		final Collection<EntityType> someEntityTypes = Collections.singleton(primitiveEntityType());
		final Collection<Association> someAssociations = Collections.singleton(primitiveAssociation());

		final List<EntityContainer> containers = containerGenerator.generate(someEntityTypes, someAssociations);

		final EntityContainer container = containers.get(0);

		assertThat(container.getAssociationSets()).hasSize(1);
	}

	@Test
	public void testGenerateLocalizedTypesIncludedInEntityAndAssociationSets()
	{
		final Collection<EntityType> someEntityTypes = Collections.singleton(localizedEntityType());
		final Collection<Association> someAssociations = Collections.singleton(localizedAssociation());

		final EntitySet generatedEntitySet = new EntitySet().setEntityType(new FullQualifiedName("TEST", LOCALIZED_TYPE));
		stubGeneratedEntityAndAssociationSets(generatedEntitySet);

		final List<EntityContainer> containers = containerGenerator.generate(someEntityTypes, someAssociations);

		assertThat(containers).hasSize(1);
		final EntityContainer container = containers.get(0);
		assertThat(container)
				.hasFieldOrPropertyWithValue("defaultEntityContainer", true)
				.hasFieldOrPropertyWithValue("name", "Container");
		assertThat(container.getEntitySets()).containsExactly(generatedEntitySet);
		assertThat(container.getAssociationSets()).containsExactly(GENERATED_ASSOCIATION);
	}

	private void stubGeneratedEntityAndAssociationSets(final EntitySet generatedEntitySet)
	{
		doReturn(generatedEntitySet).when(entitySetGenerator).generate(any());
		doReturn(GENERATED_ASSOCIATION).when(associationSetGenerator).generate(any());
	}

	private Association composedAssociation()
	{
		return new Association().setEnd2(new AssociationEnd().setRole(COMPOSED_TYPE));
	}

	private Association primitiveAssociation()
	{
		return new Association().setEnd2(new AssociationEnd().setRole(PRIMITIVE_TYPE));
	}

	private Association localizedAssociation()
	{
		return new Association().setEnd2(new AssociationEnd().setRole(LOCALIZED_TYPE));
	}

	private EntityType composedEntityType()
	{
		return new EntityType().setName(COMPOSED_TYPE).setKey(new Key().setKeys(Collections.singletonList(new PropertyRef().setName("integrationKey"))));
	}

	private EntityType primitiveEntityType()
	{
		return new EntityType().setName(PRIMITIVE_TYPE).setKey(new Key().setKeys(Collections.singletonList(new PropertyRef().setName("value"))));
	}

	private EntityType localizedEntityType()
	{
		return new EntityType().setName(LOCALIZED_TYPE).setKey(new Key().setKeys(Collections.singletonList(new PropertyRef().setName("language"))));
	}
}