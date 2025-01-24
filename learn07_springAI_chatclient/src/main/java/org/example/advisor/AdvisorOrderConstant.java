package org.example.advisor;

import org.springframework.core.Ordered;

/**
 * ClassName:AdvisorOrderConstant
 * Package:org.example.advisor
 * Description:
 *
 * @Date:2025/1/21 15:49
 * @Author:qs@1.com
 */
public class AdvisorOrderConstant {
    public static final int SIMPLE_LOGGER_ADVISOR_ORDER = Ordered.HIGHEST_PRECEDENCE;

    // 优先级比 SIMPLE_LOGGER_ADVISOR_ORDER 低，值越大优先级越低
    public static final int RE_READING_ADVISOR_ORDER = Ordered.HIGHEST_PRECEDENCE + 1;

}
