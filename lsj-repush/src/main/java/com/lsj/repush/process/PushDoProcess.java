package com.lsj.repush.process;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.lsj.repush.ParcelActivity;
import com.lsj.repush.ResponseData;
import com.lsj.repush.common.ExcelSplitReader;
import com.lsj.repush.common.split.SplitListener;
import com.lsj.repush.common.split.SplitModel;
import com.lsj.repushbase.common.RePushDoProcess;
import com.lsj.repushbase.common.SkipFailException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * PushDoProcess .
 *
 * @author lsj
 * @date 2023-06-11 12:21
 */
@Slf4j
public class PushDoProcess extends RePushDoProcess<ParcelActivity> {

    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(100, 100, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));


    /**
     * .
     *
     * @return .
     */
    @Override
    protected ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    @Override
    protected List<ParcelActivity> readFromPath(String path) {
        List<SplitModel> modelList = ExcelSplitReader.readExcelToList(path, SplitModel.class);
        return modelList.stream().map(lpCodeModel -> buildParcelActivity(lpCodeModel.getValue())).collect(Collectors.toList());
    }

    @Override
    protected String keyString(ParcelActivity parcelActivity) {
        return parcelActivity.getParams().getMainCode();
    }

    @Override
    protected void handleRespBody(ParcelActivity parcelActivity, String string) {
        ResponseData responseData = JSONUtil.toBean(string, ResponseData.class);
        if (!responseData.isSuccess()) {
            log.error("{}-失败:{}", keyString(parcelActivity), responseData.getErrorMsg());
            if (responseData.getErrorMsg().endsWith("非平台订单")) {
                throw new SkipFailException(string);
            } else {
                throw new RuntimeException(string);
            }
        }
    }

    @Override
    protected String getUrl() {
        return "http://localhost:6060/repush/test";
    }

    @Override
    protected String getBaseDir() {
        return "E:/临时/重推/20230611-1";
    }

    @Override
    protected InputStream getFile() {
        try {
            return Files.newInputStream(new File(getBaseDir() + "/test1.xlsx").toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<String> cutFile(InputStream inputStream) {
        try (InputStream is = inputStream) {
            EasyExcel.read(is)
                    .head(SplitModel.class)
                    .registerReadListener(new SplitListener(getTaskSplitDir(), getCutPageSize()))
                    .sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Arrays.stream(Objects.requireNonNull(new File(getTaskSplitDir()).listFiles())).map(File::getAbsolutePath).collect(Collectors.toList());
    }

    private ParcelActivity buildParcelActivity(String lpCode) {
        ParcelActivity parcelActivity = new ParcelActivity();
        ParcelActivity.Params params = new ParcelActivity.Params();
        params.setActivityType("parcel");
        params.setSelectType("LP_CODE");
        params.setNodeCode("SORTING_CENTER");
        params.setActivityCode("SORTING_CENTER_ORDER_INFO_NOTIFY");
        params.setNeedUpdateState(false);
        params.setResourceCode("Tran_Store_13452544");
        params.setNeedPreActivityCheck(false);
        params.setPreActivityType("parcel");
        params.setCroBorSecStage(false);
        params.setOrderType(0);
        params.setMainCode(lpCode);
        parcelActivity.setParams(params);
        parcelActivity.setTenantCode("AE_GLOBAL@CAINIAO_GLOBAL");
        return parcelActivity;
    }

    public static void main(String[] args) {
        PushDoProcess pushDoProcess = new PushDoProcess();
        pushDoProcess.execute("33333333333333333");
    }

    /**
     * 切分，每个文件的数量.
     */
    @Override
    public Long getCutPageSize() {
        return 1000L;
    }
}
