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

package com.hybris.datahub.core.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.util.CSVConstants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

@UnitTest
public class DataHubDataFragmentUnitTest extends AbstractScriptFragmentTest<DataHubDataFragment>
{
	private static final String VALID_IMPEX_HEADER = "INSERT Category;code[unique=true];$catalogVersion";
	private static final String VALID_URL = "https://somehost/rest/123/Catalog";
	private static final String INVALID_URL = "https://somehost/rest";
	private static final String DATA = "1;Spring Catalog;";
	private static final String HEADER = "#$HEADER: x-TenantId=master";

	private DataHubFacade dataHub;

	@Before
	public void setUp() throws Exception
	{
		dataHub = setUpDataHub();
		fragment = spy(new DataHubDataFragment(dataHub));
		doNothing().when(fragment).validateImpexHeader(any(String.class), any(String.class));
	}

	private DataHubFacade setUpDataHub()
	{
		final InputStream input = new ByteArrayInputStream(DATA.getBytes());
		final DataHubFacade facade = Mockito.mock(DataHubFacade.class);
		doReturn(input).when(facade).readData(eq(VALID_URL), anyMapOf(String.class, Object.class));
		return facade;
	}

	@Test
	public void testTheFacadeImplementationCanBeReadBack()
	{
		assertThat(fragment.getDataHubFacade()).isSameAs(dataHub);
	}

	@Test
	public void testScriptFragmentIsEmptyBeforeAnyLineWasAdded() throws IOException
	{
		assertThat(fragment.getContent()).isEmpty();
	}

