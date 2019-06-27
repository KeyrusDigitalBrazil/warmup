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

package de.hybris.platform.odata2services.odata.schema.attribute;

import static de.hybris.platform.integrationservices.model.MockIntegrationObjectItemModelBuilder.itemModelBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyMetadataGenerator;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AliasAnnotationGeneratorUnitTest
{
	@InjectMocks
	private final AliasAnnotationGenerator aliasGenerator = new AliasAnnotationGenerator();
	@Mock
	private IntegrationKeyMetadataGenerator integrationKeyMetadataGenerator;
	private final IntegrationObjectItemModel item = itemModelBuilder().build();

	@Test
	public void testGenerateGivenNull()
	{
		when(integrationKeyMetadataGenerator.generateKeyMetadata(null)).thenReturn(null);
		
		assertThat(aliasGenerator.generate(null)).isNull();
	}

	@Test
	public void testGenerateNoUniqueProperties()
	{
		when(integrationKeyMetadataGenerator.generateKeyMetadata(item)).thenReturn("");

		assertThat(aliasGenerator.generate(item)).isNull();
	}

	@Test
	public void testGenerateAlias()
	{
		when(integrationKeyMetadataGenerator.generateKeyMetadata(item)).thenReturn("SomeType_uniqueAttribute");
		final AnnotationAttribute aliasAnnotation = aliasGenerator.generate(item);

		assertThat(aliasAnnotation).isNotNull();
		assertThat(aliasAnnotation.getName()).isEqualTo("s:Alias");
		assertThat(aliasAnnotation.getText()).isEqualTo("SomeType_uniqueAttribute");
	}
}