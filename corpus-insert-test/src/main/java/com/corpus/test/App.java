package com.corpus.test;


import com.corpus.test.po.CorpusBean;
import com.corpus.test.task.CorpusInsertTask;
import com.corpus.test.task.CorpusInsertTastWithJDBC;
import com.corpus.test.util.ConnectionPool;
import com.corpus.test.util.JayCommonUtil;
import com.corpus.test.util.ReadCorpusUtil;
import com.corpus.test.util.SqlSessionFactoryUtil;
import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liutingna on 2017/11/28.
 *
 * @author liutingna
 */
public class App {
    public static void main(String[] args) throws Exception{
        long start = System.currentTimeMillis();
        //样例数据见demoData/demo.txt
        String path = "D:\\testData\\nlp\\0-DICT-CORE.TXT";
        List<CorpusBean> corpusBeanList = ReadCorpusUtil.read(path);
        long endRead = System.currentTimeMillis();
        System.out.println("Read File Consume: " + (endRead - start) + "ms");

        List<List<CorpusBean>> listList = JayCommonUtil.splitList(corpusBeanList, 10000);
        CountDownLatch countDownLatch = new CountDownLatch(listList.size());
        SqlSession sqlSession = SqlSessionFactoryUtil.openSession();

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (List<CorpusBean> corpusBeans : listList) {
            executorService.execute(new CorpusInsertTask(corpusBeans, countDownLatch, sqlSession));
        }

       /* for (List<CorpusBean> corpusBeans : listList) {
            executorService.execute(new CorpusInsertTastWithJDBC(corpusBeans, countDownLatch));
        }
*/
        countDownLatch.await();
        long endInsert = System.currentTimeMillis();
        System.out.println("All consume: " + (endInsert - start) + "ms");
    }

}

