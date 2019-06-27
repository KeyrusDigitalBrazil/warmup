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
package de.hybris.platform.integrationservices.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.enums.RelationEndCardinalityEnum;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.core.model.type.RelationMetaTypeModel;

public class MockRelationDescriptorModelBuilder extends BaseMockAttributeDescriptorModelBuilder<MockRelationDescriptorModelBuilder, RelationDescriptorModel>
{
	private String sourceType;
	private String targetType;
	private RelationEndCardinalityEnum sourceCardinality;
	private RelationEndCardinalityEnum targetCardinality;
	private boolean isSource;
	private RelationDescriptorModel targetAttribute;
	private RelationDescriptorModel sourceAttribute;

	private static MockRelationDescriptorModelBuilder relationDescriptorModelBuilder()
	{
		return new MockRelationDescriptorModelBuilder();
	}

	public static MockRelationDescriptorModelBuilder oneToOneRelation()
	{
		return relationDescriptorModelBuilder()
				.withSourceCardinality(RelationEndCardinalityEnum.ONE)
				.withTargetCardinality(RelationEndCardinalityEnum.ONE);
	}

	public static MockRelationDescriptorModelBuilder oneToManyRelation()
	{
		return relationDescriptorModelBuilder()
				.withSourceCardinality(RelationEndCardinalityEnum.ONE)
				.withTargetCardinality(RelationEndCardinalityEnum.MANY);
	}

	public static MockRelationDescriptorModelBuilder manyToOneRelation()
	{
		return relationDescriptorModelBuilder()
				.withSourceCardinality(RelationEndCardinalityEnum.MANY)
				.withTargetCardinality(RelationEndCardinalityEnum.ONE);
	}

	public MockRelationDescriptorModelBuilder withSource(final String source)
	{
		this.sourceType = source;
		return this;
	}

	public MockRelationDescriptorModelBuilder withTarget(final String target)
	{
		this.targetType = target;
		return this;
	}

	MockRelationDescriptorModelBuilder withSourceCardinality(final RelationEndCardinalityEnum sourceCardinality)
	{
		this.sourceCardinality = sourceCardinality;
		return this;
	}

	MockRelationDescriptorModelBuilder withTargetCardinality(final RelationEndCardinalityEnum targetCardinality)
	{
		this.targetCardinality = targetCardinality;
		return this;
	}

	public MockRelationDescriptorModelBuilder withIsSource(final boolean isSource)
	{
		this.isSource = isSource;
		return this;
	}

	public MockRelationDescriptorModelBuilder withSourceAttribute(final MockRelationAttributeDescriptorModelBuilder attr)
	{
		return withSourceAttribute(attr.build());
	}

	MockRelationDescriptorModelBuilder withSourceAttribute(final RelationDescriptorModel attr)
	{
		sourceAttribute = attr;
		return this;
	}

	public MockRelationDescriptorModelBuilder withTargetAttribute(final MockRelationAttributeDescriptorModelBuilder attr)
	{
		return withTargetAttribute(attr.build());
	}

	MockRelationDescriptorModelBuilder withTargetAttribute(final RelationDescriptorModel attr)
	{
		targetAttribute = attr;
		return this;
	}

	public RelationDescriptorModel build()
	{
		final ComposedTypeModel mockComposedTypeModel = composedTypeModel(targetType + "Relation");
		final RelationMetaTypeModel relationTypeModel = relationMetaTypeModel();

		final RelationDescriptorModel model = createMock(RelationDescriptorModel.class);
		when(model.getRelationType()).thenReturn(relationTypeModel);
		when(model.getIsSource()).thenReturn(isSource);
		when(model.getAttributeType()).thenReturn(mockComposedTypeModel);
		return model;
	}

	private RelationMetaTypeModel relationMetaTypeModel()
	{
		final ComposedTypeModel sourceComposedTypeModel = composedTypeModel(sourceType);
		final ComposedTypeModel targetComposedTypeModel = composedTypeModel(targetType);

		final RelationMetaTypeModel relationMetaTypeModel = mock(RelationMetaTypeModel.class);
		when(relationMetaTypeModel.getSourceType()).thenReturn(sourceComposedTypeModel);
		when(relationMetaTypeModel.getTargetType()).thenReturn(targetComposedTypeModel);
		when(relationMetaTypeModel.getSourceTypeCardinality()).thenReturn(sourceCardinality);
		when(relationMetaTypeModel.getTargetTypeCardinality()).thenReturn(targetCardinality);
		when(relationMetaTypeModel.getTargetAttribute()).thenReturn(targetAttribute);
		when(relationMetaTypeModel.getSourceAttribute()).thenReturn(sourceAttribute);
		return relationMetaTypeModel;
	}

	@Override
	protected MockRelationDescriptorModelBuilder myself()
	{
		return this;
	}
}
