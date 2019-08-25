package com.ak47.plugins.remote.handler;

import com.ak47.plugins.common.SystemContent;
import com.ak47.plugins.exception.ResourceException;
import com.ak47.plugins.remote.RemoteResourceHandler;
import com.ak47.plugins.remote.handler.ftp.FtpClientFactory;
import com.ak47.plugins.remote.handler.ftp.FtpClientPool;
import com.ak47.plugins.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class FtpResourceHandler implements RemoteResourceHandler,InitializingBean {

    @Value("${ftp.times}")
    private final int DEFAULT_TIMES = 5;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.host}")
    private String host;
    @Value("${ftp.username}")
    private String username;
    @Value("${ftp.password}")
    private String password;
    @Value("${ftp.jarPath}")
    private String jarPath;
    @Value("${ftp.resourcePath}")
    private String resourcePath;
    private FtpClientPool ftpClientPool;

    public static void main(String[] args){
        FtpClientPool ftpClientPool = new FtpClientPool(new FtpClientFactory(21,"192.168.85.129","ftpcmx","ftpcmx"));
        FTPClient ftpClient = ftpClientPool.borrowClient();
        try {
            ftpClient.listFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        ftpClientPool = new FtpClientPool(new FtpClientFactory(port,host,username,password));
    }

    @Override
    public boolean support(String url) {
        if(StringUtils.isBlank(url)){
            return false;
        }
        return url.toLowerCase().startsWith("ftp");
    }

    @Override
    public String uploadHandle(InputStream inputStream,String fileName) {
        if(uploadHandle(inputStream,fileName,jarPath)){
            return "ftp://" + host + ":" + port + jarPath + SystemContent.SEPARATOR + fileName;
        }
        return null;
    }

    private boolean uploadHandle(InputStream inputStream, String fileName, String path) {
        FTPClient ftpClient = ftpClientPool.borrowClient();
        try {
            boolean isSuccess = changeWorking(ftpClient,path,DEFAULT_TIMES);
            if(!isSuccess){
                throw new ResourceException("FTP目录切换失败");
            }
            ftpClient.storeFile(fileName, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException("上传失败");
        } finally {
            ftpClientPool.returnClient(ftpClient);
        }
        return true;
    }

    private boolean changeWorking(FTPClient ftpClient,String path,int times){
        try {
            if(--times < 0){
                return false;
            }
            boolean isSuccess = ftpClient.changeWorkingDirectory(path);
            if(!isSuccess){
                ftpClient.makeDirectory(path);
                return changeWorking(ftpClient,path,times);
            }
            return isSuccess;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException("FTP目录切换失败");
        }
    }

    @Override
    public boolean storeResourceFileHandler(File file) {
        try (InputStream inputStream = new FileInputStream(file);){
            return uploadHandle(inputStream,file.getName(),resourcePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteHandle(String jar) {
        FTPClient ftpClient = ftpClientPool.borrowClient();
        try {
            boolean isSuccess = changeWorking(ftpClient,jarPath,DEFAULT_TIMES);
            if(!isSuccess){
                throw new ResourceException("FTP目录切换失败");
            }
            ftpClient.deleteFile(jar);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException("上传失败");
        } finally {
            ftpClientPool.returnClient(ftpClient);
        }
        return true;
    }

    @Override
    public File downloadJar(String filename,String temp) {
        FTPClient ftpClient = ftpClientPool.borrowClient();
        try {
            boolean isSuccess = changeWorking(ftpClient,jarPath,DEFAULT_TIMES);
            if(!isSuccess){
                throw new ResourceException("FTP目录切换失败");
            }

            for (FTPFile ftpFile : ftpClient.listFiles()) {
                if(ftpFile.getName().equals(filename)){
                    File file = FileUtil.ifAbsent(temp + SystemContent.SEPARATOR + filename);
                    try (OutputStream outputStream = new FileOutputStream(file)){
                        ftpClient.retrieveFile(filename,outputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new ResourceException("下载失败");
                    }
                    return file;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException("下载失败");
        } finally {
            ftpClientPool.returnClient(ftpClient);
        }
        return null;
    }

    @Override
    public boolean downloadResourceHandler(String path) {
        String filename = path.substring(path.lastIndexOf(SystemContent.SEPARATOR)+1);
        FTPClient ftpClient = ftpClientPool.borrowClient();
        try {
            boolean isSuccess = changeWorking(ftpClient,resourcePath,DEFAULT_TIMES);
            if(!isSuccess){
                throw new ResourceException("FTP目录切换失败");
            }

            for (FTPFile ftpFile : ftpClient.listFiles()) {
                if(ftpFile.getName().equals(filename)){
                    File file = FileUtil.ifAbsent(path);
                    try (OutputStream outputStream = new FileOutputStream(file)){
                        return ftpClient.retrieveFile(filename,outputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new ResourceException("下载失败");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException("下载失败");
        } finally {
            ftpClientPool.returnClient(ftpClient);
        }
        return false;
    }

    @Override
    public String fetchResourceContent(String path) {
        if(!FileUtil.exists(path)){
            downloadResourceHandler(path);
        }
        File file = new File(path);
        try (InputStream inputStream =  new FileInputStream(file);
             Reader reader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(reader)){
            StringBuilder resource = new StringBuilder();
            String line;
            while((line=bufferedReader.readLine())!=null){
                resource.append(line);
            }
            return resource.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException("下载失败");
        }
    }
}
