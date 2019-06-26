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
package com.hybris.backoffice.spring;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.bootstrap.config.PlatformConfig;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Utilities;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.ReaderEventListener;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


@IntegrationTest
public class BeansDefinitionImportParserTest extends ServicelayerTest
{
	private static final String ATTRIBUTE_RESOURCES = "resources";

	private final BeansDefinitionImportParser parser = new BeansDefinitionImportParser();

	@Test
	public void shouldGetBeanDefinitionsForAllBackofficeModulesWhenNoPatternDefined() throws Exception
	{
		// given
		final Element ELEMENT = mock(Element.class);
		final ParserContext PARSER_CONTEXT = new ParserContext(mock(XmlReaderContext.class),
				mock(BeanDefinitionParserDelegate.class));
		final List<File> BACKOFFICE_SPRING_MODULES = getBackofficeModules().stream()
				.map(extension -> new File(extension.getItemsXML().getParent(), extension.getName() + "-backoffice-spring.xml"))
				.filter(File::exists).collect(Collectors.toList());

		// when
		final Resource[] resources = parser.getResources(ELEMENT, PARSER_CONTEXT);

		// then
		assertThat(resources).hasSize(BACKOFFICE_SPRING_MODULES.size());
		for (final Resource resource : resources)
		{
			assertThat(BACKOFFICE_SPRING_MODULES).contains(resource.getFile());
		}
	}

	@Test
	public void shouldGetMatchingBeanDefinitionsWhenPatternDefined() throws Exception
	{
		// given
		final String PATTERN_RESOURCES = "backoffice-spring.xml";
		final URL URL_RESOURCE = getClass().getResource("/backoffice-spring.xml");

		final Element ELEMENT = mock(Element.class);
		final NamedNodeMap MAP_ATTRIBUTES = mock(NamedNodeMap.class);
		final Node NODE_RESOURCES = mock(Node.class);
		when(ELEMENT.hasAttributes()).thenReturn(Boolean.TRUE);
		when(ELEMENT.getAttributes()).thenReturn(MAP_ATTRIBUTES);
		when(MAP_ATTRIBUTES.getNamedItem(eq(ATTRIBUTE_RESOURCES))).thenReturn(NODE_RESOURCES);
		when(NODE_RESOURCES.getNodeValue()).thenReturn(PATTERN_RESOURCES);

		final ParserContext PARSER_CONTEXT = new ParserContext(mock(XmlReaderContext.class),
				mock(BeanDefinitionParserDelegate.class));

		// when
		final Resource[] resources = parser.getResources(ELEMENT, PARSER_CONTEXT);

		// then
		assertThat(resources).hasSize(1);
		assertThat(resources[0].getFile()).isEqualTo(FileUtils.toFile(URL_RESOURCE));
	}

	@Test
	public void shouldLoadAllBeansFromSpecifiedResources() throws Exception {
		// given
		final Resource FILE_RESOURCE = new ClassPathResource("/test/import-parser-spring.xml");

		final BeanDefinitionRegistry BEAN_REGISTRY = mock(BeanDefinitionRegistry.class);
		final XmlBeanDefinitionReader READER = new XmlBeanDefinitionReader(BEAN_REGISTRY);
		final XmlReaderContext READER_CONTEXT = new XmlReaderContext(mock(Resource.class), mock(ProblemReporter.class),
				mock(ReaderEventListener.class), mock(SourceExtractor.class), READER, mock(NamespaceHandlerResolver.class));
		final ParserContext PARSER_CONTEXT = new ParserContext(READER_CONTEXT, mock(BeanDefinitionParserDelegate.class));

		// when
		parser.importResources(new Resource[] { FILE_RESOURCE }, PARSER_CONTEXT);

		// then
		final ArgumentCaptor<String> beanNames = ArgumentCaptor.forClass(String.class);
		verify(BEAN_REGISTRY, times(2)).registerBeanDefinition(beanNames.capture(), any());

		assertThat(beanNames.getAllValues()).containsExactly("load-all-beans-from-specified-resources", "load-all-beans-from-specified-resources-2");
	}

	private static List<ExtensionInfo> getBackofficeModules()
	{
		final PlatformConfig PLATFORM_CONFIG = ConfigUtil.getPlatformConfig(Utilities.class);
		return PLATFORM_CONFIG.getExtensionInfosInBuildOrder().stream()
				.filter(ext -> Boolean.parseBoolean(ext.getMeta("backoffice-module"))).collect(Collectors.toList());
	}

}
