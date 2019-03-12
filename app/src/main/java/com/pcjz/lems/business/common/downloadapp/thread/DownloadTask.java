package com.pcjz.lems.business.common.downloadapp.thread;

import android.content.Context;
import android.util.Log;

import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.downloadapp.callback.DownloadCallBack;
import com.pcjz.lems.business.common.downloadapp.event.EventMessage;
import com.pcjz.lems.business.common.downloadapp.services.DownloadService;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.entity.offline.ThreadBean;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.storage.downloaddb.AppInfoDao;
import com.pcjz.lems.business.storage.downloaddb.AppInfoDaoImpl;
import com.pcjz.lems.business.storage.downloaddb.ThreadDao;
import com.pcjz.lems.business.storage.downloaddb.ThreadDaoImpl;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kun on 2016/11/11.
 * 下载任务
 */
public class DownloadTask implements DownloadCallBack {

    private final AppInfoBean appBean;
    private ThreadDao dao;
    private String mPhone;

    /**
     * 总下载完成进度
     */
    private int finishedProgress = 0;
    /**
     * 下载线程信息集合
     */
    private List<ThreadBean> threads;
    /**
     * 下载线程集合
     */
    private List<DownloadThread> downloadThreads = new ArrayList<>();

    /**
     * 下载item集合
     */
    private List<AppInfoBean> appInfoBeans = new ArrayList<>();
    private final AppInfoDao appDao;

    public DownloadTask(Context context, AppInfoBean appBean, int downloadThreadCount) {
        this.appBean = appBean;
        dao = new ThreadDaoImpl(context);
        appDao = new AppInfoDaoImpl(context);
        mPhone = SharedPreferencesManager.getString(ResultStatus.PHONE);
        if (mPhone == null) {
            mPhone = "";
        }
        //初始化下载线程
        initDownThreads(downloadThreadCount);
    }

    private void initDownThreads(int downloadThreadCount) {
        //查询数据库中的下载线程信息
        threads = dao.getThreads(appBean.getUrl(), mPhone);
        if (threads.size() == 0) {//如果列表没有数据 则为第一次下载
            Log.w("AAA", "第一次下载");
            //根据下载的线程总数平分各自下载的文件长度
            int length = appBean.getLength() / downloadThreadCount;
            for (int i = 0; i < downloadThreadCount; i++) {
                ThreadBean thread = new ThreadBean(i, appBean.getUrl(), i * length,
                        (i + 1) * length - 1, 0, mPhone);
                if (i == downloadThreadCount - 1) {
                    thread.setEnd(appBean.getLength());
                }
                //将下载线程保存到数据库
                dao.insertThread(thread);
                threads.add(thread);
            }
            //如果是更新下载，需要删除之前的数据库
            if (appBean.isUpdate()) {
                appDao.deleteApp(appBean.getId());
            }
            //保存下载的App对象
            appDao.insertApp(appBean);
            //发送广播提示下载数量
            EventMessage eventMessage1 = new EventMessage(5, "myDownloadNo");
            EventBus.getDefault().post(eventMessage1);
        }
        //创建下载线程开始下载
        for (ThreadBean thread : threads) {
            finishedProgress += thread.getFinished();
            DownloadThread downloadThread = new DownloadThread(appBean, thread, this);
            DownloadService.executorService.execute(downloadThread);
            downloadThreads.add(downloadThread);
        }
        Log.w("AAA", " 开始下载：" + finishedProgress);
    }

    /**
     * 暂停下载
     */
    public void pauseDownload() {
        for (DownloadThread downloadThread : downloadThreads) {
            if (downloadThread != null) {
                downloadThread.setPause(true);
            }
        }
    }

    @Override
    public void pauseCallBack(ThreadBean threadBean) {
        //保存下载进度到数据库
        Log.w("AAA", "保存数据:" + threadBean.toString());
        dao.updateThread(threadBean.getUrl(), threadBean.getId(), threadBean.getFinished(), mPhone);

        appDao.updateApp(appBean.getBasicUrl(), appBean.getId(), appBean.getFinished());
        EventMessage message = new EventMessage(4, appBean);
        EventBus.getDefault().post(message);
    }

    private long curTime = 0;

    @Override
    public void progressCallBack(int length) {
        //i++;
        //TLog.log("length" +"i=" + i + "-" + length);
        finishedProgress += length;
        //每100毫秒发送刷新进度事件
        if (System.currentTimeMillis() - curTime > 100 || finishedProgress == appBean.getLength()) {
            TLog.log("finishedprogress", finishedProgress + "");
            appBean.setFinished(finishedProgress);
            EventMessage message = new EventMessage(3, appBean);
            EventBus.getDefault().post(message);
            curTime = System.currentTimeMillis();
        }
    }

    @Override
    public synchronized void threadDownLoadFinished(ThreadBean threadBean) {
        for (ThreadBean bean : threads) {
            if (bean.getId() == threadBean.getId()) {
                //从列表中将已下载完成的线程信息移除
                threads.remove(bean);
                break;
            }
        }
        if (threads.size() == 0) {//如果列表size为0 则所有线程已下载完成
            //删除数据库中的信息
            dao.deleteThread(appBean.getUrl(), mPhone);
            appBean.setFinished(appBean.getLength());

            appDao.updateApp(appBean.getBasicUrl(), appBean.getId(), appBean.getLength());
            //发送下载完成事件
            EventMessage message = new EventMessage(2, appBean);
            EventBus.getDefault().post(message);
        }
    }

    public AppInfoBean getFileBean() {
        return appBean;
    }
}
