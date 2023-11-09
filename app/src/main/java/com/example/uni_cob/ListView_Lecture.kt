package com.example.uni_cob

class ListView_Lecture {
    private var storeName: String? = null
    private var remain_stat: String? = null

    constructor(storeName: String, remain_stat: String){
        this.storeName = storeName
        this.remain_stat = remain_stat
    }

    fun setStoreName(storeName: String) {
        this.storeName = storeName
    }

    fun setRemainStat(remain_stat: String) {
        this.remain_stat = remain_stat
    }

    fun getStoreName(): String? {
        return this.storeName
    }

    fun getRemainStat(): String? {
        return this.remain_stat
    }
}