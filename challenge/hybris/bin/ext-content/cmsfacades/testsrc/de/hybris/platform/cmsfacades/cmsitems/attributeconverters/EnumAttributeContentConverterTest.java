package de.hybris.platform.cmsfacades.cmsitems.attributeconverters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EnumAttributeContentConverterTest
{
	private static final String DYNAMIC_ENUM_CODE = "some code";

	private enum TestEnum { FIRST, TWO };

	@Mock
	private HybrisEnumValue dynamicEnum;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;

	@Mock
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;

	@Mock
	private EnumerationService enumerationService;

	@InjectMocks
	private EnumAttributeContentConverter enumAttributeContentConverter;

	@Before
	public void setUp()
	{
		when(dynamicEnum.getCode()).thenReturn(DYNAMIC_ENUM_CODE);
		when(enumerationService.getEnumerationValue(HybrisEnumValue.class, DYNAMIC_ENUM_CODE)).thenReturn(dynamicEnum);
	}

	@Test
	public void givenNull_WhenConvertModelToDataIsCalled_ThenItReturnsNull()
	{
		// WHEN
		final Object result = enumAttributeContentConverter.convertModelToData(attributeDescriptor, null);

		// THEN
		assertNull(result);
	}

	@Test
	public void givenNonEnum_WhenConvertModelToDataIsCalled_ThenItReturnsNull()
	{
		// GIVEN
		Mockito.<Class<?>>when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor)).thenReturn(String.class);

		// WHEN
		final Object result = enumAttributeContentConverter.convertModelToData(attributeDescriptor, "some non enum");

		// THEN
		assertNull(result);
	}

	@Test
	public void givenRegularEnum_WhenConvertModelToDataIsCalled_ThenItReturnsTheEnumName()
	{
		// GIVEN
		Mockito.<Class<?>>when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor)).thenReturn(TestEnum.class);

		// WHEN
		final Object result = enumAttributeContentConverter.convertModelToData(attributeDescriptor, TestEnum.FIRST);

		// THEN
		assertThat(result, is(TestEnum.FIRST.name()));
	}

	@Test
	public void givenDynamicEnum_WhenConvertModelToDataIsCalled_ThenItReturnsTheEnumCode()
	{
		// GIVEN
		Mockito.<Class<?>>when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor)).thenReturn(HybrisEnumValue.class);

		// WHEN
		final Object result = enumAttributeContentConverter.convertModelToData(attributeDescriptor, dynamicEnum);

		// THEN
		assertThat(result, is(DYNAMIC_ENUM_CODE));
	}

	@Test
	public void givenNull_WhenConvertDataToModelIsCalled_ThenItReturnsNull()
	{
		// WHEN
		Object result = enumAttributeContentConverter.convertDataToModel(attributeDescriptor, null);

		// THEN
		assertNull(result);
	}

	@Test(expected = ConversionException.class)
	public void givenNonEnum_WhenConvertDataToModelIsCalled_ThenItThrowsConversionException()
	{
		// GIVEN
		Mockito.<Class<?>>when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor)).thenReturn(String.class);

		// WHEN
		enumAttributeContentConverter.convertDataToModel(attributeDescriptor, "some string");
	}

	@Test
	public void given_WhenConvertDataToModelIsCalled_ThenItReturnsTheEnumerationValue()
	{
		// GIVEN
		Mockito.<Class<?>>when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor)).thenReturn(TestEnum.class);

		// WHEN
		final Object result = enumAttributeContentConverter.convertDataToModel(attributeDescriptor, TestEnum.FIRST.name());

		// THEN
		assertThat(result, is(TestEnum.FIRST));
	}

	@Test
	public void givenDynamicEnum_WhenConvertDataToModelIsCalled_ThenItReturnsTheEnumerationValue()
	{
		// GIVEN
		Mockito.<Class<?>>when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor)).thenReturn(HybrisEnumValue.class);

		// WHEN
		final Object result = enumAttributeContentConverter.convertDataToModel(attributeDescriptor, DYNAMIC_ENUM_CODE);

		// THEN
		assertThat(result, is(dynamicEnum));
	}
}
