package com.adjust.price.service.other;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.PostConstruct;
import java.util.List;

public interface ManualManager<T> {

    JpaRepository<T, Long> getRepo();

    void addLog(String logs);

    default void saveData( List<T> dataList) {
        long t1 = System.currentTimeMillis();
        addLog(String.format("Start Saving %s data into DB", dataList.get(0).getClass().getSimpleName()));
        getRepo().deleteAll();
        getRepo().saveAll(dataList);
        long t2 = System.currentTimeMillis() - t1;
        addLog(String.format("Ended saving data into DB, timecost: %sms", t2));
        reload();
    }

    List<T> getData();

    @PostConstruct
    default void reload(){
       addLog("ManualManager>>Start searching data...");
        long t1 = System.currentTimeMillis();
        getData().clear();
        getData().addAll(getRepo().findAll());
        addLog(String.format("ManualManager>>End searching data, timecost: %sms", System.currentTimeMillis() - t1));
    }

}
