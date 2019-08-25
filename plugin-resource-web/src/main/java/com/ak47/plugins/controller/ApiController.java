package com.ak47.plugins.controller;


import com.ak47.plugins.common.RequestCodeEnum;
import com.ak47.plugins.config.ResourceDefinition;
import com.ak47.plugins.factory.DefaultResourceFactory;
import com.ak47.plugins.model.VO.ApiResult;
import com.ak47.plugins.model.VO.BaseResult;
import com.ak47.plugins.model.VO.LayuiTablePage;
import com.ak47.plugins.model.VO.LayuiTablePageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("api")
public class ApiController {

    @Value("${local.jar.temp}")
    private String temp=System.getProperty("base.dir")+"/.local/";

    @Autowired
    private DefaultResourceFactory defaultResourceFactory;

    @GetMapping("getList")
    public LayuiTablePage<ResourceDefinition> getList() {
        return LayuiTablePageFactory.success(defaultResourceFactory.getResourcesList());
    }

    @GetMapping("getResource")
    public String getResource() {
        return defaultResourceFactory.fetchResourceContent();
    }

    @GetMapping("getJar.jar")
    public void getJar(int id, HttpServletResponse response) {
        File file = defaultResourceFactory.getJar(id,temp,"ftp");
        response.setHeader( "Content-Disposition ","attachment; filename=" + file.getName());
        try (OutputStream outputStream = response.getOutputStream();
             InputStream inputStream = new FileInputStream(file);){
            byte[] bytes = new byte[1024];
            while(inputStream.read(bytes) != -1){
                outputStream.write(bytes);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("uploadJar")
    public BaseResult uploadJar(MultipartFile file) {
        File localhost = new File(temp + file.getOriginalFilename());
        try {
            if(!localhost.getParentFile().exists()){
                localhost.getParentFile().mkdirs();
                localhost.createNewFile();
            } else if(!localhost.exists()){
                localhost.createNewFile();
            }
            file.transferTo(localhost);
            defaultResourceFactory.addUploadFile(localhost);
            return ApiResult.success(file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.fail(RequestCodeEnum.ERROR);
        }
    }

    @PostMapping("upload")
    public BaseResult upload(ResourceDefinition resourceDefinition) {
        File file = defaultResourceFactory.getLocalFile(resourceDefinition.getJar());
        try (InputStream inputStream = new FileInputStream(file)){
            resourceDefinition.setUrl("ftp");
            if(defaultResourceFactory.uploadResources(resourceDefinition,inputStream,false)){
                return BaseResult.success();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file.delete();
        }
        return BaseResult.fail(RequestCodeEnum.UPLOAD_FAIL);
    }
}
