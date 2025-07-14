/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

/**
 * <p>Utility class for extracting path variables from the current HTTP request.</p>
 *
 * <p>This class provides a helper method to retrieve path variables dynamically
 * from the request context in a Spring MVC application.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * PathVariableHelper helper = new PathVariableHelper();
 * String value = helper.getPathVariable("id");
 * }
 * </pre>
 *
 * @author achraf (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 18 mars 2025</p>
 */
public class PathVariableHelper {

    /**
     * Helper method to extract path variables from the current HTTP request.
     */
    public static String getPathVariable(String name) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE) != null
                    ? ((Map<String, String>)
                    request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                    .get(name)
                    : null;
        }
        return null;
    }
}
