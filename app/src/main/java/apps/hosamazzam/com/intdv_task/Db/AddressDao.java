package apps.hosamazzam.com.intdv_task.Db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AddressDao {

    @Query("SELECT * FROM addresses")
    List<Address> getAll();

    @Query("SELECT * FROM addresses WHERE uid IN (:userIds)")
    List<Address> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM addresses WHERE name LIKE :word ")
    Address findByName(String word);

    @Query("SELECT * FROM addresses WHERE id = :uid ")
    Address findById(String uid);

    @Insert
    void insertAll(Address... addresses);

    @Insert
    void insert(Address address);

    @Delete
    void delete(Address address);
}
