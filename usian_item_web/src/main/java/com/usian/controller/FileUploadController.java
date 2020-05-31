package com.usian.controller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.usian.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    //注入上传图片工具类
    @Autowired
    private FastFileStorageClient storageClient;

    //定义图片格式类型,不能修改的安全的
    private static final List<String> type= Arrays.asList("image/jpeg","image/jpg","image/gif");

    @RequestMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {

        //1、校验图片类型是否正确
        String contentType = file.getContentType();   //获取上传图片的类型
        if(!type.contains(contentType)){ //判断上传图片的类型是否匹配
           return Result.error("上传的图片类型不正确，图片不合法");
        }

        //2、校验上传图片是否为空
        BufferedImage image = ImageIO.read(file.getInputStream());  //获取上传图片的内容
        if (image == null){   //判断是否为空
            return Result.error("上传的图片为空，");
        }

        //3、将上传的图片保存到服务器
        String filename = file.getOriginalFilename();  //获取上传图片的名称
        String afterLast = StringUtils.substringAfterLast(filename, ".");  //截取图片名称  以.结尾
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), afterLast, null);  //上传 参数以将图片以流的形式上传，参数二图片大小，参数三图片名称

        //生成url地址返回
        return Result.ok("http://image.usian.com/"+storePath.getFullPath()); //获取上传图片返回的url路劲
    }
}
