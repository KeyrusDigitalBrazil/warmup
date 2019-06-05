package br.com.keyrus.warmup.core.stamp.interceptors;

import br.com.keyrus.warmup.core.model.StampModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;


public class StampValidateInterceptor implements ValidateInterceptor<StampModel>
{
	public static final List<String> MODIFIED_FIELDS = Arrays.asList(StampModel.CODE, StampModel.MEDIA, StampModel.PRIORITY);
	public static final String NO_DIGITS_REGEX = ".*[0-9]+.*";

	@Override
	public void onValidate(final StampModel stamp, final InterceptorContext ctx) throws InterceptorException
	{
		if (ctx.isNew(stamp) || isModified(stamp, ctx))
		{
			validateCode(stamp);
			validatePriority(stamp);
			validateMedia(stamp);
		}
	}

	private void validateCode(StampModel stamp) throws InterceptorException {
		if (StringUtils.isEmpty(stamp.getCode())) {
			throw new InterceptorException("Stamp code can't be empty! Not valid: '" + stamp.getCode() + "'.");
		} else if (stamp.getCode().matches(NO_DIGITS_REGEX)) {
			throw new InterceptorException("Stamp code can't contain digits! Not valid: '" + stamp.getCode() + "'.");
		}
	}

	private void validatePriority(StampModel stamp) throws InterceptorException {
		if (stamp.getPriority() == null) {
			throw new InterceptorException("Stamp priority can't be 'null'! Not valid: '" + stamp.getPriority() + "'.");
		} else if (stamp.getPriority() < 1) {
			throw new InterceptorException("Stamp priority can't be lower than 1! Not valid: '" + stamp.getPriority() + "'.");
		} else if (stamp.getPriority() > 999) {
			throw new InterceptorException("Stamp priority can't be greater than 999! Not valid: '" + stamp.getPriority() + "'.");
		}
	}

	private void validateMedia(StampModel stamp) throws InterceptorException {
		if (stamp.getMedia() == null) {
			throw new InterceptorException("Stamp media can't be 'null'! Not valid: '" + stamp.getMedia() + "'.");
		}
	}

	private boolean isModified(final StampModel stamp, final InterceptorContext ctx) {
		return MODIFIED_FIELDS.stream().anyMatch(field -> ctx.isModified(stamp, field));
	}
}
