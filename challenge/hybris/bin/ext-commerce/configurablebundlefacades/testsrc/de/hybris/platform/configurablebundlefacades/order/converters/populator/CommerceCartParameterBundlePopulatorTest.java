package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link CommerceCartParameterBundlePopulator}
 */
@UnitTest
public class CommerceCartParameterBundlePopulatorTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Mock
	private BundleTemplateService bundleTemplateService;
	@InjectMocks
	private final CommerceCartParameterBundlePopulator commerceCartParameterBundlePopulator = new CommerceCartParameterBundlePopulator();

	private AddToCartParams source;
	private CommerceCartParameter target;
	private BundleTemplateModel bundleTemplate;
	
	private static final String BUNDLE_TEMPLATE_ID = "BUNDLE_ID";
	private static final String WRONG_BUNDLE_TEMPLATE_ID = "WRONG_BUNDLE_ID";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		source = new AddToCartParams();
		target = new CommerceCartParameter();
		
		bundleTemplate = new BundleTemplateModel();
		bundleTemplate.setId(BUNDLE_TEMPLATE_ID);
		
		when(bundleTemplateService.getBundleTemplateForCode(eq(BUNDLE_TEMPLATE_ID))).thenReturn(bundleTemplate);
		
	}

	@Test
	public void shouldThrowExceptionWhenSourceIsNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter addToCartParams can not be null");

		commerceCartParameterBundlePopulator.populate(null, new CommerceCartParameter());
	}

	@Test
	public void shouldThrowExceptionWhenTargetIsNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter parameter can not be null");

		commerceCartParameterBundlePopulator.populate(new AddToCartParams(), null);
	}

	@Test
	public void testPopulateNoBundleTemplate()
	{
		commerceCartParameterBundlePopulator.populate(source, target);
		
		assertThat(target.getBundleTemplate()).isNull();
	}

	@Test
	public void testPopulateBundleTemplate()
	{
		source.setBundleTemplateId(BUNDLE_TEMPLATE_ID);
		commerceCartParameterBundlePopulator.populate(source, target);

		assertThat(target.getBundleTemplate()).isNotNull();
		assertThat(target.getBundleTemplate()).isEqualTo(bundleTemplate);
	}

	@Test
	public void shouldThrowExceptionIfBundleTemplateDoesNotExist()
	{
		when(bundleTemplateService.getBundleTemplateForCode(eq(WRONG_BUNDLE_TEMPLATE_ID))).thenThrow(
				new ModelNotFoundException("Bundle template " + WRONG_BUNDLE_TEMPLATE_ID + " was not found."));
		source.setBundleTemplateId(WRONG_BUNDLE_TEMPLATE_ID);

		commerceCartParameterBundlePopulator.populate(source, target);

		assertThat(target.getBundleTemplate()).isNull();
	}
}
