package execption;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import response.Message;
import response.type.ResponseCodeType;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/10 00:29
 * @Description: 处理 PassportController 抛出的关于用户认证,授权的异常
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PassportExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(PassportExceptionHandler.class);

    /**
     * @Description: 处理未知用户名异常
     */
    @ExceptionHandler(UnknownAccountException.class)
    public Message unknownAccountExceptionHandler(UnknownAccountException e) {
        logger.error("使用不存在的用户名", e);
        return new Message(ResponseCodeType.PARAM_ERROR, "用户名错误", false);
    }

    /**
     * @Description: 处理密码错误
     */
    @ExceptionHandler(IncorrectCredentialsException.class)
    public Message incorrectCredentialsExceptionHandler(IncorrectCredentialsException e) {
        logger.error("使用错误的密码", e);
        return new Message(ResponseCodeType.PARAM_ERROR, "密码错误", false);
    }

    @ExceptionHandler(AuthorizationException.class)
    public Message AuthorizationException(AuthorizationException e) {
        return new Message(ResponseCodeType.PERMISSION_DENIED, "没有足够的权限", false);
    }

    @ExceptionHandler(AuthenticationException.class)
    public Message AuthenticationException(AuthenticationException e) {
        logger.error("");
        return new Message(ResponseCodeType.PARAM_ERROR, "登录失败", false);
    }

    /**
     * @Description: 处理token过期
     */
    @ExceptionHandler(TokenExpiredException.class)
    public Message tokenExpiredExceptionHandler(TokenExpiredException e) {
        logger.error("token 过期", e);
        return new Message(ResponseCodeType.TOKEN_EXPIRED, "token过期", false);
    }
}