	@Test
	public void testCommentCannotBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("# das ist ein kommentar");
	}

	@Test
	public void testMacroCannotBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])");
	}

	@Test
	public void testEmptyLineCanBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("");
	}

	@Test
	public void testNullCannotBeAdded() throws IOException
	{
		final boolean wasAdded = fragment.addLine(null, new ArrayList<>());

		assertLineWasNotAdded(wasAdded, null);
	}

	@Test
	public void testSomeTextCannotBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("[unique=true,default=apparelProductCatalog:Staged]");
	}

	@Test
	public void testINSERT_UPDATEHeaderCanBeAdded() throws IOException
	{
		testLineThatShouldBeAdded("INSERT_UPDATE Category;code[unique=true];$catalogVersion");
	}

	@Test
	public void testREMOVEHeaderCanBeAdded() throws IOException
	{
		testLineThatShouldBeAdded("REMOVE Category;code[unique=true];$catalogVersion");
	}

	@Test
	public void testINSERTHeaderCanBeAdded() throws IOException
	{
		testLineThatShouldBeAdded(VALID_IMPEX_HEADER);
	}

	@Test
	public void testUrlIsEmptyBeforeTheUrlCommentWasAdded()
	{
		assertThat(fragment.getUrl()).isEmpty();
	}

	@Test
	public void testURLCommentCanBeAdded()
	{
		final List<ImpExFragment> fragments = new ArrayList<>();
		fragment.addLine(VALID_IMPEX_HEADER, fragments);
		final boolean wasAdded = fragment.addLine("#$URL: https://somehost/rest/123/Catalog?fields=parent,catalog,version", fragments);

		assertThat(wasAdded).isTrue();
		assertThat(fragment.getUrl()).isEqualTo("https://somehost/rest/123/Catalog?fields=parent,catalog,version");
	}

	@Test
	public void testHeadersAreEmptyBeforeAnyHeaderIsAdded()
	{
		final Map<String, String> headers = fragment.getHeaders();

		assertThat(headers).isNotNull()
						   .isEmpty();
	}

	@Test
	public void testHEADERCommentCanBeAdded()
	{
		fragment.addLine(VALID_IMPEX_HEADER, null);
		final boolean wasAdded = fragment.addLine(HEADER, new ArrayList<>());

		assertThat(wasAdded).isTrue();
		assertThat(fragment.getHeader("x-TenantId")).isEqualTo("master");
	}

	@Test
	public void testIgnoresUnparsibleHeader()
	{
		final boolean added = fragment.addLine("#$HEADER: x-Tenant: master", new ArrayList<>());

		assertThat(added).isFalse();
		assertThat(fragment.getHeaders()).isEmpty();
	}

	@Test
	public void testContentIsDataFromTheIntegrationLayerInsteadOfAddedLines() throws IOException
	{
		final List<ImpExFragment> fragments = new ArrayList<>();
		fragment.addLine(VALID_IMPEX_HEADER, fragments);
		fragment.addLine("#$URL: " + VALID_URL, fragments);
		fragment.addLine(HEADER, fragments);

		final String content = fragment.getContent();

		assertThat(content).isEqualTo(VALID_IMPEX_HEADER + CSVConstants.HYBRIS_LINE_SEPARATOR + DATA);
	}

	@Test
	public void testPassesUrlAndHeadersToTheIntegrationLayer() throws IOException
	{
		final ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
		fragment.addLine(VALID_IMPEX_HEADER);
		fragment.addLine("#$URL: " + VALID_URL);
		fragment.addLine(HEADER);

		fragment.getContent();

		verify(dataHub).readData(url.capture(), headers.capture());
		assertThat(url.getValue()).isEqualTo(VALID_URL);
		assertThat(headers.getValue().get("x-TenantId")).isEqualTo("master");
	}

	@Test
	public void testDataFromTheIntegrationLayerCanBeRetrievedAsStream() throws IOException
	{
		fragment.addLine(VALID_IMPEX_HEADER);
		fragment.addLine("#$URL: " + VALID_URL);
		fragment.addLine(HEADER);

		final String content = readContentFromTheInputStream();

		assertThat(content).isEqualTo(VALID_IMPEX_HEADER + CSVConstants.HYBRIS_LINE_SEPARATOR + DATA);
	}

	@Test(expected = IOException.class)
	public void testOtherExceptionsAreConvertedToIOExceptionWhenInputStreamIsRetreived() throws IOException
	{
		throwExceptionOnReadingFromRemoteResource(new IllegalStateException(), INVALID_URL);

		fragment.addLine(VALID_IMPEX_HEADER);
		fragment.addLine("#$URL: " + INVALID_URL);
		fragment.addLine(HEADER);

		readContentFromTheInputStream();
	}

	@Test
	public void testHeaderCanBeAdded() throws IOException
	{
		testLineThatShouldBeAdded("INSERT_UPDATE Category;code[unique=true];$catalogVersion");
	}

	@Test
	public void testValidMacroAndHeader() throws IOException
	{
		final ImpExFragment macroFragment = new ImpexMacroFragment();
		macroFragment.addLine("$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]" + CSVConstants.HYBRIS_LINE_SEPARATOR);
		final String impexHeader = "INSERT_UPDATE Catalog;id[unique=true]";
		final List<ImpExFragment> fragments = new ArrayList<>();
		fragments.add(macroFragment);

		final boolean added = fragment.addLine(impexHeader, fragments);

		assertLineIsInTheFragment(added, impexHeader);
	}

	@Test(expected = ImpexValidationException.class)
	public void testValidMacroAndInvalidHeader() throws Exception
	{
		final ImpExFragment macroFragment = new ImpexMacroFragment();
		macroFragment.addLine("$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]" + CSVConstants.HYBRIS_LINE_SEPARATOR);
		final String invalidImpexHeader = "INSERT_UPDATE jlkfsdf";
		final List<ImpExFragment> fragments = new ArrayList<>();
		fragments.add(macroFragment);

		doThrow(new ImpExException("Invalid Impex Header")).when(fragment).validateImpexHeader(invalidImpexHeader + CSVConstants.HYBRIS_LINE_SEPARATOR, macroFragment.getContent());

		fragment.addLine(invalidImpexHeader);
		fragment.addLine("#########", fragments);
	}

	@Test(expected = ImpexValidationException.class)
	public void testInvalidHeaderAndCannotReadBody() throws Exception
	{
		final ImpExFragment macroFragment = new ImpexMacroFragment();
		macroFragment.addLine("$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]" + CSVConstants.HYBRIS_LINE_SEPARATOR);
		final String invalidImpexHeader = "INSERT_UPDATE jlkfsdf";
		final List<ImpExFragment> fragments = new ArrayList<>();
		fragments.add(macroFragment);

		doThrow(new ImpExException("Invalid Impex Header")).when(fragment).validateImpexHeader(invalidImpexHeader + CSVConstants.HYBRIS_LINE_SEPARATOR, macroFragment.getContent());
		doThrow(new IOException("Invalid Impex Header")).when(fragment).getImpexBody();

		fragment.addLine(invalidImpexHeader);
		fragment.addLine("#########", fragments);
	}

	@Test
	public void testHeaderCannotBeAddedTwice()
	{
		fragment.addLine("INSERT product");
		final boolean secondHeaderWasAdded = fragment.addLine("INSERT_UPDATE product");
		assertThat(secondHeaderWasAdded).isFalse();
	}

	private void throwExceptionOnReadingFromRemoteResource(final IllegalStateException ex, final String url)
	{
		doThrow(ex).when(dataHub).readData(eq(url), anyMapOf(String.class, Object.class));
	}
}
