package com.yingf.mapper;

import com.yingf.domain.entity.FileUploadInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author yingf
 */
@Repository
public interface FileUploadInfoMapper {

    /**
     * 插入单个数据
     * @param record FileUploadInfo实体类对象
     * @return 插入成功的数目
     */
    int insert(FileUploadInfo record);

    /**
     * 插入单个数据, 允许实体类某个field不存在
     * @param record FileUploadInfo实体类对象
     * @return 插入成功的数目
     */
    int insertSelective(FileUploadInfo record);


    /**
     * 根据文件md5值获取文件信息
     * @param  md5 文件md5值
     * @return null or 文件信息
     */
    FileUploadInfo findByFileMd5(String md5);


    /**
     * 通过文件id 更新该文件的上传状态
     * @param fileId 文件ID
     * @param status 文件状态
     * @return 更新结果
     */
    int updateFileUploadStatus(@Param("fileId") long fileId, @Param("status") String status);
}