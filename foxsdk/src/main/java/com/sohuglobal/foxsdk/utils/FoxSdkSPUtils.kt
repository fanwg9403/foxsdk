package com.sohuglobal.foxsdk.utils

import android.content.Context
import android.content.SharedPreferences
import com.sohuglobal.foxsdk.core.WishFoxSdk


/**
 *
 * 主要功能:
 * @Description:
 * @author: 范为广
 * @date: 2025年10月11日 17:12
 */
class FoxSdkSPUtils private constructor(spName: String?) {
    private val sp: SharedPreferences
    private val edit: SharedPreferences.Editor

    init {
        WishFoxSdk.requireInitialized()
        sp = WishFoxSdk.getContext().getSharedPreferences(spName, MODE)
        edit = sp.edit()
    }

    /**
     * SP中写入String
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String?, value: String?) {
        sp.edit().putString(key, value).apply()
    }

    /**
     * SP中读取String
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`null`
     */
    fun getString(key: String?): String {
        return getString(key, "")
    }

    /**
     * SP中读取String
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getString(key: String?, defaultValue: String?): String {
        return sp.getString(key, defaultValue) ?: ""
    }

    /**
     * SP中写入int
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String?, value: Int) {
        sp.edit().putInt(key, value).apply()
    }

    /**
     * SP中读取int
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getInt(key: String?): Int {
        return getInt(key, -1)
    }

    /**
     * SP中读取int
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getInt(key: String?, defaultValue: Int): Int {
        return sp.getInt(key, defaultValue)
    }

    /**
     * SP中写入long
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String?, value: Long) {
        sp.edit().putLong(key, value).apply()
    }

    /**
     * SP中读取long
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getLong(key: String?): Long {
        return getLong(key, -1L)
    }

    /**
     * SP中读取long
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getLong(key: String?, defaultValue: Long): Long {
        return sp.getLong(key, defaultValue)
    }

    /**
     * SP中写入float
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String?, value: Float) {
        sp.edit().putFloat(key, value).apply()
    }

    /**
     * SP中读取float
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getFloat(key: String?): Float {
        return getFloat(key, -1f)
    }

    /**
     * SP中读取float
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getFloat(key: String?, defaultValue: Float): Float {
        return sp.getFloat(key, defaultValue)
    }

    /**
     * SP中写入boolean
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String?, value: Boolean) {
        sp.edit().putBoolean(key, value).apply()
    }

    /**
     * SP中读取boolean
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`false`
     */
    fun getBoolean(key: String?): Boolean {
        return getBoolean(key, false)
    }

    /**
     * SP中读取boolean
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    /**
     * SP中写入String集合
     *
     * @param key    键
     * @param values 值
     */
    fun put(key: String?, values: MutableSet<String?>?) {
        sp.edit().putStringSet(key, values).apply()
    }

    /**
     * SP中读取StringSet
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`null`
     */
    fun getStringSet(key: String?): MutableSet<String?>? {
        return getStringSet(key, mutableSetOf<String?>())
    }

    /**
     * SP中读取StringSet
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getStringSet(key: String?, defaultValue: MutableSet<String?>?): MutableSet<String?>? {
        return sp.getStringSet(key, defaultValue)
    }

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    fun getAll(): MutableMap<String?, *>? {
        return sp.all
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    fun remove(key: String?) {
        sp.edit().remove(key).apply()
    }

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    fun contains(key: String?): Boolean {
        return sp.contains(key)
    }

    /**
     * SP中清除所有数据
     */
    fun clear() {
        sp.edit().clear().apply()
    }

    fun <T> save(keyword: String?, value: T?): FoxSdkSPUtils {
        val editor = sp.edit()
        when (value) {
            null -> {
                editor.remove(keyword).apply()
            }

            is String -> {
                editor.putString(keyword, value as String).apply()
            }

            is Int -> {
                editor.putInt(keyword, value as Int).apply()
            }

            is Boolean -> {
                editor.putBoolean(keyword, value as Boolean).apply()
            }

            is Long -> {
                editor.putLong(keyword, value as Long).apply()
            }

            is Float -> {
                editor.putFloat(keyword, value as Float).apply()
            }
        }
        return this
    }

    fun <T> get(keyword: String?, defValue: T?): T? {
        val value: T?
        when (defValue) {
            is String -> {
                val s: String = sp.getString(keyword, defValue as String)!!
                value = s as T
            }

            is Int -> {
                val i = sp.getInt(keyword, defValue as Int)
                value = i as T
            }

            is Long -> {
                val l = sp.getLong(keyword, defValue as Long)
                value = l as T
            }

            is Float -> {
                val f = sp.getFloat(keyword, defValue as Float)
                value = f as T
            }

            is Boolean -> {
                val b = sp.getBoolean(keyword, defValue as Boolean)
                value = b as T
            }

            else -> {
                value = defValue
            }
        }
        return value
    }

    fun set(key: String?, value: String?) {
        edit.putString(key, value)
        edit.apply()
    }

    fun setSync(key: String?, value: String?) {
        edit.putString(key, value)
        edit.commit()
    }

    fun get(key: String?): String {
        return sp.getString(key, null) ?: ""
    }

    companion object {
        private val sSPMap: MutableMap<String?, FoxSdkSPUtils?> = HashMap<String?, FoxSdkSPUtils?>()

        //	private final static String name = "words_config";  为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。
        private const val MODE = Context.MODE_PRIVATE

        val instance: FoxSdkSPUtils
            /**
             * 获取SP实例
             *
             * @return [FoxSdkSPUtils]
             */
            get() = getInstance("")

        /**
         * 获取SP实例
         *
         * @param spName sp名
         * @return [FoxSdkSPUtils]
         */
        fun getInstance(spName: String? = "WishFoxSdkSP"): FoxSdkSPUtils {
            var spName = spName
            if (isSpace(spName)) {
                spName = "spUtils"
            }
            var sp = sSPMap[spName]
            if (sp == null) {
                sp = FoxSdkSPUtils(spName)
                sSPMap.put(spName, sp)
            }
            return sp
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) {
                return true
            }
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }
    }
}