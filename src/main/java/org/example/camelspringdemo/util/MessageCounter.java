package org.example.camelspringdemo.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class MessageCounter {

    private int txtCount = 0;
    private int xmlCount = 0;
    private int otherCount = 0;
    private int totalCount = 0;

    public synchronized void resetCounters() {
        log.debug("Counters: txt" + txtCount + " xml" + xmlCount + " other" + otherCount);
        this.txtCount = 0;
        this.xmlCount = 0;
        this.otherCount = 0;
        this.totalCount = 0;
    }

    public synchronized void incTxtCounter() {
        txtCount++;
        totalCount++;
    }

    public synchronized void incXmlCounter() {
        xmlCount++;
        totalCount++;
    }

    public synchronized void incOtherCounter() {
        otherCount++;
        totalCount++;
    }

    public synchronized String getCounters() {
        return "Counters: txt = " + txtCount + ", xml = " + xmlCount + ", other = " + otherCount;
    }

}
