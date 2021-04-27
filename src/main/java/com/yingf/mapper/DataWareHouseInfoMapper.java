package com.yingf.mapper;

import com.yingf.domain.entity.DataWareHouseInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yinf Fangjunjin
 */
@Repository
public interface DataWareHouseInfoMapper {
    /**
     * 插入数据仓库基本信息
     * @param record 记录
     * @return 成功插入则返回1, 失败则为0
     */
    int insert(DataWareHouseInfo record);

    /**
     * 可选择的插入数据仓库基本信息
     * @param record 存在null值的记录
     * @return 成功插入则返回1, 失败则为0
     */
    int insertSelective(DataWareHouseInfo record);

    /**
     * 通过数据仓库模块名称查找对应的基本信息
     * @param dataModelName 数据仓库模块名称
     * @return 指定的数据仓库模块基本信息
     */
    DataWareHouseInfo selectOneByDataModelName(@Param("dataModelName") String dataModelName);


    /**
     * 有选择的更新记录
     * @param record  记录
     * @return 更新结果
     */
    int updateSelective(DataWareHouseInfo record);


    /**
     * 通过数据模块的名称删除record
     * @param dataModelName 数据模块的名称
     * @return 成功删除的数目
     */
    int deleteRecordByDataModelName(String dataModelName);


    /**
     * 从数据库获取数据仓库首页信息
     * @param offset    limit offset
     * @param pageSize  limit size
     * @return List 分页结果
     */
    List<DataWareHouseInfo> selectDataInfo(@Param("offset") int offset, @Param("pageSize") int pageSize);


    /**
     * 获取数据仓库的总记录数
     * @return 数据仓库的记录数
     */
    int selectTotalCount();

    /**
     * 通过指定的dataModelName得到相应的数据表集合
     * @param dataModelName 当前数据模块名称
     * @return 数据表集合
     */
    String selectOriginalTableName(String dataModelName);


    /**
     * 查询所有模块名称
     * @return list
     */
    List<String> selectAllModelName();
}