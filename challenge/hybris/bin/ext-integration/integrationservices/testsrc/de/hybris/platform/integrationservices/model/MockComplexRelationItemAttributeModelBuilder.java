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

import de.hybris.platform.core.enums.RelationEndCardinalityEnum;
import de.hybris.platform.core.model.type.RelationDescriptorModel;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public class MockComplexRelationItemAttributeModelBuilder extends BaseMockItemAttributeModelBuilder<MockComplexRelationItemAttributeModelBuilder>
{
	private static final String DEFAULT_SOURCE_TYPE = "MasterItemType";
	private static final String DEFAULT_TARGET_TYPE = "ChildItemType";
	private static final RelationEndCardinalityEnum DEFAULT_SOURCE_CARDINALITY = RelationEndCardinalityEnum.ONE;
	private static final RelationEndCardinalityEnum DEFAULT_TARGET_CARDINALITY = RelationEndCardinalityEnum.MANY;

	private String source = DEFAULT_SOURCE_TYPE;
	private String target = DEFAULT_TARGET_TYPE;
	private RelationEndCardinalityEnum sourceCardinality = DEFAULT_SOURCE_CARDINALITY;
	private RelationEndCardinalityEnum targetCardinality = DEFAULT_TARGET_CARDINALITY;
	private boolean isSource = true;
	private RelationDescriptorModel sourceAttribute;
	private RelationDescriptorModel targetAttribute;

	MockComplexRelationItemAttributeModelBuilder()
	{}

	public MockComplexRelationItemAttributeModelBuilder withSource(final String source)
	{
		this.source = source;
		return withIntegrationObjectItemCode(source);
	}

	public MockComplexRelationItemAttributeModelBuilder withTarget(final String target)
	{
		this.target = target;
		return withReturnIntegrationObject(target);
	}

	public MockComplexRelationItemAttributeModelBuilder withSourceCardinality(final RelationEndCardinalityEnum sourceCardinality)
	{
		this.sourceCardinality = sourceCardinality;
		return this;
	}

	public MockComplexRelationItemAttributeModelBuilder withTargetCardinality(final RelationEndCardinalityEnum targetCardinality)
	{
		this.targetCardinality = targetCardinality;
		return this;
	}

	public MockComplexRelationItemAttributeModelBuilder withIsSource(final boolean isSource)
	{
		this.isSource = isSource;
		return this;
	}

	public MockComplexRelationItemAttributeModelBuilder withSourceAttribute(final MockRelationAttributeDescriptorModelBuilder builder)
	{
		return withSourceAttribute(builder.build());
	}

	private MockComplexRelationItemAttributeModelBuilder withSourceAttribute(final RelationDescriptorModel model)
	{
		sourceAttribute = model;
		return this;
	}

	public MockComplexRelationItemAttributeModelBuilder withTargetAttribute(final MockRelationAttributeDescriptorModelBuilder builder)
	{
		return withTargetAttribute(builder.build());
	}

	private MockComplexRelationItemAttributeModelBuilder withTargetAttribute(final RelationDescriptorModel model)
	{
		targetAttribute = model;
		return this;
	}

	@Override
	public IntegrationObjectItemAttributeModel build()
	{
		preBuildCheck();

		attributeDescriptorBuilderOrDefault(MockRelationDescriptorModelBuilder.oneToManyRelation())
				.withTarget(target)
				.withSourceCardinality(sourceCardinality)
				.withTargetCardinality(targetCardinality)
				.withIsSource(isSource)
				.withSourceAttribute(sourceAttribute)
				.withTargetAttribute(targetAttribute);

		return super.build();
	}

	private void preBuildCheck()
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(source) && StringUtils.isNotBlank(target)
						&& sourceCardinality != null && targetCardinality != null,
				"Source, target, source cardinality, and target cardinality are required for building the IntegrationObjectItemAttributeModel");
	}

	@Override
	protected MockComplexRelationItemAttributeModelBuilder myself()
	{
		return this;
	}
}
