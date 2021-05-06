package com.yingf.service.impl;

import com.yingf.constant.DataCenterConstant;
import com.yingf.constant.enums.FileStatus;
import com.yingf.constant.enums.UploadFileActionType;
import com.yingf.domain.entity.FileUploadInfo;
import com.yingf.domain.query.MultipartFileQuery;
import com.yingf.domain.vo.CheckFileUpLoadVO;
import com.yingf.mapper.FileUploadInfoMapper;
import com.yingf.service.IFileTransferService;
import com.yingf.util.FileUtil;
import com.yingf.util.SnowflakeIdWorker;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author yingf Fangjunjin
 * @Description 文件传输 - Service
 * @Date 2021/3/9
 */
@Component
public class FileTransferServiceImpl implements IFileTransferService {

    private static final Logger log = LoggerFactory.getLogger(FileTransferServiceImpl.class);

    final FileUploadInfoMapper fileUploadInfoMapper;

    final SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    public FileTransferServiceImpl(FileUploadInfoMapper fileUploadInfoMapper, SnowflakeIdWorker snowflakeIdWorker) {
        this.fileUploadInfoMapper = fileUploadInfoMapper;
        this.snowflakeIdWorker = snowflakeIdWorker;
    }


    @Override
    public CheckFileUpLoadVO findByFileMd5(String md5) {
        /* 主要根据文件的md5值, 查找数据库已经文件系统, 判断该文件的上传状态 */
        FileUploadInfo uploadFile = fileUploadInfoMapper.findByFileMd5(md5);
        CheckFileUpLoadVO checkFileUpLoadVO = new CheckFileUpLoadVO();
        if (uploadFile == null) {
            //没有上传过文件
            checkFileUpLoadVO.setStatus(FileStatus.NOT_EXIST.name());
            checkFileUpLoadVO.setFileId(snowflakeIdWorker.nextId());
        } else {
            log.debug(uploadFile.toString());
            //上传过文件 判断文件现在还是否存在
            log.debug("检测文件上传的路径为: {}", uploadFile.getFilePath());
            File file = new File(uploadFile.getFilePath());
            if (!file.exists()) {
                //若不存在
                checkFileUpLoadVO.setStatus(FileStatus.NOT_EXIST.name());
                checkFileUpLoadVO.setFileId(uploadFile.getFileId());
            } else {
                //若文件存在 判断此时是部分上传了 还是已全部上传
                String fileStatus = uploadFile.getFileStatus();
                if (FileStatus.INCOMPLETE.name().equals(fileStatus)) {
                    //文件只上传了一部分
                    checkFileUpLoadVO.setStatus(FileStatus.INCOMPLETE.name());
                    checkFileUpLoadVO.setFileId(uploadFile.getFileId());
                } else if (FileStatus.EXIST.name().equals(fileStatus)) {
                    //文件早已上传完整
                    checkFileUpLoadVO.setStatus(FileStatus.EXIST.name());
                    checkFileUpLoadVO.setFileId(uploadFile.getFileId());
                } else {
                    log.error("文件({})上传发生错误!", uploadFile.getFileName());
                }
            }
        }
        log.debug(checkFileUpLoadVO.toString());
        return checkFileUpLoadVO;
    }


