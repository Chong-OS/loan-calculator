package com.chongos.loancalculator.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import com.chongos.loancalculator.data.dao.AccountDao;
import com.chongos.loancalculator.data.dao.InstalmentDao;
import com.chongos.loancalculator.data.dao.RateDao;
import com.chongos.loancalculator.data.entity.Account;
import com.chongos.loancalculator.data.entity.Instalment;
import com.chongos.loancalculator.data.entity.Rate;

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
@Database(entities = {Account.class, Rate.class, Instalment.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

  public abstract AccountDao account();

  public abstract RateDao rate();

  public abstract InstalmentDao instalment();
}