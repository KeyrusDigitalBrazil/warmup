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
package com.hybris.backoffice.config.zk.au;

import com.hybris.cockpitng.admin.CockpitAdminService;
import com.hybris.cockpitng.core.config.CockpitConfigurationException;
import com.hybris.cockpitng.core.config.impl.DefaultCockpitConfigurationService;
import com.hybris.cockpitng.core.config.impl.jaxb.Config;
import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.core.util.jaxb.SchemaValidationStatus;
import com.hybris.cockpitng.util.CockpitSessionService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigUploadAuExtensionTest
{

	private static final String XML_CONTENT = "xml-content";

	@Spy
	private DefaultConfigUploadAuExtension ext;

	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private DefaultCockpitConfigurationService configurationService;
	@Mock
	private CockpitAdminService cockpitAdminService;
	@Mock
	private Unmarshaller unmarshaller;
	@Mock
	private CockpitProperties cockpitProperties;
	@Mock
	private CockpitSessionService sessionService;

	@Before
	public void setUp() throws JAXBException
	{
		doReturn(configurationService).when(ext).getCockpitConfigurationService(request);
		doReturn(cockpitAdminService).when(ext).getCockpitAdminService(request);
		doReturn(unmarshaller).when(ext).getConfigUnmarshaller(request);
		doReturn(cockpitProperties).when(ext).getCockpitProperties(request);
		doReturn(sessionService).when(ext).getCockpitSessionService(request);
	}

	@Test
	public void handleConfigUploadShouldSaveConfigurationsWithWarnings() throws IOException
	{
		//given
		doReturn(StringUtils.EMPTY).when(ext).fetchConfigFromRequest(any());
		when(configurationService.validate(any())).thenReturn(SchemaValidationStatus.warning());

		//when
		ext.handleConfigUpload(request, response);

		//then
		verify(ext).storeConfig(any(), any(), any());
	}

	@Test
	public void handleConfigUploadShouldNotSaveConfigurationsWithErrors() throws IOException
	{
		//given
		doReturn(StringUtils.EMPTY).when(ext).fetchConfigFromRequest(any());
		when(configurationService.validate(any())).thenReturn(SchemaValidationStatus.error());

		//when
		ext.handleConfigUpload(request, response);

		//then
		verify(ext, times(0)).storeConfig(any(), any(), any());
	}

	@Test(expected = IllegalArgumentException.class)
	public void serviceShouldFailOnUnrecognisedPathInfo()
	{
		//when
		ext.service(request, response, "/unknownPath");
	}

	@Test
	public void serviceShouldDispatchToHandleUpload()
	{
		//given
		doNothing().when(ext).handleConfigUpload(any(), any());

		//when
		ext.service(request, response, "/configUpload");

		//then
		verify(ext).handleConfigUpload(request, response);
	}

	@Test
	public void serviceShouldDispatchToHandleValidate()
	{
		//given
		doNothing().when(ext).handleConfigValidate(any(), any());

		//when
		ext.service(request, response, "/configValidate");

		//then
		verify(ext).handleConfigValidate(request, response);
	}

	@Test
	public void handleConfigValidateShouldNotInteractOnTurnedOffValidation()
	{
		//given
		doReturn(false).when(ext).shouldValidateCockpitConfig(request);

		//when
		ext.handleConfigValidate(request, response);

		//then
		verifyZeroInteractions(response);
	}

	@Test
	public void handleConfigValidateShouldSetHeadersOnResponseOnValidationResult() throws IOException
	{
		//given
		doReturn(true).when(ext).shouldValidateCockpitConfig(request);
		doReturn(null).when(ext).getInputStream(request);
		final SchemaValidationStatus error = SchemaValidationStatus.error("Ops!");
		doReturn(error).when(configurationService).validate(any());
		doReturn("I failed").when(ext).getValidationLabelForStatus(error);

		//when
		ext.handleConfigValidate(request, response);

		//then
		verify(response).addHeader("validationStatus", "error");
		verify(response).addHeader("validationLabel", "I failed");
	}

	@Test
	public void getInputStreamShouldProvideStreamWithRequestData() throws IOException
	{
		//given
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader("Test content")));

		//when
		final InputStream inputStream = ext.getInputStream(request);

		//then
		final StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer);
		assertThat(writer.toString()).isEqualTo("Test content");
	}


	@Test
	public void handleConfigUploadShouldPassRequestData() throws IOException
	{
		//given
		final SchemaValidationStatus successStatus = SchemaValidationStatus.success();
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(XML_CONTENT)));
		doReturn(false).when(ext).isConfigurationFiltered(request);
		when(configurationService.validate(any())).thenReturn(successStatus);

		//when
		ext.handleConfigUpload(request, response);

		//then
		verify(ext).storeConfig(XML_CONTENT, request, response);
		verify(configurationService).setConfigAsString(XML_CONTENT);
		verify(cockpitAdminService).refreshCockpit();
	}

	@Test
	public void storeConfigShouldMergeConfigurationIfFiltered() throws JAXBException, CockpitConfigurationException
	{
		//given
		doReturn(true).when(ext).isConfigurationFiltered(request);
		final Config changes = mock(Config.class);
		final Config merged = mock(Config.class);
		doReturn(changes).when(configurationService).getChangesAsConfig("a config", unmarshaller);
		doReturn(merged).when(ext).getConfigWithAppliedChanges(changes, request);

		//when
		ext.storeConfig("a config", request, response);

		//then
		verify(configurationService).storeRootConfig(merged);
		verify(cockpitAdminService).refreshCockpit();
	}

	@Test
	public void isConfigurationFilteredShouldReturnFalseOnNull()
	{
		//given
		doReturn(null).when(sessionService).getAttribute("showFilterOptions");

		//when
		final boolean filtered = ext.isConfigurationFiltered(request);

		//then
		assertThat(filtered).isFalse();
	}

	@Test
	public void isConfigurationFilteredShouldReturnTrueOnBooleanTrue()
	{
		//given
		doReturn(true).when(sessionService).getAttribute("cockpitConfigurationFiltered");

		//when
		final boolean filtered = ext.isConfigurationFiltered(request);

		//then
		assertThat(filtered).isTrue();
	}

	@Test
	public void handleErrorShouldSet500ErrorCode()
	{
		//when
		ext.handleError(response, new Exception());

		//then
		verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	@Test
	public void shouldValidateCockpitConfigShouldReturnFalseOnFalseString()
	{
		//given
		doReturn(false).when(cockpitProperties).getBoolean("cockpitng.validate.cockpitConfig.orchestrator");

		//when
		final boolean validate = ext.shouldValidateCockpitConfig(request);

		//then
		assertThat(validate).isFalse();
	}

	@Test
	public void shouldValidateCockpitConfigShouldReturnTrueOnTrueString()
	{
		//given
		doReturn(true).when(cockpitProperties).getBoolean("cockpitng.validate.cockpitConfig.orchestrator");

		//when
		final boolean validate = ext.shouldValidateCockpitConfig(request);

		//then
		assertThat(validate).isTrue();
	}

	@Test
	public void shouldValidationBeCalledOnConfigurationUpload() throws IOException
	{
		//given
		final SchemaValidationStatus successStatus = SchemaValidationStatus.success();
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(XML_CONTENT)));
		when(configurationService.validate(any())).thenReturn(successStatus);

		//when
		ext.handleConfigUpload(request, response);

		//then
		verify(configurationService).validate(any());
	}
}
