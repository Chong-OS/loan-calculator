package com.chongos.loancalculator.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.chongos.loancalculator.data.entity.Rate;
import io.reactivex.Flowable;
import java.util.List;

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
@Dao
public interface RateDao {

  @Insert
  long insert(Rate rate);

  @Update
  int update(Rate rate);

  @Delete
  int delete(Rate rate);

  @Query("SELECT * from rate where account_id = :id ORDER BY _order ASC")
  Flowable<List<Rate>> loadAll(long id);
}
