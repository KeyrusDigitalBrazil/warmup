package br.com.keyrus.warmup.core.util;

import br.com.keyrus.warmup.core.exception.InvalidMediaParamsException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static br.com.keyrus.warmup.core.constants.KeyruswarmupCoreConstants.DEFAULT_STAMP_SEPARATOR;
import static org.apache.commons.io.FilenameUtils.getExtension;

public class StampUtils {

    public static final Logger LOG = Logger.getLogger(StampUtils.class);

    public static String getStampName(final File stamp) throws InvalidMediaParamsException {
        return getStampParams(stamp).get(0);
    }

    public static Integer getStampPriority(final File stamp) throws InvalidMediaParamsException, NumberFormatException {

        final List<String> params = getStampParams(stamp);

        final String priority = params.get(1)
                .replace(".", "")
                .replace(getExtension(stamp.getName()), "");

        try {
            return Integer.valueOf(priority);
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Could not convert the value of priority inside the stamp file with name '" + stamp.getName() + "'!");
        }
    }

    private static List<String> getStampParams(final File stamp) throws InvalidMediaParamsException {

        validateStamp(stamp);

        final List<String> params = Arrays.asList(stamp.getName().split(DEFAULT_STAMP_SEPARATOR));

        if (CollectionUtils.isEmpty(params) || params.size() != 2) {
            throw new InvalidMediaParamsException("Media file with name '" + stamp.getName() + "' have an incorrect pattern.");
        }

        return params;
    }

    public static void validateStamp(final File stamp) {
        Assert.assertNotNull("File can't be empty.", stamp);
        Assert.assertNotNull("File name can't be empty.", stamp);
        Assert.assertNotEquals("File name can't be empty.", "", stamp);
    }

}
