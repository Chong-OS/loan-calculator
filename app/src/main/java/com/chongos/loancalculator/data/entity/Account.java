package com.chongos.loancalculator.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
@Entity(tableName = "account")
public class Account {

  @PrimaryKey(autoGenerate = true)
  public long id;
  public String name;
  public double amount;
  public long startTime;
  public int years;

}
