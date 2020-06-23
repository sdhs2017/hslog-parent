package com.hs.elsearch.entity;

/**
 * range范围的参数
 */
public class QueryRange {
    private boolean isGtInclude;//是否包含gt->gte
    private String gt;//value>gt
    private boolean isLtInclude;//是否包含lt->lte
    private String lt;//value<lt
    public QueryRange(String gt,boolean isGtInclude,String lt,boolean isLtInclude){
        this.gt = gt;
        this.isGtInclude = isGtInclude;
        this.lt = lt;
        this.isLtInclude = isLtInclude;
    }
    public boolean getIsGtInclude() {
        return isGtInclude;
    }

    public void setIsGtInclude(boolean isGtInclude) {
        this.isGtInclude = isGtInclude;
    }

    public String getGt() {
        return gt;
    }

    public void setGt(String gt) {
        this.gt = gt;
    }

    public boolean getIsLtInclude() {
        return isLtInclude;
    }

    public void setIsLtInclude(boolean isLtInclude) {
        this.isLtInclude = isLtInclude;
    }

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }
}
