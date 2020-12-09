package com.imitatejd.maven.imitatejd_order_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/5 20:49
 * @Description:
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class PollingParam {

    public String orderNo;

    public HashMap<String, Object> notifyInterval;

    public Integer currentNotifyNumber;

    public Integer maxNotifyNumber;

    public Integer getDelayedTime() {
        if (notifyInterval.get(currentNotifyNumber + "") == null) return  -2;
        return new Integer(notifyInterval.get(currentNotifyNumber + "").toString());
    }

    public Integer getCurrentNotifyNumber() {
        if (currentNotifyNumber <= maxNotifyNumber) {
            return currentNotifyNumber;
        }
        return -1;
    }

    public boolean isExceedMaxNotifyNumber() {
        return this.getCurrentNotifyNumber() == -1 ? true : false;
    }
}
