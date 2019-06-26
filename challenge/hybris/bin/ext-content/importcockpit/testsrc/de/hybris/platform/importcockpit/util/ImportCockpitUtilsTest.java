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
package de.hybris.platform.importcockpit.util;

import static de.hybris.platform.importcockpit.util.ImportCockpitUtils.readSecurelyFromXML;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.importcockpit.enums.ImpexImportMode;
import de.hybris.platform.importcockpit.services.mapping.impl.AbstractImportCockpitMappingService;
import de.hybris.platform.importcockpit.services.mapping.jaxb.MappingPersistence;
import de.hybris.platform.testframework.HybrisJUnit4ClassRunner;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(HybrisJUnit4ClassRunner.class)
public class ImportCockpitUtilsTest
{

	public static final String QA_EMPTY_MAPPING_XML = "/qa/mappings/QAEmptyMapping.xml";
	public static final String QA_XXE_EMPTY_MAPPING_XML = "/qa/security/XXE_EmptyMapping_Entity.xml";

	@Test
	public void readFromNonVulnerableFile() throws JAXBException, XMLStreamException
	{
		final MappingPersistence fromXML = readFromFile(QA_EMPTY_MAPPING_XML);
		assertThat(fromXML).isNotNull();
		assertThat(fromXML.getMode()).isEqualTo(ImpexImportMode.INSERT_UPDATE);
	}

	@Test(expected = UnmarshalException.class)
	public void readFromXXEVulnerableFile() throws JAXBException, XMLStreamException
	{
		readFromFile(QA_XXE_EMPTY_MAPPING_XML);
	}

	protected <T> T readFromFile(final String url) throws JAXBException, XMLStreamException
	{
		final InputStream stream = ImportCockpitUtilsTest.class.getResourceAsStream(url);
		final InputStreamReader reader = new InputStreamReader(stream);
		return (T) readSecurelyFromXML(reader, AbstractImportCockpitMappingService.JAXB_MAPPING_ROOT_TYPES);
	}

}
