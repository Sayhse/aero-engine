package com.ly.aeroengine.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ly.aeroengine.entity.FileChunkRecord;
import com.ly.aeroengine.entity.FileRecord;
import com.ly.aeroengine.entity.bo.FileProcessBo;
import com.ly.aeroengine.entity.bo.ShardMetadataBo;
import com.ly.aeroengine.entity.request.FileProcessParam;
import com.ly.aeroengine.entity.request.MultipartFileParam;
import com.ly.aeroengine.enums.FileResultCodeEnum;
import com.ly.aeroengine.exception.FileServiceBizException;
import com.ly.aeroengine.mapper.FileChunkRecordMapper;
import com.ly.aeroengine.mapper.FileRecordMapper;
import com.ly.aeroengine.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.Records;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.Cleaner;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.ly.aeroengine.constant.FileKeyConstant.*;
import static com.ly.aeroengine.enums.FileResultCodeEnum.*;


/**
 * @Author ly
 * @Date 2024 03 13 16 41
 **/
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload.dir}")
    private String FILE_UPLOAD_DIR;
    @Value("${file.upload.initial.shardSize}")
    private Long SHARD_SIZE;
    @Value("${file.upload.initial.concurrency}")
    private Integer CONCURRENCY;
    @Value("${hadoop.hdfs.host}")
    private String hdfsHost;
    @Value("${hadoop.hdfs.port}")
    private String hdfsPort;
    @Resource
    private FileRecordMapper fileRecordMapper;
    @Resource
    private FileChunkRecordMapper fileChunkRecordMapper;
    @Autowired
    private FileSystem fileSystem;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    ServletWebServerApplicationContext context;
    private static int retryNum = 0;

    @Override
    public Path mkdir(String loginName) {
        Path path = new Path("/user" + loginName + "/" + System.currentTimeMillis());
        try {
            if (!fileSystem.exists(path))
                fileSystem.mkdirs(path);
        } catch (IOException e) {
            throw new FileServiceBizException(FileResultCodeEnum.FOLDER_CREATE_FAILED);
        }
        //首先判断文件系统实例是否为null 如果不为null 进行关闭
        if (fileSystem != null){
            try {
                fileSystem.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    @Override
    public String fileUpload2HDFS(MultipartFileParam fileParam, Path dst) {
        try {
            //创建本地文件路径 hdfs上传路径
            Path src = new Path(FILE_UPLOAD_DIR + File.separator + "preprocess" + File.separator + fileParam.getName());
            //文件上传动作
            fileSystem.copyFromLocalFile(src,dst);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "文件上传成功";
    }

    @Override
    public ShardMetadataBo getShardMetadata(String userId) {
        //TODO 返回 shardSize concurrency

        // 1.尝试从redis获取 shardSize concurrency
        String shardSizeKey = FILE_CHUNK_SHARD_SIZE + userId;
        String shardSize = redisTemplate.opsForValue().get(shardSizeKey);
        String concurrencyKey = FILE_CHUNK_CONCURRENCY + userId;
        String concurrency = redisTemplate.opsForValue().get(concurrencyKey);
        // 2.如果获取没有获取到数据 说明这是第一次查询 需要获取初始值
        if (shardSize == null ||concurrency == null){
            // 3.从配置中心获取初始值 返回
            return new ShardMetadataBo(SHARD_SIZE,CONCURRENCY);
        }
        else
        // 4.如果获取到 返回
        {
            return new ShardMetadataBo(Long.parseLong(shardSize),Integer.parseInt(concurrency));
        }
    }

    @Override
    public boolean fileUpload2Local(MultipartFileParam file, LocalDateTime arriveTime) throws IOException {
        boolean chunkFlag = file.isChunkFlag();
        if (!chunkFlag) {
            // 不分片小文件
            return singleUpload(file);
        }
        // 分片大文件
        return chunkUpload(file,arriveTime);
    }

    @Override
    public List<FileChunkRecord> check(String md5) {
        if (StrUtil.isBlank(md5)){
            throw new FileServiceBizException(FILE_IS_EMPTY);
        }
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_md5", md5);
        FileRecord record = fileRecordMapper.selectOne(queryWrapper);
        if (Objects.nonNull(record)) {
            throw new FileServiceBizException(FILE_HAS_BEEN_UPLOADED);
        }
        List<FileChunkRecord> records = fileChunkRecordMapper.queryByMd5(md5);
        return records;
    }

    @Override
    public boolean preProcess(MultipartFileParam file) {
        try {
            String pythonScriptPath = "src/main/resources/preprocess.py";
            String filePathInput = FILE_UPLOAD_DIR + File.separator + file.getName();
            String filePathOutput = FILE_UPLOAD_DIR + File.separator + "preprocess" + File.separator + file.getName();

            Process process = Runtime.getRuntime().exec("python " + pythonScriptPath + " " + filePathInput + " " + filePathOutput);
            int wait = process.waitFor();
            if (wait == 0){
                return true;
            }
            else{
                throw new FileServiceBizException(FILE_PREPROCESS_ERROR);
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileProcessBo processFile(FileProcessParam fileProcessParam) {
        System.setProperty("HADOOP_USER_NAME","root");
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.set("yarn.resourcemanager.hostname","192.168.88.62");
        yarnConfiguration.set("yarn.nodemanager.hostname","192.168.88.62");
        yarnConfiguration.set("yarn.nodemanager.hostname","192.168.88.63");
        yarnConfiguration.set("yarn.nodemanager.hostname","192.168.88.64");
        yarnConfiguration.set("yarn.nodemanager.aux-services","mapreduce_shuffle");

        //创建yarnClient实例
        try (YarnClient yarnClient = YarnClient.createYarnClient()) {
            yarnClient.init(yarnConfiguration);
            //启动yarnClient
            yarnClient.start();
            //创建YarnClientApplication实例
            YarnClientApplication application = yarnClient.createApplication();
            GetNewApplicationResponse newApplicationResponse = application.getNewApplicationResponse();
            // 设置应用程序名称和队列
            ApplicationSubmissionContext appContext = application.getApplicationSubmissionContext();
            ApplicationId appId = appContext.getApplicationId();
            appContext.setApplicationName("MyYarnApplication");
            appContext.setQueue("default");
            appContext.setApplicationType("MAPREDUCE");

            ContainerLaunchContext amContainer = Records.newRecord(ContainerLaunchContext.class);
            amContainer.setCommands(Collections.singletonList("$HADOOP_HOME/bin/hadoop jar /export/servers/jar_test/mapreduce-1.0-SNAPSHOT.jar com.ly.mapreduce.JobMain"));

            appContext.setAMContainerSpec(amContainer);

            // 设置资源请求
            org.apache.hadoop.yarn.api.records.Resource resource = Records.newRecord(org.apache.hadoop.yarn.api.records.Resource.class);
            resource.setMemorySize(2048); // 设置内存资源大小
            resource.setVirtualCores(1); // 设置虚拟核心数

            appContext.setResource(resource);
            yarnClient.submitApplication(appContext);

            String preAppId = appId.toString();
            String[] split = preAppId.split("_");
            int last = Integer.parseInt(split[split.length - 1]);
            last++;
            String lastNum = String.valueOf(last);
            split[split.length - 1] = lastNum;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                stringBuilder.append(split[i]);
                if (i != (split.length - 1)){
                    stringBuilder.append("_");
                }
            }
            String realAppId = stringBuilder.toString();

            ApplicationId applicationId = ApplicationId.fromString(realAppId);
            // 打印应用程序ID
            System.out.println("Submitted YARN application. ApplicationId: " + applicationId);
            ApplicationReport report = yarnClient.getApplicationReport(applicationId);
            YarnApplicationState state = report.getYarnApplicationState();
            while (state != YarnApplicationState.FINISHED && state != YarnApplicationState.FAILED && state != YarnApplicationState.KILLED){
                report = yarnClient.getApplicationReport(applicationId);
                state = report.getYarnApplicationState();
                log.info("Got application report " +
                        ", clientToAMToken=" + report.getClientToAMToken()
                        + ", appDiagnostics=" + report.getDiagnostics()
                        + ", appMasterHost=" + report.getHost()
                        + ", appQueue=" + report.getQueue()
                        + ", appMasterRpcPort=" + report.getRpcPort()
                        + ", appStartTime=" + report.getStartTime()
                        + ", yarnAppState=" + report.getYarnApplicationState().toString()
                        + ", distributedFinalState=" + report.getFinalApplicationStatus().toString()
                        + ", appTrackingUrl=" + report.getTrackingUrl()
                        + ", appUser=" + report.getUser());
                Thread.sleep(10000);
            }if (state == YarnApplicationState.FAILED) {
                //应用跑失败，重试
                if (retryNum < 3){
                    yarnClient.stop();
                    this.processFile(fileProcessParam);
                }else {
                    //出问题了返回前端
                    throw new FileServiceBizException(FILE_PROCESS_ERROR);
                }
            }else if (state == YarnApplicationState.KILLED){
                //应用被强制中止，报错给前端
                throw new FileServiceBizException(FILE_PROGRAM_RUDE_ABORT);
            }
            yarnClient.stop();
        } catch (YarnException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        //应用跑成功，打回给前端
        return FileProcessBo.builder().msg("文件处理成功！").build();
    }

    private boolean chunkUpload(MultipartFileParam fileParam, LocalDateTime arriveTime) throws IOException {
        // 分片大文件upload流程  一个大文件分片后的所有分片文件的MD5都与原本的大文件一样

        int currentChunk = fileParam.getChunk();
        long totalSize = fileParam.getTotalSize();
        String fileName = fileParam.getName();
        String fileMd5 = fileParam.getMd5();
        int lastChunkFlag = fileParam.getLastChunkFlag();
        MultipartFile multipartFile = fileParam.getFile();
        long beginByteIndex = fileParam.getBeginByteIndex();

        String parentDir = FILE_UPLOAD_DIR + File.separator + fileMd5 + File.separator;
        String tempFileName = fileName + "_tmp";

        // 写入到临时文件
        long startTime = System.currentTimeMillis();
        File tmpFile = tmpFile(parentDir, tempFileName, multipartFile, currentChunk, totalSize, fileMd5,beginByteIndex);
        long endTime = System.currentTimeMillis();

        // 计算出写分块文件的时长（处理时长） 和 拿到传输时长
        Duration duration = Duration.between(fileParam.getUploadTime(), arriveTime);
        long transferTime = duration.toMillis();
        log.info("transferTime: ",transferTime);
        long executionTime = endTime - startTime;
        log.info("executionTime: ",executionTime);

        long shardSize = fileParam.getShardSize();
        long nextShardSizeLong = shardSize;

        if (transferTime > executionTime){
            // 如果传输大于处理 shardSize（下一次分块大小）= shardSize * 1.25
            double nextShardSize = shardSize * 1.25;
            nextShardSizeLong = Math.round(nextShardSize);
        }
        else if (transferTime < executionTime){
            // 如果传输小于处理 shardSize（下一次分块大小）= shardSize * 0.75
            double nextShardSize = shardSize * 0.75;
            nextShardSizeLong = Math.round(nextShardSize);
        }
        log.info("nextShardSizeLong: ",nextShardSizeLong);

        // 拿Tomcat线程池数据 getPoolSize()看是否达到200，如果没有则 concurrency * 1.25
        int concurrency = fileParam.getConcurrency();
        int nextConcurrencyInt = concurrency;
        ThreadPoolExecutor executor= (ThreadPoolExecutor)((TomcatWebServer) context.getWebServer())
                .getTomcat().getConnector().getProtocolHandler().getExecutor();
        int currentPoolSize = executor.getPoolSize();
        if (currentPoolSize < 200){
            float nextConcurrency = (float) (concurrency * 1.25);
            nextConcurrencyInt = Math.round(nextConcurrency);
        }
        log.info("nextConcurrencyInt: ",nextConcurrencyInt);

        // 将新的nextShardSizeLong nextConcurrencyInt forCount 存入redis
        storeToRedis(nextShardSizeLong,nextConcurrencyInt,fileParam.getForCount(),fileParam.getLoginName());

        if (lastChunkFlag == 1) {
            // 检查md5是否一致
            if (!checkFileMd5(tmpFile, fileMd5)) {
                cleanUp(tmpFile, fileMd5);
                throw new RuntimeException("文件md5检测不符合要求, 请检查!");
            }
            // 文件重命名, 成功则新增文件的记录
            if (!renameFile(tmpFile, fileName)) {
                throw new RuntimeException("文件重命名失败, 请检查!");
            }
            recordFile(fileName, fileMd5, parentDir);

            //删除redis中的文件缓存
            String shardSizeKey = FILE_CHUNK_SHARD_SIZE + fileParam.getLoginName();
            String concurrencyKey = FILE_CHUNK_CONCURRENCY + fileParam.getLoginName();
            String forCountKey = FILE_CHUNK_FOR_COUNT + fileParam.getLoginName();

            redisTemplate.opsForValue().getAndDelete(shardSizeKey);
            redisTemplate.opsForValue().getAndDelete(concurrencyKey);
            redisTemplate.opsForValue().getAndDelete(forCountKey);
            log.info("文件上传完成, 时间是:{}, 文件名称是:{}", DateUtil.now(), fileName);
        }


        return true;
    }

    private void storeToRedis(long nextShardSizeLong, int nextConcurrencyInt, int forCount, @NotBlank(message = "用户名不能为空") String loginName) {
        String shardSizeKey = FILE_CHUNK_SHARD_SIZE + loginName;
        String redisShardSize = redisTemplate.opsForValue().get(shardSizeKey);
        String concurrencyKey = FILE_CHUNK_CONCURRENCY + loginName;
        String redisConcurrency = redisTemplate.opsForValue().get(concurrencyKey);
        String forCountKey = FILE_CHUNK_FOR_COUNT + loginName;
        String redisForCount = redisTemplate.opsForValue().get(forCountKey);
        // 1.获取redis中数据判断是否存在
        if (redisShardSize == null || redisConcurrency == null || redisForCount == null){
            // 2.若不存在则说明为第一次存入 直接存入
            redisTemplate.opsForValue().setIfAbsent(shardSizeKey,String.valueOf(nextShardSizeLong));
            redisTemplate.opsForValue().setIfAbsent(concurrencyKey,String.valueOf(nextConcurrencyInt));
            redisTemplate.opsForValue().setIfAbsent(forCountKey,String.valueOf(forCount));
        }
        else {
            // 3.若存在则说明两种情况 （1）当前轮不为第一轮且此次存入为当前轮第一次存入 （2）此次存入为前几轮或当前轮第n次存入（n>1）
            // 4.存在则判断redis中forCount 与 curForCount
            if (Integer.parseInt(redisForCount) < forCount){
                // 5.如果redisForCount < curForCount 属于情况（1） 直接存入
                redisTemplate.opsForValue().setIfAbsent(shardSizeKey,String.valueOf(nextShardSizeLong));
                redisTemplate.opsForValue().setIfAbsent(concurrencyKey,String.valueOf(nextConcurrencyInt));
                redisTemplate.opsForValue().setIfAbsent(forCountKey,String.valueOf(forCount));
            }
            // 6.如果redisForCount >= curForCount 属于情况（2） 直接不做存入 跳出方法
        }

    }

    private boolean checkFileMd5(File file, String fileMd5) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        String checkMd5 = DigestUtils.md5DigestAsHex(fis).toUpperCase();
        fis.close();
        if (checkMd5.equals(fileMd5.toUpperCase())) {
            return true;
        }
        return false;
    }
    private void cleanUp(File file, String md5) {
        if (file.exists()) {
            file.delete();
        }
        // 删除上传记录
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        fileChunkRecordMapper.delete(queryWrapper);
    }
    private boolean renameFile(File toBeRenamed, String toFileNewName) {
        // 检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            log.info("File does not exist: " + toBeRenamed.getName());
            return false;
        }
        String parentPath = toBeRenamed.getParent();
        File newFile = new File(parentPath + File.separatorChar + toFileNewName);
        // 如果存在, 先删除
        if (newFile.exists()) {
            newFile.delete();
        }
        return toBeRenamed.renameTo(newFile);
    }

    public void recordFile(String fileName, String fileMd5, String parentDir) {
        Date now = new Date();
        String filePath = parentDir + fileName;
        long size = FileUtil.size(new File(filePath));
        String sizeStr = size / (1024 * 1024) + "Mb";
        // 更新文件记录
        FileRecord record = new FileRecord();
        record.setFileName(fileName).setFileMd5(fileMd5).setFileSize(sizeStr).setFilePath(filePath)
                .setUploadStatus(1).setCreateTime(now).setUpdateTime(now);
        fileRecordMapper.insert(record);
        // 删除分片文件的记录
        fileChunkRecordMapper.deleteByMd5(fileMd5);
    }

    private File tmpFile(String parentDir, String tempFileName, MultipartFile multipartFile, int currentChunk, long totalSize, String fileMd5, long beginByteIndex) throws IOException {
        log.info("开始上传文件, 时间是:{}, 文件名称是:{}", DateUtil.now(), tempFileName);

        File tmpDir = new File(parentDir);
        File tmpFile = new File(parentDir, tempFileName);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        RandomAccessFile tempRaf = new RandomAccessFile(tmpFile, "rw");
        if (tempRaf.length() == 0) {
            tempRaf.setLength(totalSize);
        }
        // 写入该分片数据
        FileChannel fc = tempRaf.getChannel();
        MappedByteBuffer map = fc.map(FileChannel.MapMode.READ_WRITE, beginByteIndex, multipartFile.getSize());
        map.put(multipartFile.getBytes());
        clean(map);
        fc.close();
        tempRaf.close();

        // 记录已经完成的分片
        FileChunkRecord fileChunkRecord = new FileChunkRecord();
        fileChunkRecord.setMd5(fileMd5).setUploadStatus("1").setChunk(currentChunk);
        fileChunkRecordMapper.insert(fileChunkRecord);
        log.info("文件上传完成, 时间是:{}, 文件名称是:{}", DateUtil.now(), tempFileName);
        return tmpFile;
    }

    private static void clean(MappedByteBuffer map) {
        try {
            Method getCleanerMethod = map.getClass().getMethod("cleaner");
            Cleaner.create(map, null);
            getCleanerMethod.setAccessible(true);
            Cleaner cleaner = (Cleaner) getCleanerMethod.invoke(map);
            cleaner.clean();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private boolean singleUpload(MultipartFileParam file) {
        //小文件upload流程
        MultipartFile multipartFile = file.getFile();
        File baseFile = new File(FILE_UPLOAD_DIR);
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        try {
            //将文件放入指定文件夹
            multipartFile.transferTo(new File(baseFile,file.getName()));
            //记录文件信息到数据库
            Date now = new Date();
            FileRecord fileRecord = new FileRecord();
            String filePath = FILE_UPLOAD_DIR + File.separator + file.getName();
            long size = FileUtil.size(new File(filePath));
            //得出小文件多少Mb
            String sizeStr = size / (1024 * 1024) + "Mb";
            fileRecord.setFileName(file.getName()).setFilePath(filePath).setUploadStatus(1)
                    .setFileMd5(file.getMd5()).setCreateTime(now).setUpdateTime(now).setFileSize(sizeStr);
            fileRecordMapper.insert(fileRecord);
        } catch (IOException e) {
            log.error("小文件单独上传出错，原因是:{}，时间是:{}",e.getMessage());
        }
        return true;
    }
}