package com.geccocrawler.gecco.spider.internal;

/**
 * @author: zhangwei
 * @date: 23:22/2019-01-01
 */
public interface PrioprityQueueNode {

    int INDEX_NOT_IN_QUEUE = -1;

    int priorityQueueIndex(DefaultPriorityQueue<?> queue);

    void priorityQueueIndex(DefaultPriorityQueue<?> queue, int i);
}
