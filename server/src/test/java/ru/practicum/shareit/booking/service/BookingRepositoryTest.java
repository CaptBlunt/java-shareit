package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.CommentRepository;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;


    @BeforeEach
    void delete() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();

        userRepository.save(userOwner);
        userRepository.save(userBooker);
        itemRepository.save(item);
        itemRepository.save(itemTwo);
        bookingRepository.save(booking);
        bookingRepository.save(bookingTwo);
        bookingRepository.save(bookingThree);
    }

    User userOwner = new User("bob@gamail.com", "Bob");
    User userBooker = new User("bob2@gamail.com", "Bob2");

    Item item = new Item("Test", "Test", userOwner, true);
    Item itemTwo = new Item("Test2", "Test2", userOwner, true);

    Booking booking = new Booking(itemTwo, userBooker, BookingStatus.APPROVED, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(2));
    Booking bookingTwo = new Booking(itemTwo, userBooker, BookingStatus.REJECTED, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusDays(1));
    Booking bookingThree = new Booking(item, userBooker, BookingStatus.APPROVED, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3));

    PageRequest page = PageRequest.of(1 / 10, 10);


    @Test
    void findBookingsWhenOwnerIdOneAndBookingsTwo() {
        List<Booking> bookings = bookingRepository.findByOwnerId(userOwner.getId(), page);

        System.out.println(bookings);
        assertEquals(bookings.size(), 3);
        assertEquals(bookings.get(0).getId(), bookingThree.getId());
    }

    @Test
    void findBookingsWhenOwnerIdOneAndItemIdTwoInPast() {
        List<Booking> bookings = bookingRepository.findByOwnerIdAndItemIdPastBookings(userOwner.getId(), itemTwo.getId());

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findBookingsWhenOwnerIdOneAndStatusRejected() {
        List<Booking> bookings = bookingRepository.findByOwnerIdAndStatus(userOwner.getId(), "REJECTED", page);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), bookingTwo.getId());
        assertEquals(bookings.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void findBookingsWhenOwnerIdOneInFuture() {
        List<Booking> bookings = bookingRepository.findByOwnerIdFutureBookings(userOwner.getId(), page);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), bookingThree.getId());
    }

    @Test
    void findBookingsWhenBookerIdTwoInPresent() {
        List<Booking> bookings = bookingRepository.findByBookerIdCurrentBookings(userBooker.getId(), page);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), userBooker.getId());
    }
}