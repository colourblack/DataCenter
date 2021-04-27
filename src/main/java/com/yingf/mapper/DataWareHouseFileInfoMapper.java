package com.yingf.mapper;

import com.yingf.domain.entity.DataWareHouseFileInfo;
import org.springframework.stereotype.Repository;

/**
 * @author yingf
 */
@Repository
public interface DataWareHouseFileInfoMapper {

    /**
     * 插入单个数据, 允许实体类某个field不存在
     * @param record DataWareHouseFileInfo实体对象
     * @return 插入成功的数目
     */
    int insert(DataWareHouseFileInfo record);

    /**
     * 插入单个数据, 允许实体类某个field不存在
     * @param record DataWareHouseFileInfo实体对象
     * @return 插入成功的数目
     */
    int insertSelective(DataWareHouseFileInfo record);

    /**
     * 通过数据模块的名称删除record
     * @param dataModelName 数据模块的名称
     * @return 成功删除的数目
     */
    int deleteRecordByDataModelName(String dataModelName);
}