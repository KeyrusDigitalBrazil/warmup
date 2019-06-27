package de.hybris.platform.configurablebundlefacades.converters.populator;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.stream.Collectors;

import static de.hybris.platform.servicelayer.util.ServicesUtil.*;

/**
 * Populates {@link ProductData} with starter bundles assigned to this product.
 */
public class ProductBundleStarterPopulator <SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductPopulator<SOURCE, TARGET>
{
	private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;
	private BundleTemplateService bundleTemplateService;

	@Override
	public void populate(SOURCE source, TARGET target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("target", target);
		validateParameterNotNullStandardMessage("source", source);

		final List<BundleTemplateModel> bundleTemplates = getStarterBundleTemplatesByProduct(source);
		target.setBundleTemplates(getBundleTemplateConverter().convertAll(bundleTemplates));
	}

	protected List<BundleTemplateModel> getStarterBundleTemplatesByProduct(final ProductModel product) {
		final List<BundleTemplateModel> bundleTemplates = bundleTemplateService.getBundleTemplatesByProduct(product);
		return bundleTemplates.stream()
				.filter(bundleTemplate -> bundleTemplate.getBundleSelectionCriteria() != null
						&& bundleTemplate.getBundleSelectionCriteria().isStarter())
				.collect(Collectors.toList());
	}

	protected Converter<BundleTemplateModel, BundleTemplateData> getBundleTemplateConverter()
	{
		return bundleTemplateConverter;
	}

	@Required
	public void setBundleTemplateConverter(Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter)
	{
		this.bundleTemplateConverter = bundleTemplateConverter;
	}

	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	@Required
	public void setBundleTemplateService(BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}
}
