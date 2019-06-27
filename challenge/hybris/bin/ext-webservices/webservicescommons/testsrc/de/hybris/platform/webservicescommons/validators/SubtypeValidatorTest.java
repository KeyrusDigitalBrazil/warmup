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
package de.hybris.platform.webservicescommons.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.mapping.SubclassRegistry;
import de.hybris.platform.webservicescommons.mapping.impl.DefaultSubclassRegistry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@UnitTest
public class SubtypeValidatorTest
{
	private static String BASE_ERROR = "empty base";
	private static String SUB_ERROR = "empty subType";
	private static String OTHER_ERROR = "empty other";

	Validator validator;

	@Before
	public void setup()
	{
		final SubclassRegistry subclassRegistry = new DefaultSubclassRegistry();
		subclassRegistry.registerSubclass(Base.class, SubType.class);

		final SubtypeValidator validator = new SubtypeValidator();

		final Collection<Validator> validators = new HashSet<Validator>();
		validators.add(new BaseValidator());
		validators.add(new SubValidator());
		validators.add(new OtherValidator());


		validator.setValidators(validators);
		validator.setMarkerInterface(Marker.class);
		validator.init();

		this.validator = validator;
	}

	private Errors createErrors(final Object object, final String name)
	{
		return new BeanPropertyBindingResult(object, name);
	}

	@Test
	public void supportsBase()
	{
		//then
		assertTrue(validator.supports(Base.class));
	}

	@Test
	public void supportsSubType()
	{
		//then
		assertTrue(validator.supports(SubType.class));
	}

	@Test
	public void supportsOhter()
	{
		//then
		assertFalse(validator.supports(OtherType.class));
	}

	@Test
	public void validateBaseOk()
	{
		//given
		final Base base = new Base("aaa");
		final Errors errors = createErrors(base, "base");

		//when
		validator.validate(base, errors);

		//then
		assertFalse(errors.hasErrors());
	}

	@Test
	public void validateBaseErrors()
	{
		//given
		final Base base = new Base(null);
		final Errors errors = createErrors(base, "base");

		//when
		validator.validate(base, errors);

		//then
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getErrorCount());
		final ObjectError objectError = errors.getAllErrors().get(0);
		assertEquals(BASE_ERROR, objectError.getCode());
	}

	@Test
	public void validateSubeOk()
	{
		//given
		final Base base = new SubType("aaa", "bbb");
		final Errors errors = createErrors(base, "base");

		//when
		validator.validate(base, errors);

		//then
		assertFalse(errors.hasErrors());
	}

	@Test
	public void validateSubBaseError()
	{
		//given
		final Base base = new SubType(null, "bbb");
		final Errors errors = createErrors(base, "base");

		//when
		validator.validate(base, errors);

		//then
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getErrorCount());
		final ObjectError objectError = errors.getAllErrors().get(0);
		assertEquals(BASE_ERROR, objectError.getCode());
	}

	@Test
	public void validateSubError()
	{
		//given
		final Base base = new SubType("aaa", null);
		final Errors errors = createErrors(base, "base");

		//when
		validator.validate(base, errors);

		//then
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getErrorCount());
		final ObjectError objectError = errors.getAllErrors().get(0);
		assertEquals(SUB_ERROR, objectError.getCode());
	}

	@Test
	public void validateSubAllError()
	{
		//given
		final Base base = new SubType(null, null);
		final Errors errors = createErrors(base, "base");

		//when
		validator.validate(base, errors);

		//then
		assertTrue(errors.hasErrors());
		assertEquals(2, errors.getErrorCount());

		final Set<String> errorCodes = errors.getAllErrors().stream().map(e -> e.getCode()).collect(Collectors.toSet());

		assertTrue(errorCodes.contains(SUB_ERROR));
		assertTrue(errorCodes.contains(BASE_ERROR));
	}

	public static class Base
	{
		String base;

		Base(final String base)
		{
			this.base = base;
		}

		public String getBase()
		{
			return base;
		}
	}

	public static class SubType extends Base
	{
		String subType;

		SubType(final String base, final String subType)
		{
			super(base);
			this.subType = subType;
		}

		public String getSubType()
		{
			return subType;
		}
	}

	public static class OtherType
	{
		String otherType;

		OtherType(final String otherType)
		{
			this.otherType = otherType;
		}

		public String getOtherType()
		{
			return otherType;
		}
	}

	private interface Marker
	{
		//marker interface
	}

	private static class BaseValidator implements Validator, Marker
	{
		@Override
		public boolean supports(final Class<?> clazz)
		{
			return Base.class.isAssignableFrom(clazz);
		}

		@Override
		public void validate(final Object target, final Errors errors)
		{
			ValidationUtils.rejectIfEmpty(errors, "base", BASE_ERROR);
		}
	}

	private static class SubValidator implements Validator, Marker
	{
		@Override
		public boolean supports(final Class<?> clazz)
		{
			return SubType.class.isAssignableFrom(clazz);
		}

		@Override
		public void validate(final Object target, final Errors errors)
		{
			ValidationUtils.rejectIfEmpty(errors, "subType", SUB_ERROR);
		}
	}

	private static class OtherValidator implements Validator
	{
		@Override
		public boolean supports(final Class<?> clazz)
		{
			return OtherType.class.isAssignableFrom(clazz);
		}

		@Override
		public void validate(final Object target, final Errors errors)
		{
			ValidationUtils.rejectIfEmpty(errors, "otherType", OTHER_ERROR);
		}
	}

}
