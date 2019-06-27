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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;

import com.hybris.datahub.core.dto.ItemImportTaskData;
import com.hybris.datahub.core.facades.ImportError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFragmentReaderUnitTest
{
	private static final String IMPEX_MACROS = "$baseProduct=baseProduct(code, catalogVersion(catalog(id[default='apparelProductCatalog']),version[default='Staged']))\n" +
			"$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])[unique=true,default=apparelProductCatalog:Staged]\n";
	private static final String IMPEX_HEADER = "INSERT_UPDATE Product;code[unique=true]; name[lang=en]; Unit(code); $catalogVersion[unique=true,allowNull=true];description[lang=en];approvalStatus(code);ean;manufacturerName\n";
	private static final String CALLBACK_URL = "#$URL: https://integration.layer.host/rest/123/Product/en?fields=code,name,unit,catalogVersion,description,approvalStatus\n";
	private static final String CALLBACK_HEADERS = "#$HEADER: x-TenantId=master\n#$HEADER: someOtherHeader=boo\n";
	private static final String SEPARATOR_LINE = "###########";

	@Spy
	private DefaultFragmentReader reader = new DefaultFragmentReader();
	@Mock
	private DataHubFacade facade;

	private DataHubDataFragment dataHubDataFragmentSpy;
	private ImpExFragment impexMacroFragmentSpy;

	@Before
	public void setUp() throws ImpExException
	{
		reader.setDataHubFacade(facade);
		final DataHubDataFragment dataHubDataFragment = new DataHubDataFragment(reader.getFacade());
		dataHubDataFragmentSpy = spy(dataHubDataFragment);
		final ImpexMacroFragment impexMacroFragment = new ImpexMacroFragment();
		impexMacroFragmentSpy = spy(impexMacroFragment);
		doNothing().when(dataHubDataFragmentSpy).validateImpexHeader(any(String.class), any(String.class));
		doReturn(new ImpExFragment[]{impexMacroFragmentSpy, dataHubDataFragmentSpy, new ConstantTextFragment()}).when(reader).getFragmentsToTry();
	}

	@Test
	public void testSplitsImpexScriptIntoLogicalBlocks() throws ImpExException
	{
		final List<ImpExFragment> blocks = reader.readScriptFragments(scriptCtx());

		assertThat(blocks).hasSize(3);
	}

	@Test
	public void testPreservesTheOrderOfTheFragmentsAsInTheOriginalScript() throws ImpExException
	{
		final ImpExFragment[] blocks = reader.readScriptFragments(scriptCtx()).toArray(new ImpExFragment[3]);

		assertThat(blocks[0]).isInstanceOf(ImpexMacroFragment.class);
		assertThat(blocks[1]).isInstanceOf(DataHubDataFragment.class);
	}

	@Test
	public void testInjectFacadeIntoTheDataFragment() throws ImpExException
	{
		final DataHubDataFragment dataFrag = extractIntegrationLayerDataFragment(scriptCtx());

		assertThat(dataFrag.getDataHubFacade()).isNotNull()
											   .isEqualTo(facade);
	}

	public void testImportErrorIsCreatedWhenMacroFragmentCannotBeRed() throws ImpExException, IOException
	{
		doThrow(new IOException()).when(impexMacroFragmentSpy).getContent();
		final ItemImportTaskData itemImportTaskData = scriptCtx();
		reader.readScriptFragments(itemImportTaskData);
		assertThat(itemImportTaskData.getHeaderErrors()).isNotEmpty();
	}

	@Test
	public void testValidationErrorIsAddedToContext() throws ImpExException
	{
		final List<ImpExFragment> fragments = new ArrayList<>();
		fragments.add(impexMacroFragmentSpy);
		fragments.add(dataHubDataFragmentSpy);
		doThrow(new ImpexValidationException(Collections.singletonList(new ImportError()))).when(dataHubDataFragmentSpy).addLine(SEPARATOR_LINE, fragments);
		final ItemImportTaskData itemImportTaskData = scriptCtx();
		reader.readScriptFragments(itemImportTaskData);
		assertThat(itemImportTaskData.getHeaderErrors()).isNotEmpty();
	}

	@Test
	public void testDataHubDataFragmentIsNotIncludedInFragmentsListWhenItContainsAnInvalidHeader() throws ImpExException
	{
		final List<ImpExFragment> fragments = new ArrayList<>();
		fragments.add(impexMacroFragmentSpy);
		fragments.add(dataHubDataFragmentSpy);
		doThrow(new ImpexValidationException(Collections.singletonList(new ImportError()))).when(dataHubDataFragmentSpy).addLine(SEPARATOR_LINE, fragments);
		final List<ImpExFragment> validFragments = reader.readScriptFragments(scriptCtx());
		assertThat(validFragments).doesNotContain(dataHubDataFragmentSpy);
	}

	private DataHubDataFragment extractIntegrationLayerDataFragment(final ItemImportTaskData scriptCtx) throws ImpExException
	{
		final List<ImpExFragment> blocks = reader.readScriptFragments(scriptCtx);
		return (DataHubDataFragment) blocks.get(1);
	}

	private ItemImportTaskData scriptCtx()
	{
		final ItemImportTaskData ctx = new ItemImportTaskData();
		final String script = IMPEX_MACROS + "\n" + IMPEX_HEADER + CALLBACK_URL + CALLBACK_HEADERS + SEPARATOR_LINE;
		ctx.setImpexMetaData(script.getBytes());
		return ctx;
	}
}
