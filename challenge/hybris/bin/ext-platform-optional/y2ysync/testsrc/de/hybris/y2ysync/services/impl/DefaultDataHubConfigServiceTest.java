/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.y2ysync.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.y2ysync.XMLContentAssert;
import de.hybris.y2ysync.model.Y2YColumnDefinitionModel;
import de.hybris.y2ysync.model.Y2YStreamConfigurationContainerModel;
import de.hybris.y2ysync.model.Y2YStreamConfigurationModel;
import de.hybris.y2ysync.services.DataHubConfigService;
import de.hybris.y2ysync.services.DataHubExtGenerationConfig;
import de.hybris.y2ysync.services.SyncConfigService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;


@IntegrationTest
public class DefaultDataHubConfigServiceTest extends ServicelayerTransactionalBaseTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private DataHubConfigService dataHubConfigService;
	@Resource
	private SyncConfigService syncConfigService;
	@Resource
	private TypeService typeService;
	private Y2YStreamConfigurationContainerModel container;

	@Before
	public void setUp() throws Exception
	{
		container = syncConfigService.createStreamConfigurationContainer("testContainer");
		modelService.save(container);
	}

	@Test
	public void shouldThrowIllegalStateExceptionWhenForUnsavedStreamConfiguration() throws Exception
	{
		// given
		final Set<AttributeDescriptorModel> attributeDescriptors = getAttributeDescriptorsFor("Product", "code", "catalogVersion",
				"name", "thumbnail");
		final Y2YStreamConfigurationModel configuration = syncConfigService.createStreamConfiguration(container, "Product",
				attributeDescriptors, Collections.emptySet());

		try
		{
			final DataHubExtGenerationConfig dataHubExtGenerationConfig = getDataHubExtGenerationConfig();
			// when
			dataHubConfigService.createModelDefinitions(configuration, dataHubExtGenerationConfig);
			fail("Should throw IllegalStateException");
		}
		catch (final IllegalStateException e)
		{
			// then fine
		}
	}

	@Test
	public void shouldThrowIllegalStateExceptionWhenForUnsavedStreamConfigurationContainer() throws Exception
	{
		// given
		final Y2YStreamConfigurationContainerModel container = syncConfigService.createStreamConfigurationContainer("unsaved");

		try
		{
			// when
			dataHubConfigService.createModelDefinitions(container);
			fail("Should throw IllegalStateException");
		}
		catch (final IllegalStateException e)
		{
			// then fine
		}
	}

	@Test
	public void shouldGenerateDataHubModelForStreamConfiguration() throws Exception
	{
		// given
		final Set<AttributeDescriptorModel> attributeDescriptors = getAttributeDescriptorsFor("Product", "code", "catalogVersion",
				"name", "thumbnail");
		final Y2YStreamConfigurationModel configuration = syncConfigService.createStreamConfiguration(container, "Product",
				attributeDescriptors, Collections.emptySet());
		modelService.save(configuration);

		final DataHubExtGenerationConfig dataHubExtGenerationConfig = getDataHubExtGenerationConfig();

		// when
		final String modelDefinitions = dataHubConfigService.createModelDefinitions(configuration, dataHubExtGenerationConfig);

		// then
		assertThat(modelDefinitions).isNotNull();
		assertThat(modelDefinitions.replaceAll("[\\r\\n]+", StringUtils.EMPTY))
				.isEqualTo(getExpectedXmlFromResource("dataHubModelFromStreamConfig.xml")
						.replaceFirst("<!--[\\s\\S]*?-->", StringUtils.EMPTY).replaceAll("[\\r\\n\\t]+", StringUtils.EMPTY));
	}

	private DataHubExtGenerationConfig getDataHubExtGenerationConfig()
	{
		final DataHubExtGenerationConfig dataHubExtGenerationConfig = new DataHubExtGenerationConfig();
		dataHubExtGenerationConfig.setGenerateRawItems(true);
		dataHubExtGenerationConfig.setGenerateCanonicalItems(true);
		dataHubExtGenerationConfig.setGenerateTargetItems(true);
		dataHubExtGenerationConfig.setPrettyFormat(false);
		dataHubExtGenerationConfig.setTargetExportCodes("");
		dataHubExtGenerationConfig.setTargetExportURL("http://www.xsd2xml.com");
		dataHubExtGenerationConfig.setTargetPassword("str1234");
		dataHubExtGenerationConfig.setTargetType("str1234");
		dataHubExtGenerationConfig.setTargetUserName("str1234");
		return dataHubExtGenerationConfig;
	}

	@Test
	public void shouldGenerateDataHubModelForStreamConfigurationContainer() throws Exception
	{
		// given
		final Set<AttributeDescriptorModel> productDescriptors = getAttributeDescriptorsFor("Product", "code", "catalogVersion",
				"name", "thumbnail");
		final Set<AttributeDescriptorModel> titleDescriptors = getAttributeDescriptorsFor("Title", "code", "name");
		final Y2YStreamConfigurationModel productConfig = syncConfigService.createStreamConfiguration(container, "Product",
				productDescriptors, Collections.emptySet());
		final Y2YStreamConfigurationModel titleConfig = syncConfigService.createStreamConfiguration(container, "Title",
				titleDescriptors, Collections.emptySet());
		modelService.saveAll(productConfig, titleConfig);

		final DataHubExtGenerationConfig dataHubExtGenerationConfig = getDataHubExtGenerationConfig();

		// when
		final String modelDefinitions = dataHubConfigService.createDataHubExtension(container, dataHubExtGenerationConfig);

		// then
		assertThat(modelDefinitions).isNotNull();
		final String generatedXml = modelDefinitions.replaceAll("[\\r\\n]+", StringUtils.EMPTY).replace(
				"xmlns=\"http://www.hybris.com/schema/\"", StringUtils.EMPTY);
		final String expectedXml = getExpectedXmlFromResource("dataHubModelFromStreamConfigContainer.xml").replace(
				"xmlns=\"http://www.hybris.com/schema/\"", StringUtils.EMPTY).replaceAll("[\\r\\n\\t]+", StringUtils.EMPTY);
		XMLContentAssert.assertThat(generatedXml).hasTheSameContentAs(expectedXml);
	}

	@Test
	public void shouldGenerateDataHubModelForStreamConfigurationWithUntypedColumnDefintion() throws Exception
	{
		final Y2YColumnDefinitionModel mediaColDef = syncConfigService.createUntypedColumnDefinition("@media(Translator)",
				"pullURL");
		final Set<AttributeDescriptorModel> mediaDesriptors = getAttributeDescriptorsFor("Media", "code", "mime");
		final Y2YStreamConfigurationModel configuration = syncConfigService.createStreamConfiguration(container, "Media",
				mediaDesriptors, Sets.newHashSet(mediaColDef));
		modelService.save(configuration);

		// when
		final DataHubExtGenerationConfig dataHubExtGenerationConfig = getDataHubExtGenerationConfig();

		final String modelDefinitions = dataHubConfigService.createModelDefinitions(configuration, dataHubExtGenerationConfig);

		// then
		assertThat(modelDefinitions).isNotNull();
		assertThat(modelDefinitions.replaceAll("[\\r\\n]+", StringUtils.EMPTY))
				.isEqualTo(getExpectedXmlFromResource("dataHubModelForStreamConfigurationWithUntypedColumnDefintion.xml")
						.replaceFirst("<!--[\\s\\S]*?-->", StringUtils.EMPTY).replaceAll("[\\r\\n\\t]+", StringUtils.EMPTY));
	}

	private Set<AttributeDescriptorModel> getAttributeDescriptorsFor(final String typeCode, final String... qualifiers)
	{
		return Arrays.asList(qualifiers).stream().map(q -> typeService.getAttributeDescriptor(typeCode, q))
				.sorted((d1, d2) -> d1.getQualifier().compareTo(d2.getQualifier()))
				.collect(Collectors.toCollection(LinkedHashSet::new));

	}

	private String getExpectedXmlFromResource(final String fileName)
	{
		final InputStream resource = DefaultDataHubConfigServiceTest.class.getResourceAsStream("/test/datahubxml/" + fileName);

		try
		{
			return IOUtils.toString(resource, "UTF-8").replaceAll("[\\r\\n]+", StringUtils.EMPTY);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
