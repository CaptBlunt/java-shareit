package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query(value = "select * from bookings as booking " +
            "left join items as it on it.id = booking.item_id " +
            "where it.owner_id = ?1 " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findByOwnerId(Integer userId, PageRequest pageable);

    @Query(value = "select * from bookings as booking " +
            "right join items as it on it.id = booking.item_id " +
            "where it.owner_id = ?1 and booking.end_date < current_timestamp " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findByOwnerIdPastBookings(Integer userId, PageRequest pageable);

    @Query(value = "select * from bookings as booking " +
            "right join items as it on it.id = booking.item_id " +
            "where it.owner_id = ?1 and it.id = ?2 and booking.start_date < current_timestamp and booking.status <> 'REJECTED' " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findByOwnerIdAndItemIdPastBookings(Integer userId, Integer itemId);

    @Query(value = "select * from bookings as booking " +
            "left join items as it on it.id = booking.item_id " +
            "where it.owner_id = ?1 and booking.status = ?2 " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findByOwnerIdAndStatus(Integer userId, String status, PageRequest pageable);

    @Query(value = "select * from bookings as booking " +
            "right join items as it on it.id = booking.item_id " +
            "where it.owner_id = ?1 and booking.start_date > current_timestamp " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findByOwnerIdFutureBookings(Integer userId, PageRequest pageable);

    @Query(value = "select * from bookings as booking " +
            "right join items as it on it.id = booking.item_id " +
            "where it.owner_id = ?1 and it.id = ?2 and booking.start_date > current_timestamp and booking.status <> 'REJECTED' " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findByOwnerIdAndItemIdFutureBookings(Integer userId, Integer itemId);

    @Query(value = "select * from bookings " +
            "right join items as it on it.id = bookings.item_id " +
            "where bookings.item_id = ?1", nativeQuery = true)
    List<Booking> findByItemId(Integer itemId);

    @Query(value = "select * from bookings " +
            "right join items as it on it.id = bookings.item_id " +
            "where bookings.booker_id = ?1 and it.id = ?2 and bookings.start_date < current_timestamp ", nativeQuery = true)
    List<Booking> findByBookerIdAndItemIdPastBookings(Integer userId, Integer itemId);

    @Query(value = "select * from bookings " +
            "right join items as it on it.id = bookings.item_id " +
            "where bookings.booker_id = ?1 and it.id = ?2 and bookings.end_date > current_timestamp", nativeQuery = true)
    List<Booking> findByBookerIdAndItemIdFutureBookings(Integer userId, Integer itemId);

    @Query(value = "select * from bookings as booking " +
            "left join items as it on it.id = booking.item_id " +
            "where it.owner_id = ?1 and current_timestamp between booking.start_date and booking.end_date " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findByOwnerIdCurrentBookings(Integer userId, PageRequest pageable);

    @Query(value = "select * from bookings as booking " +
            "where booking.booker_id = ?1 and booking.status = ?2", nativeQuery = true)
    List<Booking> findByBookerAndStatus(Integer userId, String status, PageRequest pageable);


    @Query(value = "select * from bookings as booking " +
            "where booking.booker_id = ?1 " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findByBookerOrderByStartDesc(Integer userId, PageRequest pageable);

    @Query(value = "select * from bookings as booking " +
            "where booking.booker_id = ?1 and " +
            "booking.end_date < current_timestamp " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findByBookerIdPastBookings(Integer userId, PageRequest pageable);

    @Query(value = "select * from bookings as booking " +
            "where booking.booker_id = ?1 and " +
            "booking.start_date > current_timestamp " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findByBookerIdFutureBookings(Integer userId, PageRequest pageable);

    @Query(value = "select * from bookings as booking " +
            "where booking.booker_id = ?1 and " +
            "current_timestamp between booking.start_date and booking.end_date " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findByBookerIdCurrentBookings(Integer userId, PageRequest pageable);
}
