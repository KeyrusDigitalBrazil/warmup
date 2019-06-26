package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.cmsfacades.types.populator.CmsStructureEnumTypeComponentTypeAttributePopulator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.enumeration.EnumerationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CmsStructureEnumTypeComponentTypeAttributePopulatorTest
{
	// --------------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------------
	private static final String LABEL_ATTRIBUTE_ID = "label";
	private static final String VALUE_ATTRIBUTE_ID = "value";
	private static final String DOT = ".";
	private static final String PREFIX = "someprefix";
	private static final String SUFFIX = "somesuffix";

	public enum TestEnum implements HybrisEnumValue
	{
		VAL_1("VAL_1"),
		VAL_2("VAL_2"),
		VAL_3("VAL_3");

		TestEnum(final String code)
		{
			this.code = code.intern();
		}

		private final String code;
		private final static String TYPE_NAME = "Test Enum";

		@Override
		public String getCode()
		{
			return this.code;
		}

		@Override
		public String getType()
		{
			return TYPE_NAME;
		}
	}

	public static class DynamicEnum implements HybrisEnumValue
	{
		@Override
		public String getCode()
		{
			return null;
		}

		@Override
		public String getType()
		{
			return null;
		}
	}

	// --------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------
	@Mock
	private AttributeDescriptorModel attributeDescriptorModel;

	@Mock
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;

	@Mock
	private EnumerationService enumerationService;

	private ComponentTypeAttributeData componentTypeAttributeData = new ComponentTypeAttributeData();

	@InjectMocks
	private CmsStructureEnumTypeComponentTypeAttributePopulator populatorUnderTest;

	// --------------------------------------------------------------------------------
	// Set Up
	// --------------------------------------------------------------------------------
	@Before
	public void SetUp()
	{
		populatorUnderTest.setPrefix(PREFIX);
		populatorUnderTest.setSuffix(SUFFIX);
	}

	// --------------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------------
	@Test
	public void givenNonDynamicEnum_WhenPopulateIsCalled_ThenDropdownStructureIsConfigured()
	{
		// GIVEN
		doReturn(TestEnum.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptorModel);

		// WHEN
		populatorUnderTest.populate(attributeDescriptorModel, componentTypeAttributeData);

		// THEN
		assertFalse(componentTypeAttributeData.isPaged());
		assertThat(componentTypeAttributeData.getIdAttribute(), is(VALUE_ATTRIBUTE_ID));
		assertThat(componentTypeAttributeData.getLabelAttributes(), contains(LABEL_ATTRIBUTE_ID));

		assertOptionHasRightData(componentTypeAttributeData.getOptions().get(0), TestEnum.VAL_1.name(), buildLabel(TestEnum.VAL_1, TestEnum.class));
		assertOptionHasRightData(componentTypeAttributeData.getOptions().get(1), TestEnum.VAL_2.name(), buildLabel(TestEnum.VAL_2, TestEnum.class));
		assertOptionHasRightData(componentTypeAttributeData.getOptions().get(2), TestEnum.VAL_3.name(), buildLabel(TestEnum.VAL_3, TestEnum.class));
	}

	@Test
	public void givenDynamicEnum_WhenPopulateIsCalled_ThenDropdownStructureIsConfigured()
	{
		// GIVEN
		doReturn(DynamicEnum.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptorModel);
		doReturn(Arrays.asList(TestEnum.VAL_1, TestEnum.VAL_2, TestEnum.VAL_3)).when(enumerationService).getEnumerationValues(DynamicEnum.class);

		// WHEN
		populatorUnderTest.populate(attributeDescriptorModel, componentTypeAttributeData);

		// THEN
		assertOptionHasRightData(componentTypeAttributeData.getOptions().get(0), TestEnum.VAL_1.name(), buildLabel(TestEnum.VAL_1, DynamicEnum.class));
		assertOptionHasRightData(componentTypeAttributeData.getOptions().get(1), TestEnum.VAL_2.name(), buildLabel(TestEnum.VAL_2, DynamicEnum.class));
		assertOptionHasRightData(componentTypeAttributeData.getOptions().get(2), TestEnum.VAL_3.name(), buildLabel(TestEnum.VAL_3, DynamicEnum.class));
	}

	protected void assertOptionHasRightData(final OptionData optionData, final String expectedKey, final String expectedLabel)
	{
		assertEquals(optionData.getId(), expectedKey);
		assertEquals(optionData.getLabel(), expectedLabel);
	}

	protected String buildLabel(final TestEnum enumValue, final Class enumClass)
	{
		return PREFIX + DOT +
				enumClass.getSimpleName().toLowerCase() + DOT +
				enumValue.name().toLowerCase() + DOT + SUFFIX;
	}
}
