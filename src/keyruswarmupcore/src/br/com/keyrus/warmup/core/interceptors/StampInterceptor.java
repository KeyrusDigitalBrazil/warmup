package br.com.keyrus.warmup.core.interceptors;

import br.com.keyrus.warmup.core.model.StampModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.apache.log4j.Logger;

public class StampInterceptor implements ValidateInterceptor<StampModel> {

    private static final Logger LOGGER = Logger.getLogger(StampInterceptor.class);

    @Override
    public void onValidate(StampModel stamp, InterceptorContext interceptorContext) throws InterceptorException {
        LOGGER.info("checking stamp object " + stamp.getCode() + " " + stamp.getPriority() + " " + stamp.getMedia());
        StringBuilder sb = new StringBuilder();
        sb.append(checkPriority(stamp.getPriority()))
                .append(checkCode(stamp.getCode()))
                .append(checkMedia(stamp.getMedia()));
        if(sb.length()>0){
            throw new InterceptorException(sb.toString());
        }
    }

    private String checkPriority(Integer priority){
        if (priority == null) {
            return "Stamp priority should not be empty. ";
        } else if (priority >= 1000) {
            return "Stamp priority should be lower than 1000. ";
        } else if (priority <= 0) {
            return "Stamp priority should be greater than 0. ";
        }
        return "";
    }

    private String checkCode(String code){
        if (code == null || code.trim().length() == 0) {
            return "Stamp code should not be empty. ";
        } else if (code.matches(".*\\d.*")) {
            return "Stamp code should not contain numbers. ";
        }
        return "";
    }

    private String checkMedia(MediaModel media){
        if(media == null){
            return "Stamp media should not be empty. ";
        }
        return "";
    }

}
