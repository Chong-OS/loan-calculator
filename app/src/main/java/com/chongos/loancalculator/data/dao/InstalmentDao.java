package com.chongos.loancalculator.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.chongos.loancalculator.data.entity.Instalment;
import io.reactivex.Flowable;
import java.util.List;

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
@Dao
public interface InstalmentDao {

  @Insert
  long insert(Instalment instalment);

  @Insert
  void inserts(List<Instalment> instalment);

  @Update
  int update(Instalment instalment);

  @Update
  int updates(List<Instalment> instalment);

  @Delete
  int delete(Instalment instalment);

  @Delete
  int deletes(List<Instalment> instalment);

  @Query("SELECT * from instalment where account_id = :id ORDER BY _order ASC")
  Flowable<List<Instalment>> loadAll(long id);
}