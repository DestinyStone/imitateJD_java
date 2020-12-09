package com.imitatejd.maven.imitatejd_item_controller.handler.execption;

import com.imitatejd.maven.imitatejd_item_controller.controller.ItemController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import response.Message;
import response.type.ResponseCodeType;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/12 22:46
 * @Description:
 */
@RestControllerAdvice(basePackageClasses = ItemController.class)
public class ItemControllerExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(ItemControllerExceptionHandler.class);

    @ExceptionHandler
    public Message nullPointerExceptionHandler(NullPointerException e) {
        logger.error("参数错误" + e, e.getMessage());
        return new Message(ResponseCodeType.PARAM_ERROR, null, false);
    }
}
