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
package de.hybris.platform.previewwebservices.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.previewwebservices.dto.CatalogVersionWsDTO;
import de.hybris.platform.webservicescommons.util.LocalViewExecutor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


@UnitTest
public class CatalogVersionWsDTOValidatorTest
{
	private final static String CATALOG_VERSION_WS_DTO = "catalogVersionWsDTO";
	private final static String CATALOG_ID = "Electronics";
	private final static String CATALOG_VERSION = "Staged";

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private LocalViewExecutor localViewExecutor;

	@InjectMocks
	protected CatalogVersionWsDTOValidator validator = new CatalogVersionWsDTOValidator();

	protected CatalogVersionWsDTO catalogVersionWsDTO = createCatalogVersionWsDTO(CATALOG_ID, CATALOG_VERSION);
	protected Errors errors = new BeanPropertyBindingResult(catalogVersionWsDTO, CATALOG_VERSION_WS_DTO);

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldPassValidation()
	{
		//when
		validator.validate(catalogVersionWsDTO, errors);

		//then
		assertFalse(errors.hasErrors());
	}

	@Test
	public void shouldFailValidationWhenNullCatalogId()
	{
		//given
		catalogVersionWsDTO.setCatalog(null);

		//when
		validator.validate(catalogVersionWsDTO, errors);

		//then
		assertTrue(errors.hasErrors());
		assertEquals(errors.getFieldError().getField(), "catalog");
		assertEquals(errors.getFieldError().getCode(), "field.required");
	}

	@Test
	public void shouldFailValidationWhenNullCatalogVersion()
	{
		//given
		catalogVersionWsDTO.setCatalogVersion(null);

		//when
		validator.validate(catalogVersionWsDTO, errors);

		//then
		assertTrue(errors.hasErrors());
		assertEquals(errors.getFieldError().getField(), "catalogVersion");
		assertEquals(errors.getFieldError().getCode(), "field.required");
	}

	private CatalogVersionWsDTO createCatalogVersionWsDTO(final String catalogId, final String catalogVersion)
	{
		final CatalogVersionWsDTO catalogVersionWsDTO = new CatalogVersionWsDTO();
		catalogVersionWsDTO.setCatalog(catalogId);
		catalogVersionWsDTO.setCatalogVersion(catalogVersion);
		return catalogVersionWsDTO;
	}
}
