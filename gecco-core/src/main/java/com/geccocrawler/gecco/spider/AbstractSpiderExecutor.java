package com.geccocrawler.gecco.spider;

/**
 * @author: zhangwei
 * @date: 16:11/2019-01-03
 */
public abstract class AbstractSpiderExecutor implements SpiderExecutor {

    private SpiderExecutorGroup group;

    public AbstractSpiderExecutor(){
        this(null);
    }

    public AbstractSpiderExecutor(SpiderExecutorGroup group) {
        this.group = group;
    }

    @Override
    public SpiderExecutorGroup parent() {
        return group;
    }
}
