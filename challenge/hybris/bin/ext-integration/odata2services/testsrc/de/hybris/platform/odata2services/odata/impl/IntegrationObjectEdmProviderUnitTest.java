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

package de.hybris.platform.odata2services.odata.impl;

import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.toFullQualifiedName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationSet;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IntegrationObjectEdmProviderUnitTest
{
	private static final String ENTITY_TYPE_VALUE = "MyType";
	private static final String ENTITYSET_NAME = "Products";
	private static final String ENTITY_NAME = "TestProduct";
	private static final String NAMESPACE = "TestOData";
	private static final String INVALID = "Invalid";
	private static final String ENTITY_TYPE = "entityType";
	private static final String SERVICE = "service";
	private static final String SERVICE_VALUE = "MyService";

	private IntegrationObjectEdmProvider provider;
	@Mock
	private IntegrationObjectService integrationObjectService;
	@Mock
	private SchemaGenerator schemaGenerator;
	private final Schema schema = new Schema();

	@Before
	public void setUp()
	{
		provider = new IntegrationObjectEdmProvider(integrationObjectService, schemaGenerator, mockContext());
	}

	@Test
	public void testGetSchemas() throws ODataException
	{
		mockSchemaGenerationForType(ENTITY_TYPE_VALUE, SERVICE_VALUE);
		final List<Schema> schemas = whenGetSchemasIsCalled();

		thenSchemasAreReturned(schemas);
	}

	@Test
	public void testGetSchemas_NoTypeSpecified() throws ODataException
	{
		provider = new IntegrationObjectEdmProvider(integrationObjectService, schemaGenerator, mockContextWithNoEntityType());
		mockSchemaGenerationForAllTypes();
		final List<Schema> schemas = whenGetSchemasIsCalled();

		thenSchemasAreReturned(schemas);
	}

	@Test
	public void testGetSchemas_NoTypeSpecified_IsCachedAfterFirstCall() throws ODataException
	{
		provider = new IntegrationObjectEdmProvider(integrationObjectService, schemaGenerator, mockContextWithNoEntityType());
		mockSchemaGenerationForAllTypes();
		whenGetSchemasIsCalled();
		whenGetSchemasIsCalled();

		verify(schemaGenerator, times(1)).generateSchema(any());
	}

	@Test(expected = ODataException.class)
	public void testSomethingBadHappens() throws ODataException
	{
		doThrow(new RuntimeException("Test expected exception")).when(integrationObjectService).findAllDependencyTypes(ENTITY_TYPE_VALUE, SERVICE_VALUE);

		whenGetSchemasIsCalled();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetSchemas_NoServiceSpecified() throws ODataException
	{
		provider = new IntegrationObjectEdmProvider(integrationObjectService, schemaGenerator, mockContextWithNoService());

		whenGetSchemasIsCalled();
	}

	@Test
	public void testGetEntitySet()
	{
		mockContainerWithEntityAndAssociations(SchemaUtils.CONTAINER_NAME);

		final EntitySet entitySet = provider.getEntitySet(SchemaUtils.CONTAINER_NAME, ENTITYSET_NAME);

		assertThat(entitySet)
				.isNotNull();
	}

	@Test
	public void testGetEntitySet_WithDifferentContainerName()
	{
		mockContainerWithEntityAndAssociations(INVALID);

		final EntitySet entitySet = provider.getEntitySet(SchemaUtils.CONTAINER_NAME, ENTITYSET_NAME);

		assertThat(entitySet)
				.isNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetEntitySet_WithOutEntityContainerName()
	{
		provider.getEntitySet(StringUtils.EMPTY, ENTITYSET_NAME);
	}

	@Test
	public void testGetEntitySet_Null()
	{
		mockContainerWithEntityAndAssociations(SchemaUtils.CONTAINER_NAME);

		final EntitySet entitySet = provider.getEntitySet(SchemaUtils.CONTAINER_NAME, INVALID);

		assertThat(entitySet)
				.isNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetEntitySet_WithOutEntitySetName()
	{
		provider.getEntitySet(SchemaUtils.CONTAINER_NAME, StringUtils.EMPTY);
	}

	@Test
	public void testGetAssociationSet() throws ODataException
	{
		mockContainerWithEntityAndAssociations(SchemaUtils.CONTAINER_NAME);

		final AssociationSet associationSet = provider.getAssociationSet(SchemaUtils.CONTAINER_NAME, toFullQualifiedName("Product"),
				StringUtils.EMPTY, StringUtils.EMPTY);

		assertThat(associationSet)
				.isNotNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetAssociationSet_WithOutAssociationName() throws ODataException
	{
		provider.getAssociationSet(StringUtils.EMPTY, toFullQualifiedName(""),
				StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test
	public void testGetAssociationSet_Null() throws ODataException
	{
		mockContainerWithEntityAndAssociations(SchemaUtils.CONTAINER_NAME);

		final AssociationSet associationSet = provider.getAssociationSet(SchemaUtils.CONTAINER_NAME,
				toFullQualifiedName(INVALID), StringUtils.EMPTY, StringUtils.EMPTY);

		assertThat(associationSet)
				.isNull();
	}

	@Test
	public void testGetAssociationSet_InvalidContainer() throws ODataException
	{
		mockContainerWithEntityAndAssociations(INVALID);

		final AssociationSet associationSet = provider.getAssociationSet(SchemaUtils.CONTAINER_NAME,
				toFullQualifiedName("ProductInvalid"), StringUtils.EMPTY, StringUtils.EMPTY);

		assertThat(associationSet)
				.isNull();
	}


	@Test
	public void testGetEntityType()
	{
		mockEntityType();
		final EntityType entityType = provider.getEntityType(toFullQualifiedName(ENTITY_NAME));

		assertThat(entityType)
				.isNotNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetEntityType_WithOutEntityName()
	{
		final EntityType entityType = provider.getEntityType(toFullQualifiedName(""));

		assertThat(entityType)
				.isNotNull();
	}

	@Test
	public void testGetEntityType_Null()
	{
		mockEntityType();
		final EntityType entityType = provider.getEntityType(toFullQualifiedName(INVALID));

		assertThat(entityType)
				.isNull();
	}

	@Test
	public void testGetAssociation()
	{
		mockEntityAssociations();

		final Association association = provider.getAssociation(toFullQualifiedName(NAMESPACE));

		assertThat(association)
				.isNotNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetAssociation_WithOutAssociation()
	{
		provider.getAssociation(toFullQualifiedName(""));
	}

	@Test
	public void testGetAssociation_Null()
	{
		mockEntityAssociations();

		final Association association = provider.getAssociation(toFullQualifiedName(INVALID));

		assertThat(association)
				.isNull();
	}

	@Test
	public void testGetEntityContainerInfo_WithOutContainerName()
	{
		final EntityContainerInfo containerInfo = provider.getEntityContainerInfo(null);

		assertThat(containerInfo)
				.isNotNull();
		assertThat(containerInfo.isDefaultEntityContainer())
				.isTrue();
	}

	@Test
	public void testGetEntityContainerInfo()
	{
		final EntityContainerInfo containerInfo = provider.getEntityContainerInfo(SchemaUtils.CONTAINER_NAME);

		assertThat(containerInfo)
				.isNotNull();
		assertThat(containerInfo.isDefaultEntityContainer())
				.isTrue();
	}

	private void mockSchemaGenerationForAllTypes()
	{
		final IntegrationObjectItemModel type1 = mock(IntegrationObjectItemModel.class);
		final IntegrationObjectItemModel type2 = mock(IntegrationObjectItemModel.class);
		final Set<IntegrationObjectItemModel> types = Sets.newHashSet(type1, type2);
		doReturn(types).when(integrationObjectService).findAllIntegrationObjectItems(SERVICE_VALUE);
		doReturn(schema).when(schemaGenerator).generateSchema(new HashSet<>(types));
	}

	private void mockEntityType()
	{
		final Schema schema = mock(Schema.class);
		final EntityType entityType = mock(EntityType.class);
		final List<EntityType> ENTITY_TYPES = Collections.singletonList(entityType);

		doReturn(schema).when(schemaGenerator).generateSchema(any());
		doReturn(ENTITY_TYPES).when(schema).getEntityTypes();
		doReturn(ENTITY_NAME).when(entityType).getName();
	}

	private void mockContainerWithEntityAndAssociations(final String containerName)
	{
		final EntityContainer container = mock(EntityContainer.class);
		final EntitySet entitySet = mock(EntitySet.class);
		final Schema schema = mock(Schema.class);
		final AssociationSet associationSet = mock(AssociationSet.class);

		final List<AssociationSet> ASSOCIATION_SET = Collections.singletonList(associationSet);
		final List<EntityContainer> ENTITY_CONTAINERS = Collections.singletonList(container);
		final List<EntitySet> ENTITY_SETS = Collections.singletonList(entitySet);

		doReturn(schema).when(schemaGenerator).generateSchema(any());
		doReturn(ENTITY_CONTAINERS).when(schema).getEntityContainers();
		doReturn(ENTITY_SETS).when(container).getEntitySets();
		doReturn(containerName).when(container).getName();
		doReturn(ASSOCIATION_SET).when(container).getAssociationSets();
		doReturn(ENTITYSET_NAME).when(entitySet).getName();
		doReturn(toFullQualifiedName("Product")).when(associationSet).getAssociation();
	}

	private void mockEntityAssociations()
	{
		final Schema schema = mock(Schema.class);
		final Association association = mock(Association.class);
		final List<Association> associationList = Collections.singletonList(association);

		doReturn(schema).when(schemaGenerator).generateSchema(any());
		doReturn(associationList).when(schema).getAssociations();
		doReturn(NAMESPACE).when(association).getName();
	}

	private void thenSchemasAreReturned(final List<Schema> schemas)
	{
		assertThat(schemas)
				.isNotNull()
				.hasSize(1)
				.contains(schema);
	}

	private void mockSchemaGenerationForType(final String type, final String service)
	{
		final Set<IntegrationObjectItemModel> models = Sets.newHashSet(mock(IntegrationObjectItemModel.class));
		doReturn(models).when(integrationObjectService).findAllDependencyTypes(type, service);
		doReturn(schema).when(schemaGenerator).generateSchema(models);
	}

	private ODataContext mockContext()
	{
		final ODataContext context = mock(ODataContext.class);
		doReturn(ENTITY_TYPE_VALUE).when(context).getParameter(ENTITY_TYPE);
		doReturn(SERVICE_VALUE).when(context).getParameter(SERVICE);
		return context;
	}

	private ODataContext mockContextWithNoEntityType()
	{
		final ODataContext context = mock(ODataContext.class);
		doReturn(SERVICE_VALUE).when(context).getParameter(SERVICE);
		return context;
	}

	private ODataContext mockContextWithNoService()
	{
		final ODataContext context = mock(ODataContext.class);
		doReturn(ENTITY_TYPE_VALUE).when(context).getParameter(ENTITY_TYPE);
		return context;
	}

	private List<Schema> whenGetSchemasIsCalled() throws ODataException
	{
		return provider.getSchemas();
	}
}