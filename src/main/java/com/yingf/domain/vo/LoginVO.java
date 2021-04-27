package com.yingf.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 4/25/21 4:23 PM
 */
@ToString
@Data
@NoArgsConstructor
public class LoginVO implements Serializable {

    private final static long serialVersionUID = 1L;

    private String username;

    private String password;

    private Integer userId;




}
