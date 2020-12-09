package execption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import response.Message;
import response.type.ResponseCodeType;

/**
 * @Auther: ASUS
 * @Date: 2020/10/10 00:38
 * @Description: 处理全部为捕获的异常
 */
@RestControllerAdvice
public class GobalExecptionHandler {

    private Logger logger = LoggerFactory.getLogger(GobalExecptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Message UnKnowExceptionHandler(Exception e) {
        logger.error("未知的异常", e);
        return new Message(ResponseCodeType.UN_KNOW_ERROR, "未知错误", false);
    }
}
