package com.yesing.moneysocial.msgmoney;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.yesing.moneysocial.msgmoney.MsgMoneyEntity;

import com.yesing.moneysocial.msgmoney.MsgMoneyEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig msgMoneyEntityDaoConfig;

    private final MsgMoneyEntityDao msgMoneyEntityDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        msgMoneyEntityDaoConfig = daoConfigMap.get(MsgMoneyEntityDao.class).clone();
        msgMoneyEntityDaoConfig.initIdentityScope(type);

        msgMoneyEntityDao = new MsgMoneyEntityDao(msgMoneyEntityDaoConfig, this);

        registerDao(MsgMoneyEntity.class, msgMoneyEntityDao);
    }
    
    public void clear() {
        msgMoneyEntityDaoConfig.getIdentityScope().clear();
    }

    public MsgMoneyEntityDao getMsgMoneyEntityDao() {
        return msgMoneyEntityDao;
    }

}