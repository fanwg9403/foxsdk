package com.sohuglobal.foxsdk.data.model.entity

/**
 * @file FSCreateOrder
 * 文件说明：商品下单实体
 *
 * @author 王金强
 * @date 2025/10/21 14:29
 */
data class FSCreateOrder(
    val code_url: String,
    val extend: String,
    val isspid: String,
    val jump_url: String,
    val pay_type: String,
    val pos_id: String,
    val pos_seq: String,
    val response_type: String,
    val result: Result,
    val sign: String,
    val sys_seq: String,
    val trans_time: String,
    val type: String,
    val virtual_coin: String,
    val trade_type: String,
    val trade_number: String,
    val busy_type: String,
    val busy_code: String,
    val busy_title: String,
    val busy_amount: String,
    val operate_channel: String

)

data class Result(
    val comment: String,
    val id: String
)