package com.adambritt.workshift.db;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.adambritt.workshift.api.Shift;
import com.adambritt.workshift.api.Shop;
import com.adambritt.workshift.api.User;

@RegisterBeanMapper(Shop.class)
public interface ShopDao {
	@SqlUpdate("create table if not exists shops("
			+ "  id identity primary key"
			+ ", name varchar(255) not null);")
	public void createShopTable();
	
	@SqlUpdate("INSERT INTO shops (NAME) VALUES(:name)")
	@GetGeneratedKeys("id")
	public int addUser(@BindBean Shop shop);
	
	@SqlQuery("SELECT id, name FROM shops WHERE id = :id")
	public Shop getShopById(@Bind("id") long id);
	
	@SqlQuery("SELECT id, name"
			+ " FROM users"
			+ " inner join users_shops"
			+ " on users.id = users_shops.userId"
			+ " WHERE users_shops.shopId = :id")
	@RegisterBeanMapper(User.class)
	public List<User> getUsersforShop(@Bind("id") long id);

	@SqlQuery("SELECT id, startTS, endTS"
			+ " FROM shifts"
			+ " WHERE shopId = :id")
	@RegisterBeanMapper(Shift.class)
	public List<Shift> getShiftsforShop(@Bind("id") long id);

	@SqlQuery("SELECT id, name FROM shops")
	public List<Shop> getAllShops();



}
