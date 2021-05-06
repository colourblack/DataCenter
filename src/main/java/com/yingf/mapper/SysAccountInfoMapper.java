package com.yingf.mapper;

import com.yingf.domain.entity.SysAccountInfo;
import org.springframework.stereotype.Repository;

/**
 * @author yingf Fangjunjin
 */
@Repository
public interface SysAccountInfoMapper {
    /**
     * 插入用户信息
     * @param record 用户信息
     * @return 插入的用户id
     */
    int insert(SysAccountInfo record);

    /**
     * 插入用户信息
     * @param record 用户信息
     * @return 插入的用户id
     */
    int insertSelective(SysAccountInfo record);

    /**
     * 查询用户信息
     * @param accountName 用户名
     * @return 用户信息 or null
     */
    SysAccountInfo selectAccount(String accountName);
}