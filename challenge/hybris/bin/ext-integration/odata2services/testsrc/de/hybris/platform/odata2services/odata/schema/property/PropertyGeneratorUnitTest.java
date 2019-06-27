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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PropertyGeneratorUnitTest
{
	@InjectMocks
	private PropertyGenerator propertyGenerator;
	@Mock
	private SchemaElementGenerator<List<AnnotationAttribute>, IntegrationObjectItemAttributeModel> attributeListGenerator;

	private final IntegrationObjectItemAttributeModel attributeModel = model();
	private final AnnotationAttribute annotationAttribute = new AnnotationAttribute();

	@Before
	public void setup()
	{
		when(attributeListGenerator.generate(attributeModel)).thenReturn(Collections.singletonList(annotationAttribute));
	}

	@Test
	public void testGenerateSimpleProperty()
	{
		final Property prop = propertyGenerator.generate(attributeModel);

		assertThat(prop).isInstanceOf(SimpleProperty.class);
		final SimpleProperty simpleProp = (SimpleProperty) prop;
		assertThat(simpleProp.getName()).isEqualTo(attributeModel.getAttributeName());
		assertThat(simpleProp.getType()).isEqualTo(EdmSimpleTypeKind.Double);
		assertThat(simpleProp.getAnnotationAttributes()).containsExactly(annotationAttribute);
	}

	@Test
	public void testGenerateNullAttributeModel()
	{
		assertThatThrownBy(() -> propertyGenerator.generate(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGenerateNullAttributeDescriptor()
	{
		assertThatThrownBy(() -> propertyGenerator.generate(mock(IntegrationObjectItemAttributeModel.class)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private IntegrationObjectItemAttributeModel model()
	{
		final AttributeDescriptorModel descriptor = new AttributeDescriptorModel();
		final TypeModel type = new AtomicTypeModel();
		type.setCode("java.lang.Double");

		descriptor.setAttributeType(type);
		descriptor.setQualifier("price");
		descriptor.setPrimitive(true);

		final IntegrationObjectItemAttributeModel model = new IntegrationObjectItemAttributeModel();
		model.setAttributeDescriptor(descriptor);
		model.setAttributeName("usprice");
		model.setIntegrationObjectItem(new IntegrationObjectItemModel());
		return model;
	}
}