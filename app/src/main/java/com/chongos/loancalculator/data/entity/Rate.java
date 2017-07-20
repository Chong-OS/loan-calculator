package com.chongos.loancalculator.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author ChongOS
 * @since 19-Jul-2017
 */
@Entity(
    tableName = "rate",
    indices = @Index("account_id"),
    foreignKeys = @ForeignKey(
        entity = Account.class,
        parentColumns = "id",
        childColumns = "account_id"
    )
)
public class Rate {

  @PrimaryKey(autoGenerate = true)
  public long id;
  @ColumnInfo(name = "account_id")
  public long accountId;
  public double rate;
  @ColumnInfo(name = "_order")
  public int order;
  public int months;
}
