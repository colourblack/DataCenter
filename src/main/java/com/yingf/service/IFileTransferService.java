package com.yingf.service;

import com.yingf.project.datacenter.query.MultipartFileQuery;
import com.yingf.project.datacenter.vo.CheckFileUpLoadVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author yingf Fangjunjin
 * @Description 文件传输服务
 * @Date 2021/3/9
 */
public interface IFileTransferService {

    /**
     * 根据所给的Md5值判断该文件是否已经存在于系统中
     * @param md5 文件的Md5值
     * @return 包含文件上传状态和文件id信息的传输对象
     */
    CheckFileUpLoadVO findByFileMd5(String md5);


    /**
     * 文件上传的具体方法, 通过判断文件的状态来控制上传的进度
     * @param form           文件上传的参数信息
     * @param multipartFile  Springboot 封装的http文件上传参数
     * @return 文件上传结果, 用CheckFileUpLoadVO对象传输
     * @throws IOException 传输过程中发生IO错误
     */
    CheckFileUpLoadVO doUpload(MultipartFileQuery form, MultipartFile multipartFile) throws IOException;

}
