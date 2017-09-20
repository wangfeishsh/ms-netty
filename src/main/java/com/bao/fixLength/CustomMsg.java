package com.bao.fixLength;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by nannan on 2017/9/18.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomMsg {
    //交易码 4位定长
    private String code;
    //报文体长度 8位定长，不足左补零
    private String length;
    //报文体
    private String body;
}
