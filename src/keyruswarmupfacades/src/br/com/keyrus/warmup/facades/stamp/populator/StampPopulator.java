package br.com.keyrus.warmup.facades.stamp.populator;

import br.com.keyrus.warmup.core.model.StampModel;
import br.com.keyrus.warmup.facades.stamp.data.StampData;
import de.hybris.platform.commercefacades.product.converters.populator.ImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class StampPopulator implements Populator<StampModel, StampData> {

    private ImagePopulator imagePopulator;

    @Override
    public void populate(StampModel source, StampData target) throws ConversionException {

        final ImageData image = new ImageData();

        imagePopulator.populate(source.getMedia(), image);

        target.setCode(source.getCode());
        target.setImage(image);
    }

    public void setImagePopulator(ImagePopulator imagePopulator) {
        this.imagePopulator = imagePopulator;
    }
}