    @Override
    public CheckFileUpLoadVO doUpload(MultipartFileQuery form, MultipartFile multipartFile) throws IOException {
        CheckFileUpLoadVO checkFileUpLoadVO = new CheckFileUpLoadVO();

        // 获取form中的信息
        Long fileId = form.getUuid();
        Integer index = form.getIndex();
        Integer total = form.getTotal();
        String md5 = form.getMd5();
        String partMd5 = form.getPartMd5();
        String size = form.getSize();
        String fileName = form.getName();
        String suffix = FileUtil.getExtensionName(fileName).toLowerCase();

        log.debug("上传文件, form表单的详细信息: {}", form.toString());

        // 文件存储路径 - 文件夹路径
        String saveDirectory = DataCenterConstant.UPLOAD_EXCEL_FILE_PATH + File.separator + fileId;
        // 文件存储路径 - 文件路径
        String filePath = saveDirectory + File.separator + fileId + "." + suffix;
        log.debug("saveDirectory: {}", saveDirectory);
        log.debug("filePath: {}", filePath);

        //验证路径是否存在，不存在则创建目录
        File path = new File(saveDirectory);
        if (!path.exists()) {
            path.mkdirs();
        }

        //文件分片位置
        File file = new File(saveDirectory, fileId + "_" + index);
        log.debug(file.getAbsolutePath());

        //根据action不同执行不同操作. check:校验分片是否上传过; upload:直接上传分片
        if (UploadFileActionType.CHECK.name().equals(form.getAction())) {

            if (!file.exists()) {
                checkFileUpLoadVO.setStatus(FileStatus.NOT_EXIST.name());
                checkFileUpLoadVO.setFileId(fileId);
            }

            String md5Str = FileUtil.getFileMd5(file);
            if (md5Str != null && md5Str.equals(partMd5)) {
                // 分片已上传过
                if (!index.equals(total)) {
                    // 若该分片不是最后一个分片, 则返回前端文件状态, 告知前端check下一个分片
                    checkFileUpLoadVO.setStatus(FileStatus.INCOMPLETE.name());
                    checkFileUpLoadVO.setFileId(fileId);
                    return checkFileUpLoadVO;
                }
            }
        } else if (UploadFileActionType.UPLOAD.name().equals(form.getAction())) {
            //分片上传过程中出错,有残余时需删除分块后,重新上传
            if (file.exists()) {
                file.delete();
            }

            multipartFile.transferTo(new File(saveDirectory, fileId + "_" + index));

            checkFileUpLoadVO.setStatus(FileStatus.INCOMPLETE.name());
            checkFileUpLoadVO.setFileId(fileId);

            if (!index.equals(total)) {
                return checkFileUpLoadVO;
            }
        }

        if (path.isDirectory()) {
            File[] fileArray = path.listFiles();
            if (fileArray != null) {
                if (fileArray.length == total) {
                    //分块全部上传完毕,合并
                    File newFile = new File(saveDirectory, fileId + "." + suffix);
                    FileOutputStream outputStream = new FileOutputStream(newFile, true);
                    //文件追加写入
                    for (int i = 0; i < fileArray.length; i++) {
                        File tmpFile = new File(saveDirectory, fileId + "_" + (i + 1));
                        FileUtils.copyFile(tmpFile, outputStream);
                        //应该放在循环结束删除 可以避免 因为服务器突然中断 导致文件合并失败 下次也无法再次合并
                        tmpFile.delete();
                    }
                    log.debug("成功生成文件 - {}", fileName);
                    outputStream.close();

                    // 将FileUploadInfo中文件信息的状态记录修改为 上传成功(EXIST)
                    if (fileUploadInfoMapper.updateFileUploadStatus(fileId, FileStatus.EXIST.name()) <= 0) {
                        FileUploadInfo uploadFile = new FileUploadInfo();
                        uploadFile.setFileId(fileId);
                        uploadFile.setFileStatus(FileStatus.EXIST.name());
                        uploadFile.setFileName(fileName);
                        uploadFile.setFileMd5(md5);
                        uploadFile.setFileSuffix(suffix);
                        uploadFile.setFilePath(filePath);
                        uploadFile.setFileSize(size);
                        fileUploadInfoMapper.insertSelective(uploadFile);
                    }
                    checkFileUpLoadVO.setFileId(fileId);
                    checkFileUpLoadVO.setStatus(FileStatus.EXIST.name());
                    return checkFileUpLoadVO;
                } else if (index == 1) {
                    // 文件第一个分片上传时将文件信息记录到数据库
                    FileUploadInfo uploadFile = new FileUploadInfo();
                    String name = FileUtil.getFileNameWithOutSuffix(fileName);

                    uploadFile.setFileName(name);
                    uploadFile.setFileSuffix(suffix);
                    uploadFile.setFileId(fileId);
                    uploadFile.setFilePath(filePath);
                    uploadFile.setFileSize(size);
                    uploadFile.setFileMd5(md5);
                    uploadFile.setFileStatus(FileStatus.INCOMPLETE.name());
                    fileUploadInfoMapper.insertSelective(uploadFile);
                    log.debug("文件{}第一次上传, 数据库持久化文件信息.", fileName);
                }
            }
        }
        return checkFileUpLoadVO;
    }


}
