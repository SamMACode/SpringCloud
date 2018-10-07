package com.netflix.cloud.zuul.gateway.filter;

import com.netflix.cloud.zuul.gateway.constant.RedisConstant;
import com.netflix.cloud.zuul.gateway.utils.CookieUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVLET_DETECTION_FILTER_ORDER;

/**
 * 经过zuul的请求必须都携带token参数
 *
 * @author dong
 * @create 2018-10-06 上午10:49
 **/
@Component
public class AuthFilter extends ZuulFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return SERVLET_DETECTION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 在run方法中是实现真正的过滤请求的业务逻辑.
     * */
    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        /**
         * order/create 只能买家登录(cookie里有openid).
         * order/finish 只能卖家登录(cookie里有token,并且对应的redis中有值).
         * order/list 都可以访问.
         * */
        if("/order/order/create".equals(request.getRequestURI())) {
            Cookie cookie = CookieUtil.getCookie(request, "openid");
            if(cookie == null || StringUtils.isEmpty(cookie.getValue())) {
                requestContext.setSendZuulResponse(false);
                requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        // "order/finish"只能卖家进行访问该url地址.
        if("/order/order/finish".equals(request.getRequestURI())) {
            Cookie uuidToken = CookieUtil.getCookie(request, "token");
            if(uuidToken == null || uuidToken.getValue() == null
                    || StringUtils.isEmpty(stringRedisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_TEMPLATE, uuidToken)))) {
                requestContext.setSendZuulResponse(false);
                requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }
        return null;
    }
}