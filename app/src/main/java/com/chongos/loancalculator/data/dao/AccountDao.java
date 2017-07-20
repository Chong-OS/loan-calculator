package com.chongos.loancalculator.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.chongos.loancalculator.data.entity.Account;
import io.reactivex.Flowable;
import java.util.List;

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
@Dao
public interface AccountDao {

  @Insert
  long insert(Account account);

  @Update
  int update(Account account);

  @Delete
  int delete(Account account);

  @Query("SELECT * from account ORDER BY id DESC")
  Flowable<List<Account>> loadAll();

  @Query("SELECT * from account where id = :id LIMIT 1")
  Flowable<Account> load(long id);

}
