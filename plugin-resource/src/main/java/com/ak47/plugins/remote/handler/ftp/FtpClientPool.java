package com.ak47.plugins.remote.handler.ftp;

import com.ak47.plugins.common.SystemContent;
import com.ak47.plugins.exception.ResourceException;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class FtpClientPool {
    private final static Logger logger = LoggerFactory.getLogger(FtpClientPool.class);

    private static final int DEFAULT_POOL_SIZE = 16;

    private BlockingQueue<FTPClient> pool;

    private FtpClientFactory factory;

    public FtpClientPool(FtpClientFactory factory) {
        this(factory, DEFAULT_POOL_SIZE);
    }

    public FtpClientPool(FtpClientFactory factory,int size) {
        this.factory = factory;
        this.pool = new ArrayBlockingQueue<>(size);
        initPool(size);
    }

    private void initPool(int maxPoolSize) {
        try {
            int count = 0;
            while (count < maxPoolSize) {
                pool.offer(factory.makeClient(),10,TimeUnit.SECONDS);
                count ++;
            }
        } catch (Exception e) {
            logger.error("ftp连接池初始化失败",e);
        }

    }
    public FTPClient borrowClient(){
        try {
            FTPClient client = pool.take();
            if(client == null) {
                client = factory.makeClient();
            }else if(!factory.validateClient(client)) {
                factory.makeClient(client);
            }
            return client;
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new ResourceException("ftpclient获取失败");
        }

    }

    public void returnClient(FTPClient ftpClient){
        try {
            ftpClient.changeWorkingDirectory("/");
            if(ftpClient != null && !pool.offer(ftpClient, 10, TimeUnit.SECONDS)) {
                factory.destroyClient(ftpClient);
            }
        } catch (Exception e) {
            logger.error("归还对象失败",e);
        }
    }
}
