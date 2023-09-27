package com.adambritt.workshift.db;

import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;

import com.adambritt.workshift.api.Shift;
import com.adambritt.workshift.api.Shop;
import com.adambritt.workshift.api.User;

public interface ShiftDao {

	@SqlUpdate("create table if not exists shifts("
			+ "  id identity primary key"
			+ ", shopId bigint"
			+ ", startTS timestamp with time zone not null"
			+ ", endTS timestamp with time zone not null"
			+ ", constraint fk_shopId foreign key(shopId) references shops(id)"
			+ ");")
	public void createShiftTable();
	
	@SqlUpdate("INSERT INTO shifts (startTS, endTS, shopId) VALUES(:startTS, :endTS, :shopId)")
	@GetGeneratedKeys("id")
	public int addShift(@BindBean Shift.Add shift);
	
	@SqlUpdate("INSERT INTO shifts_users (shiftId, userId) VALUES(:shiftId, :userId)")
	public void addUserToShift(@Bind("shiftId") long shiftId, @Bind("userId") long userId);
	
	@SqlUpdate("INSERT INTO users_shops (userId, shopId) VALUES(:userId, :shopId)")
	public void addUserToShop(@Bind("shopId") long shopId, @Bind("userId") long userId);
	
	@SqlQuery("SELECT shifts.id as a_id, shifts.startTS as a_startTS, shifts.endTS as a_endTS"
			+ ", shops.id as b_id, shops.name as b_name"
			+ " FROM shifts"
			+ " inner join shops"
			+ " on shifts.shopId = shops.id"
			+ " WHERE shifts.id = :id")
	@RegisterBeanMapper(value = Shift.class, prefix = "a")
	@RegisterBeanMapper(value = Shop.class, prefix = "b")
	@UseRowReducer(ShopReducer.class)
	public Shift getShiftById(@Bind("id") long id);
	

	@SqlQuery("SELECT id, name"
			+ " FROM users"
			+ " inner join shifts_users"
			+ " on users.id = shifts_users.userId"
			+ " WHERE shifts_users.shiftId = :id")
	@RegisterBeanMapper(User.class)
	public List<User> getUsersforShift(@Bind("id") long id);

	@SqlQuery("SELECT shifts.id as a_id, shifts.startTS as a_startTS, shifts.endTS as a_endTS"
			+ ", shops.id as b_id, shops.name as b_name"
			+ " FROM shifts"
			+ " inner join shifts_users"
			+ " on shifts.id = shifts_users.shiftId"
			+ " inner join shops"
			+ " on shifts.shopId = shops.id"
			+ " where shifts_users.userId = :userId")
	@RegisterBeanMapper(value = Shift.class, prefix = "a")
	@RegisterBeanMapper(value = Shop.class, prefix = "b")
	@UseRowReducer(ShopReducer.class)
	public List<Shift> getShiftsForUser(@Bind("userId") long userId);
	
	@SqlQuery("SELECT shifts.id as a_id, shifts.startTS as a_startTS, shifts.endTS as a_endTS"
			+ ", shops.id as b_id, shops.name as b_name"
			+ " FROM shifts"
			+ " inner join shops"
			+ " on shifts.shopId = shops.id")
	@RegisterBeanMapper(value = Shift.class, prefix = "a")
	@RegisterBeanMapper(value = Shop.class, prefix = "b")
	@UseRowReducer(ShopReducer.class)
	public List<Shift> getAllShifts();
	
	
	class ShopReducer implements LinkedHashMapRowReducer<Integer, Shift> {

		@Override
		public void accumulate(Map<Integer, Shift> container, RowView rowView) {
			Shift shift = container.computeIfAbsent(rowView.getColumn("a_id", Integer.class), id -> rowView.getRow(Shift.class));
			Shop shop = rowView.getRow(Shop.class);
			shift.setShop(shop);
		}
	}
}
