package com.ak47.plugins.remote.handler.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FtpClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(FtpClientFactory.class);
    private int port;
    private String host;
    private String username;
    private String password;

    private int connectTimeout = 1000 * 10;

    public FtpClientFactory(int port, String host, String username, String password, int connectTimeout) {
        this.port = port;
        this.host = host;
        this.username = username;
        this.password = password;
        this.connectTimeout = connectTimeout;
    }

    public FtpClientFactory(int port, String host, String username, String password) {
        this.port = port;
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public FTPClient makeClient() {
        FTPClient ftpClient = new FTPClient();
        makeClient(ftpClient);
        return ftpClient;
    }

    public FTPClient makeClient(FTPClient ftpClient) {
        ftpClient.setConnectTimeout(connectTimeout);
        try {
            ftpClient.connect(host, port);
            boolean result = ftpClient.login(username, password);
            int reply = ftpClient.getReplyCode();
            if (!result || !FTPReply.isPositiveCompletion(reply)) {
                logger.info("ftp登录失败,username: {}", username);
                return null;
            }
            ftpClient.setControlEncoding("utf-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //被动模式 被动模式是客户端向服务端发送PASV命令，服务端随机开启一个端口并通知客户端，客户端根据该端口与服务端建立连接，然后发送数据。服务端是两种模式的，
            //使用哪种模式取决于客户端，同时关键点在于网络环境适合用哪种模式，比如客户端在防火墙内，则最好选择被动模式
            //在mac下测试用被动模式没问题，用主动模式则报错，在linux服务器上则相反
            //ftpClient.enterLocalPassiveMode();
            ftpClient.enterLocalActiveMode();
        } catch (IOException e) {
            logger.error("makeClient exception", e);
            destroyClient(ftpClient);
        }
        return ftpClient;
    }

    public void destroyClient(FTPClient ftpClient){
        try {
            if(ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (Exception e) {
            logger.error("ftpClient logout exception",e);
        } finally {
            try {
                if(ftpClient != null) {
                    ftpClient.disconnect();
                }
            } catch (Exception e2) {
                logger.error("ftpClient disconnect exception",e2);
            }

        }
    }

    public boolean validateClient(FTPClient ftpClient) {
        try {
            return ftpClient.sendNoOp();
        } catch (Exception e) {
            logger.error("ftpClient validate exception",e);
        }
        return false;
    }
}
