package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DbGenerate {

    public static void main(String[] args){
/*        Schema schema = new Schema(1, "com.wosloveslife.httppro.db");
        Entity entity = schema.addEntity("DownloadEntity");
        entity.addLongProperty("start_position");
        entity.addLongProperty("end_position");
        entity.addLongProperty("progress_position");
        entity.addStringProperty("download_url");
        entity.addIntProperty("thread_id");
        entity.addIdProperty().autoincrement();

        try {
            new DaoGenerator().generateAll(schema,"daogenerate/src/gen");
        } catch (Exception e) {
            e.printStackTrace();
        }
*/


/*
      // 创建一个用户表
        Schema schema = new Schema(1, "com.yesing.moneysocial.db");
        Entity entity = schema.addEntity("UserEntity"); // 用户表(除用户自己以外的用户的数据)

        entity.addIntProperty("_id").primaryKey(); // 主ID 自增长

        entity.addStringProperty("ID").unique().notNull(); // 用户ID
        entity.addStringProperty("nike");   // 昵称
        entity.addStringProperty("des");    // 简介
        entity.addStringProperty("headImage");  // 头像地址
        entity.addStringProperty("backImage");  //

        entity.addStringProperty("exp");   // 经验值
        entity.addStringProperty("level"); // 等级

        entity.addStringProperty("confirm");    // 认证信息
        entity.addStringProperty("sex");   // 性别
        entity.addStringProperty("birthday");   // 生日
        entity.addStringProperty("video");  // 简介视频
        entity.addStringProperty("tags");   // 个人标签组(是一个数组,通过字符串拼接或json)

        entity.addStringProperty("recentBlogDs");   // 最近的三个状态(是一个数组,通过字符串拼接或json)

        entity.addStringProperty("msgPrice");    // 聊天的价格(别人需要支付给该用户的金额)
        entity.addStringProperty("spendedMoney");   // 用户花费的钱(单词拼写错误 应该是spentMoney)
        entity.addStringProperty("recivedMoney");  // 用户接收的钱(单词拼写错误 应该是receivedMoney)

        entity.addDateProperty("createData");   // 自己追加的, 创建的日期
*/



/*
        // 创建 关注表
        Schema schema = new Schema(1, "com.yesing.moneysocial.userwatching");
        Entity entity = schema.addEntity("UserWatchingEntity");
        entity.addIntProperty("_id").primaryKey(); // 主ID 自增长
        entity.addStringProperty("userID");
        entity.addStringProperty("watchingID");
*/



        // 创建 聊天待领取交易表
        Schema schema = new Schema(1, "com.yesing.moneysocial.msgmoney");
        Entity entity = schema.addEntity("MsgMoneyEntity");
        entity.addLongProperty("_id").primaryKey(); // 主ID 自增长
        entity.addStringProperty("fromID");
        entity.addStringProperty("toID");
        entity.addStringProperty("money");
        entity.addLongProperty("endTime");
        entity.addLongProperty("created");


        try {
            new DaoGenerator().generateAll(schema,"daogenerate/src/gen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
